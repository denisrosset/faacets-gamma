package com.faacets
package core

import com.faacets.laws.DataLaws

import net.alasc.laws.AnyRefLaws

import com.faacets.data.Textable.syntax._

class ScenarioSuite extends FaacetsSuite {

  import com.faacets.laws.Scenarios.{scenarioCloner, scenarioInstances}
  import com.faacets.laws.Scenarios.Large._

  checkAll("Scenario", DataLaws[Scenario].textable)

  checkAll("Scenario", AnyRefLaws[Scenario]._eq)

  test("Parse scenario with whitespace") {
    assert("[   (2  2)    (2   2)   ]".parseUnsafe[Scenario] === Scenario.CHSH)
  }

}