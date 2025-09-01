import kotlin.math.max

class PiManager {
    companion object {
        private const val PI = "3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612"
    }

    var currentIndex = 0

    public fun next(): Char? {
        currentIndex++

        return PI[currentIndex - 1]
    }

    public fun back(): Char? {
        if (currentIndex == 0) return null
        currentIndex--
        return PI[currentIndex]
    }

    fun getDigit(index: Int): Char? {
        if (index == 0) return PI[0]
        if (index + 1 >= PI.length) return null
        // skip '.'
        return PI[index + 1]
    }
}