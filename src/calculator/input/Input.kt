package calculator.input

class Input {

    companion object {

        fun translateToCommand(commandStr: String): Command {
            for (command in Command.values()) {
                if (command.equalsStr(commandStr)) return command
            }
            throw UnknownCommandException("Unknown command: $commandStr")
        }

    }

}