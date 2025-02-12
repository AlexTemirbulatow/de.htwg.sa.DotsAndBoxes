package de.htwg.se.dotsandboxes.util

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class PackTSpec extends AnyWordSpec {
  "A PackT" should {
    "apply the map function correctly" in {
      val pack = new PackT[Int](List(1, 2, 3, 4))
      val result = pack.map(x => x * 2)
      result shouldBe List(2, 4, 6, 8)
    }

    "handle an empty list correctly" in {
      val pack = new PackT[Int](List())
      val result = pack.map(x => x * 2)

      result shouldBe List()
    }

    "apply a transformation on strings" in {
      val pack = new PackT[String](List("a", "b", "c"))
      val result = pack.map(_.toUpperCase)
      result shouldBe List("A", "B", "C")
    }
  }
}
