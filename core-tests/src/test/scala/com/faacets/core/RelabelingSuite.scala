package com.faacets.core

import com.faacets.FaacetsSuite
import com.faacets.core.ref.{BellOperator, POVM}
import com.faacets.laws._
import net.alasc.algebra.PermutationAction
import net.alasc.finite.Grp
import net.alasc.laws.{AnyRefLaws, Grps, PermutationActionLaws}
import org.typelevel.discipline.Laws
import spire.algebra.{Eq, Group}
import spire.laws.{ActionLaws, GroupLaws}

class RelabelingSuite extends FaacetsSuite {

  test("Identity are parsed correctly") {
    assert("".parseUnsafe[Relabeling] === Relabeling.id)
    assert("".parseUnsafe[PartyRelabeling] === PartyRelabeling.id)
  }

  locally {
    import Relabelings._
    import Scenarios.Large._
    checkAll("Relabeling", AnyRefLaws[Relabeling]._eq)
    checkAll("Relabeling", DataLaws[Relabeling].textable)
    checkAll("Relabeling", GroupLaws[Relabeling].group)
    implicit val eqSymbol: Eq[Symbol] = Eq.fromUniversalEquals[Symbol]
    import spire.std.int._
    import spire.std.tuples._
    checkAll("Relabeling", ActionLaws[Relabeling, POVM].groupAction)
    test("Destructuring") {
      forAll((r: Relabeling) => r === (r.outputPart |+| r.inputPart |+| r.partyPart))
      forAll((r: Relabeling) => r === (r.outputInputPart |+| r.partyPart))
      forAll((r: Relabeling) => r === Group[Relabeling].combineAll(r.components.map(_.toRelabeling)))
    }
    test("Compatibility with reference implementation") {
      forAll((x: Relabeling, y: Relabeling) => (x |+| y).toRefRelabeling === (x.toRefRelabeling |+| y.toRefRelabeling))
      forAll((r: Relabeling, p: POVM) => (p <|+| r) === (p <|+| r.toRefRelabeling))
    }
  }

  locally {
    import Grps.arbGrp
    import Relabelings.arbRelabeling
    import Scenarios.Small._
    import net.alasc.perms.default._
    checkAll("Grp[Relabeling]", DataLaws[Grp[Relabeling]].coded)
  }


  def relabelingLaws(rep: Scenario => PermutationAction[Relabeling])(implicit scenario: Scenario): Laws#RuleSet = {
    import Relabelings.arbRelabelingInScenario
    implicit def action = rep(scenario)
    PermutationActionLaws[Relabeling].faithfulPermutationAction
  }

  locally {
    import Scenarios.Small._

    nestedCheckAll[Scenario]("Relabeling.Marginal", Scenario.CHSH)(
      relabelingLaws(_.marginalAction)(_))

    nestedCheckAll[Scenario]("Relabeling.Probability", Scenario.CHSH)(
      relabelingLaws(_.probabilityAction)(_))

  }

  locally {
    import Scenarios.Tiny._

    nestedCheckAll[Scenario]("Relabeling.Strategy", Scenario.CHSH)(
      relabelingLaws(_.strategyAction)(_))

  }

  test("Relabeling action on expressions") {
    val s = Scenario.nmk(3, 3, 3)
    forAll(Exprs.genExpr(s), Relabelings.genRelabeling(s)) { (expr, r) =>
      val expr1 = (expr <|+|? r).get
      val expr2 = BellOperator.toDExpr(s, BellOperator.fromGenExpr(expr) <|+| r).fold(x => false, _ === expr1.toDExpr)
    }
  }

  test("Action on triplets") {
    (POVM(0, 0, 1) <|+| (rel"A0(0,1,2) A(0,2) (A,C)")) shouldBe POVM(2, 2, 2)
  }

}
