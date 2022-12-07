package calculator.input

import calculator.debugMePrintln

class Input {

    companion object {

        fun translateToCommand(commandStr: String): Command {
            for (command in Command.values()) {
                if (command.equalsStr(commandStr)) return command
            }
            throw UnknownCommandException("Unknown command: $commandStr")
        }

        fun isVariableAssignment(input: String) = input
            .matches("^\\s*[a-zA-Z]+\\s*=\\s*(.*)\\s*".toRegex())
            .also { debugMePrintln("`$input` is ${if (!it) "NOT" else ""} a variable assignment") }

        fun isValidVariableAssignment(input: String) = input
            .matches("^\\s*[a-zA-Z]+\\s*=\\s*-?(0|[1-9][0-9]*|[a-zA-Z]+)\\s*".toRegex())
            .also { debugMePrintln("`$input` is ${if (!it) "NOT" else ""} a VALID variable assignment") }

        fun isVariableInquiry(input: String) = input
            .matches("\\s*[a-zA-Z]+\\s*".toRegex())
            .also { debugMePrintln("`$input` is ${if (!it) "NOT" else ""} a variable inquiry") }

        fun sanitizeInput(input: String) = input
            .replace("[\\+\\-\\*\\/\\^\\(\\)]".toRegex()) { match -> " ${match.value} " }
            .trim()
            .replace("[' ']+".toRegex(), " ")
            .also { debugMePrintln("original input: >$input< \nsanitized input: >$it<") }
    }

}