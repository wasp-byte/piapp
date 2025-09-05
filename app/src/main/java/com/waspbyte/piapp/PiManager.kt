import android.graphics.Color
import android.widget.TextView
import kotlin.math.min

class PiManager(val textView: TextView, val maxTextWidth: Int) {
    companion object {
        private const val PI =
            "314159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612"
        private const val DOT = 1
    }

    private var currentIndex = -1
    private var currentText = StringBuilder()
    private var visibleIndex = 0
    private var wrongAttempts = mutableListOf<Int>()
    private var isCorrect = mutableListOf<Boolean>()

    fun isDot() = currentIndex == DOT - 1

    fun next(c: Char): Char? {
        currentIndex++
        if (wrongAttempts.size <= currentIndex) {
            wrongAttempts.add(0)
            isCorrect.add(false)
        }

        currentText.append(c)
        val paint = textView.paint
        while (paint.measureText(getText()) > maxTextWidth && visibleIndex <= currentIndex) {
            visibleIndex++
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

    fun back() {
        if (currentIndex == -1) return
        isCorrect[currentIndex] = false
        currentText.deleteAt(currentText.length - 1)
        currentIndex--
        if (visibleIndex > 0) visibleIndex--
    }

    fun clear() {
        currentIndex = -1
        visibleIndex = 0
        wrongAttempts.clear()
        isCorrect.clear()
        textView.text = ""
    }

    fun getPoints(): Float {
        val initialPoints = (currentIndex + 1) * 5
        var points = initialPoints

        for ((correct, attempts) in isCorrect.zip(wrongAttempts)) {
            points -= if (correct) {
                min(attempts, 5)
            } else {
                5
            }
        }

        return points.toFloat() / initialPoints.toFloat()
    }

    fun getText(): String {
        if (currentIndex < 0) return ""
        val start = visibleIndex
        val end = currentIndex
        val sb = StringBuilder()
        for (i in start..end) {
            sb.append(currentText[i])
            if (i == DOT - 1) sb.append('.')
        }
        return sb.toString()
    }

    fun getColors(): List<Int> {
        if (currentIndex < 0) return emptyList()
        val colors = ArrayList<Int>()
        val start = visibleIndex
        val end = currentIndex
        for (i in start..end) {
            colors.add(if (isCorrect[i]) Color.BLACK else Color.RED)
            if (i == DOT - 1) colors.add(Color.BLACK)
        }
        return colors
    }
}