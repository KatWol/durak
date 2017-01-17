package de.htwg.se.util

trait Command {
  def execute
  def undo
}

class CommandManager {
  private var commands = List[Command]()
  private var nextPointer: Int = 0

  def executeCommand(cmd: Command) = {
    cmd.execute
    var newCommands = List[Command]()
    for (i <- 0 to nextPointer - 1) {
      newCommands = newCommands ::: List(commands(i))
    }
    newCommands = newCommands ::: List(cmd)

    commands = newCommands
    nextPointer = nextPointer + 1
  }

  def canUndo = nextPointer > 0

  def undo = {
    if (canUndo) {
      nextPointer = nextPointer - 1
      val cmd = commands(nextPointer)
      cmd.undo
    } else throw new IllegalStateException("Can't undo last action")
  }

  def canRedo = nextPointer < commands.length

  def redo = {
    if (canRedo) {
      val cmd = commands(nextPointer)
      nextPointer = nextPointer + 1
      cmd.execute
    } else throw new IllegalStateException("No action to redo")
  }

  def reset = new CommandManager
}