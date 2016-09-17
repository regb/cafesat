//package regolic.dpllt.qfeuf
//
//import regolic.asts.core.Trees._
//import regolic.asts.fol.Trees._
//
///*
// * All regular function calls are replaced by calls to "apply", which is a 2-ary
// * function. Functions become arguments of "apply":
// * e.g. g(a, b) => apply(apply(g, a), b)
// */
//object Currifier {
//
//  private def curry(t: Term): Term = {
//    //def makeFuns(terms: List[Term]): Term = {
//    //  terms match {
//    //    case x :: Nil => x
//    //    case x :: xs => Apply(makeFuns(xs), curry(x))
//    //    case _ => throw new Exception("Impossible case when matching terms "+ terms)
//    //  }
//    //}
//
//    t match {
//      case a@Apply(_, _) => a
//      case v@Variable(_, _) => v
//      case c@FunctionApplication(_, Nil) => c
//      case FunctionApplication(funSym@FunctionSymbol(name, paramSorts, returnSort), x::xs) => {
//        val newSort = paramSorts.foldRight(returnSort)((s, acc) => FunctionSort(s, acc))
//        val newSymbol = FunctionSymbol(name, List(), newSort)
//        val newFun = FunctionApplication(newSymbol, List())
//        xs.foldLeft(Apply(newFun, curry(x)): Term)((t, arg) => Apply(t, curry(arg)))
//      }
//    }
//  }
//
//  def apply(t: Term): Term = curry(t)
//
//  def apply(eq: PredicateApplication): PredicateApplication = eq match {
//    case Equals(s, t) => Equals(curry(s), curry(t))
//  }
//}
