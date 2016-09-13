package cafesat
package theories.adt

import org.scalatest._

import scala.reflect.ClassTag

class AdtSolverIntsTests extends FlatSpec with AdtSolverSpecHelpers {

  trait SIntSig extends FreshSolver {
    def Succ(pred: Term) = Constructor(0,0,List(pred))
    val Zero = Constructor(0,1,List())
    def Pred(succ: Term) = Selector(0,0,0,succ)
  
    val sigSInt = Seq(Seq(0), Seq()) // Succ(SInt), Zero
    val sigSIntDts = Seq(Seq(Zero), Seq())
    override val sig = Signature(Seq(sigSInt), Seq(sigSIntDts))
  }

  "Solver" should "return sat on empty instance" in new SIntSig {
    assertSat()
  }

  it should "return sat on zero = zero" in new SIntSig {
    override val eqs = Seq((Zero, Zero))
    assertSat()
  }
  it should "return sat on zero = zero with indirection" in new SIntSig {
    val n = Variable(1)
    override val eqs = Seq((Zero, n), (n, Zero))
    assertSat()
  }

  it should "return unsat as zero cannot be the successor of n" in new SIntSig {
    val n = Variable(1)
    val m = Variable(2)
    override val eqs = Seq( (m, Succ(n)), (m, Zero) )
    assertUnsatDueTo[EmptyLabelling]()
  }
  it should "return unsat when s(s(n)) = s(n)" in new SIntSig {
    val n = Variable(1)
    val m = Variable(2)
    override val eqs = Seq( (m, Succ(Succ(n))), (m, Succ(n)) )
    assertUnsatDueTo[Cyclic]()
  }
}
