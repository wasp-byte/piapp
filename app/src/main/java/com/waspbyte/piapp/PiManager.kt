import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

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
        if (currentIndex == -1) return null
        if (currentIndex == DOT) currentIndex--
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

        for (i in 0..currentIndex) {
            colors.add(if (isCorrect[i]) Color.GREEN else Color.RED)
            if (i == DOT-1)
                colors.add(Color.GREEN)
        }

        return colors.toList()
    }
}