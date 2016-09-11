package cafesat
package theories.adt

import org.scalatest._

import scala.reflect.ClassTag

/**
 * Created by gs on 14.05.15.
 */
class AdtSolverSpec extends FlatSpec with BeforeAndAfter {

  private var _currentTestName: String = "<Unset>"
  def currentTestName = "unknown"//_currentTestName
  //override protected def runTest(testName: String, reporter: Reporter,
  //                                stopper: Stopper, configMap: Map[String, Any],
  //                                tracker: Tracker): Unit = {
  //  _currentTestName = testName
  //  super.runTest(testName, reporter, stopper, configMap, tracker)
  //}

  import Types._

  trait FreshSolver {
    val solver = new AdtSolver
    val sig: Signature
    val declaredTypes: Typing = Map()
    val eqs: Seq[(Term, Term)] = Seq()
    val ineqs: Seq[(Term, Term)] = Seq()
    val tests: Seq[Tester] = Seq()
    val negtests: Seq[Tester] = Seq()

    val expectSplitting: Option[Boolean] = None //Some(false)

    def checkSplitting() = {
      val didSplit = solver.debugDidSplit()
      if (expectSplitting.exists(_ != didSplit))
        fail(if (didSplit) "Unexpected splitting" else "Expected splitting, but none occurred")
    }

    def solve =
      solver.solve(Instance(sig, declaredTypes, eqs, ineqs, tests, negtests))

    def assertSat(dumpModel: Boolean = false) = {
      solve match {
        case Unsat(reason) =>
          fail(s"Unexpectedly unsat: $reason\n" + solver.dumpTerms())
        case Sat(model) => // Ok
          if (dumpModel) {
            println(s"Model:")
//            for (terms <- model; termsSorted = terms.sortBy(solver.termNiceness(_)) if terms.size > 1)
//              println(s"\t${termsSorted.mkString(" = ")}")
            for ((lblOption, terms) <- model; termsSorted = terms.sortBy(solver.termNiceness))
              println(s"\t$lblOption | ${termsSorted.mkString(" = ")}")
          }
      }
      checkSplitting()
    }
    def assertUnsat() = {
      solve match {
        case Sat(_) => fail(s"Unexpectedly sat")
        case _ => // Ok
      }
      checkSplitting()
    }
    def assertUnsatDueTo[T <: UnsatReason]()(implicit ev: ClassTag[T]) = {
      solve match {
        case Sat(_) => fail(s"Unexpectedly sat")
        case Unsat(_: T) => // Ok
        case Unsat(reason) => fail(s"Expected unsat due to $ev, instead got $reason")
      }
      checkSplitting()
    }
  }
  trait SimpleFiniteSig extends FreshSolver {
    val Fina = Constructor(0,0,List())
    val Finb = Constructor(0,1,List())

    val sigFin = Seq(Seq(), Seq()) // Cona, Conb
    val sigFinDts = Seq(Seq(), Seq())
    val sig = Signature(Seq(sigFin), Seq(sigFinDts))
  }
  trait FiniteAndListSig extends SimpleFiniteSig {
    def Cons(h: Term, t:Term) = Constructor(1,0,List(h,t))
    val Nil = Constructor(1,1,List())
    def Head(cons: Term) = Selector(1,0,0,cons)
    def Tail(cons: Term) = Selector(1,0,1,cons)

    val sigList = Seq(Seq(0,1), Seq()) // Cons(Fin, List), Nil
    val sigListDts = Seq(Seq(Nil, Nil), Seq())
    override val sig = Signature(Seq(sigFin, sigList), Seq(sigFinDts, sigListDts))
  }
  trait SIntAndIntListSig extends FreshSolver {
    def Succ(pred: Term) = Constructor(0,0,List(pred))
    val Zero = Constructor(0,1,List())
    def Pred(succ: Term) = Selector(0,0,0,succ)

    def Cons(h: Term, t:Term) = Constructor(1,0,List(h,t))
    val Nil = Constructor(1,1,List())
    def Head(cons: Term) = Selector(1,0,0,cons)
    def Tail(cons: Term) = Selector(1,0,1,cons)

