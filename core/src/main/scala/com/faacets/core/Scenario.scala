package com.faacets
package core

import spire.algebra.{Eq, VectorSpace}
import spire.math.{Rational, SafeLong}
import spire.syntax.eq._
import spire.syntax.cfor._

/*
import net.alasc.algebra._
import net.alasc.finite.{Grp, Rep}
import net.alasc.util.Tuple2Int
import net.alasc.perms.default._
import util._*/

import fastparse.noApi._

import com.faacets.data.Parsable
import com.faacets.data.Textable.syntax._

import perm._
import consolidate.Merge

/** Description of a Bell scenario.
  * 
  * A `Scenario` object describes a Bell scenario composed of several parties,
  * each having a certain number of measurement settings, each setting having a number
  * of possible outcomes.
  * 
  * Instances of `Scenario` also enable:
  * 
  * - description of the different representations using `Repr` objects,
  * - conversion of `Expr` or `Corr` between representations,
  * - `Term` instances to represent the elements of a `Expr` or `Corr` in any representation,
  * - the construction of its Bell permutation group, some remarkable subgroups 
  and the following actions:
  * - `marginalAction` defines the action of the group on concatenated single party
  *    marginals \\( P(a|x) P(b|y) ... \\),
  * - `probabilityAction` defines the action of the group on probability distributions
  *    \\( P(a b ... | x y ...) \\),
  * - `strategyAction` defines the action of the group on strategy weights.
  * 
  * A `Scenario` can be represented in plain text using the following grammar:
  * - `Scenario := [Party Party ... ]`
  * - `Party := (Input Input ...)`
  * - `Input := number >= 1`
  * 
  * Examples:
  * 
  * - the CHSH scenario is written down [(2 2) (2 2)],
  * - the Sliwa scenario is written down [(2 2) (2 2) (2 2)],
  * - the I2233 scenario is written down [(3 3) (3 3)],
  * - the I3322 scenario is written down [(2 2 2) (2 2 2]].*/
final class Scenario private (val parties: Seq[Party]) {

  override def hashCode = parties.hashCode
  override def toString = this.toText
  override def equals(any: Any) = any match {
    case that: Scenario => this eq that
    case _ => false
  }

  def hasHomogenousParties = (1 until parties.length).forall(parties(_) == parties(0))
  /** The scenario can be represented using a Java identifier,
    * for example to name keys in the repository.*/
  def toIdentifier = parties.map(party => party.inputs.mkString("")).mkString("_")

  val minNumInputs = parties.map(_.inputs.length).min
  val maxNumInputs = parties.map(_.inputs.length).max
  val minNumOutputs = parties.flatMap(_.inputs).min
  val maxNumOutputs = parties.flatMap(_.inputs).max
  val nParties = parties.length


  /** Number of different input tuples, i.e. the number of different measurements overall.
    * Example: Scenario.CHSH.nInputTuples === 4. */
  val nInputTuples = parties.foldLeft(SafeLong(1)) { case (mul, party) => mul * SafeLong(party.nInputs) }


  /*
  val shape = new Shape(parties)
  val shapeLattice = ShapeLattice(parties)

  def primitiveShape(repr: Representation): PrimitiveShape = repr match {
    case NPRepresentation | SPRepresentation => shapeP
    case NCRepresentation => shapeNC
    case NGRepresentation => shapeNG
    case SCRepresentation => shapeSC
    case SGRepresentation => shapeSG
    case TRepresentation => shapeT
    case WRepresentation => shapeW
  }*/

