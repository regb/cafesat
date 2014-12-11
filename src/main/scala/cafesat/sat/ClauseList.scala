package cafesat.sat

import Solver._

/*
 * Linked list which provides efficient prepend as well as an iterator
 * that traverse elements efficiently and provide a remove method that
 * can be safely called while using the iterator, with O(1) removal of
 * the current element.
 *
 * Ideal data structure for the adjacency list of watched literals.
 *
 * It uses a pool of object and try to reuse as much as possible, the size
 * parameter declares the size of the pool. We need to collect statistics on the
 * typical size of these adjacency lists to tune the size parameter.
 */
class ClauseList(size: Int) {

  private class ClauseNode() {
    var clause: Clause = null
    var next: ClauseNode = null
  }

  assert(size > 0)

  private var root: ClauseNode = null
  private var freeNodes: Array[ClauseNode] = new Array(size)
  private var freeNodesPointer = size - 1
  allocateNodes()

  def isEmpty = root == null

  def prepend(clause: Clause) {
    var tmp = root
    root = getFreeNode()
    root.clause = clause
    root.next = tmp
  }

  def remove(clause: Clause) {
    var node = root
    var prec: ClauseNode = null
    while(node != null && node.clause != clause) {
      prec = node
      node = node.next
    }
    if(node != null) {
      if(prec != null)
        prec.next = node.next
      if(node == root)
        root = node.next
      freeNode(node)
    }
  }

  def contains(clause: Clause): Boolean = {
    var node = root
    while(node != null && node.clause != clause) {
      node = node.next
    }
    node != null
  }

  override def toString(): String = {
    val it = iterator
    var res = "["
    while(it.hasNext()) {
      val cl = it.next()
      res += (cl + "\n")
    }
    res += "]"
    res
  }


  private def getFreeNode() = {
    if(freeNodesPointer < 0)
      allocateNodes()

    val res = freeNodes(freeNodesPointer)
    freeNodesPointer -= 1
    res
  }

  private def allocateNodes() {
    var i = size-1
    while(i >= 0) {
      freeNodes(i) = new ClauseNode()
      i -= 1
    }
    freeNodesPointer = size-1
  }

  private def freeNode(n: ClauseNode) {
    if(freeNodesPointer < size-1) {
      freeNodesPointer += 1
      n.next = null
      freeNodes(freeNodesPointer) = n
    }
  }

  class Iterator(start: ClauseNode) {

    private val fakeNode = getFreeNode() //special fake node for the start, so that hasNext can simply check for next
    fakeNode.next = start
    private var currentNode: ClauseNode = fakeNode
    private var predNode: ClauseNode = null

    def next(): Clause = {
      assert(hasNext())
      if(currentNode != fakeNode)
        predNode = currentNode
      currentNode = currentNode.next
      currentNode.clause
    }

    def hasNext(): Boolean = currentNode.next != null

    //remove will remove this element from the list without interfering with the iteration. It will not move forward the
    //current pointer element, but you should not call remove before starting the iterator nor at the end of twice on the
    //same element
    def remove() {
      val removedNode = currentNode
      fakeNode.next = removedNode.next
      currentNode = fakeNode
      if(removedNode == root) {
        root = removedNode.next
      } 
      if(predNode != null) {
        predNode.next = removedNode.next
      }
      freeNode(removedNode)
    }

  }

  def iterator: Iterator = new Iterator(root)

}
