//package regolic.dpllt.qfeuf
//
//import regolic.asts.core.Trees._
//import regolic.asts.core.Manip._
//import regolic.asts.fol.Trees._
//
///*
// * Flatten nested function calls by introducing auxiliary variables
// * Needs one extra equality compared to "Fast congruence closure with
// * extensions" paper
// */
//object Flattener {
//
//  private def freshVar(sort: Sort) = freshVariable("variable", sort)
//  
//  private val terms = collection.mutable.Map[FunctionApplication, (List[PredicateApplication], Variable)]()
//
//
//  def transform(term: Term): (Term, Map[String, FunctionApplication]) = {
//    var eqs: Map[String, FunctionApplication] = Map()
//
//    val newTerm = mapPostorder(term, (f: Formula) => f, (t: Term) => t match {
//      case app@FunctionApplication(fun, arg::args) => {
//        val fv = freshVariable(fun.name, fun.returnSort)
//        eqs += (fv.name -> app)
//        FunctionApplication(FunctionSymbol(fv.name, Nil, fv.sort), Nil)
//      }
//      case t => t
//    })
//
//    (newTerm, eqs)
//  }
//
//  def transform(formula: Formula): (Formula, Map[String, FunctionApplication]) = {
//    var eqs: Map[String, FunctionApplication] = Map()
//    var names: Map[FunctionApplication, String] = Map()
//
//    val newFormula = mapPostorder(formula, (f: Formula) => f, (t: Term) => t match {
//      case app@FunctionApplication(fun, arg::args) => names.get(app) match {
//        case None =>
//          val fv = freshVariable(fun.name, fun.returnSort)
//          eqs += (fv.name -> app)
//          names += (app -> fv.name)
//          FunctionApplication(FunctionSymbol(fv.name, Nil, fv.sort), Nil)
//        case Some(n) => Variable(n, fun.returnSort)
//      }
//      case t => t
//    })
//
//    (newFormula, eqs)
//  }
//
//  private def extract(f: FunctionApplication, acc: List[PredicateApplication] = Nil): (List[PredicateApplication], Variable) = {
//    if(terms.contains(f)) {
//      terms(f)
//    } else {
//      val retVal = f match {
//        case Apply((t1: Variable), (t2: Variable)) => {
//          val fv = freshVar(f.fSymbol.returnSort)
//          (Equals(f, fv) :: acc, fv)
//        }
//        case Apply((t1: FunctionApplication), (t2: Variable)) => {
//          val (l, lVar) = extract(t1, acc)
//          val fv = freshVar(f.fSymbol.returnSort)
//          (Equals(Apply(lVar, t2), fv) :: l, fv)
//        }
//        case Apply((t1: Variable), (t2: FunctionApplication)) => {
//          val (r, rVar) = extract(t2, acc)
//          val fv = freshVar(f.fSymbol.returnSort)
//          (Equals(Apply(t1, rVar), fv) :: r, fv)
//        }
//        case Apply((t1: FunctionApplication), (t2: FunctionApplication)) => {
//          val (l, lVar) = extract(t1, acc)
//          val (r, rVar) = extract(t2, l)
//          val fv = freshVar(f.fSymbol.returnSort)
//          (Equals(Apply(lVar, rVar), fv) :: r, fv)
//        }
//        case _ => throw new Exception("Unsupported function "+ f)
//      }
//      terms(f) = retVal
//      retVal
//    }
//  }
//
//  private def flatten(eq: PredicateApplication): List[PredicateApplication] = {
//    eq match {
//      case Equals((t1: Variable), (t2: Variable)) =>
//        Equals(t1, t2) :: Nil
//      case Equals((t1: Variable), (t2: FunctionApplication)) => {
//        val (r, rVar) = extract(t2)
//        Equals(t1, rVar) :: r
//      }
//      case Equals((t1: FunctionApplication), (t2: Variable)) => {
//        val (l, lVar) = extract(t1)
//        Equals(lVar, t2) :: l
//      }
//      case Equals((t1: FunctionApplication), (t2: FunctionApplication)) => {
//        val (l, lVar) = extract(t1)
//        val (r, rVar) = extract(t2)
//        Equals(lVar, rVar) :: l ::: r
//      }
//      case _ => throw new Exception("Unsupported terms "+ eq)
//    }
//  }
//
//  def apply(eq: PredicateApplication): List[PredicateApplication] = flatten(eq)
//
//}
