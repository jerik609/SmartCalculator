package calculator.input

import calculator.core.TaskEvaluator
import calculator.debugMe
import java.lang.NumberFormatException
import java.util.Scanner

/**
 * Reads input and calls the respective branch (should implement handlers at some point)
 */
class Controller(private val scanner: Scanner, private val taskEvaluator: TaskEvaluator) {

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
                    // noop

                // handle command
                } else if (inputStr[0] == '/') {
                    try {
                        val command = Input.translateToCommand(inputStr)
                        performCommand(command)
                    } catch (e: UnknownCommandException) {
                        println("Unknown command")
                    }

                // handle computation
                } else {
                    try {
                        println(taskEvaluator.processInput(inputStr.split(" ")).toInt())
                    } catch (e: InvalidExpressionException) {
                        debugMe(e.message ?: "")
                        println("Invalid expression")
                    } catch (e: NumberFormatException) {
                        debugMe(e.message ?: "")
                        println("Invalid expression")
                    }
                }
            }
        } while (!terminate)
    }

}