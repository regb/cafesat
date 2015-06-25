package cafesat
package api

import Formulas._

import asts.fol.Trees.{Or, And, freshPropositionalVariable}

import scala.language.implicitConversions

/** Contains helper functions for building [[Formulas.Formula]].
  *
  * The basic building blocks of formulas are true/false literals and propositional
  * variables. You get a propositional variable with [[FormulaBuilder.propVar]] and
  * [[FormulaBuilder.bool2formula]] provides an implicit conversion from `Boolean`
  * to [[Formulas.True]]/[[Formulas.False]].
  *
  * Once you have a [[Formulas.Formula]] instance, you can combine it using its method
  * matching regular boolean operators. You can also use n-ary combinators 
  * [[FormulaBuilder.or]] and [[FormulaBuilder.and]].

  * {{{
  * import FormulaBuilder._
  *
  * val a = propVar("a")
  * val b = propVar("b")
  * and(a, (!a || b), (!b || false))
  * }}}
  */
object FormulaBuilder {

  /** Builds a disjunction of formulas.
    *
    * @param fs sequence of formulas to be combined
    * @return a new Formula instance that represents the disjunction of the input formulas
    */
  def or(fs: Formula*): Formula = new Formula(Or(fs.map(_.formula): _*))

  /** Builds a conjunction of formulas.
    *
    * @param fs sequence of formulas to be combined
    * @return a new Formula instance that represents the conjunction of the input formulas
    */
  def and(fs: Formula*): Formula = new Formula(And(fs.map(_.formula): _*))

  /** Returns the formula representation of a `Boolean`.
    *
    * @param b a Boolean literal to wrap into a formula
    * @return [[Formulas.True]] if input is `true` and [[Formulas.False]] if input is `false`
    */
  implicit def bool2formula(b: Boolean) = if(b) True else False
  implicit def boolList2formulaList(bs: List[Boolean]): List[Formula] = bs.map(b => if(b) True else False)

  /** Returns the formula representation of a propositional variable.
    *
    * The propositional variable is always fresh and unique, there is no need to use
    * a different name for different invocations. You need to maintain a pointer to the
    * returned instance if you want to refer to it later in your code.
    *
    * @param prefix a String prefix to the name of that variable
    * @return A fresh and unique propositional variable with a name starting with `prefix`
    */
  def propVar(prefix: String = "P"): PropVar = new PropVar(freshPropositionalVariable(prefix))

}
