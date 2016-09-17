//package regolic.smt.qfeuf
//
//import regolic.asts.fol.Trees._
//import regolic.asts.core.Trees._
//import regolic.asts.core.Manip._
//
//import org.scalatest.FunSuite
//
//class FastCongruenceClosureSuite extends FunSuite {
//
//  import FastCongruenceClosure._
//
//  private val sort = Sort("A", List())
//  private val f1Sym = FunctionSymbol("f1", List(sort), sort)
//  private val f2Sym = FunctionSymbol("f2", List(sort, sort), sort)
//  private val f3Sym = FunctionSymbol("f3", List(sort, sort, sort), sort)
//  private val g1Sym = FunctionSymbol("g1", List(sort), sort)
//  private val g2Sym = FunctionSymbol("g2", List(sort, sort), sort)
//  private val g3Sym = FunctionSymbol("g3", List(sort, sort, sort), sort)
//  private def f1(t: Term): Term = FunctionApplication(f1Sym, List(t))
//  private def f2(t1: Term, t2: Term): Term = FunctionApplication(f2Sym, List(t1, t2))
//  private def f3(t1: Term, t2: Term, t3: Term): Term = FunctionApplication(f3Sym, List(t1, t2, t3))
//  private def g1(t: Term): Term = FunctionApplication(g1Sym, List(t))
//  private def g2(t1: Term, t2: Term): Term = FunctionApplication(g2Sym, List(t1, t2))
//  private def g3(t1: Term, t2: Term, t3: Term): Term = FunctionApplication(g3Sym, List(t1, t2, t3))
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
//  test("basic merge") {
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3)
//    assert(!cc1.areCongruent(Constant(0), Constant(1)))
//    assert(!cc1.areCongruent(Constant(1), Constant(2)))
//    assert(!cc1.areCongruent(Constant(0), Constant(2)))
//    cc1.merge(0, 1)
//    assert(cc1.areCongruent(Constant(0), Constant(1)))
//    cc1.merge(1, 2)
//    assert(cc1.areCongruent(Constant(1), Constant(2)))
//    assert(cc1.areCongruent(Constant(0), Constant(2)))
//    assert(cc1.areCongruent(Constant(2), Constant(0)))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(5)
//    assert(!cc2.areCongruent(Constant(0), Constant(1)))
//    assert(!cc2.areCongruent(Constant(1), Constant(2)))
//    assert(!cc2.areCongruent(Constant(0), Constant(2)))
//    assert(!cc2.areCongruent(Constant(2), Constant(4)))
//    cc2.merge(0, 1)
//    assert(cc2.areCongruent(Constant(0), Constant(1)))
//    cc2.merge(3, 2)
//    assert(!cc2.areCongruent(Constant(1), Constant(2)))
//    assert(!cc2.areCongruent(Constant(0), Constant(2)))
//    assert(!cc2.areCongruent(Constant(2), Constant(4)))
//    assert(cc2.areCongruent(Constant(2), Constant(3)))
//
//    cc2.merge(0, 4)
//    assert(cc2.areCongruent(Constant(0), Constant(4)))
//    assert(cc2.areCongruent(Constant(1), Constant(4)))
//    assert(!cc2.areCongruent(Constant(0), Constant(2)))
//    assert(!cc2.areCongruent(Constant(2), Constant(4)))
//
//    cc2.merge(3, 4)
//    assert(cc2.areCongruent(Constant(0), Constant(4)))
//    assert(cc2.areCongruent(Constant(1), Constant(4)))
//    assert(cc2.areCongruent(Constant(0), Constant(2)))
//    assert(cc2.areCongruent(Constant(2), Constant(4)))
//    assert(cc2.areCongruent(Constant(3), Constant(1)))
//    assert(cc2.areCongruent(Constant(3), Constant(4)))
//  }
//
//  test("merge with apply") {
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(4)
//    cc1.merge(0, 1, 2) //g(a) = b
//    assert(!cc1.areCongruent(Constant(0), Constant(1)))
//    assert(!cc1.areCongruent(Constant(0), Constant(2)))
//    assert(cc1.areCongruent(Apply(Constant(0), Constant(1)), Constant(2))) //assert g(a) = b
//    cc1.merge(2, 3) // b = c
//    assert(cc1.areCongruent(Apply(Constant(0), Constant(1)), Constant(3))) //assert g(a) = c
//    assert(!cc1.areCongruent(Constant(0), Constant(1)))
//    assert(!cc1.areCongruent(Constant(0), Constant(2)))
//    assert(!cc1.areCongruent(Constant(0), Constant(3)))
//    assert(!cc1.areCongruent(Constant(1), Constant(2)))
//    assert(!cc1.areCongruent(Constant(1), Constant(3)))
//    cc1.merge(0, 1, 3) //g(a) = c
//    assert(!cc1.areCongruent(Constant(0), Constant(1)))
//    assert(!cc1.areCongruent(Constant(0), Constant(2)))
//    assert(!cc1.areCongruent(Constant(0), Constant(3)))
//    assert(!cc1.areCongruent(Constant(1), Constant(2)))
//    assert(!cc1.areCongruent(Constant(1), Constant(3)))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(4) //f, a, b, c
//    cc2.merge(0, 1, 2) //f(a) = b
//    assert(!cc2.areCongruent(Constant(2), Constant(3))) // b != c
//    cc2.merge(0, 1, 3) //f(a) = c
//    assert(cc2.areCongruent(Constant(2), Constant(3))) // b = c
//
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(5) //f, a, b, c, d
//    cc3.merge(0, 1, 2) //f(a) = b
//    cc3.merge(0, 2, 3) //f(f(a)) = c
//    cc3.merge(0, 3, 1) //f(f(f(a))) = a
//    assert(cc3.areCongruent(Apply(Constant(0), Apply(Constant(0), Apply(Constant(0), Constant(1)))), Constant(1)))
//    assert(!cc3.areCongruent(Apply(Constant(0), Apply(Constant(0), Constant(1))), Constant(1)))
//
//    val cc4 = new FastCongruenceClosure
//    cc4.initialize(8)
//    cc4.merge(6, 0, 2)
//    cc4.merge(6, 1, 3)
//    cc4.merge(7, 3, 5)
//    cc4.merge(7, 4, 0)
//    cc4.merge(0, 1)
//    cc4.merge(4, 2)
//    assert(cc4.areCongruent(Constant(0), Constant(5)))
//
//    val cc5 = new FastCongruenceClosure
//    cc5.initialize(8)
//    cc5.merge(6, 0, 2)
//    cc5.merge(6, 1, 3)
//    cc5.merge(7, 3, 5)
//    cc5.merge(7, 4, 0)
//    cc5.merge(4, 2)
//    cc5.merge(0, 1)
//    assert(cc5.areCongruent(Constant(0), Constant(5)))
//  }
//
//  test("simple explain") {
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3)
//    cc1.merge(0, 1)
//    val ex1 = cc1.explain(0, 1)
//    assert(ex1.size === 1)
//    assert(ex1.head === Left((0, 1)))
//    cc1.merge(1,2)
//    val ex2 = cc1.explain(1, 2)
//    assert(ex2.size === 1)
//    assert(ex2.head === Left((1, 2)))
//    val ex3 = cc1.explain(0, 2)
//    assert(ex3.size === 2)
//    assert(ex3.contains(Left((1, 2))))
//    assert(ex3.contains(Left((0, 1))))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(3)
//    cc2.merge(1, 0)
//    val ex4 = cc2.explain(0, 1)
//    assert(ex4.size === 1)
//    assert(ex4.head === Left((1, 0)))
//    cc2.merge(1,2)
//    val ex5 = cc2.explain(1, 2)
//    assert(ex5.size === 1)
//    assert(ex5.head === Left((1, 2)))
//    val ex6 = cc2.explain(0, 2)
//    assert(ex6.size === 2)
//    assert(ex6.contains(Left((1, 2))))
//    assert(ex6.contains(Left((1, 0))))
//
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(4)
//    cc3.merge(1, 0)
//    cc3.merge(2, 3)
//    val ex7 = cc3.explain(3, 2)
//    assert(ex7.size === 1)
//    assert(ex7.head === Left((2, 3)))
//    cc3.merge(1, 2)
//    val ex8 = cc3.explain(0, 2)
//    assert(ex8.size === 2)
//    assert(ex8.contains(Left((1, 2))))
//    assert(ex8.contains(Left((1, 0))))
//    val ex9 = cc3.explain(1, 3)
//    assert(ex9.size === 2)
//    assert(ex9.contains(Left((1, 2))))
//    assert(ex9.contains(Left((2, 3))))
//    val ex10 = cc3.explain(0, 3)
//    assert(ex10.size === 3)
//    assert(ex10.contains(Left((1, 0))))
//    assert(ex10.contains(Left((2, 3))))
//    assert(ex10.contains(Left((1, 2))))
//  }
//
//  test("explain with apply") {
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(4)
//    cc1.merge(0, 1, 2) //f(a) = b
//    cc1.merge(0, 1, 3) //f(a) = c
//    val ex1 = cc1.explain(2, 3)
//    assert(ex1.size == 2)
//    assert(ex1.contains(Right((0, 1, 2))))
//    assert(ex1.contains(Right((0, 1, 3))))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(5)
//    cc2.merge(0, 1, 3) //f(a) = c
//    cc2.merge(0, 2, 4) //f(b) = d
//    cc2.merge(1, 2) //a = b
//    val ex2 = cc2.explain(3, 4)
//    assert(ex2.size == 3)
//    assert(ex2.contains(Left((1, 2))))
//    assert(ex2.contains(Right((0, 1, 3))))
//    assert(ex2.contains(Right((0, 2, 4))))
//
//  }
//
//  test("positive setTrue") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 0, true, null)
//    val lit3 = Literal(Left(0, 2), 0, true, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    assert(!cc1.isTrue(lit1))
//    cc1.setTrue(lit1)
//    assert(cc1.isTrue(lit1))
//    assert(!cc1.isTrue(lit2))
//    cc1.setTrue(lit2)
//    assert(cc1.isTrue(lit1))
//    assert(cc1.isTrue(lit2))
//    assert(cc1.isTrue(lit3))
//  }
//
//  test("negative setTrue") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 0, true, null)
//    val lit3 = Literal(Left(0, 2), 0, true, null)
//    val lit4 = Literal(Left(0, 1), 0, false, null)
//    val lit5 = Literal(Left(1, 2), 0, false, null)
//    val lit6 = Literal(Left(0, 2), 0, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    cc1.setTrue(lit1)
//    assert(cc1.isTrue(lit1))
//    assert(!cc1.isTrue(lit2))
//    assert(!cc1.isTrue(lit4))
//    cc1.setTrue(lit2)
//    assert(cc1.isTrue(lit1))
//    assert(cc1.isTrue(lit2))
//    assert(cc1.isTrue(lit3))
//    assert(!cc1.isTrue(lit4))
//    assert(!cc1.isTrue(lit5))
//    assert(!cc1.isTrue(lit6))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(3, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    cc2.setTrue(lit4)
//    assert(cc2.isTrue(lit4))
//    assert(!cc2.isTrue(lit1))
//    cc2.setTrue(lit2)
//    assert(cc2.isTrue(lit2))
//    assert(cc2.isTrue(lit4))
//    assert(cc2.isTrue(lit6))
//    assert(!cc2.isTrue(lit3))
//  }
//
//  test("setTrue InconsistencyException") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 1, true, null)
//    val lit3 = Literal(Left(0, 2), 2, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    cc1.setTrue(lit3)
//    cc1.setTrue(lit1)
//    intercept[InconsistencyException]{cc1.setTrue(lit2)}
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(3, Set(lit1, lit2, lit3))
//    cc2.setTrue(lit1)
//    cc2.setTrue(lit3)
//    intercept[InconsistencyException]{cc2.setTrue(lit2)}
//
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(3, Set(lit1, lit2, lit3))
//    cc3.setTrue(lit1)
//    cc3.setTrue(lit2)
//    intercept[InconsistencyException]{cc3.setTrue(lit3)}
//
//    val lit4 = Literal(Left(2, 3), 3, true, null)
//    val lit5 = Literal(Left(0, 1), 0, false, null)
//
//    val cc4 = new FastCongruenceClosure
//    cc4.initialize(5, Set(lit1, lit2, lit3, lit4, lit5))
//    cc4.merge(4, 2, 0) //f(c) = a
//    cc4.merge(4, 3, 1) //f(d) = b
//    cc4.setTrue(lit4)
//    intercept[InconsistencyException]{cc4.setTrue(lit5)}
//    
//    val cc5 = new FastCongruenceClosure
//    cc5.initialize(5, Set(lit1, lit2, lit3, lit4, lit5))
//    cc5.merge(4, 2, 0) //f(c) = a
//    cc5.merge(4, 3, 1) //f(d) = b
//    cc5.setTrue(lit5)
//    intercept[InconsistencyException]{cc5.setTrue(lit4)}
//
//    val lit7 = Literal(Left(0, 0), 4, false, null)
//    val cc6 = new FastCongruenceClosure
//    cc6.initialize(1, Set(lit7))
//    intercept[InconsistencyException]{cc6.setTrue(lit7)}
//  }
//
//  test("setTrue InconsistencyException with apply") {
//    val lit1 = Literal(Left(3, 4), 0, true, null)
//    val lit2 = Literal(Left(3, 4), 0, false, null)
//    val lit3 = Literal(Left(1, 2), 0, true, null)
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(5, Set(lit1, lit2, lit3))
//    cc1.merge(0, 1, 3)
//    cc1.merge(0, 2, 4)
//    cc1.setTrue(lit2)
//    intercept[InconsistencyException]{cc1.setTrue(lit3)}
//  }
//
//  test("advanced setTrue") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(2, 3), 0, true, null)
//    val lit3 = Literal(Left(0, 3), 0, false, null)
//    val lit4 = Literal(Left(1, 2), 0, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(4, Set(lit1, lit2, lit3, lit4))
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    assert(!cc1.isTrue(lit4))
//    assert(!cc1.isTrue(lit3))
//    cc1.setTrue(lit3)
//    assert(cc1.isTrue(lit4))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(4, Set(lit1, lit2, lit3, lit4))
//    cc2.setTrue(lit3)
//    assert(cc2.isTrue(lit3))
//    assert(!cc2.isTrue(lit1))
//    assert(!cc2.isTrue(lit2))
//    cc2.setTrue(lit1)
//    cc2.setTrue(lit2)
//    assert(cc2.isTrue(lit3))
//    assert(cc2.isTrue(lit4))
//
//    val lit5 = Literal(Left(1, 3), 0, true, null)
//    val lit6 = Literal(Left(0, 2), 0, true, null)
//
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(4, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    cc3.setTrue(lit1)
//    cc3.setTrue(lit3)
//    cc3.setTrue(lit4)
//    intercept[InconsistencyException]{ cc3.setTrue(lit5) }
//    intercept[InconsistencyException]{ cc3.setTrue(lit6) }
//  }
//
//  test("setTrue basic theory propagation") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 1, true, null)
//    val lit3 = Literal(Left(0, 2), 2, true, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    val csq1 = cc1.setTrue(lit1)
//    assert(csq1.isEmpty)
//    val csq2 = cc1.setTrue(lit2)
//    assert(csq2.size === 1)
//    assert(csq2.contains(lit3))
//
//    val lit4 = Literal(Left(2, 3), 3, true, null)
//    val lit5 = Literal(Left(0, 3), 4, true, null)
//    val lit6 = Literal(Left(1, 3), 5, true, null)
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(4, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    val csq3 = cc2.setTrue(lit1)
//    assert(csq3.isEmpty)
//    val csq4 = cc2.setTrue(lit4)
//    assert(csq4.isEmpty)
//    val csq5 = cc2.setTrue(lit2)
//    assert(csq5.size === 3)
//    assert(csq5.contains(lit5))
//    assert(csq5.contains(lit3))
//    assert(csq5.contains(lit6))
//  }
//
//  test("setTrue theory propagation of negative literals") {
//    val lit1 = Literal(Left(0, 1), 0, false, null)
//    val lit2 = Literal(Left(1, 2), 1, true, null)
//    val lit3 = Literal(Left(0, 2), 2, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    val csq1 = cc1.setTrue(lit1)
//    assert(csq1.isEmpty)
//    val csq2 = cc1.setTrue(lit2)
//    assert(csq2.size === 1)
//    assert(csq2.contains(lit3))
//
//    val lit4 = Literal(Left(2, 3), 3, true, null)
//    val lit5 = Literal(Left(1, 3), 4, true, null)
//    val lit6 = Literal(Left(0, 3), 5, false, null)
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(4, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    val csq3 = cc2.setTrue(lit1)
//    assert(csq3.isEmpty)
//    val csq4 = cc2.setTrue(lit4)
//    assert(csq4.isEmpty)
//    val csq5 = cc2.setTrue(lit2)
//    assert(csq5.size === 3)
//    assert(csq5.contains(lit3))
//    assert(csq5.contains(lit5))
//    assert(csq5.contains(lit6))
//
//    val lit7 = Literal(Left(0, 1), 0, true, null)
//    val lit8 = Literal(Left(0, 4), 6, false, null)
//    val lit9 = Literal(Left(1, 4), 7, false, null)
//    val lit10 = Literal(Left(2, 4), 8, false, null)
//    val lit11 = Literal(Left(3, 4), 9, false, null)
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10, lit11))
//    cc3.setTrue(lit7)
//    cc3.setTrue(lit4)
//    cc3.setTrue(lit8)
//    cc3.setTrue(lit9)
//    val csq6 = cc3.setTrue(lit5)
//    assert(csq6.size === 3)
//    assert(csq6.contains(lit2))
//    assert(csq6.contains(lit10))
//    assert(csq6.contains(lit11))
//
//    val cc4 = new FastCongruenceClosure
//    cc4.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10, lit11))
//    cc4.setTrue(lit7) //a = b
//    cc4.setTrue(lit4) //c = d
//    val csq7 = cc4.setTrue(lit8) //a != e
//    assert(csq7.size === 1)
//    assert(csq7.contains(lit9)) //b != e
//    val csq8 = cc4.setTrue(lit5) //b = d
//    assert(csq8.size === 3)
//    assert(csq8.contains(lit2)) //b = c
//    assert(csq8.contains(lit10)) //c != e
//    assert(csq8.contains(lit11)) //d != e
//
//    val cc5 = new FastCongruenceClosure
//    cc5.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10, lit11))
//    cc5.setTrue(lit7) //a = b
//    cc5.setTrue(lit4) //c = d
//    val csq9 = cc5.setTrue(lit10) //c != e
//    assert(csq9.size === 1)
//    assert(csq9.contains(lit11))
//    val csq10 = cc5.setTrue(lit5)
//    assert(csq10.size === 3)
//    assert(csq10.contains(lit2))
//    assert(csq10.contains(lit8))
//    assert(csq10.contains(lit9))
//  }
//
//  test("negative setTrue theory propagation") {
//    val lit1 = Literal(Left(1, 2), 0, true, null)
//    val lit2 = Literal(Left(0, 1), 1, false, null)
//    val lit3 = Literal(Left(0, 2), 2, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    val csq1 = cc1.setTrue(lit1)
//    assert(csq1.isEmpty)
//    val csq2 = cc1.setTrue(lit2)
//    assert(csq2.size === 1)
//    assert(csq2.contains(lit3))
//  }
//
//  test("setTrue propagation basics redundancy") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 1, true, null)
//    val lit3 = Literal(Left(2, 3), 2, false, null)
//    val lit4 = Literal(Left(2, 4), 3, true, null)
//    val lit5 = Literal(Left(3, 4), 4, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(5, Set(lit1, lit2, lit3, lit4, lit5))
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    cc1.setTrue(lit3)
//    cc1.setTrue(lit5)
//    assert(cc1.setTrue(lit4).isEmpty)
//
//    val lit6 = Literal(Left(2, 3), 2, true, null)
//    val lit7 = Literal(Left(1, 2), 1, false, null)
//    val lit8 = Literal(Left(0, 3), 5, false, null)
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8))
//    cc2.setTrue(lit1) //a = b
//    cc2.setTrue(lit6) //c = d
//    assert(cc2.setTrue(lit7).size === 1) //b != c
//    assert(cc2.setTrue(lit8).isEmpty) //a != d
//  }
//
//  test("setTrue with apply") {
//    val lit1 = Literal(Left(1, 2), 0, true, null)
//    val lit2 = Literal(Left(3, 4), 1, true, null)
//    val lit3 = Literal(Left(1, 3), 2, true, null)
//    val lit4 = Literal(Left(2, 4), 3, true, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(5, Set(lit1, lit2, lit3, lit4))
//    cc1.merge(0, 1, 3) //f(a) = b
//    cc1.merge(0, 2, 4) //f(c) = d
//    val csq1 = cc1.setTrue(lit1)
//    assert(csq1.size === 1)
//    assert(csq1.contains(lit2))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(5, Set(lit1, lit2, lit3, lit4))
//    cc2.merge(0, 1, 3) //f(a) = b
//    cc2.merge(0, 2, 4) //f(c) = d
//    val csq2 = cc2.setTrue(lit2)
//    assert(csq2.size === 0)
//  }
//
//  test("negative setTrue with apply") {
//    val lit1 = Literal(Left(1, 2), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 0, false, null)
//    val lit3 = Literal(Left(3, 4), 0, true, null)
//    val lit4 = Literal(Left(3, 4), 0, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(5, Set(lit1, lit2, lit3, lit4))
//    cc1.merge(0, 1, 3) //f(a) = b
//    cc1.merge(0, 2, 4) //f(c) = d
//    val csq1 = cc1.setTrue(lit2)
//    assert(csq1.size === 0)
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(5, Set(lit1, lit2, lit3, lit4))
//    cc2.merge(0, 1, 3) //f(a) = b
//    cc2.merge(0, 2, 4) //f(c) = d
//    val csq2 = cc2.setTrue(lit4)
//    println(csq2)
//  }
//
//  test("basic explanation") {
//    val lit1 = Literal(Left(1, 2), 0, true, null)
//    val lit2 = Literal(Left(0, 1), 0, true, null)
//    val lit3 = Literal(Left(0, 2), 0, true, null)
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    val expl1 = cc1.explanation(lit3)
//    assert(expl1.size === 2)
//    assert(expl1.contains(lit1))
//    assert(expl1.contains(lit2))
//
//    val lit4 = Literal(Left(2, 3), 0, true, null)
//    val lit5 = Literal(Left(0, 3), 0, true, null)
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(4, Set(lit1, lit2, lit3, lit4, lit5))
//    cc2.setTrue(lit2)
//    cc2.setTrue(lit4)
//    cc2.setTrue(lit1)
//    val expl2 = cc2.explanation(lit5)
//    assert(expl2.size === 3)
//    assert(expl2.contains(lit2))
//    assert(expl2.contains(lit4))
//    assert(expl2.contains(lit1))
//
//    val lit6 = Literal(Left(0, 4), 0, true, null)
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    cc3.setTrue(lit2)
//    cc3.setTrue(lit6) //add irrelevant literal in explanation
//    cc3.setTrue(lit4)
//    cc3.setTrue(lit1)
//    val expl3 = cc3.explanation(lit5)
//    assert(expl3.size === 3)
//    assert(expl3.contains(lit2))
//    assert(expl3.contains(lit4))
//    assert(expl3.contains(lit1))
//    assert(!expl3.contains(lit6)) //explanation should not contains lit6
//  }
//
//  test("basic explanation returns same literal") {
//    val lit1 = Literal(Left(1, 2), 0, true, null)
//    val lit2 = Literal(Left(0, 1), 1, true, null)
//    val lit3 = Literal(Left(0, 2), 2, true, null)
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    val expl1 = cc1.explanation(lit3)
//    assert(expl1.size === 2)
//    assert(expl1.contains(lit1))
//    assert(expl1.contains(lit2))
//  }
//
//
//  test("explanation with apply basic") {
//
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(2, 3), 0, true, null)
//    val lit3 = Literal(Left(1, 2), 0, true, null)
//    val lit4 = Literal(Left(0, 3), 0, true, null)
//    val lit5 = Literal(Right(4, 0, 2), 0, true, null)
//    val lit6 = Literal(Right(4, 1, 3), 0, true, null)
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    cc1.merge(4, 0, 2) //TODO: should be passed via setTrue maybe ?
//    cc1.merge(4, 1, 3)
//    cc1.setTrue(lit1)
//    val expl1 = cc1.explanation(lit2)
//    /* //TODO: should it return the lit5 and 6 ?
//    assert(expl1.size === 3)
//    assert(expl1.contains(lit1))
//    assert(expl1.contains(lit5))
//    assert(expl1.contains(lit6))
//    */
//    assert(expl1.size === 1)
//    assert(expl1.contains(lit1))
//
//    val lit7 = Literal(Left(0,5), 0, true, null)
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(6, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7))
//    cc2.merge(4, 0, 2)
//    cc2.merge(4, 1, 3)
//    cc2.setTrue(lit7)
//    cc2.setTrue(lit1)
//    val expl2 = cc2.explanation(lit2)
//    /*
//    assert(expl2.size === 3)
//    assert(expl2.contains(lit1))
//    assert(expl2.contains(lit5))
//    assert(expl2.contains(lit6))
//    */
//    assert(expl2.size === 1)
//    assert(expl2.contains(lit1))
//
//  }
//
//  test("explanation with apply advanced") {
//    val lit1 = Literal(Left(2, 3), 0, true, null) //c = d
//    val lit2 = Literal(Left(4, 2), 0, true, null) //e = c
//    val lit3 = Literal(Left(4, 1), 0, true, null) //e = b
//    val lit4 = Literal(Left(1, 5), 0, true, null) //b = f
//    val lit5 = Literal(Left(0, 1), 0, true, null) //a = b
//    val lit6 = Literal(Right(6, 5, 3), 0, true, null) //g(f) = d
//    val lit7 = Literal(Right(6, 5, 3), 0, true, null) //g(d) = a
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(7, Set(lit1, lit2, lit3, lit4, lit5))
//    cc1.merge(6, 5, 3) //g(f) = d
//    cc1.merge(6, 3, 0) //g(d) = a
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    cc1.setTrue(lit3)
//    cc1.setTrue(lit4)
//    val expl1 = cc1.explanation(lit5)
//    //assert(expl1.size == 6)
//    assert(expl1.size == 4)
//    assert(expl1.contains(lit1))
//    assert(expl1.contains(lit2))
//    assert(expl1.contains(lit3))
//    assert(expl1.contains(lit4))
//    //assert(expl1.contains(lit6))
//    //assert(expl1.contains(lit7))
//  }
//
//  test("explanation of negative setTrue") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 0, false, null)
//    val lit3 = Literal(Left(0, 2), 0, false, null)
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    val expl1 = cc1.explanation(lit3)
//    assert(expl1.size === 2)
//    assert(expl1.contains(lit1))
//    assert(expl1.contains(lit2))
//
//    val lit4 = Literal(Left(2, 3), 0, true, null)
//    val lit5 = Literal(Left(0, 3), 0, false, null)
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(4, Set(lit1, lit2, lit3, lit4, lit5))
//    cc2.setTrue(lit1)
//    cc2.setTrue(lit4)
//    cc2.setTrue(lit2)
//    val expl2 = cc2.explanation(lit5)
//    assert(expl2.size === 3)
//    assert(expl2.contains(lit1))
//    assert(expl2.contains(lit4))
//    assert(expl2.contains(lit2))
//
//    val lit6 = Literal(Left(0, 4), 0, true, null)
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    cc3.setTrue(lit1)
//    cc3.setTrue(lit6) //add irrelevant literal in explanation
//    cc3.setTrue(lit4)
//    cc3.setTrue(lit2)
//    val expl3 = cc3.explanation(lit5)
//    assert(expl3.size === 3)
//    assert(expl3.contains(lit1))
//    assert(expl3.contains(lit4))
//    assert(expl3.contains(lit2))
//    assert(!expl3.contains(lit6)) //explanation should not contains lit6
//
//    val lit7 = Literal(Left(0, 4), 0, false, null)
//    val lit8 = Literal(Left(1, 4), 0, false, null)
//    val lit9 = Literal(Left(2, 4), 0, false, null)
//    val lit10 = Literal(Left(3, 4), 0, false, null)
//    val lit11 = Literal(Left(0, 3), 0, true, null)
//    val cc4 = new FastCongruenceClosure
//    cc4.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10, lit11))
//    cc4.setTrue(lit1) //a = b
//    cc4.setTrue(lit4) //c = d
//    cc4.setTrue(lit7) //a != e
//    val csq1 = cc4.setTrue(lit11) //a = d
//    assert(csq1.size === 2)
//    assert(csq1.contains(lit9))
//    assert(csq1.contains(lit10))
//    val expl4 = cc4.explanation(lit9)
//    assert(expl4.contains(lit7))
//    val expl5 = cc4.explanation(lit10)
//    assert(expl5.contains(lit7))
//
//    val cc5 = new FastCongruenceClosure
//    cc5.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10, lit11))
//    cc5.setTrue(lit1) //a = b
//    cc5.setTrue(lit4) //c = d
//    cc5.setTrue(lit7) //a != e
//    cc5.setTrue(lit8) //b != e
//    val csq2 = cc5.setTrue(lit11) //a = d
//    assert(csq2.size === 2)
//    assert(csq2.contains(lit9))
//    assert(csq2.contains(lit10))
//    val expl6 = cc5.explanation(lit9)
//    assert(expl4.contains(lit7) || expl4.contains(lit8))
//    val expl7 = cc5.explanation(lit10)
//    assert(expl5.contains(lit7) || expl5.contains(lit8))
//  }
//
//  test("backtrack basic") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 0, true, null)
//    val lit3 = Literal(Left(0, 2), 0, true, null)
//    val lit4 = Literal(Left(0, 1), 0, false, null)
//    val lit5 = Literal(Left(0, 2), 0, false, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3, lit4, lit5))
//    cc1.setTrue(lit1)
//    cc1.backtrack(1)
//    cc1.setTrue(lit4)
//    val csq1 = cc1.setTrue(lit2)
//    assert(csq1.size === 1)
//    assert(csq1.contains(lit5))
//    assert(cc1.isTrue(lit5))
//    assert(cc1.isTrue(lit4))
//    assert(!cc1.isTrue(lit1))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(3, Set(lit1, lit2, lit3, lit4, lit5))
//    cc2.setTrue(lit1)
//    cc2.setTrue(lit2)
//    cc2.backtrack(2)
//    cc2.setTrue(lit4)
//    val csq2 = cc2.setTrue(lit2)
//    assert(csq2.size === 1)
//    assert(csq2.contains(lit5))
//    assert(cc2.isTrue(lit5))
//    assert(!cc2.isTrue(lit1))
//    assert(cc2.isTrue(lit2))
//
//    val lit6 = Literal(Left(2, 3), 0, true, null)
//    val lit7 = Literal(Left(1, 3), 0, false, null)
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(4, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7))
//    cc3.setTrue(lit1)
//    cc3.setTrue(lit6)
//    cc3.setTrue(lit2)
//    cc3.backtrack(1)
//    cc3.setTrue(lit5)
//    cc3.isTrue(lit7)
//
//    val lit8 = Literal(Left(3, 4), 0, true, null)
//    val cc4 = new FastCongruenceClosure
//    cc4.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8))
//    cc4.setTrue(lit1)
//    cc4.setTrue(lit5)
//    intercept[InconsistencyException]{ cc4.setTrue(lit2) }
//    cc4.backtrack(2)
//    cc4.setTrue(lit8)
//    cc4.setTrue(lit2)
//    assert(cc4.isTrue(lit3))
//
//    val cc5 = new FastCongruenceClosure
//    cc5.initialize(2, Set(lit1))
//    assert(!cc5.isTrue(lit1))
//    cc5.setTrue(lit1)
//    assert(cc5.isTrue(lit1))
//    cc5.backtrack(1)
//    assert(!cc5.isTrue(lit1))
//
//
//  }
//
//  test("backtrack with apply") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 2), 0, true, null)
//    val lit3 = Literal(Left(2, 3), 0, true, null)
//    val lit4 = Literal(Left(0, 3), 0, true, null) //a = d
//    val lit5 = Literal(Left(1, 4), 0, true, null)
//    val lit6 = Literal(Left(0, 4), 0, true, null)
//    val lit7 = Literal(Left(0, 2), 0, false, null)
//    val lit8 = Literal(Left(0, 2), 0, true, null)
//    val lit9 = Literal(Left(5, 2), 0, false, null) //f != c
//    val lit10 = Literal(Left(4, 2), 0, true, null)
//    val lit11 = Literal(Left(5, 0), 0, true, null)
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(7, Set(lit1, lit2, lit3, lit4, lit5, lit9))
//    cc1.merge(6, 0, 2)
//    cc1.merge(6, 1, 3)
//    cc1.setTrue(lit1)
//    assert(cc1.isTrue(lit3))
//    cc1.backtrack(1)
//    assert(!cc1.isTrue(lit3))
//    cc1.setTrue(lit1)
//    assert(cc1.isTrue(lit3))
//
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(7, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit9))
//    cc2.merge(6, 0, 2)
//    cc2.merge(6, 1, 3)
//    cc2.setTrue(lit1)
//    assert(cc2.isTrue(lit3))
//    cc2.backtrack(1)
//    assert(!cc2.isTrue(lit3))
//    cc2.setTrue(lit5)
//    assert(!cc2.isTrue(lit3))
//    cc2.setTrue(lit6)
//    assert(cc2.isTrue(lit1))
//    assert(cc2.isTrue(lit3))
//    cc2.backtrack(2)
//    cc2.setTrue(lit6)
//    cc2.setTrue(lit5)
//    assert(cc2.isTrue(lit1))
//    assert(cc2.isTrue(lit3))
//
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(3)
//    cc3.setTrue(lit7)
//    cc3.setTrue(lit1)
//    intercept[InconsistencyException]{ cc3.setTrue(lit2) }
//    cc3.backtrack(2)
//    intercept[InconsistencyException]{ cc3.setTrue(lit8) }
//
//    val cc4 = new FastCongruenceClosure
//    cc4.initialize(8)
//    cc4.merge(6, 0, 2)
//    cc4.merge(6, 1, 3)
//    cc4.merge(7, 3, 5)
//    cc4.merge(7, 4, 0)
//    cc4.setTrue(lit1)
//    assert(cc4.isTrue(lit3))
//    cc4.setTrue(lit10)
//    assert(cc4.isTrue(lit11))
//    cc4.backtrack(2)
//    cc4.setTrue(lit10)
//    assert(!cc4.isTrue(lit11))
//    cc4.setTrue(lit1)
//    assert(cc4.isTrue(lit11))
//
//    val cc5 = new FastCongruenceClosure
//    cc5.initialize(7)
//    cc5.merge(6, 4, 1) //g(e) = b
//    cc5.merge(6, 4, 5) //g(e) = f
//    cc5.merge(6, 5, 1) //g(f) = b
//    cc5.merge(6, 5, 2) //g(f) = c
//    intercept[InconsistencyException]{ cc5.setTrue(lit9) }
//    cc5.backtrack(1)
//    cc5.setTrue(lit4)
//    assert(cc5.isTrue(lit4))
//    assert(!cc5.isTrue(lit9))
//
//    //this testcase attempts to force lookup to take a not being removed by backtracking
//    val lit12 = Literal(Left(4, 0), 0, true, null)
//    val cc6 = new FastCongruenceClosure
//    cc6.initialize(7)
//    cc6.merge(6, 0, 2) //f(a) = c
//    cc6.merge(6, 1, 3) //f(b) = d
//    cc6.setTrue(lit6) //a = e
//    assert(cc6.isTrue(lit6))
//    assert(!cc6.isTrue(lit1))
//    assert(!cc6.isTrue(lit3))
//    cc6.backtrack(1)
//    assert(!cc6.isTrue(lit6))
//    cc6.setTrue(lit5) //b = e
//    assert(cc6.isTrue(lit5))
//    assert(!cc6.isTrue(lit1))
//    assert(!cc6.isTrue(lit3))
//  }
//
//  test("backtracking with explanation") {
//    val lit1 = Literal(Left(1, 2), 0, true, null)
//    val lit2 = Literal(Left(0, 1), 0, true, null)
//    val lit3 = Literal(Left(0, 2), 0, true, null)
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(3, Set(lit1, lit2, lit3))
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    cc1.backtrack(1)
//    cc1.setTrue(lit2)
//    val expl1 = cc1.explanation(lit3)
//    assert(expl1.size === 2)
//    assert(expl1.contains(lit1))
//    assert(expl1.contains(lit2))
//
//    val lit4 = Literal(Left(2, 3), 0, true, null)
//    val lit5 = Literal(Left(0, 3), 0, true, null)
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(4, Set(lit1, lit2, lit3, lit4, lit5))
//    cc2.setTrue(lit2)
//    cc2.setTrue(lit4)
//    cc2.setTrue(lit1)
//    cc2.backtrack(1)
//    cc2.setTrue(lit1)
//    val expl2 = cc2.explanation(lit5)
//    assert(expl2.size === 3)
//    assert(expl2.contains(lit2))
//    assert(expl2.contains(lit4))
//    assert(expl2.contains(lit1))
//
//    val lit6 = Literal(Left(0, 4), 0, true, null)
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(5, Set(lit1, lit2, lit3, lit4, lit5, lit6))
//    cc3.setTrue(lit2)
//    cc3.setTrue(lit6) //add irrelevant literal in explanation
//    cc3.setTrue(lit4)
//    cc3.setTrue(lit1)
//    cc3.backtrack(1)
//    cc3.setTrue(lit1)
//    val expl3 = cc3.explanation(lit5)
//    assert(expl3.size === 3)
//    assert(expl3.contains(lit2))
//    assert(expl3.contains(lit4))
//    assert(expl3.contains(lit1))
//    assert(!expl3.contains(lit6)) //explanation should not contains lit6
//    cc3.backtrack(4)
//    cc3.setTrue(lit2)
//    cc3.setTrue(lit4)
//    cc3.setTrue(lit1)
//    val expl4 = cc3.explanation(lit5)
//    assert(expl4.size === 3)
//    assert(expl4.contains(lit2))
//    assert(expl4.contains(lit4))
//    assert(expl4.contains(lit1))
//    assert(!expl4.contains(lit6)) //explanation should not contains lit6
//
//    val lit7 = Literal(Left(1, 3), 0, true, null)
//    val cc4 = new FastCongruenceClosure
//    cc4.initialize(4, Set(lit1, lit2, lit3, lit4, lit7))
//    cc4.setTrue(lit1)
//    cc4.setTrue(lit2)
//    assert(cc4.isTrue(lit3))
//    cc4.backtrack(1)
//    cc4.setTrue(lit4)
//    assert(cc4.isTrue(lit7))
//    assert(!cc4.isTrue(lit3))
//    val expl5 = cc4.explanation(lit7)
//    assert(expl5.size === 2)
//    assert(expl5.contains(lit1))
//    assert(expl5.contains(lit4))
//  }
//
//  test("Theory propagation not redundant") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 3), 1, true, null)
//    val lit3 = Literal(Left(0, 2), 2, true, null)
//    val lit4 = Literal(Left(2, 3), 3, true, null)
//    val lit5 = Literal(Left(0, 3), 4, true, null)
//    val lit6 = Literal(Left(2, 3), 5, false, null) //c != d
//    val lit7 = Literal(Left(1, 2), 6, true, null) //b == c
//    val lit8 = Literal(Left(0, 3), 4, false, null) //a != d
//    val lit9 = Literal(Left(5, 4), 7, true, null) // f == e
//    val lit10 = Literal(Left(2, 4), 8, true, null) // c == e
//    val lit11 = Literal(Left(3, 4), 9, false, null) // d != e
//    val lit12 = Literal(Left(4, 5), 10, true, null) // e == f
//    val lit13 = Literal(Left(0, 5), 11, true, null) // a == f
//
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(6, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10, lit11, lit12, lit13))
//    cc1.setTrue(lit1) //a == b
//    cc1.setTrue(lit6) //c != d
//    val csq1 = cc1.setTrue(lit7) //b == c
//    assert(csq1.size === 2)
//    assert(csq1.contains(lit3))
//    assert(csq1.contains(lit8))
//    cc1.setTrue(lit11) //e != d
//    cc1.setTrue(lit12) //e == f
//    val csq2 = cc1.setTrue(lit13) //a == f
//    assert(!csq2.contains(lit3))
//    assert(!csq2.contains(lit8))
//  }
//
//  test("Explanation no cycle") {
//    val lit1 = Literal(Left(0, 1), 0, true, null)
//    val lit2 = Literal(Left(1, 3), 1, true, null)
//    val lit3 = Literal(Left(0, 2), 2, true, null)
//    val lit4 = Literal(Left(2, 3), 3, true, null)
//    val lit5 = Literal(Left(0, 3), 4, true, null)
//    val cc1 = new FastCongruenceClosure
//    cc1.initialize(4, Set(lit1, lit2, lit3, lit4, lit5))
//    cc1.setTrue(lit1)
//    cc1.setTrue(lit2)
//    assert(cc1.isTrue(lit5))
//    cc1.setTrue(lit3)
//    cc1.setTrue(lit4)
//    val expl1 = cc1.explanation(lit5)
//    assert(expl1.size === 2)
//    assert(expl1.contains(lit1))
//    assert(expl1.contains(lit2))
//    cc1.backtrack(2)
//    cc1.setTrue(lit3)
//    cc1.setTrue(lit4)
//    val expl2 = cc1.explanation(lit5)
//    assert(expl2.size === 2)
//    assert(expl2.contains(lit1))
//    assert(expl2.contains(lit2))
//
//    val lit6 = Literal(Left(2, 3), 5, false, null) //c != d
//    val lit7 = Literal(Left(1, 2), 6, true, null) //b == c
//    val lit8 = Literal(Left(0, 3), 4, false, null) //a != d
//    val lit9 = Literal(Left(5, 4), 7, true, null) // f == e
//    val lit10 = Literal(Left(2, 4), 8, true, null) // c == e
//    val lit11 = Literal(Left(3, 4), 9, false, null) // d != e
//    val lit12 = Literal(Left(4, 5), 10, true, null) // e == f
//    val lit13 = Literal(Left(0, 5), 11, true, null) // a == f
//    val cc2 = new FastCongruenceClosure
//    cc2.initialize(6, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10))
//    cc2.setTrue(lit1) // a == b
//    cc2.setTrue(lit6) // c != d
//    cc2.setTrue(lit7) // b == c
//    assert(cc2.isTrue(lit8)) // a != d
//    val expl3 = cc2.explanation(lit8)
//    assert(expl3.size === 3)
//    assert(expl3.contains(lit1))
//    assert(expl3.contains(lit6))
//    assert(expl3.contains(lit7))
//    cc2.setTrue(lit9)
//    cc2.setTrue(lit10)
//    val expl4 = cc2.explanation(lit8)
//    assert(expl4.size === 3)
//    assert(expl4.contains(lit1))
//    assert(expl4.contains(lit6))
//    assert(expl4.contains(lit7))
//
//    val cc3 = new FastCongruenceClosure
//    cc3.initialize(6, Set(lit1, lit2, lit3, lit4, lit5, lit6, lit7, lit8, lit9, lit10, lit11, lit12, lit13))
//    cc3.setTrue(lit1) //a == b
//    cc3.setTrue(lit6) //c != d
//    cc3.setTrue(lit7) //b == c
//    cc3.setTrue(lit11) //e != d
//    cc3.setTrue(lit12) //e == f
//    cc3.setTrue(lit13) //a == f
//    val expl5 = cc2.explanation(lit8)
//    assert(expl5.size === 3)
//    assert(expl5.contains(lit1))
//    assert(expl5.contains(lit6))
//    assert(expl5.contains(lit7))
//  }
//
//  //TODO: test redundant setTrue (multiple same, or implied ones), with backtracking and explain
//  //TODO: test with different literals id
//
//}
