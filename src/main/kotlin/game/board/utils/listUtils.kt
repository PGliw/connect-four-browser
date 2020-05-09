package game.board.utils

fun List<Int>.containsSeqenceOf(sequenceLength: Int, vararg element: Int): Boolean {
    for (i in 0..(size - sequenceLength)) {
        val slice = this.slice(i until (i + sequenceLength))
        if (slice.all { it in element }) return true
    }
    return false
}

fun List<Int>.countSubsequenceOf(sequenceLength: Int, countedElement: Int, ignoredElement: Int): Int {
    var maxSubsequenceLength = 0
    for (i in 0..(size - sequenceLength)) {
        val slice = this.slice(i until (i + sequenceLength))
        if (slice.all { it == countedElement || it == ignoredElement}) {
            val sliceSubsequenceLength = slice.count { it == countedElement }
            if (maxSubsequenceLength < sliceSubsequenceLength) {
                maxSubsequenceLength = sliceSubsequenceLength
            }
        }
    }
    return maxSubsequenceLength
}