package core
package util

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

    "return the correct hashCode for non-empty PackT" in {
      val pack = new PackT[Int](List(1, 2, 3))
      pack.hashCode() shouldBe List(1, 2, 3).hashCode()
    }

    "return the correct hashCode for an empty PackT" in {
      val pack = new PackT[Int](List())
      pack.hashCode() shouldBe List().hashCode()
    }

    "correctly check equality with another PackT with the same list" in {
      val pack1 = new PackT[Int](List(1, 2, 3))
      val pack2 = new PackT[Int](List(1, 2, 3))
      (pack1 == pack2) shouldBe true
    }

    "correctly check equality with another PackT with a different list" in {
      val pack1 = new PackT[Int](List(1, 2, 3))
      val pack2 = new PackT[Int](List(4, 5, 6))
      (pack1 == pack2) shouldBe false
    }

    "return false when comparing with a non-PackT object" in {
      val pack = new PackT[Int](List(1, 2, 3))
      (pack.equals("test")) shouldBe false
    }
  }
}
