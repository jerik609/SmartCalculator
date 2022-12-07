package calculator.input

import calculator.core.Operator
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

        fun sanitizeInput(input: String): String {

            // validate brackets
            if (input.contains(')') != input.contains('(')) {
                throw InvalidExpressionException("number of opening and closing brackets do not match")
            }

            val xyz = input.trim().split("\\s".toRegex()).toMutableList()
            for (item in xyz.indices) {
                if (xyz[item].matches("-+".toRegex())) {
                    xyz[item] = if (xyz[item].length % 2 == 0) "+" else "-"
                } else if (xyz[item].matches("\\++".toRegex())) {
                    xyz[item] = "+"
                }
            }
            return xyz.joinToString(" ").replace("[\\+\\-\\*\\/\\^\\(\\)]".toRegex()) { match -> " ${match.value} " }
                .trim()
                .replace("[' ']+".toRegex(), " ")
                .also { debugMePrintln("original input: >$input< \nsanitized input: >$it<") }
        }
    }

}