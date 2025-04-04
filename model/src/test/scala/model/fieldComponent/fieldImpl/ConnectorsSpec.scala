package model.fieldComponent.fieldImpl

import model.fieldComponent.fieldImpl.{ConnectedColumn, ConnectedRow, Connectors, Dot, Empty, EmptyColumn, EmptyRow}
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
        model.fieldComponent.fieldImpl.Connectors.apply("O") should be(new Dot().toString)
        model.fieldComponent.fieldImpl.Connectors.apply("-") should be(new EmptyRow().toString)
        model.fieldComponent.fieldImpl.Connectors.apply("=") should be(new ConnectedRow().toString)
        model.fieldComponent.fieldImpl.Connectors.apply("¦") should be(new EmptyColumn().toString)
        model.fieldComponent.fieldImpl.Connectors.apply("‖") should be(new ConnectedColumn().toString)
        model.fieldComponent.fieldImpl.Connectors.apply("") should be(new Empty().toString)
        model.fieldComponent.fieldImpl.Connectors.apply("xyz") should be("")
      }
    }
  }
}
