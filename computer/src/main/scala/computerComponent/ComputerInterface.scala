package computerComponent

import de.github.dotsandboxes.lib.Move

trait ComputerInterface:
  def calculateMove(field: String): Option[Move]
