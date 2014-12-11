//package cafesat
//
//import java.io.File
//
//object Main {
//
//  private var dimacs = false
//  private var smtlib2 = false
//  private var time = false
//
//  private val optionsHelp: String = (
//    "  --dimacs             The input file is to be interpreted as a DIMACS CNF SAT problem" + "\n" +
//    "  --smtlib2            The input file is to be interpreted as an SMT problem in SMTLIB version 2 format" + "\n" +
//    "  --debug=[1-5]        Debug level" + "\n" +
//    "  --tags=t1:...        Filter out debug information that are not of one of the given tags" + "\n" +
//    "  --timeout=N          Timeout in seconds" + "\n" +
//    "  --stats              Print statistics information" +
//    "  --restartfactor=N    Restart strategy factor" + "\n" +
//    "  --restartinterval=N  Restart strategy initial interval" + "\n" +
//    "  --time               Time the solving phase" + "\n" +
//    "  --no-print-success   Desactivate the :print-success default option of SMT-LIB standard"
//  )
//
//  def processOptions(options: Array[String]) {
//    for(option <- options) {
//      option match {
//        case "dimacs"        =>                           dimacs = true
//        case "smtlib2"        =>                          smtlib2 = true
//        case "time"        =>                             time = true
//        case "no-print-success"    =>                     Settings.printSuccess = Some(false)
//
//        case "stats"         =>                           Settings.stats = true
//        case "verbose" =>                                 Settings.logLevel = Logger.Debug
//        case "trace" =>                                   Settings.logLevel = Logger.Trace
//
//        //case s if s.startsWith("debug=") =>               Settings.debugLevel = try { 
//        //                                                    s.substring("debug=".length, s.length).toInt 
//        //                                                  } catch { 
//        //                                                    case _ => 0 
//        //                                                  }
//        //case s if s.startsWith("tags=") =>                Settings.debugTags = Set(splitList(s.substring("tags=".length, s.length)): _*)
//        case s if s.startsWith("timeout=") =>             Settings.timeout = try { 
//                                                            Some(s.substring("timeout=".length, s.length).toInt)
//                                                          } catch { 
//                                                            case (_: Throwable) => None
//                                                          }
//        case s if s.startsWith("restartinterval=") =>     try { 
//                                                            Settings.restartInterval = s.substring("restartInterval=".length, s.length).toInt
//                                                          } catch { 
//                                                            case (_: Throwable) =>
//                                                          }
//        case s if s.startsWith("restartfactor=") =>       try { 
//                                                            Settings.restartFactor = s.substring("restartFactor=".length, s.length).toDouble
//                                                          } catch { 
//                                                            case (_: Throwable) =>
//                                                          }
//        case _ => //Reporter.error("Invalid option: " + option + "\nValid options are:\n" + optionsHelp)
//      }
//    }
//  }
//
//  import regolic.sat.Solver
//  import Solver.Results._
//
//  def satSolver(f: File, withSmt: Boolean = false)(implicit context: Context) = {
//    val is = new java.io.FileInputStream(f)
//    val (satInstance, nbVars) = regolic.parsers.Dimacs.cnf(is)
//    val s = new Solver(nbVars)
//    if(withSmt) {
//      val s = new dpllt.DPLLSolver[dpllt.BooleanTheory.type](nbVars, dpllt.BooleanTheory)
//      val cnf = satInstance.map(clause => {
//        val lits: Set[s.theory.Literal] =
//          clause.map(l => dpllt.BooleanTheory.PropositionalLiteral(l.getID, if(l.polarity) 1 else 0))
//        lits
//      }).toSet
//      cnf.foreach(lits => s.addClause(lits))
//      val res = s.solve(dpllt.BooleanTheory.makeSolver(cnf))
//      res match {
//        case dpllt.DPLLSolver.Results.Satisfiable(_) => println("sat")
//        case dpllt.DPLLSolver.Results.Unsatisfiable => println("unsat")
//        case dpllt.DPLLSolver.Results.Unknown => println("unknown")
//      }
//      res
//    } else {
//      satInstance.foreach(s.addClause(_))
//      val res = s.solve()
//      res match {
//        case Satisfiable(_) => println("sat")
//        case Unsatisfiable => println("unsat")
//        case Unknown => println("unknown")
//      }
//      res
//    }
//  }
//
//  def main(arguments: Array[String]) {
//    try {
//      val (options0, trueArgs) = arguments.partition(str => str.startsWith("--"))
//      val options = options0.map(str => str.substring(2))
//      processOptions(options)
//
//      val logger = Settings.logLevel match {
//        case Logger.Warning => DefaultStdErrLogger
//        case Logger.Debug => VerboseStdErrLogger
//        case Logger.Trace => TraceStdErrLogger
//      }
//      implicit val context = Context(logger = logger)
//
//      if(trueArgs.size == 0) {
//        val repl = new regolic.smtlib.REPL
//        repl.run
//      }
//      val inputFile = trueArgs(0)
//      val start = System.currentTimeMillis
//      if(dimacs) {
//        satSolver(new java.io.File(inputFile))
//      } else {
//        val is = new java.io.FileReader(inputFile)
//        val lexer = new _root_.smtlib.lexer.Lexer(is)
//        regolic.smtlib.Interpreter.execute(new _root_.smtlib.parser.Parser(lexer))
//      }
//      val end = System.currentTimeMillis
//      val elapsed = end - start
//      if(time)
//        println(elapsed/1000d)
//      sys.exit(0)
//    } catch {
//      case (e: Throwable) =>
//        e.printStackTrace
//        sys.exit(1)
//    }
//  }
//
//  private def splitList(lst: String) : Seq[String] = lst.split(':').map(_.trim).filter(!_.isEmpty)
//
//  private def recursiveListFiles(f: File): Array[File] = {
//    val these = f.listFiles
//    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
//  }
//
//}
