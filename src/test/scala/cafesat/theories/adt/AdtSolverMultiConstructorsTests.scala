package cafesat
package theories.adt

import org.scalatest._

import scala.reflect.ClassTag

class AdtSolverMultiConstructorsTests extends FlatSpec with AdtSolverSpecHelpers {

  import Types._

  trait FiniteAndMultiCtors extends SimpleFiniteSig {
    def C1(t: Term) = Constructor(1,0,List(t))
    def C2(t: Term) = Constructor(1,1,List(t))

    def S1(c1: Term) = Selector(1,0,0,c1)
    def S2(c2: Term) = Selector(1,1,0,c2)
  
    val sigMulti = Seq(Seq(0), Seq(0)) // C1(Fin), C2(Fin)
    val sigMultiDts = Seq(Seq(Fina), Seq(Finb))
    override val sig = Signature(Seq(sigFin, sigMulti), Seq(sigFinDts, sigMultiDts))
  }

  "Solver" should "return sat on empty constraints" in new FiniteAndMultiCtors {
    assertSat()
  }

  it should "return sat on trivial constraints" in new FiniteAndMultiCtors {
    override val eqs = Seq((Variable(1), Variable(1)))
    assertSat()
  }

  it should "return unsat when x is used with different selectors" in new FiniteAndMultiCtors {
    val x = Variable(1)
    val y = Variable(2)
    val z = Variable(3)
    override val eqs = Seq((S1(x), y), (S2(x), z))
    assertUnsat()
  }
}
