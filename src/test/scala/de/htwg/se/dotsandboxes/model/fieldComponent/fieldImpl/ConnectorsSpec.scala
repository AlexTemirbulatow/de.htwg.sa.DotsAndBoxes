package de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class ConnectorsSpec extends AnyWordSpec {
  "Connectors" when {
    "accessing" should {
      "have the right string representation implicit" in {
        Connectors("O") should be("O")
        Connectors("-") should be("-")
        Connectors("=") should be("=")
        Connectors("¦") should be("¦")
        Connectors("‖") should be("‖")
        Connectors("") should be("")
        Connectors("xyz") should be("")
      }
      "have the right string representation explicit" in {
        Connectors.apply("O") should be(new Dot().toString)
        Connectors.apply("-") should be(new EmptyRow().toString)
        Connectors.apply("=") should be(new ConnectedRow().toString)
        Connectors.apply("¦") should be(new EmptyColumn().toString)
        Connectors.apply("‖") should be(new ConnectedColumn().toString)
        Connectors.apply("") should be(new Empty().toString)
        Connectors.apply("xyz") should be("")
      }
    }
  }
}
