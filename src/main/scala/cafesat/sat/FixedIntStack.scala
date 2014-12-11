package cafesat.sat

/*
 * This implements a stack of int of finite size using an array.
 * This is intended to be used for the trail in the SAT solver.
 */
class FixedIntStack(maxSize: Int) {
  val stack: Array[Int] = new Array(maxSize)
  private var topIndex: Int = -1

  def push(el: Int) {
    topIndex += 1
    stack(topIndex) = el
  }
  def pop(): Int = {
    val res = stack(topIndex)
    topIndex -= 1
    res
  }
  def top: Int = stack(topIndex)
  def isEmpty: Boolean = topIndex == -1
  def contains(el: Int): Boolean = {
    var i = topIndex
    while(i >= 0) {
      if(stack(i) == el)
        return true
      i -= 1
    }
    false
  }

  /*
   * get provides checked access to element in the stack
   */
  def get(i: Int): Int = {
    assert(i >= 0 && i <= topIndex)
    stack(i)
  }

  /* 
   * unchecked access
   */
  def apply(i: Int): Int = stack(i)

  def size: Int = topIndex + 1

}
