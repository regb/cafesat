package regolic.sat

import Solver.Results._
import Solver.Clause

import org.scalatest.FunSuite

class IncrementalSuite extends FunSuite {

  private val a = new Literal(0, true)
  private val na = new Literal(0, false)
  private val b = new Literal(1, true)
  private val nb = new Literal(1, false)

  test("Incremental run sat/unsat with assumption") {
    val s = new Solver(2)

    val clauses = List(Set(na, b))
    clauses.foreach(s.addClause(_))
    val result1 = s.solve()
    assert(result1.isInstanceOf[Satisfiable])

    s.addClause(Set(na, nb))
    val result2 = s.solve(Array(a))
    assert(result2 === Unsatisfiable)
  }

  test("empty solve call") {
    val s = new Solver(0)
    val result = s.solve()
    // vacuously true (sat) should be okay
    assert(result.isInstanceOf[Satisfiable])
  }

  test("large dimacs example") {
    val is = getClass.getResourceAsStream("/uuf100-013.cnf")
    val (satInstance, nbVars) = regolic.parsers.Dimacs.cnf(is)
    val s = new Solver(nbVars)

    var i = 0
    var sResult: Result = Unknown
    for(c <- satInstance) {
      s.addClause(c)
      sResult = s.solve()
      i += 1

      // reference solver (all clauses added immediately)
      val r = new Solver(nbVars)
      satInstance.take(i).foreach(r.addClause(_))
      val rResult = r.solve()
      assert(sResult.getClass === rResult.getClass)
    }

    assert(sResult === Unsatisfiable)
  }

}
