package calculator.handlers

import calculator.core.TaskEvaluator
import calculator.core.UnknownVariableException
import calculator.debugMePrintln
import calculator.input.InvalidExpressionException
import java.lang.NumberFormatException

class ComputationHandler(private val taskEvaluator: TaskEvaluator): Handler {

    override fun isForMe(input: String): Boolean {
        return true
    }

    override fun handle(input: String) {
        debugMePrintln(">>> computation handler <<<")
        try {
            println(taskEvaluator.processInput(input.split(" ")).toInt())
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