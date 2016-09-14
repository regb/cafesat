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

  it should "return sat with simple but deep term equality" in new SIntSig {
    val x = Variable(1)
    val y = Variable(2)

    override val eqs = Seq((Succ(Succ(Succ(Succ(x)))), Succ(Succ(Succ(Succ(y))))), (x, y))
    assertSat()
  }
  it should "return unsat with deep congruence of different elements" in new SIntSig {
    val x = Variable(1)
    val y = Variable(2)

    override val eqs = Seq((Succ(Succ(Succ(Succ(x)))), Succ(Succ(Succ(Succ(y))))))
    override val ineqs = Seq((x, y))
    assertUnsat()
  }
  it should "return sat with deep term equality split accross several parts" in new SIntSig {
    val x = Variable(1)
    val y = Variable(2)
    val z = Variable(3)

    override val eqs = Seq(
      (Succ(Succ(Succ(Succ(x)))), Succ(Succ(y))),
      (y, Succ(Succ(z))),
      (x, z)
    )
    assertSat()
  }
  it should "return unsat with deep term equality split accross several parts with base distinct" in new SIntSig {
    val x = Variable(1)
    val y = Variable(2)
    val z = Variable(3)

    override val eqs = Seq(
      (Succ(Succ(Succ(Succ(x)))), Succ(Succ(y))),
      (y, Succ(Succ(z)))
    )
    override val ineqs = Seq((x, z))
    assertUnsat()
  }
}