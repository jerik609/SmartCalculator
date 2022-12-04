package calculator.input

import calculator.core.TaskEvaluator
import calculator.core.UnknownVariableException
import calculator.debugMe
import java.lang.NumberFormatException
import java.util.Scanner

/**
 * Reads input and calls the respective branch (should implement handlers at some point)
 */
class Controller(private val scanner: Scanner, private val taskEvaluator: TaskEvaluator) {

    companion object {

        fun isVariableAssignment(input: String) = input
            .matches("^\\s*[a-zA-Z]+\\s*=\\s*(.*)\\s*".toRegex())
            .also { debugMe("`$input` is ${if (!it) "NOT" else ""} a variable assignment") }

        fun isValidVariableAssignment(input: String) = input
            .matches("^\\s*[a-zA-Z]+\\s*=\\s*([1-9][0-9]*|[a-zA-Z]+)\\s*".toRegex())
            .also { debugMe("`$input` is ${if (!it) "NOT" else ""} a VALID variable assignment") }

        fun isVariableInquiry(input: String) = input
            .matches("\\s*[a-zA-Z]+\\s*".toRegex())
            .also { debugMe("`$input` is ${if (!it) "NOT" else ""} a variable inquiry") }
    }

    private var terminate = false

    private fun printHelp() {
        println("The program calculates the sum of numbers")
    }

    private fun performCommand(command: Command) {
        when(command) {
            Command.EXIT -> {
                println("Bye!")
                terminate = true
            }
            Command.HELP -> printHelp()
            else -> println("Unhandled command: $command")
        }
    }

    fun mainLoop() {
        do {
            if (scanner.hasNextLine()) {

                // read input
                val inputStr = scanner.nextLine()

                // empty input
                if (inputStr.isEmpty()) {
                    continue
                }

                val input = inputStr.split(" ").map { it.trim() }

                // handle command
                if (input[0][0] == '/') {
                    debugMe(">>> command handler <<<")
                    try {
                        val command = Input.translateToCommand(inputStr)
                        performCommand(command)
                    } catch (e: UnknownCommandException) {
                        println("Unknown command")
                    }

                // handle variable assignment
                } else if (inputStr.contains("=".toRegex())) {
                    debugMe(">>> variable handler <<<")

                    // valid variable assignment
                    if (isValidVariableAssignment(inputStr)) {
                        with(inputStr.split("=").map { it.trim() }) {

                            // attempt to assign variable to variable
                            if (this[1].matches("[a-zA-Z]+".toRegex())) {
                                if (taskEvaluator.getVariable(this[1]) != null) {
                                    debugMe("assigning variable to a variable, value ${taskEvaluator.getVariable(this[1])}")
                                    taskEvaluator.registerVariable(this[0] to taskEvaluator.getVariable(this[1])!!)
                                } else {
                                    println("Unknown variable")
                                }

                            // regular "number to variable" assignment
                            } else {
                                taskEvaluator.registerVariable(this[0] to this[1].toDouble())
                            }
                        }
                    } else if (isVariableAssignment(inputStr)) {
                        println("Invalid assignment")
                    } else {
                        println("Invalid identifier")
                    }

                // inquiry of variable
                } else if (input.size == 1 && input[0].contains("[a-zA-Z]".toRegex())) {
                    if (isVariableInquiry(inputStr)) {
                        println(taskEvaluator.getVariable(inputStr.trim())?.toInt() ?: "Unknown variable")
                    } else {
                        println("Invalid identifier")
                    }

                // handle computation
                } else {
                    debugMe(">>> computation handler <<<")
                    try {
                        println(taskEvaluator.processInput(inputStr.split(" ")).toInt())
                    } catch (e: InvalidExpressionException) {
                        debugMe(e.message ?: "")
                        println("Invalid expression")
                    } catch (e: NumberFormatException) {
                        debugMe(e.message ?: "")
                        println("Invalid expression")
                    } catch (e: UnknownVariableException) {
                        debugMe(e.message ?: "")
                        println("Unknown variable")
                    }
                }
            }
        } while (!terminate)
    }
}
