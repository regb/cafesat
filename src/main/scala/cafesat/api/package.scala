package cafesat

/** Provides a simple abstraction on top of CafeSat functionalities.
  *
  * This is the expected entry point to use CafeSat as a library to an
  * external project. It provides a simple and high-level abstraction over
  * formulas with an opaque type [[cafesat.api.Formulas.Formula]], along 
  * with a simple language to build formulas and query their satisfiability.
  *
  * [[FormulaBuilder]] provides the main building blocks to build formulas.
  * You typically start a formula with a [[FormulaBuilder.propVar]] to obtain
  * a propositional variable and then you can use boolean operators to build
  * a more complex formula out of it.
  *
  * [[cafesat.api.Solver]] provides functions to query a solver over formulas.
  *
  * Here is a short example where CafeSat is used to check the satisfiability of
  * a simple formula:
  * {{{
  * import FormulaBuilder._
  *
  * val a = propVar("a")
  * val b = propVar("b")
  * val f = a && (!a || b)
  * Solver.solveForSatisfiability(f) match {
  *   case None => ???
  *   case Some(model) => ???
  * }
  * }}}
  */
package object api {

}
