package cafesat
package api

import Formulas.{Formula, PropVar}

import asts.fol.Trees._
import asts.fol.Manip._

import sat.{Solver => USolver} //U for Underlying
import sat.Solver.Results._
import sat.Solver.Clause
import sat.ConjunctiveNormalForm
import sat.Literal

trait Solver {

  type Var
  type Lit

  def newVar(): Var

  def mkLit(v: Var, pol: Boolean): Lit

  def addClause(lits: Vector[Lit]): Unit

  def solve(assumptions: Vector[Lit]): Boolean

  def model: Map[Var, Boolean]

}


/** Contains helper functions to query CafeSat solvers on formulas. */
object Solver {

  type Model = Map[PropVar, Boolean]

  /** Checks the satisfiability of a formula.
    *
    * @param formula the formula to check for satisfiability
    * @return `Some(model)` if the formula is satisfiable, and None if unsatisfiable
    */
  def solveForSatisfiability(formula: Formula): Option[Model] = {
    val f = formula.formula
    val simpleF = simplify(f)
    simpleF match {
      case True() => Some(Map()) //TODO: provide random values
      case False() => None
      case _ => {
        val (clauses, nbVars, mapping) = ConjunctiveNormalForm(simplify(f))
        val s = new USolver(nbVars)
        clauses.foreach(s.addClause(_))
        val solveRes = s.solve()
        solveRes match {
          case Satisfiable(model) =>
            Some(mapping.map(p => (new PropVar(p._1), model(p._2))))
          case Unsatisfiable => None
          case Unknown =>
            sys.error("shouldn't be unknown")
        }
      }
    }
  }

}