    val sigSInt = Seq(Seq(0), Seq()) // Succ(SInt), Zero
    val sigSIntDts = Seq(Seq(Zero), Seq())
    val sigList = Seq(Seq(0,1), Seq()) // Cons(SInt, List), Nil
    val sigListDts = Seq(Seq(Zero, Nil), Seq())
    override val sig = Signature(Seq(sigSInt, sigList), Seq(sigSIntDts, sigListDts))
  }

  before {
    println(s"== $currentTestName ==")
  }
  after {
    println("")
  }


  "Solver" should "return sat on empty constraints" in new SimpleFiniteSig {
    assertSat()
  }

  it should "return sat on trivial constraints" in new SimpleFiniteSig {
    // TODO: This case (any many other simple ones) should work without splitting as
    //  soon as inequality detection has been improved
//    override val expectSplitting = Some(false)
    override val eqs = Seq((Variable(1), Variable(1)))
    assertSat()
  }

  it should "return unsat on trivial inequality" in new SimpleFiniteSig {
    override val ineqs = Seq((Variable(1), Variable(1)))
    assertUnsatDueTo[InvalidEquality]()
  }

  it should "return sat on trivial unification" in new SimpleFiniteSig {
    override val eqs = Seq((Variable(1), Fina))
    assertSat()
  }
  it should "return sat on trivial multiple unification" in new SimpleFiniteSig {
    override val eqs = Seq((Variable(1), Fina), (Variable(2), Finb))
    assertSat()
  }

  it should "return sat on trivial equality of constructors" in new SimpleFiniteSig {
    override val eqs = Seq((Finb, Finb))
    assertSat()
  }

  it should "return unsat on trivially distinct constructors" in new SimpleFiniteSig {
    override val eqs = Seq((Fina, Finb))
    assertUnsatDueTo[EmptyLabelling]()
  }

  it should "return unsat on simply distinct constructors 1" in new SimpleFiniteSig {
    override val eqs = Seq((Variable(1), Fina), (Variable(1), Finb) )
    assertUnsatDueTo[EmptyLabelling]()
  }
  it should "return unsat on simply distinct constructors 2" in new SimpleFiniteSig {
    override val eqs = Seq((Variable(1), Fina), (Finb, Variable(1)) )
    assertUnsatDueTo[EmptyLabelling]()
  }
  it should "return unsat on simple inequality" in new SimpleFiniteSig {
    override val eqs = Seq((Variable(1), Fina), (Variable(2), Fina) )
    override val ineqs = Seq((Variable(1), Variable(2)))
    assertUnsatDueTo[InvalidEquality]()
  }

  it should "return unsat on distinct constructors with variable equality" in new SimpleFiniteSig {
    override val eqs = Seq(
      (Variable(1), Fina),
      (Variable(2), Finb),
      (Variable(1), Variable(2))
    )
    assertUnsatDueTo[EmptyLabelling]()
  }

  it should "return sat on simple equality 1" in new SimpleFiniteSig {
    override val eqs = Seq((Variable(1), Fina), (Variable(1), Fina) )
    assertSat()
  }
  it should "return sat on simple equality 2" in new SimpleFiniteSig {
    override val eqs = Seq(
      (Variable(1), Fina),
      (Variable(2), Fina),
      (Variable(1), Variable(2))
    )
    assertSat()
  }

  it should "return sat on list equality" in new FiniteAndListSig {
    val x = Variable(1)
    val y = Variable(2)
    override val eqs = Seq( (Cons(x,Nil), Cons(y,Nil)) )
    assertSat()
  }

  it should "return unsat on list inequality" in new FiniteAndListSig {
    val x = Variable(1)
    val y = Variable(2)
    override val eqs = Seq( (Cons(x,Nil), Cons(y,Nil)) )
    override val ineqs = Seq( (x,y) )
    assertUnsatDueTo[InvalidEquality]()
  }

  it should "return unsat on list cycle" in new FiniteAndListSig {
    val x = Variable(1)
    override val eqs = Seq( (Cons(x,Nil), x) )
    assertUnsatDueTo[Cyclic]()
  }

