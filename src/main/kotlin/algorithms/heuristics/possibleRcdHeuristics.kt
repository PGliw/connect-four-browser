package algorithms.heuristics

import game.board.Board
import game.board.utils.columns
import game.board.utils.containsSeqenceOf
import game.board.utils.diagonals
import game.board.utils.rows

/**
 * @return (p1 winning rows + columns + diagonals) - (p2 winning rows + columns + diagonals)
 * "winning row / column / diagonal" means that player p1 can still complete 4 items in this row / column / diagonal
 */
fun Board.possibleRcdHeuristics(): Int {

    val lines = mutableListOf<List<Int>>().apply {
        addAll(fields.columns())
        addAll(fields.rows())
        addAll(fields.diagonals())
    }.toList()

    // if player 1 won then return +Inf
    lines.count { it.containsSeqenceOf(4, 1) }.let { wonLines ->
        if (wonLines > 0) return Int.MAX_VALUE
    }

    // if player 1 lost then return -Inf
    lines.count { it.containsSeqenceOf(4, 2) }.let { lostLines ->
        if (lostLines > 0) return Int.MIN_VALUE
    }

    // count winning rows, columns and diagonals for p1 player
    val player1WinningLines = lines.count { it.containsSeqenceOf(4, 1, 0) }

    // count winning rows, columns and diagonals for p2 player
    val player2WinningLines = lines.count { it.containsSeqenceOf(4, 2, 0) }

    // subtract the results
    // the bigger the result, the better state is for player1
    // the lower the result, the better state is for player2
    return player1WinningLines - player2WinningLines
}
