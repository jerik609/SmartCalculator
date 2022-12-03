package calculator.input

import calculator.core.TaskEvaluator
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
                val inputStr = scanner.nextLine()

                // we're dealing with a valid command
                val command = Input.translateToCommand(inputStr)
                if (command != Command.NOT_A_COMMAND) {
                    performCommand(command)
                    continue
                }

                // not a command, must be a computation task
                if (inputStr.isNotEmpty()) {
                    println(taskEvaluator.processInput(inputStr.split(" ")).toInt())
                    continue
                }

                // something is not correct, report problem
                if (inputStr.isNotEmpty()) println("invalid input: $inputStr")
            }
        } while(!terminate)
    }

}