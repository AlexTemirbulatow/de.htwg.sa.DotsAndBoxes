package lib

enum BoardSize(val dimensions: (Int, Int)):
  case Small extends BoardSize((4, 3))
  case Medium extends BoardSize((5, 4))
  case Large extends BoardSize((8, 6))

enum PlayerSize(val size: Int):
  case Two extends PlayerSize(2)
  case Three extends PlayerSize(3)
  case Four extends PlayerSize(4)

enum PlayerType(val number: Int):
  case Human extends PlayerType(1)
  case Computer extends PlayerType(2)

enum ComputerDifficulty(val number: Int):
  case Easy extends ComputerDifficulty(1)
  case Medium extends ComputerDifficulty(2)
  case Hard extends ComputerDifficulty(3)

object GameConfig:
  def boardSizes(size: String): BoardSize = size match
    case "1" => BoardSize.Small
    case "2" => BoardSize.Medium
    case "3" => BoardSize.Large
    case _   => BoardSize.Medium

  def playerSizes(size: String): PlayerSize = size match
    case "2" => PlayerSize.Two
    case "3" => PlayerSize.Three
    case "4" => PlayerSize.Four
    case _   => PlayerSize.Two

  def playerType(number: String): PlayerType = number match
    case "1" => PlayerType.Human
    case "2" => PlayerType.Computer
    case _   => PlayerType.Human 

  def computerDifficulty(number: String): ComputerDifficulty = number match
    case "1" => ComputerDifficulty.Easy
    case "2" => ComputerDifficulty.Medium
    case "3" => ComputerDifficulty.Hard
    case _   => ComputerDifficulty.Medium
