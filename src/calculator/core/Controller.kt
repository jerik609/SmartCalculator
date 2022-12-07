package calculator.core

import calculator.data.VariablePool
import calculator.handlers.CommandHandler
import calculator.handlers.ComputationHandler
import calculator.handlers.VariableAssignmentHandler
import calculator.handlers.VariableInquiryHandler
import java.util.Scanner
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Reads input and calls the respective branch (should implement handlers at some point)
 */
class Controller(private val scanner: Scanner) {

    private val terminate = AtomicBoolean(false)

    private val variablePool = VariablePool()

    private val taskEvaluator = TaskEvaluator(variablePool)

    private val commandHandler = CommandHandler(terminate)
    private val computationHandler = ComputationHandler(taskEvaluator)
    private val variableAssignmentHandler = VariableAssignmentHandler(variablePool)
    private val variableInquiryHandler = VariableInquiryHandler(variablePool)

    fun mainLoop() {
        do {
            if (scanner.hasNextLine()) {

                val inputStr = scanner.nextLine()

                if (inputStr.isEmpty()) {
                    continue
                }

                if (commandHandler.isForMe(inputStr)) {
                    commandHandler.handle(inputStr)
                } else if (variableAssignmentHandler.isForMe(inputStr)) {
                    variableAssignmentHandler.handle(inputStr)
                } else if (variableInquiryHandler.isForMe(inputStr)) {
                    variableInquiryHandler.handle(inputStr)
                } else {
                    computationHandler.handle(inputStr)
                }
            }
        } while (!terminate.get())
    }
}
