package calculator

import calculator.core.Operator
import calculator.core.TaskEvaluator
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    scanner.useLocale(Locale.US)

    val taskEvaluator = TaskEvaluator()

    while (scanner.hasNextLine()) {
        taskEvaluator.fillStackForCalculation(scanner.nextLine().split(" "))
        println(taskEvaluator.calculateStack().toInt())
    }

    val x = Operator.Companion.OperatorType.PLUS


//    val controller = Controller(scanner)
//
//    controller.mainLoop()
}
