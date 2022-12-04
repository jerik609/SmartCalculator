package calculator.core

import calculator.debugMePrintln
import calculator.input.Input
import java.util.*

/**
 * How does it work:
 *
 * I use a stack, you can imagine it as running on a mountain ridge - up you go slowly, down you go quickly - until you
 * reach another climb up.
 *
 * Thus, "the look ahead" logic:
 *
 * if "look ahead" operator's priority is higher, then put on stack (cant evaluate for now)
 * if "look ahead" operator's priority is same or lower, evaluate term, put it on stack and try to process stack
 *     read "operations" from stack and process them, as long as operator on stack is higher or same priority as "look ahead"
 *     if "look ahead" is higher, then stop processing the stack and process input
 *
 * given that input is well-formed (valid calculation task), then we should be able to calculate the task
 * anything else indicates an invalid input
 *
 * rationale:
 * - when operators are same priority or lower, we immediately calculate
 * - we only put operators on stack with an increasing priority - in an order which has to be evaluated top to bottom
 * - when evaluating the stack, we will do so, until we are not able, because the stack (to the left) has now
 * a lower priority than unprocessed input to the right
 * - when the end of the input is reached, the last operator is a special "zero priority" operator and this will ensure
 * that the stack can be completely consumed
 *
 */
class TaskEvaluator(private val variablePool: VariablePool) {

    private val mainStack = Stack<StackItem>()

    private fun getOperandFromRawInput(raw: String): Operand {
        return if (raw.matches("[a-zA-Z]+".toRegex())) {
            Operand(variablePool.getVariable(raw) ?: throw UnknownVariableException("Unknown variable $raw"))
        } else {
            Operand(raw.toDouble())
        }
    }

    /**
     * Calculates the stack, until only one element remains, which will be left on the stack.
     * @param lookAhead priority of the "next" operator, we can calculate the stack as long as stack priority is lower
     */
    private fun eatStack(lookAhead: Operator) {
        debugMePrintln("  EAT_STACK: stack: $mainStack")
        debugMePrintln("  EAT_STACK: ----- beginning main loop -----")
        do {
            if (mainStack.size < 3) {
                debugMePrintln("  EAT_STACK: ----- no more terms left on stack, depth: ${mainStack.size} -----")
                break
            }

            val rightOperand = mainStack.pop() as Operand
            val operator = mainStack.pop() as Operator
            val leftOperand = mainStack.pop() as Operand

            debugMePrintln("  EAT_STACK: stack operator: $operator, lookAhead operator: $lookAhead")

            // can't evaluate further, lookAhead has higher priority
            if (operator.type.priority < lookAhead.type.priority) {
                debugMePrintln("  EAT_STACK: can't continue, lookAhead has higher priority")
                mainStack.push(leftOperand)
                mainStack.push(operator)
                mainStack.push(rightOperand)
                break
            } else {
                debugMePrintln("  EAT_STACK: eating the stack, lookAhead has same or lower priority")
                mainStack.push(operator.performOperation(leftOperand, rightOperand))
            }
            debugMePrintln("  EAT_STACK: ----- next loop -----")
        } while (true)

        debugMePrintln("  EAT_STACK: done -> top element on stack: ${mainStack.peek()}")
    }

    /**
     * Processes the calculation task.
     * @param words the calculation task in form of split words
     * @return result of the calculation
     */
    fun processInput(input: String): Double {

        // sanitize input and split
        val words = Input.sanitizeInput(input).split(" ")

        // stack must be empty before any calculation
        check(mainStack.empty())

        debugMePrintln("input: $words")

        if (words.isEmpty()) {
            debugMePrintln("nothing, empty term")
            return 0.0
        }

        // the iterator
        val word = words.iterator()

        // one word, must be an operand
        if (words.size == 1) {
            // let it crash if no double, whatever, own fault
            debugMePrintln("just one fella here, must be an operand = print it as a result")
            return getOperandFromRawInput(word.next()).value
        }

        require(words.size >= 3) // anything else would be nonsense and deserves a crash

        mainStack.push(getOperandFromRawInput(word.next()))
        mainStack.push(Operator.getOperatorFromString(word.next()))

        debugMePrintln("----- beginning main loop -----")
        while (true) {

            // if stack size is 1, we're done, and we can take the result
            if (mainStack.size == 1) {
                debugMePrintln("main stack size == 1")
                break
            }

            // stack: ... X1 +_1 X2 +_2 X3 +_3 <<< we're here
            val operator = mainStack.pop() as Operator // +_3
            val leftOperand = mainStack.pop() as Operand // X3

            // words: we're here >>> X4 + x_4 + X5 + x_5 ...
            val rightOperand = getOperandFromRawInput(word.next()) // X4

            debugMePrintln("processing term: $leftOperand $operator $rightOperand")

            // input is processed, just evaluate stack
            if (!word.hasNext()) {
                debugMePrintln("the end of input, now just finish stack and we're done")
                mainStack.push(operator.performOperation(leftOperand, rightOperand))
                eatStack(Operator(Operator.OperatorType.ZERO_OPERATOR))
                break
            // there's a next word
            } else {
                val lookAhead = Operator.getOperatorFromString(word.next())
                debugMePrintln("the lookAhead operator: $lookAhead")

                // if "look ahead" operator's priority is higher, only put everything on stack (can't evaluate for now)
                if (operator.type.priority < lookAhead.type.priority) {
                    debugMePrintln("lookAhead has higher priority, just put on stack and read next value")
                    mainStack.push(leftOperand)
                    mainStack.push(operator)
                    mainStack.push(rightOperand)
                // if "look ahead" operator's priority is lower or the same, calculate operation and try to consume stack
                } else {
                    debugMePrintln("lookAhead has lower or same priority, calculate & put on stack, then evaluate stack")
                    mainStack.push(operator.performOperation(leftOperand, rightOperand))
                    eatStack(lookAhead)
                }

                // push the look ahead on stack so that it's not lost! :-)
                mainStack.push(lookAhead)

                debugMePrintln("stack: $mainStack")
                debugMePrintln("----- next loop -----")
            }
        }
        debugMePrintln("done main loop: ${mainStack.peek()}")

        return (mainStack.pop() as Operand).value
    }

}