  it should "return sat on list len [_] <= len [_,_]" in new FiniteAndListSig {
    val x = Variable(1)
    override val eqs = Seq( (Cons(Fina,x), Cons(Fina,Cons(Fina,Nil))) )
    assertSat()
  }
  it should "return unsat on list len [_,_] <= len [_]" in new FiniteAndListSig {
    val x = Variable(1)
    override val eqs = Seq( (Cons(Fina,Nil), Cons(Fina,Cons(Fina,x))) )
    assertUnsatDueTo[EmptyLabelling]()
  }

  it should "return unsat on trivial selector inequality" in new FiniteAndListSig {
    val x = Variable(1)
    override val eqs = Seq( (Head(x), Fina) )
    override val ineqs = Seq( (Head(x), Fina) )
    assertUnsatDueTo[InvalidEquality]
  }
  it should "return unsat on simple selector inequality" in new FiniteAndListSig {
    val x = Variable(1)
    override val eqs = Seq( (x, Cons(Fina,Nil)) )
    override val ineqs = Seq( (Head(x), Fina) )
    assertUnsatDueTo[InvalidEquality]
  }
  it should "return sat on list equality with selectors" in new FiniteAndListSig {
    val x = Variable(1)
    val y = Variable(2)
    override val eqs = Seq( (x, Cons(y,Nil)), (Head(x), y), (Tail(x), Nil) )
    assertSat()
  }

  it should "return unsat on simple instantiation of Cons, no merge, no splitting" in new FiniteAndListSig {
    val x = Variable(1)
    override val eqs = Seq( (Head(x), Fina), (Tail(x), Nil) )
    override val ineqs = Seq( (x, Cons(Fina,Nil)) )
    override val tests = Seq( Tester(1,0,x) )
    assertUnsatDueTo[InvalidEquality]()
  }
  it should "return unsat on simple instantiation of Cons, with merge, no splitting" in new FiniteAndListSig {
    val x = Variable(1)
    val y = Variable(2)
    override val eqs = Seq( (Head(x), Fina), (Tail(y), Nil), (x,y) )
    override val ineqs = Seq( (x, Cons(Fina,Nil)) )
    override val tests = Seq( Tester(1,0,x) )
    assertUnsatDueTo[InvalidEquality]()
  }
  it should "return unsat on simple instantiation of Cons, with merge, no splitting, free var" in new FiniteAndListSig {
    val x = Variable(1)
    val y = Variable(2)
    val z = Variable(3)
    override val eqs = Seq( (Head(x), z), (Tail(y), Nil), (x,y) )
    override val ineqs = Seq( (x, Cons(z,Nil)) )
    override val tests = Seq( Tester(1,0,x) )
    assertUnsatDueTo[InvalidEquality]()
  }
  it should "return unsat on simple instantiation of Cons, with splitting" in new FiniteAndListSig {
//    solver.debugOn
    override val expectSplitting = Some(true)
    val x = Variable(1)
    val z = Variable(3)
    override val eqs = Seq( (Head(x), z), (Tail(x), Nil) )
    override val ineqs = Seq( (x, Cons(z,Nil)) )
    assertUnsatDueTo[InvalidEquality]()
  }

  it should "return unsat on degenerate cyclic list example" in new FiniteAndListSig {
//    solver.debugOn
    override val expectSplitting = Some(true)
    val x = Variable(1)
    val z = Variable(3)
    def TailN: (Int, Term) => Term = (n, arg) => n match {
      case 0 => arg
      case 1 => Tail(arg)
      case i => Tail(TailN(i-1, arg))
    }
    override val eqs = Seq( (TailN(2,z), x), (z, x) )
    override val tests = Seq( Tester(1,0,z) )
    assertUnsat()
  }

  // TODO: Test case to check Instantiate 2 rule


  it should "return sat on our sample for branching" in new SIntAndIntListSig {
    solver.debugOn
    val x = Variable(1)
    val m = Variable(2)
    val y = Variable(3)
    override val eqs = Seq( (x, Cons(m, y)) )
    override val ineqs = Seq( (m, Zero), (Head(y), Zero) )
    assertSat(true)
  }

}
