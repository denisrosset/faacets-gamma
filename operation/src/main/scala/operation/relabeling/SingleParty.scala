package com.faacets
package operation
package relabeling
/*
import scala.collection.immutable
import scala.collection.mutable

import spire.algebra.{Action, Order}
import spire.syntax.group._
import spire.syntax.action._

import net.alasc.math.Perm

import core._
import perm._

/** Single party marginals
  * 
  * Implements a fast lookup for the minimal lexicographic representative of
  * single party marginals in the NP representation.
  */
object SingleParty {
  type Value = Int
  /** Returns the party relabeling `pr` such that `(seq <|+| pr.inverse)` is minimal. */
  def findMinimalPermutation(party: Party)(seqF: Int => Value): PartyRelabeling = {
    implicit def representation = party.probabilityRepresentation
    implicit def action = party.probabilityAction

    val length = representation.size
    val n = party.inputs.length
    val inputOffset: Array[Int] = party.shape.imprimitive.offsets
    def inputIndices(x: Int): Seq[Int] = inputOffset(x) until inputOffset(x + 1)

    val outputPerm: PartyRelabeling = {
      val inputValInd: Seq[Seq[(Value, Int)]] = (0 until n).map(inputIndices).map {
        seq =>
        val mn = seq.head
        seq.map( k => ((seqF(k), k - mn)) )
      }
      val sortedInputValInd: Seq[Seq[(Value, Int)]] = inputValInd.map(_.sortBy(_._1))
      val sortedInputInd = sortedInputValInd.map(_.map(_._2))
      val outputPerms = sortedInputInd.map( images =>
        Perm.fromImages(images)
      )
      PartyRelabeling(outputPerms, Perm.Algebra.id)
    }

    val inputPerm: PartyRelabeling = {
      val inputVals: Seq[(Seq[Int], Int)] = (0 until n).map(inputIndices).map(input =>
        input.map(k => seqF(k <|+| outputPerm))).zipWithIndex
      val inputGroups = inputVals.groupBy(_._1.size)
      val inputGroupsSort: Iterable[(Seq[Int], Seq[Int])] = inputGroups.values.map {
        case seq: Seq[(Seq[Int], Int)] =>
          val originalOrder = seq.map(_._2)
          import spire.std.int._
          import spire.std.seq.SeqOrder
          import spire.compat.ordering
          val sortedOrder = seq.sortBy(_._1).map(_._2)
          (originalOrder -> sortedOrder)
      }
      val permArray = Array.fill(n)(0)
      for (group <- inputGroupsSort; (origInd, sortInd) <- group._1 zip group._2)
        permArray(origInd) = sortInd
      val inputPerm = Perm.fromImages(permArray)
      PartyRelabeling(Seq.empty, inputPerm)
    }

    inputPerm |+| outputPerm
  }
}
*/