package algorithms.heuristics

import game.board.Board
import game.board.utils.*

/**
 * @return p1 total strike - p2 total strike
 * where "total strike" means sum of promising subsequences lengths where item is set in a row / column / diagonal
 * where "promising subsequence" means that it contains only 1s and 0s in case of player 1 or 2s and 0s for player2
 * eg. for player 1:
 * (1, 1, 0, 2, 1, 2, 1, 1, 2) -> 0
 * (1, 1, 0, 1, 1, 2, 1, 1, 2) -> 3 (subsequence (1, 1, 0, 1)
 */
fun Board.totalStrikeHeuristics(): Int {

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
    val player1WinningLines = lines.sumBy { it.countSubsequenceOf(4, 1, 0) }

    // count winning rows, columns and diagonals for p2 player
    val player2WinningLines = lines.sumBy { it.countSubsequenceOf(4, 2, 0) }

    // subtract the results
    // the bigger the result, the better state is for player1
    // the lower the result, the better state is for player2
    return player1WinningLines - player2WinningLines
}