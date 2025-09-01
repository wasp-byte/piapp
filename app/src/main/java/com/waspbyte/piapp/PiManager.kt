import android.graphics.Color
import kotlin.math.max

class PiManager {
    companion object {
        private const val PI = "314159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612"
        private const val DOT = 1
    }

    var currentIndex = -1
    var wrongAttempts = mutableListOf<Int>()
    public var isCorrect = mutableListOf<Boolean>()

    fun isDot() = currentIndex == DOT - 1

    fun next(): Char? {
        currentIndex++
        if (wrongAttempts.size <= currentIndex) {
            wrongAttempts.add(0)
            isCorrect.add(false)
        }

        return PI[currentIndex]
    }

    fun check(answer: Char) {
        val correct = answer == PI[currentIndex]
        if (!correct) {
            wrongAttempts[currentIndex]++
        }
        isCorrect[currentIndex] = correct
    }

    fun back(): Char? {
        if (currentIndex == 0) return null
        isCorrect[currentIndex] = false
        currentIndex--
        return PI[currentIndex + 1]
    }

    fun getPoints(): Float {
        val initialPoints = currentIndex * 5
        var points = initialPoints

        points -= isCorrect.map { if (it) 0 else -5}.sum()
        points -= wrongAttempts.sum()

        return points / initialPoints.toFloat()
    }

    fun getColors(): List<Int> {
        val colors = mutableListOf<Int>()

        var dot = 0
        for (i in 0..currentIndex + 1) {
            if (i == DOT) {
                colors.add(Color.GREEN)
                dot = 1
            }
            else colors.add(if (isCorrect[i - dot]) Color.GREEN else Color.RED)
        }

        return colors.toList()
    }
}