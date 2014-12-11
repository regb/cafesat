package cafesat.api

import Formulas.{Formula, PropVar}

import regolic.asts.fol.Trees._
import regolic.asts.fol.Manip._

import regolic.sat.{Solver => USolver} //U for Underlying
import regolic.sat.Solver.Results._
import regolic.sat.Solver.Clause
import regolic.sat.ConjunctiveNormalForm
import regolic.sat.Literal

trait Solver {

  type Var
  type Lit

  def newVar(): Var

  def mkLit(v: Var, pol: Boolean): Lit

  def addClause(lits: Vector[Lit]): Unit

  def solve(assumptions: Vector[Lit]): Boolean

  def model: Map[Var, Boolean]

}


object Solver {

  type Model = Map[PropVar, Boolean]

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
