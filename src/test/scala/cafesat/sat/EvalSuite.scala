package cafesat.sat

import org.scalatest.FunSuite

class EvalSuite extends FunSuite {

  val l1 = new Literal(0, true)
  val l2 = new Literal(0, false)
  val l3 = new Literal(1, true)
  val l4 = new Literal(1, false)
  val l5 = new Literal(2, true)
  val l6 = new Literal(2, false)

  val c1 = Set(l1, l3, l5)
  val c2 = Set(l2, l4, l6)
  val c3 = Set(l1, l4)
  val c4 = Set(l2, l5)

  test("eval basic") {
    assert(Eval(Set(c1), Array(false, false, false)) === false)
    assert(Eval(Set(c1), Array(true, false, false)) === true)

    assert(Eval(Set(c1, c2), Array(true, true, true)) === false)
    assert(Eval(Set(c1, c2), Array(true, false, true)) === true)
    assert(Eval(Set(c1, c2), Array(true, true, false)) === true)
    assert(Eval(Set(c1, c2), Array(false, false, false)) === false)

    assert(Eval(Set(c1, c3, c4), Array(true, false, true)) === true)
    assert(Eval(Set(c1, c3, c4), Array(true, false, false)) === false)

    assert(Eval(Set(c2, c3, c4), Array(false, false, true)) === true)
    assert(Eval(Set(c2, c3, c4), Array(false, true, true)) === false)
  }

}
