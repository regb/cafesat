package cafesat

import java.io.File

import util._

object Main {

  object FileFormat extends Enumeration {
    type FileFormat = Value
    val DIMACS, SMTLIB  = Value
  }
  import FileFormat._

  private var fileFormat: Option[FileFormat] = None

  private var time = false

  private val optionsHelp: String = (
    "  --dimacs             The input file is to be interpreted as a DIMACS CNF SAT problem" + "\n" +
    //"  --smtlib2            The input file is to be interpreted as an SMT problem in SMTLIB version 2 format" + "\n" +
    "  --debug=[1-5]        Debug level" + "\n" +
    "  --tags=t1:...        Filter out debug information that are not of one of the given tags" + "\n" +
    "  --timeout=N          Timeout in seconds" + "\n" +
    "  --stats              Print statistics information" + "\n" +
    "  --restartfactor=N    Restart strategy factor" + "\n" +
    "  --restartinterval=N  Restart strategy initial interval" + "\n" +
    //"  --no-print-success   Desactivate the :print-success default option of SMT-LIB standard" + "\n"
    "  --time               Time the solving phase"
  )

  def processOptions(options: Array[String]) {
    for(option <- options) {
      option match {
        case "dimacs"        =>                           fileFormat = Some(DIMACS)
        //case "smtlib2"        =>                          fileFormat = Some(SMTLIB)
        case "time"        =>                             time = true
        //case "no-print-success"    =>                     Settings.printSuccess = Some(false)

        case "stats"         =>                           Settings.stats = true
        case "verbose" =>                                 Settings.logLevel = Logger.Debug
        case "trace" =>                                   Settings.logLevel = Logger.Trace

        //case s if s.startsWith("debug=") =>               Settings.debugLevel = try { 
        //                                                    s.substring("debug=".length, s.length).toInt 
        //                                                  } catch { 
        //                                                    case _ => 0 
        //                                                  }
        //case s if s.startsWith("tags=") =>                Settings.debugTags = Set(splitList(s.substring("tags=".length, s.length)): _*)
        case s if s.startsWith("timeout=") =>             Settings.timeout = try { 
                                                            Some(s.substring("timeout=".length, s.length).toInt)
                                                          } catch { 
                                                            case (_: Throwable) => None
                                                          }
        case s if s.startsWith("restartinterval=") =>     try { 
                                                            Settings.restartInterval = s.substring("restartInterval=".length, s.length).toInt
                                                          } catch { 
                                                            case (_: Throwable) =>
                                                          }
        case s if s.startsWith("restartfactor=") =>       try { 
                                                            Settings.restartFactor = s.substring("restartFactor=".length, s.length).toDouble
                                                          } catch { 
                                                            case (_: Throwable) =>
                                                          }
        case _ => //Reporter.error("Invalid option: " + option + "\nValid options are:\n" + optionsHelp)
      }
    }
  }

  import sat.Solver
  import Solver.Results._

  def satSolver(f: File, withSmt: Boolean = false)(implicit context: Context) = {
    val input = new java.io.FileReader(f)
    val (satInstance, nbVars) = parsers.Dimacs.cnf(input)
    val s = new Solver(nbVars)
    satInstance.foreach(s.addClause(_))
    val res = s.solve()
    res match {
      case Satisfiable(_) => println("sat")
      case Unsatisfiable => println("unsat")
      case Unknown => println("unknown")
    }
    res
  }

  def main(arguments: Array[String]) {
    try {
      val (options0, trueArgs) = arguments.partition(str => str.startsWith("--"))
      val options = options0.map(str => str.substring(2))
      processOptions(options)

      val logger = Settings.logLevel match {
        case Logger.Warning => DefaultStdErrLogger
        case Logger.Debug => VerboseStdErrLogger
        case Logger.Trace => TraceStdErrLogger
        case _ => DefaultStdErrLogger
      }
      implicit val context = Context(logger = logger)

      //if(trueArgs.size == 0) {
      //  val repl = new regolic.smtlib.REPL
      //  repl.run
      //}
      val inputFile = trueArgs(0)
      val start = System.currentTimeMillis
      if(fileFormat == Some(DIMACS)) {
        satSolver(new java.io.File(inputFile))
      } else if(fileFormat == None) {
        satSolver(new java.io.File(inputFile))
      }
      val end = System.currentTimeMillis
      val elapsed = end - start
      if(time)
        println(elapsed/1000d)
      sys.exit(0)
    } catch {
      case (e: Throwable) =>
        e.printStackTrace
        sys.exit(1)
    }
  }

  private def splitList(lst: String) : Seq[String] = lst.split(':').map(_.trim).filter(!_.isEmpty)

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

}
