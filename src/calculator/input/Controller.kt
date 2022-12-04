package calculator.input

import calculator.core.TaskEvaluator
import calculator.core.UnknownVariableException
import calculator.debugMePrintln
import calculator.handlers.CommandHandler
import java.lang.NumberFormatException
import java.util.Scanner
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Reads input and calls the respective branch (should implement handlers at some point)
 */
class Controller(private val scanner: Scanner, private val taskEvaluator: TaskEvaluator) {

    private val terminate = AtomicBoolean(false)

    private val commandHandler = CommandHandler(terminate)

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

                if (commandHandler.isForMe(inputStr)) {
                    commandHandler.handle(inputStr)

                // handle variable assignment
                } else if (inputStr.contains("=".toRegex())) {
                    debugMePrintln(">>> variable handler <<<")

                    // valid variable assignment
                    if (Input.isValidVariableAssignment(inputStr)) {
                        with(inputStr.split("=").map { it.trim() }) {

                            // attempt to assign variable to variable
                            if (this[1].matches("[a-zA-Z]+".toRegex())) {
                                if (taskEvaluator.getVariable(this[1]) != null) {
                                    debugMePrintln("assigning variable to a variable, value ${taskEvaluator.getVariable(this[1])}")
                                    taskEvaluator.registerVariable(this[0] to taskEvaluator.getVariable(this[1])!!)
                                } else {
                                    println("Unknown variable")
                                }

                            // regular "number to variable" assignment
                            } else {
                                taskEvaluator.registerVariable(this[0] to this[1].toDouble())
                            }
                        }
                    } else if (Input.isVariableAssignment(inputStr)) {
                        println("Invalid assignment")
                    } else {
                        println("Invalid identifier")
                    }

                // inquiry of variable
                } else if (input.size == 1 && input[0].contains("[a-zA-Z]".toRegex())) {
                    if (Input.isVariableInquiry(inputStr)) {
                        println(taskEvaluator.getVariable(inputStr.trim())?.toInt() ?: "Unknown variable")
                    } else {
                        println("Invalid identifier")
                    }

                // handle computation
                } else {
                    debugMePrintln(">>> computation handler <<<")
                    try {
                        println(taskEvaluator.processInput(inputStr.split(" ")).toInt())
                    } catch (e: InvalidExpressionException) {
                        debugMePrintln(e.message ?: "")
                        println("Invalid expression")
                    } catch (e: NumberFormatException) {
                        debugMePrintln(e.message ?: "")
                        println("Invalid expression")
                    } catch (e: UnknownVariableException) {
                        debugMePrintln(e.message ?: "")
                        println("Unknown variable")
                    }
                }
            }
        } while (!terminate.get())
    }
}
