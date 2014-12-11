package cafesat.api

import regolic.asts.fol.Trees.{Or, And, freshPropositionalVariable}

import cafesat.api.Formulas._

import scala.language.implicitConversions

object FormulaBuilder {

  def or(fs: Formula*): Formula = new Formula(Or(fs.map(_.formula): _*))
  def and(fs: Formula*): Formula = new Formula(And(fs.map(_.formula): _*))

  implicit def bool2formula(b: Boolean) = if(b) True else False
  implicit def boolList2formulaList(bs: List[Boolean]): List[Formula] = bs.map(b => if(b) True else False)

  def propVar(prefix: String = "P"): PropVar = new PropVar(freshPropositionalVariable(prefix))

}
