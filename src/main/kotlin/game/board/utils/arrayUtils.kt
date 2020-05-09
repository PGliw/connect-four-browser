package game.board.utils

fun pairsOfDiagonalIndexes(width: Int, height: Int): List<List<Pair<Int, Int>>> {
    val diagonals = mutableListOf<List<Pair<Int, Int>>>()
    val numberOfDiagonals: Int = width + height - 1
    for (diagonalNumber in 0 until numberOfDiagonals) {
        val diagonal = mutableListOf<Pair<Int, Int>>()
        val rowStop = 0.coerceAtLeast(diagonalNumber - width + 1)
        val rowStart = diagonalNumber.coerceAtMost(height - 1)
        for (row in rowStart downTo rowStop) {
            val col = diagonalNumber - row
            diagonal.add(Pair(row, col))
        }
        diagonals.add(diagonal)
    }
    return diagonals
}

fun Array<Array<Int>>.diagonals(): List<List<Int>> {
    val results = mutableListOf<List<Int>>()
    val rowsCount = this[0].size
    val columnsCount = this.size
    val podi = pairsOfDiagonalIndexes(rowsCount, columnsCount)

    // diagonals
    podi.forEach { diagonalIndexes ->
        val diagonal = mutableListOf<Int>()
        diagonalIndexes.forEach { (f, s) ->
            diagonal.add(this[f][s])
        }
        results.add(diagonal)
    }

    // antidiagonals
    val podiRev = podi.map { diagonal -> diagonal.map { (r, c) -> Pair(r, columnsCount - c) } }
    podiRev.forEach { diagonalIndexes ->
        val diagonal = mutableListOf<Int>()
        diagonalIndexes.forEach { (f, s) ->
            diagonal.add(this[f][s])
        }
        results.add(diagonal)
    }

    return results
}

fun Array<Array<Int>>.rows(): List<List<Int>> {
    return this.map { row -> row.toList() }.toList()
}

fun Array<Array<Int>>.columns(): List<List<Int>> {
    val columns = List(this[0].size) { mutableListOf<Int>() }
    for (row in this) {
        for ((columnIndex, value) in row.withIndex()) {
            columns[columnIndex].add(value)
        }
    }
    return columns
}

fun Array<Array<Int>>.draw() {
    this.forEach { row ->
        row.forEach {
            print("| $it ")
        }
        println("|\n-----------------------------")
    }
}

fun Array<Array<Int>>.deepCopy(): Array<Array<Int>> {
    return map { it.copyOf() }.toTypedArray()
}