  /*

  def shapeP: PrimitiveShape = shape.primitiveImprimitive

  def ind2subP(ind: Int, aArray: Array[Int], xArray: Array[Int]): Unit = {
    shapeP.ind2sub(ind, aArray) // recycle aArray
    cforRange(0 until nParties) { p =>
      val tupleAX = parties(p).ind2subP(aArray(p))
      aArray(p) = tupleAX._1
      xArray(p) = tupleAX._2
    }
  }

  def ind2subP(ind: Int): (Array[Int], Array[Int]) = {
    val aArray = new Array[Int](nParties)
    val xArray = new Array[Int](nParties)
    ind2subP(ind, aArray, xArray)
    (aArray, xArray)
  }

  def sub2indP(aArray: Array[Int], xArray: Array[Int]): Int = {
    var ind = 0
    cforRange(0 until nParties) { p =>
      val partySub = parties(p).sub2indP(aArray(p), xArray(p))
      ind = ind + shapeP.factors(p) * partySub
    }
    ind
  }

  lazy val shapeNC: PrimitiveShape = PrimitiveShape(parties.map(party => party.inputs.sum - party.inputs.size + 1).toArray)

  def ind2subNC(ind: Int, kArray: Array[Int], xArray: Array[Int]): Unit = {
    shapeNC.ind2sub(ind, kArray) // recycle kArray
    cforRange(0 until nParties) { p =>
      val  tupleKX = parties(p).ind2subNC(kArray(p))
      kArray(p) = tupleKX._1
      xArray(p) = tupleKX._2
    }
  }

  def ind2subNC(ind: Int): (Array[Int], Array[Int]) = {
    val kArray = new Array[Int](nParties)
    val xArray = new Array[Int](nParties)
    ind2subNC(ind, kArray, xArray)
    (kArray, xArray)
  }

  def sub2indNC(kArray: Array[Int], xArray: Array[Int]): Int = {
    var ind = 0
    cforRange(0 until nParties) { p =>
      val partySub = parties(p).sub2indNC(kArray(p), xArray(p))
      ind = ind + shapeNC.factors(p) * partySub
    }
    ind
  }

  def shapeNG: PrimitiveShape = shapeNC

  def ind2subNG(ind: Int, kArray: Array[Int], xArray: Array[Int]): Unit = ind2subNC(ind, kArray, xArray)

  def ind2subNG(ind: Int): (Array[Int], Array[Int]) = ind2subNC(ind)

  def sub2indNG(kArray: Array[Int], xArray: Array[Int]): Int = sub2indNC(kArray, xArray)

  def shapeSC: PrimitiveShape = shape.primitiveImprimitive

  def shapeSG: PrimitiveShape = shape.primitiveImprimitive

  def shapeT: PrimitiveShape = shape.primitivePrimitive

  def shapeW: PrimitiveShape = shape.primitivePrimitive

  def isCSignalingIndex(sub: Array[Int]): Boolean = {
    cforRange(0 until nParties) { p =>
      if (sub(p) > parties(p).shapeP.size - parties(p).nInputs)
        return true
    }
    false
  }

  def isCProperNormalizationIndex(sub: Array[Int]): Boolean = {
    var hasSignaling = false
    var hasNormalization = false
    cforRange(0 until nParties) { p =>
      if (sub(p) == 0) hasSignaling = true
      else if (sub(p) > parties(p).shapeP.size - parties(p).nInputs) hasNormalization = true
    }
    hasSignaling && hasNormalization
  }

  lazy val marginalRep = ImprimitiveImprimitiveRelabelingRep[Rational](shapeLattice)

  lazy val probabilityRep = PrimitiveImprimitiveRelabelingRep[Rational](shapeLattice)

  lazy val strategyRep = PrimitivePrimitiveRelabelingRep[Rational](shapeLattice)

  lazy val marginalAction = marginalRep.permutationAction

  lazy val probabilityAction = probabilityRep.permutationAction

  lazy val strategyAction = strategyRep.permutationAction

  lazy val subgroups = ScenarioSubgroups(this)

  lazy val probabilitySubgroups = ScenarioSubgroups(this, permuteSingleInputOutputParties = false)

  lazy val strategySubgroups = ScenarioSubgroups(this, false, false)

  /** The value `group` represents the symmetry group of the current
    * Bell scenario. This group is actually a wreath product group,
    * composed of a copy of \\( n \\) party symmetry group and the subgroup of \\( S_n \\)
    * permuting parties that are compatible, i.e. with the same output structure. */
  lazy val group: Grp[Relabeling] =
    GrpLexAnsatz.fromGeneratorsAndOrder(subgroups.generators, subgroups.order, marginalRep)

  lazy val probabilityGroup: Grp[Rep.Of[Relabeling, probabilityRep.type]] = Rep.OfGrp(
    GrpLexAnsatz.fromGeneratorsAndOrder(probabilitySubgroups.generators, probabilitySubgroups.order, probabilityRep),
    probabilityRep)

  lazy val strategyGroup: Grp[Rep.Of[Relabeling, strategyRep.type]] = Rep.OfGrp(
    GrpLexAnsatz.fromGeneratorsAndOrder(strategySubgroups.generators, strategySubgroups.order, strategyRep),
    strategyRep)
    */

}

/** Companion object for `Scenario`
  * 
  * As for `Party`, the instances of `Scenario`, when created, are kept in an
  * `UniquenessCache` such that two scenarios with the same structure are represented
  * by the same object in memory.
  */
object Scenario extends UniquenessCacheEq[Seq[Party], Scenario] {
/*  val remarkableSubgroupNames = Seq(
    "liftings", "outputPermsPerParty", "outputPerms",
    "inputPermsPerParty", "inputPerms",
    "outputInputPermsPerParty", "outputInputPerms",
    "partyPerms",
    "rest")*/

  protected def valueFromKey(parties: Seq[Party]): Scenario = new Scenario(parties)
  protected def keyFromValue(scenario: Scenario): Option[Seq[Party]] = Some(scenario.parties)

  // Factory methods
  def nmk(n: Int, m: Int, k: Int) = {
    val party = Party.mk(m, k)
    apply(Seq.fill(n)(party))
  }

  val CHSH = nmk(2, 2, 2)

  implicit val parsable: Parsable[Scenario] = new ScenarioParsable

  implicit val merge: Merge[Scenario] = consolidate.Merge.fromEquals[Scenario]

}

final class ScenarioParsable extends Parsable[Scenario] {

  import com.faacets.data.Parsers._
  import Parsers._
  import White._

  def toText(s: Scenario): String = s.parties.map(_.toText).mkString("[", " ", "]")

  val phrase = scenario ~ End

}
