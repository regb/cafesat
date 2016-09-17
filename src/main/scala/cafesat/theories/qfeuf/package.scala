//package regolic
//package dpllt
//
//import regolic.asts.core.Trees._
//import regolic.asts.theories.int.Trees.IntSort
//
//package object qfeuf {
//
//  object FunctionSort {
//    def apply(from: Sort, to: Sort) = Sort("Function", List(from, to))
//    def unapply(sort: Sort): Option[(Sort, Sort)] = sort match {
//      case Sort("Function", List(from, to)) => Some((from, to))
//      case _ => None
//    }
//  }
//
//  object TupleSort {
//    def apply(sorts: List[Sort]) = {
//      val arity = sorts.size
//      Sort("Tuple" + arity, sorts)
//    }
//    def unapply(sort: Sort): Option[List[Sort]] = {
//      try {
//        val name = sort.name
//        if(name.startsWith("Tuple")) {
//          val arity = name.substring("Tuple".size).toInt
//          val subSorts = sort.subSorts
//          if(subSorts.size == arity)
//            Some(subSorts)
//          else None
//        } else None
//      } catch {
//        case (e: NumberFormatException) => None
//      }
//    }
//  }
//
//  object ApplySymbol {
//    def apply(from: Sort, to: Sort): FunctionSymbol = 
//      FunctionSymbol("apply", List(FunctionSort(from, to), from), to)
//    def unapply(s: FunctionSymbol): Option[(Sort, Sort)] = s match {
//      case FunctionSymbol("apply", List(FunctionSort(s1, s2), s3), s4) if s1 == s3 && s2 == s4 => Some((s1, s2))
//      case _ => None
//    }
//  }
//  object Apply {
//    def apply(t1: Term, t2: Term): FunctionApplication = {
//      t1.sort match {
//        case FunctionSort(fromSort, toSort) => {
//          require(fromSort == t2.sort)
//          FunctionApplication(ApplySymbol(fromSort, toSort), List(t1, t2))
//        }
//      }
//    }
//    def unapply(pApply: FunctionApplication): Option[(Term, Term)] = pApply match {
//      case FunctionApplication(ApplySymbol(_, _), List(t1, t2)) => Some((t1, t2))
//      case _ => None
//    }
//  }
//
//}
