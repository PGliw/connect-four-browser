package game.board

import game.board.utils.*

class Board(val fields: Array<Array<Int>>) {
    fun availableColumns(): List<Int> {
        val allColumns = fields.columns().withIndex()
        val availableColumns = allColumns.filter { it.value.contains(0) }
        return availableColumns.map { it.index }
    }

    fun insert(column: Int, player: Int): Board {
        val columns = fields.columns()
        val index = columns[column].indexOfLast { it == 0 }
        val newFields = fields.deepCopy()
        newFields[index][column] = player
        return Board(newFields)
    }

    fun isEmpty() = fields.all { array -> array.all { it == 0 } }

    fun assess(): Int {
        // list of diagonals
        val diagonals = fields.diagonals()
        val rows = fields.rows()
        val columns = fields.columns()
        val allDirections = mutableListOf<List<Int>>().apply {
            addAll(diagonals)
            addAll(rows)
            addAll(columns)
        }

        return when {
            allDirections.any { list -> list.containsSeqenceOf(4, 1) } -> Int.MAX_VALUE // player 1 wins player 2 loses
            allDirections.any { list -> list.containsSeqenceOf(4, 2) } -> Int.MIN_VALUE  // player 1 wins player 2 loses
            else -> 0 // nobody is winning at the time
        }
    }

    fun isColumnAvailable(column: Int): Boolean {
        val columns = fields.columns()
        return (columns[column].contains(0))
    }

    fun draw() = fields.draw()
}