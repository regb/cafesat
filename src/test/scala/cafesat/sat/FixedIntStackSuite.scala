package regolic.sat

import org.scalatest.FunSuite

class FixedIntStackSuite extends FunSuite {


  test("basic stack") {
    val s1 = new FixedIntStack(5)
    assert(s1.size === 0)
    assert(s1.isEmpty)
    s1.push(3)
    s1.push(0)
    assert(s1.size === 2)
    assert(s1.top === 0)
    assert(s1.pop === 0)
    s1.push(1)
    s1.push(12)
    assert(s1.pop === 12)
    assert(s1.top === 1)
    assert(s1.size === 2)
    assert(s1.pop === 1)
    assert(s1.pop === 3)
    assert(s1.isEmpty)
  }
}
