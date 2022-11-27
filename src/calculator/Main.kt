package calculator

import calculator.core.TaskEvaluator
import calculator.input.Input
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    scanner.useLocale(Locale.US)

    val taskEvaluator = TaskEvaluator(scanner)


    taskEvaluator.fillStackForCalculation()


//    val controller = Controller(scanner)
//
//    controller.mainLoop()
}
