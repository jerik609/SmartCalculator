package calculator

import calculator.input.Command
import calculator.input.Input
import java.lang.NumberFormatException
import java.util.Scanner

/**
 * Reads input and
 */
class Controller(private val scanner: Scanner) {

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

    private fun getInputAsNumbers(input: String): List<Int> {
        return try {
            input.trim().split(" ").map { it.toInt() }
        } catch (e: NumberFormatException) {
            emptyList()
        }
    }

    private fun performComputation(computation: List<Int>) =
        computation.reduce{ acc, elem -> acc + elem }

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

                // we're dealing with a valid task
                // create validation result ... check for validation errors?

                // task is a executable term ... a split of


                val task = getInputAsNumbers(inputStr)
                if (task.isNotEmpty()) {





                    println(performComputation(task))
                    continue
                }

                // something is not correct, report problem
                if (inputStr.isNotEmpty()) println("invalid input: $inputStr")
            }
        } while(!terminate)
    }

}