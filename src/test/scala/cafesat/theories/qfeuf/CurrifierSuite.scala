//package regolic.smt.qfeuf
//
//import regolic.asts.fol.Trees._
//import regolic.asts.core.Trees._
//import regolic.asts.core.Manip._
//
//import org.scalatest.FunSuite
//
//class CurrifierSuite extends FunSuite {
//
//  //test if the term contains a function of arity more than 1 which is not Apply
//  private def containsNonApplyFunctions(t: Term): Boolean = t match {
//    case Apply(t1, t2) => containsNonApplyFunctions(t1) || containsNonApplyFunctions(t2)
//    case Variable(_, _) => false
//    case FunctionApplication(_, Nil) => false
//    case _ => true
//  }
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
//  private def substituteApply(t: Term): Term = {
//    mapPostorder(t, f => f, {
//      case Apply(FunctionApplication(FunctionSymbol(name, params, FunctionSort(from, to)), args), arg) => {
//        val newSymbol = FunctionSymbol(name, params ::: List(from), to)
//        FunctionApplication(newSymbol, args ::: List(arg))
//      }
//      case t => t
//    })
//  }
//        
//
//  test("basic currifier remove all functions") {
//    val t0 = a
//    val ct0 = Currifier(t0)
//    assert(!containsNonApplyFunctions(t0))
//    assert(!containsNonApplyFunctions(ct0))
//
//    val t1 = f1(a)
//    val ct1 = Currifier(t1)
//    assert(containsNonApplyFunctions(t1))
//    assert(!containsNonApplyFunctions(ct1))
//
//    val t2 = f2(a, b)
//    val ct2 = Currifier(t2)
//    assert(containsNonApplyFunctions(t2))
//    assert(!containsNonApplyFunctions(ct2))
//
//  }
//
//  test("basic nested currifier remove all functions") {
//    val t3 = f2(f1(b), c)
//    val ct3 = Currifier(t3)
//    assert(containsNonApplyFunctions(t3))
//    assert(!containsNonApplyFunctions(ct3))
//
//    val t4 = f2(f2(a, f1(b)), f2(x, f1(y)))
//    val ct4 = Currifier(t4)
//    assert(containsNonApplyFunctions(t4))
//    assert(!containsNonApplyFunctions(ct4))
//  }
//
//  test("higher arity currifier remove all functions") {
//    val t1 = f3(a, b, c)
//    val ct1 = Currifier(t1)
//    assert(containsNonApplyFunctions(t1))
//    assert(!containsNonApplyFunctions(ct1))
//
//    val t2 = f3(a, f2(a, f1(b)), f1(c))
//    val ct2 = Currifier(t2)
//    assert(containsNonApplyFunctions(t2))
//    assert(!containsNonApplyFunctions(ct2))
//
//    val t3 = f3(f2(x, f2(a, b)), f2(a, f1(b)), f1(f1(c)))
//    val ct3 = Currifier(t3)
//    assert(containsNonApplyFunctions(t3))
//    assert(!containsNonApplyFunctions(ct3))
//  }
//
//  test("currifier correct transformation") {
//    val t1 = a
//    val ct1 = Currifier(t1)
//    assert(ct1 === a)
//
//    val t2 = f1(a)
//    val ct2 = Currifier(t2)
//    assert(substituteApply(ct2) === t2)
//    
//    val t3 = f2(a, b)
//    val ct3 = Currifier(t3)
//    assert(substituteApply(ct3) === t3)
//
//    val t4 = f2(f1(b), c)
//    val ct4 = Currifier(t4)
//    assert(substituteApply(ct4) === t4)
//
//    val t5 = f3(a, b, c)
//    val ct5 = Currifier(t5)
//    assert(substituteApply(ct5) === t5)
//
//    val t6 = f3(f2(x, f2(a, b)), f2(a, f1(b)), f1(f1(c)))
//    val ct6 = Currifier(t6)
//    assert(substituteApply(ct6) === t6)
//  }
//
//}
