package cafesat
package api

import asts.core.Trees.{Formula => CoreFormula, _}
import asts.fol.Trees.{True => CoreTrue, False => CoreFalse, _}
import asts.fol.Manip.{simplify => folSimplify, _}

object Formulas {


  class Formula private[api](
    private[api] val formula: CoreFormula
  ) {

    def &&(that: Formula): Formula = new Formula(And(this.formula, that.formula))
    def ||(that: Formula): Formula = new Formula(Or(this.formula, that.formula))
    def unary_!(): Formula = new Formula(Not(this.formula))

    def iff(that: Formula): Formula = new Formula(Or(
                                                   And(this.formula, that.formula), 
                                                   And(Not(this.formula), Not(that.formula))))
    def xor(that: Formula): Formula = new Formula(Or(
                                                   And(this.formula, Not(that.formula)), 
                                                   And(Not(this.formula), that.formula)))

    override def equals(other: Any): Boolean = other match {
      case (that: Formula) => this.formula == that.formula
      case _ => false
    }

    override def hashCode: Int = this.formula.hashCode
  }


  class PropVar private[api](f: CoreFormula) extends Formula(f)

  object True extends Formula(CoreTrue())
  object False extends Formula(CoreFalse())

}
