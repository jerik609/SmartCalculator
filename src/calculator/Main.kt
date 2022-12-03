package calculator

import calculator.core.TaskEvaluator
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    scanner.useLocale(Locale.US)

    val controller = Controller(scanner, TaskEvaluator())

    controller.mainLoop()
}
