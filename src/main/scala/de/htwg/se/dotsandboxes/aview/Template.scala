package de.htwg.se.dotsandboxes
package aview

import controller.controllerComponent.ControllerInterface
import util.Move
import scala.util.Try
import util.{Event, Observer}

trait Template(controller: ControllerInterface) extends Observer:
  controller.add(this)
  def run: Unit =
    println(
        "----------------------------------\n" +
        "| Welcome to Dots And Boxes TUI! |\n" +
        "----------------------------------\n"
    )
    println(
        "--Note\n" +
        "A move consists of:\n\n" +
        "<Line> index: (1) for horizontally (2) for vertically\n" +
        "<X> coordinate: starting at (0)\n" +
        "<Y> coordinate: starting at (0)\n\n" +
        "You can type (q) to quit, (z) to undo (y) to redo,\n" +
        "(s) to save the current game state and (l) to load it."
    )
    update(Event.Move)
    gameLoop
  def gameLoop: Unit
  def analyzeInput(input: String): Option[Move]
  def finalStats: String
  def checkSyntax(vec: Char, x: Char, y: Char): Try[(Int, Int, Int)]
  def syntaxErr: String
