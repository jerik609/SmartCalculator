package calculator.core

import calculator.debugMePrintln
import calculator.input.Input
import calculator.input.InvalidExpressionException
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
    private fun eatStack(lookAhead: Operator, _acceptOpeningParentheses: Int = 0) {
        /**
         * if we encountered a closing bracket, we have to eat the stack backwards until the opening bracket
         * the way we've worked "up the stack" guarantees, that the stack sequence is ever-growing in priority
         * (ok, decreasing in fact, as we go backwards when eating - in this code)
         */
        debugMePrintln("  EAT_STACK: stack: $mainStack")
        debugMePrintln("  EAT_STACK: ----- beginning main loop -----")
        var acceptOpeningParentheses = _acceptOpeningParentheses
        debugMePrintln("  EAT_STACK: will accept $acceptOpeningParentheses opening brackets")
        do {
            if (mainStack.size < 2) {
                debugMePrintln("  EAT_STACK: ----- no more terms left on stack, depth: ${mainStack.size} -----")
                break
            }

            val rightOperand = mainStack.pop() as Operand
            val operator = mainStack.pop() as Operator

            // do not consume past opening brackets if not triggered with closing bracket
            if (operator.type == Operator.OperatorType.PARENTHESES_OPENING) {
                debugMePrintln("  EAT_STACK: left bracket encountered `$operator`, can accept $acceptOpeningParentheses opening brackets")
                if (acceptOpeningParentheses > 0) {
                    --acceptOpeningParentheses
                    debugMePrintln("  EAT_STACK: left bracket consumed, ($acceptOpeningParentheses) remaining")
                    mainStack.push(rightOperand)
                } else {
                    debugMePrintln("  EAT_STACK: cannot consume bracket, none remaining")
                    mainStack.push(operator)
                    mainStack.push(rightOperand)
                    break
                }
            } else {
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
            }
            debugMePrintln("  EAT_STACK: ----- next loop -----")
        } while (true)

        if (lookAhead.type != Operator.OperatorType.ZERO_OPERATOR && lookAhead.type != Operator.OperatorType.PARENTHESES_CLOSING) {
            mainStack.push(lookAhead)
        }

        debugMePrintln("  EAT_STACK: done -> top element on stack: ${mainStack.peek()}")
    }

    /**
     * It puts an operand on the stack including any preceding opening brackets.
     * Checks the next symbol, if it is an opening bracket, it consumes all subsequent opening brackets.
     * It finishes, when it encounters an operand, which it puts on the stack.
     * It returns `true` when it encountered a bracket.
     * If it reaches end of input and the last symbol it read is an opening bracket, it throws an exception.
     * We assume, that:
     *   - only this is valid: "((((...", "(((A...", "...A)))"
     *   - these are all the invalid cases: "(((+...", "((()..."
     * @param iterator the input iterator
     * @return true if left bracket encountered
     */
    private fun readOperandAndPutOnStackWithAllPrecedingOpeningBrackets(iterator: Iterator<String>): Boolean {
        if (!iterator.hasNext()) return false
        var word = iterator.next()
        var seenOpeningBracket = false
        do {
            if (word == Operator.OperatorType.PARENTHESES_OPENING.visual.toString()) {
                mainStack.push(Operator.getOperatorFromString(word))
                seenOpeningBracket = true
            } else {
                mainStack.push(getOperandFromRawInput(word))
                break
            }
        } while (iterator.hasNext().also { if (it) word = iterator.next() })

        // we can only have an opening bracket here, if we reached the end of the input, which is invalid
        if (word == Operator.OperatorType.PARENTHESES_OPENING.visual.toString()) {
            throw InvalidExpressionException("End of input reached while processing opening brackets")
        }

        return seenOpeningBracket
    }

    /**
     * Get number of subsequent closing brackets, so that we know how far we can evaluate the stack.
     * @param iterator the input iterator
     * @return a pair of values, the number of closing brackets and the lookAhead operator (the next operator after
     *         closing brackets)
     */
    private fun getNumberOfSubsequentClosingBrackets(iterator: Iterator<String>): Pair<Int, Operator> {
        var counter = 1 // already one found
        if (!iterator.hasNext()) return Pair(counter, Operator(Operator.OperatorType.ZERO_OPERATOR)) // end of stream
        var word = iterator.next()
        do {
            if (word == Operator.OperatorType.PARENTHESES_CLOSING.visual.toString()) {
                ++counter
            } else {
                break
            }
        } while (iterator.hasNext().also { if (it) word = iterator.next()  })

        return Pair(counter, Operator.getOperatorFromString(word))
    }

    /**
     * Processes the calculation task.
     * @param input the calculation task in form of a string
     * @return result of the calculation
     */
    fun processInput(input: String): Double {

        mainStack.clear()

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

        readOperandAndPutOnStackWithAllPrecedingOpeningBrackets(word)
        mainStack.push(Operator.getOperatorFromString(word.next()))

        debugMePrintln("----- beginning main loop -----")
        while (true) {

            // if stack size is 1, we're done, and we can take the result
            if (mainStack.size == 1) {
                debugMePrintln("main stack size == 1")
                break
            }

            // if we find an opening bracket, read all of them and go to next iteration - can't evaluate
            if (readOperandAndPutOnStackWithAllPrecedingOpeningBrackets(word)) {
                // read operator, put on stack and continue with next iteration (operand is already there)
                mainStack.push(Operator.getOperatorFromString(word.next()))

            // no opening bracket, just evaluate what's on stack (with respect to lookAhead)
            } else {
                // we have both operands and the operator on stack
                // `left` and `operator` from previous iteration, `right` from the opening bracket search
                val rightOperand = mainStack.pop() as Operand
                val operator = mainStack.pop() as Operator
                val leftOperand = mainStack.pop() as Operand

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

                    // we encountered a closing bracket, calculate and put on stack, we will evaluate it
                    // after closing bracket, only another operator is allowed - the new lookAhead - provide it, if available
                    if (lookAhead.type == Operator.OperatorType.PARENTHESES_CLOSING) {
                        debugMePrintln("encountered closing bracket, triggering stack evaluation")
                        val resultPair = getNumberOfSubsequentClosingBrackets(word)
                        mainStack.push(operator.performOperation(leftOperand, rightOperand))
                        eatStack(resultPair.second, resultPair.first)

                    // if "look ahead" operator's priority is higher, only put everything on stack (can't evaluate for now)
                    } else if (operator.type.priority < lookAhead.type.priority) {
                        debugMePrintln("lookAhead has higher priority, just put on stack and read next value")
                        mainStack.push(leftOperand)
                        mainStack.push(operator)
                        mainStack.push(rightOperand)
                        mainStack.push(lookAhead)

                    // if "look ahead" operator's priority is lower or the same, calculate operation and try to consume stack
                    } else {
                        debugMePrintln("lookAhead has lower or same priority, calculate & put on stack, then evaluate stack")
                        mainStack.push(operator.performOperation(leftOperand, rightOperand))
                        eatStack(lookAhead)
                    }

                    debugMePrintln("stack: $mainStack")
                    debugMePrintln("----- next loop -----")
                }
            }
        }
        debugMePrintln("done main loop: ${mainStack.peek()}")

        return (mainStack.pop() as Operand).value
    }

}