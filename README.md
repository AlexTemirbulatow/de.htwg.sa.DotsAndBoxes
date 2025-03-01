# Dots And Boxes


[![Scala CI](https://github.com/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes/actions/workflows/scala.yml/badge.svg?branch=developer)](https://github.com/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes/actions/workflows/scala.yml)
[![Coverage Status](https://coveralls.io/repos/github/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes/badge.svg)](https://coveralls.io/github/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes)
<br>

## Usage
You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

You can run tests with `sbt clean coverage test` and get a coverage report with `sbt coverageReport`

To run the TUI properly, set the terminal to `chcp 65001` and enable `Unicode UTF-8`
<br><br>

<table>
  <tr>
    <th>Rules</th>
    <th>GUI</th>
  </tr>
  <tr>
    <td>
      The game is played by drawing horizontal or vertical <br>
      lines between two dots. When you place the final line <br>
      that completes a box, the box and its contents <br>
      are yours. Players take turns, but whenever a player <br>
      completes a box, they get another turn. Each box <br>
      is worth one point.
      <br><br>
      The game ends when all boxes have been claimed.
      <br><br>
      The player with the most points wins. If two players <br>
      tie for the highest score, the game ends in a draw.
      <br><br>
      The game supports 2-4 players, varying board sizes, <br>
      light and dark mode, and a computer opponent with <br>
      three difficulty levels: Easy, Medium and Hard.
    </td>
    <td>
      <p align="center">
        <img src="https://github.com/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes/blob/developer/.github/resources/5_GUI_Dark.png" width="390" height="380" />
    </td>
  </tr>
  <tr>
    <th>Usage</th>
    <th>TUI</th>
  </tr>
  <tr>
    <td>
        The field consists of three separate 2D vectors: <br>
        <ul>
          <li>The first vector represents all horizontal lines</li>
          <li>The second vector represents all vertical lines</li>
          <li>The third vector represents all cell states</li>
        </ul>
        Accessing a move:
        <ul>
          <li>The horizontal vector can be accessed with (1)</li>
          <li>The vertical vector can be accessed with (2)</li>
          <li>Lines within a vector are accessed with<br>
          X and Y coordinates, starting at (0,0)</li>
        </ul>
        Therefore, a move to occupy a line consists of: <br>
        &lt;Line&gt;&lt;X&gt;&lt;Y&gt; &nbsp;&nbsp;&nbsp; e.g., 132
        <br><br>
        You can also type the following options: <br>
        (q) to quit, (z) to undo, (y) to redo, <br>
        (r) to restart, (h) for help <br>
        (s) to save the current game state <br>
        (l) to load the last saved game state
        <br><br>
        To start a new game with different settings, type <br>
        'NEW: ' followed by this space-seperated options: <br><br>
        &lt;board size&gt;: (1) 4x3, (2) 5x4, (3) 8x6 <br>
        &lt;player size&gt;: (2), (3), (4) <br>
        &lt;player type&gt;: (1) humans, (2) computers <br>
        &lt;computer difficulty&gt;: (1) easy, (2) medium, (3) hard <br>
        e.g., NEW: 2 3 2 1
    </td>
    <td>
      <p align="center">
        <img src="https://github.com/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes/blob/developer/.github/resources/5_TUI.png" width="390" height="370" />
    </td>
  </tr>
</table>
<br>

## LICENSE
This project is licensed under the Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0) License. See the [LICENSE](./LICENSE) file for more details.
