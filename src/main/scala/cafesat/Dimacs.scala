package regolic.parsers

import scala.io.Source
import java.io.InputStream

import regolic.asts.core.Trees._
import regolic.asts.fol.Trees._

import regolic.sat.Literal

/*
 * This DIMACS parser might not exactly follow the standard, but since I am not sure where the
 * actual standard is, I figured out the best would be to just be able to support at least the format
 * used in some standard benchmark.
 *
 * First a comment line can occur at any point, and is simply ignored. It still has
 * to be a complete line starting with 'c'.

 * The problem line has to appear before any clause definition, else the input will
 * be rejected. The problem line is:
 * p cnf N M
 * where N and M are > 0 integer and N is the number of variables and M is the number of clauses

 * The clauses can span several lines and different clauses can be on the same line. Space and new lines
 * are basically ignored. The only separator that matters for clause is '0', which indicate the end of a clause.
 * If the last clause is not terminated by '0', then it will simply be ignored.

 * At the end, we verified that the number of clauses indead match the declared, and reject any output
 * that does not have enough clause. Actually, we don't do that yet, but we might in the future.
 */

object Dimacs {

  def cnf(input: InputStream): (List[Set[Literal]], Int) = {

    var clauses: List[Set[Literal]] = Nil
    var nbClauses: Option[Int] = None
    var currentClause: List[Int] = Nil
    var nbVariables = 0

    for(line <- Source.fromInputStream(input).getLines()) {
      val length = line.size
      if(length > 0 && line(0) != 'c' && line(0) != '%') {
        if(line.startsWith("p cnf")) {

          if(nbClauses != None)
            throw new FileFormatException("A line starting with 'p cnf' is defined twice")

          val rest = line.substring("p cnf".length, length).split(' ').filterNot(_ == "")
          try {
            val restInts = rest.map(_.toInt)
            if(restInts.size != 2)
              throw FileFormatException("")
            nbVariables = restInts(0)
            nbClauses = Some(restInts(1))
            assert(nbClauses.get > 0 && nbVariables > 0)
          } catch {
            case (_: NumberFormatException) => throw FileFormatException("")
          }

        } else { //should be a clause
          if(nbClauses == None)
            throw new FileFormatException("A line starting with 'p cnf' should occur before any clauses")

          try {
            val numbers = line.split(' ').filterNot(_ == "").map(_.toInt)

            if(!numbers.isEmpty)
              numbers.map(i => {
                if(i == 0 && currentClause != Nil) {
                  clauses ::= (currentClause.map(i => if(i > 0) new Literal(i-1, true) else new Literal(-i-1, false))).toSet
                  currentClause = Nil
                } else
                  currentClause ::= i
              })//.asInstanceOf[List[Set[Literal]]]
          } catch {
            case (_: NumberFormatException) => throw FileFormatException("")
          }
        }
      } //else simply ignore the line, don't need to reject the input file for that
    }

    (clauses, nbVariables)
  }


  
  def apply(input: InputStream): List[Formula] = {

    var formulas: List[Formula] = List()
    var vars: Array[PredicateApplication] = null
    var nbClauses: Option[Int] = None

    var currentClause: List[Int] = Nil

    for(line <- Source.fromInputStream(input).getLines()) {
      val length = line.size
      if(length > 0 && line(0) != 'c' && line(0) != '%') {
        if(line.startsWith("p cnf")) {

          if(vars != null || nbClauses != None)
            throw new FileFormatException("A line starting with 'p cnf' is defined twice")

          val rest = line.substring("p cnf".length, length).split(' ').filterNot(_ == "")
          try {
            val restInts = rest.map(_.toInt)
            if(restInts.size != 2)
              throw FileFormatException("")
            val nbVariables = restInts(0)
            nbClauses = Some(restInts(1))
            assert(nbClauses.get > 0 && nbVariables > 0)
            vars = new Array(nbVariables)
            for(i <- 0 until nbVariables)
              vars(i) = PropositionalVariable("x" + i)
          } catch {
            case (_: NumberFormatException) => throw FileFormatException("")
          }

        } else { //should be a clause
          if(vars == null || nbClauses == None)
            throw new FileFormatException("A line starting with 'p cnf' should occur before any clauses")

          try {
            val numbers = line.split(' ').filterNot(_ == "").map(_.toInt)

            if(!numbers.isEmpty)
              numbers.map(i => {
                if(i == 0 && currentClause != Nil) {
                  formulas ::= Or(currentClause.reverse.map(i => if(i > 0) vars(i-1) else Not(vars(-i-1))).toList)
                  currentClause = Nil
                } else
                  currentClause ::= i
              })

            //val varNumbers = numbers.init
            //if(!varNumbers.isEmpty)
            //  formulas ::= Or(varNumbers.map(i => if(i > 0) vars(i-1) else Not(vars(-i-1))).toList)

          } catch {
            case (_: NumberFormatException) => throw FileFormatException("")
          }
          

        }
      } //else simply ignore the line, don't need to reject the input file for that
    }

    formulas
  }

}
