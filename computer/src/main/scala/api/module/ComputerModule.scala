package api.module

import computerComponent.ComputerInterface
import computerComponent.computerMediumImpl.ComputerMedium

object ComputerModule:
  given ComputerInterface = new ComputerMedium
