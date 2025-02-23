package de.htwg.se.dotsandboxes
package aview

import Default.given
import controller.controllerComponent.ControllerInterface
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Player
import de.htwg.se.dotsandboxes.util.BoardSize
import de.htwg.se.dotsandboxes.util.GameConfig
import de.htwg.se.dotsandboxes.util.PlayerType
import de.htwg.se.dotsandboxes.util.PlayerSize
import java.awt.{Color, Font, RenderingHints}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.border.LineBorder
import javax.swing.{ImageIcon, UIManager}
import scala.annotation.transparentTrait
import scala.swing._
import scala.swing.event._
import util.Move
import util.{Event, Observer}
import de.htwg.se.dotsandboxes.util.ComputerDifficulty
import java.awt.Cursor
import javax.swing.Timer
import java.awt.event.ActionListener
import java.awt.GradientPaint
import javax.swing.border.EmptyBorder
import javax.swing.text.AbstractDocument.Content
import de.htwg.se.dotsandboxes.util.GameConfig.playerSizes
import de.htwg.se.dotsandboxes.util.GameConfig.playerType
import de.htwg.se.dotsandboxes.util.GameConfig.computerDifficulty
import javax.swing.BorderFactory
import java.awt.Graphics
import java.util.concurrent.Flow

enum ThemeType:
  case Light
  case Dark

