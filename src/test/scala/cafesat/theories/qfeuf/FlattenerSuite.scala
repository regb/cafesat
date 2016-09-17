//package regolic.smt.qfeuf
//
//import regolic.asts.fol.Trees._
//import regolic.asts.core.Trees._
//import regolic.asts.core.Manip._
//
//import org.scalatest.FunSuite
//
//class FlattenerSuite extends FunSuite {
//
//  //test if the term contains no nested functions (functions of arity 0 are not functions)
//  private def containsNoNestedFunctions(t: Term): Boolean = t match {
//    case Variable(_, _) => true
//    case FunctionApplication(_, args) => args.forall(arg => containsNoFunctions(arg))
//    case _ => false
//  }
//
//  private def containsNoFunctions(t: Term): Boolean = t match {
//    case FunctionApplication(_, x::xs) => false
//    case _ => true
//  }
//
//  private val sort = Sort("A", List())
//  private val f1Sym = FunctionSymbol("f1", List(sort), sort)
//  private val f2Sym = FunctionSymbol("f2", List(sort, sort), sort)
//  private val f3Sym = FunctionSymbol("f3", List(sort, sort, sort), sort)
//  private def f1(t: Term): Term = FunctionApplication(f1Sym, List(t))
//  private def f2(t1: Term, t2: Term): Term = FunctionApplication(f2Sym, List(t1, t2))
//  private def f3(t1: Term, t2: Term, t3: Term): Term = FunctionApplication(f3Sym, List(t1, t2, t3))
//
//  private val x = Variable("v", sort)
//  private val y = Variable("v", sort)
//  private val z = Variable("v", sort)
//
//  private val aSym = FunctionSymbol("a", List(), sort)
//  private val bSym = FunctionSymbol("b", List(), sort)
//  private val cSym = FunctionSymbol("c", List(), sort)
//  private val a = FunctionApplication(aSym, List())
//  private val b = FunctionApplication(bSym, List())
//  private val c = FunctionApplication(cSym, List())
//
//  def substituteEqs(t: Term, eqs: Map[String, Term]): Term =
//    mapPreorder(t, f => f, {
//      case v@Variable(name, _) => eqs.get(name) match {
//        case Some(t) => t
//        case None => v
//      }
//      case t => t
//    })
//
//  test("transform") {
//    val t1 = a
//    val (r1, eqs1) = Flattener.transform(t1)
//    assert(r1 === a)
//    assert(eqs1.isEmpty)
//    assert(substituteEqs(r1, eqs1) === t1)
//
//    val t2 = f1(a)
//    val (r2, eqs2) = Flattener.transform(t2)
//    assert(containsNoFunctions(r2))
//    assert(eqs2.forall(p => containsNoNestedFunctions(p._2)))
//    assert(substituteEqs(r2, eqs2) === t2)
//
//
//    val t3 = f3(f2(a, b), f1(f1(c)), f1(y))
//    val (r3, eqs3) = Flattener.transform(t3)
//    assert(containsNoFunctions(r3))
//    assert(eqs3.forall(p => containsNoNestedFunctions(p._2)))
//    assert(substituteEqs(r3, eqs3) === t3)
//
//  }
//
//  test("transform apply") {
//    val t4 = f2(f2(f1(a), f1(b)), b)
//    val ct4 = Currifier(t4)
//    val (r4, eqs4) = Flattener.transform(ct4)
//    assert(containsNoFunctions(r4))
//    assert(eqs4.forall(p => containsNoNestedFunctions(p._2)))
//    assert(substituteEqs(r4, eqs4) === ct4)
//  }
//
//}