class GUI(using controller: ControllerInterface) extends Frame with Observer:
  controller.add(this)

  val panelSize: Dimension = new Dimension(800, 800)

  val themeColors: Map[ThemeType, (Color, Color, Color)] = Map(
    ThemeType.Light -> (Color(245, 245, 245), Color(220, 220, 220), Color(60, 60, 60)),
    ThemeType.Dark  -> (Color(70, 70, 70), Color(100, 100, 100), Color(210, 210, 210))
  )

  var isDarkTheme: Boolean = true
  var currentTheme: (Color, Color, Color) = themeColors.get(if isDarkTheme then ThemeType.Dark else ThemeType.Light).get

  var inMainMenu: Boolean = false

  val logo = ImageIO.read(File("src/resources/0_Logo.png"))
  val logoImg = ImageIcon("src/resources/0_Logo.png")
  val menu = ImageIcon("src/resources/0_Menu.png")
  val mainMenu = ImageIcon("src/resources/0_MainMenu.png")
  val dot = ImageIcon("src/resources/1_Dot.png")
  val restart = ImageIcon("src/resources/0_Restart.png")
  val undo = ImageIcon("src/resources/0_Undo.png")
  val redo = ImageIcon("src/resources/0_Redo.png")
  val night = ImageIcon("src/resources/0_Night.png")
  val sun = ImageIcon("src/resources/0_Sun.png")
  val next = ImageIcon("src/resources/0_Next.png")
  val previous = ImageIcon("src/resources/0_previous.png")
  val takenBar = ImageIcon("src/resources/1_BarTaken.png")
  val untakenBar = ImageIcon("src/resources/1_BarUntaken.png")
  val takenCol = ImageIcon("src/resources/1_ColTaken.png")
  val untakenCol = ImageIcon("src/resources/1_ColUntaken.png")
  val takenNone = ImageIcon("src/resources/2_1TakenEmpty.png")
  val takenBlue = ImageIcon("src/resources/2_TakenBlue.png")
  val takenRed = ImageIcon("src/resources/2_TakenRed.png")
  val takenGreen = ImageIcon("src/resources/2_TakenGreen.png")
  val takenYellow = ImageIcon("src/resources/2_TakenYellow.png")
  val playerBlue = ImageIcon("src/resources/3_PlayerBlue.png")
  val playerBlueComputer = ImageIcon("src/resources/3_PlayerBlueComputer.png")
  val playerRed = ImageIcon("src/resources/3_PlayerRed.png")
  val playerRedComputer = ImageIcon("src/resources/3_PlayerRedComputer.png")
  val playerGreen = ImageIcon("src/resources/3_PlayerGreen.png")
  val playerGreenComputer = ImageIcon("src/resources/3_PlayerGreenComputer.png")
  val playerYellow = ImageIcon("src/resources/3_PlayerYellow.png")
  val playerYellowComputer = ImageIcon("src/resources/3_PlayerYellowComputer.png")
  val statsBlue = ImageIcon("src/resources/4_StatsBlue.png")
  val statsBlueComputer = ImageIcon("src/resources/4_StatsBlueComputer.png")
  val statsRed = ImageIcon("src/resources/4_StatsRed.png")
  val statsRedComputer = ImageIcon("src/resources/4_StatsRedComputer.png")
  val statsGreen = ImageIcon("src/resources/4_StatsGreen.png")
  val statsGreenComputer = ImageIcon("src/resources/4_StatsGreenComputer.png")
  val statsYellow = ImageIcon("src/resources/4_StatsYellow.png")
  val statsYellowComputer = ImageIcon("src/resources/4_StatsYellowComputer.png")

  title = "Welcome to Dots And Boxes GUI !"
  iconImage = logo
  resizable = false
  menuBar = new MenuBar {
    contents += new Button(Action("") { if inMainMenu then update(Event.Move) else switchContent(setupMainMenu) }) {
      icon = mainMenu
      contentAreaFilled = false
      borderPainted = false
      focusPainted = false
      opaque = false
      cursor = new Cursor(Cursor.HAND_CURSOR)
      margin = new Insets(0, 0, 0, 0)
      tooltip = "Main Menu"

      reactions += {
        case ButtonClicked(_) =>
          opaque = true
          background = if isDarkTheme then new Color(100, 100, 100, 150) else new Color(220, 220, 220, 150)
          tooltip = "Main Menu"
          new Timer(100, _ => {
            opaque = false
            background = new Color(0, 0, 0, 0)
          }) {
            setRepeats(false)
          }.start()
      }
    }

    contents += space(15)

    contents += new Button(Action("") { if !inMainMenu then controller.publish(controller.restart) }) {
      icon = restart
      contentAreaFilled = false
      borderPainted = false
      focusPainted = false
      opaque = false
      cursor = new Cursor(Cursor.HAND_CURSOR)
      margin = new Insets(0, 0, 0, 0)
      tooltip = "Restart Game"

      reactions += {
        case ButtonClicked(_) =>
          opaque = true
          background = if isDarkTheme then new Color(100, 100, 100, 150) else new Color(220, 220, 220, 150)
          new Timer(100, _ => {
            opaque = false
            background = new Color(0, 0, 0, 0)
          }) {
            setRepeats(false)
          }.start()
      }
    }

    contents += space(6)

    contents += new Button(Action("") { if !inMainMenu then controller.publish(controller.undo) }) {
      icon = undo
      contentAreaFilled = false
      borderPainted = false
      focusPainted = false
      opaque = false
      cursor = new Cursor(Cursor.HAND_CURSOR)
      margin = new Insets(0, 0, 0, 0)
      tooltip = "Undo"

      reactions += {
        case ButtonClicked(_) =>
          opaque = true
          background = if isDarkTheme then new Color(100, 100, 100, 150) else new Color(220, 220, 220, 150)
          new Timer(100, _ => {
            opaque = false
            background = new Color(0, 0, 0, 0)
          }) {
            setRepeats(false)
          }.start()
      }
    }

    contents += space(5)

    contents += new Button(Action("") { if !inMainMenu then controller.publish(controller.redo) }) {
      icon = redo
      contentAreaFilled = false
      borderPainted = false
      focusPainted = false
      opaque = false
      cursor = new Cursor(Cursor.HAND_CURSOR)
      margin = new Insets(0, 0, 0, 0)
      tooltip = "Redo"

      reactions += {
        case ButtonClicked(_) =>
          opaque = true
          background = if isDarkTheme then new Color(100, 100, 100, 150) else new Color(220, 220, 220, 150)
          new Timer(100, _ => {
            opaque = false
            background = new Color(0, 0, 0, 0)
          }) {
            setRepeats(false)
          }.start()
      }
    }

    contents += space(163)

    contents += new Button(Action("") { switchTheme(!isDarkTheme) }) {
      icon = if isDarkTheme then sun else night
      contentAreaFilled = false
      borderPainted = false
      focusPainted = false
      opaque = false
      cursor = new Cursor(Cursor.HAND_CURSOR)
      margin = new Insets(0, 0, 0, 0)
      tooltip = if isDarkTheme then "Light Mode" else "Dark Mode"

      reactions += {
        case ButtonClicked(_) =>
          icon = if isDarkTheme then sun else night
          tooltip = if isDarkTheme then "Light Mode" else "Dark Mode"
      }
    }

    contents += space(5)

    contents += new Menu("") {
      icon = menu
      borderPainted = false
      contentAreaFilled = false
      margin = new Insets(0, 0, 0, 0)
      tooltip = "Settings"
      cursor = new Cursor(Cursor.HAND_CURSOR)
      contents += MenuItem(Action("Exit") { update(Event.Abort) })
      contents += MenuItem(Action("Save") { if !inMainMenu then controller.save })
      contents += MenuItem(Action("Load") { if !inMainMenu then controller.load })
    }

    override def paintComponent(g: Graphics2D) =
      renderHints(g)
      super.paintComponent(g)
  }

  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  menuBar.border = Swing.EmptyBorder(5, 10, 0, 0)
  menuBar.background = currentTheme._1
  update(Event.Move)
  pack()
  centerOnScreen()
  open()


  def setupMainMenu: BoxPanel = {
    background = currentTheme._1
    preferredSize = panelSize
    inMainMenu = true

    def createSelectionPanel[T](
      labelText: String,
      options: Seq[T],
      display: T => String,
      onSelect: T => Unit,
      displayIndex: Int = 0
    ): BoxPanel = {
      var currentIndex = displayIndex
      val label = new Label(display(options(currentIndex))) {
        foreground = new Color(192, 179, 72)
        font = new Font("Comic Sans MS", Font.BOLD, 18)
        horizontalAlignment = Alignment.Center
        preferredSize = new Dimension(100, preferredSize.height)
      }

      def updateLabel(delta: Int): Unit = {
        currentIndex = (currentIndex + delta + options.length) % options.length
        val selectedValue = options(currentIndex)
        label.text = display(selectedValue)
        onSelect(selectedValue)
      }

      new BoxPanel(Orientation.Vertical) {
        background = currentTheme._1
        border = Swing.EmptyBorder(0, 0, 0, 0)

        contents += new FlowPanel {
          border = Swing.EmptyBorder(0, 0, 0, 0)
          background = new Color(0, 0, 0, 0)
          contents += new Label(labelText) {
            foreground = currentTheme._3
            font = new Font("Comic Sans MS", Font.BOLD, 24)
            horizontalAlignment = Alignment.Center
            border = Swing.EmptyBorder(0, 0, 0, 0)
          }
        }

        contents += new FlowPanel {
          background = currentTheme._1
          border = Swing.EmptyBorder(0, 0, 0, 0)
          contents += new Button("") {
            contentAreaFilled = false
            borderPainted = false
            focusPainted = false
            opaque = false
            cursor = new Cursor(Cursor.HAND_CURSOR)
            margin = new Insets(0, 0, 0, 0)
            icon = new ImageIcon(previous.getImage.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH))
            reactions += { case ButtonClicked(_) => updateLabel(-1) }
          }

          contents += label

          contents += new Button("") {
            contentAreaFilled = false
            borderPainted = false
            focusPainted = false
            opaque = false
            cursor = new Cursor(Cursor.HAND_CURSOR)
            margin = new Insets(0, 0, 0, 0)
            icon = new ImageIcon(next.getImage.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH))
            reactions += { case ButtonClicked(_) => updateLabel(1) }
          }
        }
      }
    }

    val headline = new Label("MAIN MENU") {
      foreground = currentTheme._3
      font = new Font("Comic Sans MS", Font.BOLD, 35)
      horizontalAlignment = Alignment.Center
      border = Swing.EmptyBorder(0, 0, 0, 0)
    }

    var selectedBoardSize: BoardSize = controller.boardSize
    var selectedPlayerSize: PlayerSize = controller.playerSize
    var selectedPlayerType: PlayerType = controller.playerType
    var selectedDifficulty: ComputerDifficulty = computerDifficulty(controller.computerImpl)

    val boardSelection = createSelectionPanel(
      "Board Size",
      BoardSize.values.toSeq,
      b => s"${b.dimensions._1} x ${b.dimensions._2}",
      selectedBoardSize = _,
      controller.boardSize.ordinal
    )

    val playerSelection = createSelectionPanel(
      "Choose Players",
      PlayerSize.values.toSeq,
      p => s"${p.size} Players",
      selectedPlayerSize = _,
      playerSizes(controller.playerList.size.toString()).ordinal
    )

    val playerTypeSelection = createSelectionPanel(
      "Play against",
      PlayerType.values.toSeq,
      h => if (h == PlayerType.Human) "Humans" else "Computers",
      selectedPlayerType = _,
      controller.playerType.ordinal
    )

    val difficultySelection = createSelectionPanel(
      "Computer Difficulty",
      ComputerDifficulty.values.toSeq,
      d => d.toString,
      selectedDifficulty = _,
      computerDifficulty(controller.computerImpl).ordinal
    )

    val returnButton = new Button("Return") {
      font = new Font("Comic Sans MS", Font.BOLD, 18)
      background = new Color(96, 149, 106)
      foreground = Color.WHITE
      focusable = false
      focusPainted = false
      borderPainted = false
      border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
      cursor = new Cursor(Cursor.HAND_CURSOR)
      listenTo(mouse.moves)
      reactions += {
        case MouseEntered(_)  => background = new Color(96, 139, 106)
        case MouseExited(_)   => background = new Color(96, 149, 106)
        case ButtonClicked(_) => update(Event.Move)
      }
    }

    val startButton = new Button("New Game") {
      font = new Font("Comic Sans MS", Font.BOLD, 18)
      background = new Color(63, 144, 163)
      foreground = Color.WHITE
      focusable = false
      focusPainted = false
      borderPainted = false
      border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
      cursor = new Cursor(Cursor.HAND_CURSOR)
      listenTo(mouse.moves)
      reactions += {
        case MouseEntered(_)  => background = new Color(63, 130, 160)
        case MouseExited(_)   => background = new Color(63, 144, 163)
        case ButtonClicked(_) =>
          controller.initGame(
            selectedBoardSize,
            selectedPlayerSize,
            selectedPlayerType,
            GameConfig.computerImpl(selectedDifficulty)
          )
      }
    }

    new BoxPanel(Orientation.Vertical) {
      background = currentTheme._1
      contents += Swing.VGlue
      contents += new FlowPanel {
        background = new Color(0, 0, 0, 0)
        contents += headline
        border = Swing.EmptyBorder(0, 0, 0, 0)
      }
      contents += Swing.VGlue
      contents += Swing.VGlue
      contents += boardSelection
      contents += Swing.VGlue
      contents += Swing.VGlue
      contents += playerSelection
      contents += Swing.VGlue
      contents += Swing.VGlue
      contents += playerTypeSelection
      contents += Swing.VGlue
      contents += Swing.VGlue
      contents += difficultySelection
      contents += Swing.VGlue
      contents += Swing.VGlue
      contents += new FlowPanel {
        background = new Color(0, 0, 0, 0)
        hGap = 20
        contents += returnButton
        contents += startButton
      }
      contents += Swing.VGlue
      border = Swing.EmptyBorder(20, 20, 20, 20)
    }
  }

  override def update(event: Event): Unit = event match
    case Event.Abort => sys.exit
    case Event.End   => switchContent(revise(playerResult)); inMainMenu = false
    case Event.Move  => switchContent(revise(if controller.gameEnded then playerResult else playerTurn)); inMainMenu = false

  override def closeOperation: Unit = update(Event.Abort)

  def renderHints(g: Graphics2D): Unit =
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

  def space(spaces: Int): Label =
    new Label(" " * spaces) {
      opaque = false
      background = new Color(0, 0, 0, 0)
    }

  def switchContent(content: Component) =
    contents = content
    preferredSize = panelSize
    pack()
    repaint()

  def switchTheme(setDarkTheme: Boolean) =
    isDarkTheme = setDarkTheme
    currentTheme = themeColors.get(if isDarkTheme then ThemeType.Dark else ThemeType.Light).get
    menuBar.background = currentTheme._1
    menuBar.repaint()
    if inMainMenu then switchContent(setupMainMenu) else update(Event.Move)
    
  def fieldSize(): (Int, Int) =
    (controller.colSize() - 1, controller.rowSize() - 1)

  def gridSize(fieldSize: (Int, Int)): (Int, Int) =
    ((fieldSize._1 + fieldSize._1 + 1), (fieldSize._2 + fieldSize._2 + 1))

  def revise(playerState: FlowPanel): BorderPanel = new BorderPanel {
    preferredSize = panelSize
    background = currentTheme._1
    add(playerState, BorderPanel.Position.North)
    add(CellPanel(fieldSize()._1, fieldSize()._2), BorderPanel.Position.Center)
    add(playerScoreboard, BorderPanel.Position.South)
  }

  def playerTurn: FlowPanel = new FlowPanel {
    background = currentTheme._1
    contents += new Label {
      icon = controller.currentPlayer.toString match
        case "Blue"   => if controller.playerList(0).playerType == PlayerType.Human then playerBlue else playerBlueComputer
        case "Red"    => if controller.playerList(1).playerType == PlayerType.Human then playerRed else playerRedComputer
        case "Green"  => if controller.playerList(2).playerType == PlayerType.Human then playerGreen else playerGreenComputer
        case "Yellow" => if controller.playerList(3).playerType == PlayerType.Human then playerYellow else playerYellowComputer
    }
    val label = Label(s" Turn [points: ${controller.currentPoints}]")
    label.foreground = currentTheme._3
    label.font = Font("Comic Sans MS", 0, 35)
    contents += label

    override def paintComponent(g: Graphics2D) =
      renderHints(g)
      super.paintComponent(g)
  }

  def playerResult: FlowPanel = new FlowPanel {
    background = currentTheme._1
    val fontType = Font("Comic Sans MS", 0, 35)
    controller.winner match
      case "It's a draw!" =>
        contents += new Label {
          val label = Label(controller.winner)
          label.font = fontType
          label.foreground = currentTheme._3
          label.border = LineBorder(currentTheme._1, 10)
          contents += label
        }
      case _ =>
        contents += new Label {
          icon = controller.winner.substring(7) match
            case "Blue wins!"   => if controller.playerList(0).playerType == PlayerType.Human then playerBlue else playerBlueComputer
            case "Red wins!"    => if controller.playerList(1).playerType == PlayerType.Human then playerRed else playerRedComputer
            case "Green wins!"  => if controller.playerList(2).playerType == PlayerType.Human then playerGreen else playerGreenComputer
            case "Yellow wins!" => if controller.playerList(3).playerType == PlayerType.Human then playerYellow else playerYellowComputer
        }
        val label = Label(" wins!")
        label.font = fontType
        label.foreground = currentTheme._3
        contents += label

    override def paintComponent(g: Graphics2D) =
      renderHints(g)
      super.paintComponent(g)
  }

  def playerScoreboard: FlowPanel = new FlowPanel {
    background = currentTheme._2
    contents ++= controller.playerList.map { player =>
      val label = new Label {
        icon = player.playerId match
          case "Blue"   => if player.playerType == PlayerType.Human then statsBlue else statsBlueComputer
          case "Red"    => if player.playerType == PlayerType.Human then statsRed else statsRedComputer
          case "Green"  => if player.playerType == PlayerType.Human then statsGreen else statsGreenComputer
          case "Yellow" => if player.playerType == PlayerType.Human then statsYellow else statsYellowComputer
      }
      val score = new Label(s"[points: ${player.points}]  ")
      score.font = Font("Comic Sans MS", 0, 18)
      score.foreground = currentTheme._3
      new FlowPanel(label, score) { background = currentTheme._2; background = new Color(0, 0, 0, 0) }
    }

    override def paintComponent(g: Graphics2D) =
      renderHints(g)
      super.paintComponent(g)

      val width = size.width
      val shadowHeight = 20
      val gradient = new GradientPaint(0, 0, new Color(0, 0, 0, 255), 0, shadowHeight, new Color(0, 0, 0, 120))
      g.setPaint(gradient)
      g.fillRect(0, 0, width, -shadowHeight)
  }

  class CellPanel(x: Int, y: Int) extends GridPanel(gridSize(fieldSize())._2, gridSize(fieldSize())._1):
    opaque = false
    fieldBuilder

    private def fieldBuilder =
      (0 until y).foreach { row =>
        (0 until x).foreach(col => bar(row, col))
        contents += dotImg
        (0 to x).foreach(col => cell(row, col))
      }
      (0 until x).foreach(col => bar(y, col))
      contents += dotImg

    private def bar(row: Int, col: Int) =
      contents += dotImg
      contents += CellButton(1, row, col, controller.getRowCell(row, col))

    private def cell(row: Int, col: Int) =
      contents += CellButton(2, row, col, controller.getColCell(row, col))
      if col != x then
        contents += new Label {
          icon = controller.getStatusCell(row, col).toString match
            case "-" => takenNone
            case "B" => takenBlue
            case "R" => takenRed
            case "G" => takenGreen
            case "Y" => takenYellow
        }

    private def dotImg = new Label {
      icon = dot
      override def paintComponent(g: Graphics2D) =
        renderHints(g)
        super.paintComponent(g)
    }

  class CellButton(vec: Int, x: Int, y: Int, status: Boolean) extends Button:
    listenTo(mouse.moves, mouse.clicks)
    background = currentTheme._1
    borderPainted = false
    focusPainted = false
    opaque = false
    cursor = new Cursor(Cursor.HAND_CURSOR)
    enabled = !status
    lineBuilder

    private def lineBuilder =
      icon = vec match
        case 1 => if status then takenBar else takenNone
        case 2 => if status then takenCol else takenNone

    override def paintComponent(g: Graphics2D) =
      renderHints(g)
      super.paintComponent(g)

    reactions += {
      case MouseClicked(source) =>
        controller.publish(controller.put, Move(vec, x, y, true))
      case MouseEntered(source) =>
        vec match
          case 1 => if !status then icon = untakenBar
          case 2 => if !status then icon = untakenCol
      case MouseExited(source) => if !status then icon = takenNone
    }
