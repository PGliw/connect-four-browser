package algorithms

import game.board.Board

private fun miniMaxHelper(
    unaffectedBoard: Board,
    move: Int,
    turn: Int,
    level: Int,
    maxLevel: Int,
    heuristics: Board.() -> Int,
    isLogging: Boolean
): Pair<Int, Int> {
    val board = unaffectedBoard.insert(move, turn)
    val nextTurn = if (turn == 1) 2 else 1

    val assessment = board.assess()

    val score: Int = when {

        // if terminal node then return actual assessment
        assessment != 0 -> {
            if (isLogging) {
                println()
                println("Move: $move | Level: $level | Turn: $turn | isLogging: $isLogging | Score A = $assessment")
                board.draw()
                println()
            }
            assessment
        }

        // if maximum search depth is exceeded then return value of the heuristic value
        level >= maxLevel -> {
            val hscore = board.heuristics()
            if (isLogging) {
                println()
                println("Move: $move | Level: $level | Turn: $turn | isLogging: $isLogging | Score H = $hscore")
                board.draw()
                println()
            }
            hscore
        }

        // else compute minmax foreach child-state of current state
        else -> miniMax(
            board,
            nextTurn,
            level,
            maxLevel,
            heuristics,
            isLogging
        ).second
    }
    return Pair(move, score)
}

fun miniMax(
    board: Board,
    turn: Int,
    level: Int,
    maxLevel: Int,
    heuristics: Board.() -> Int,
    isLogging: Boolean = false
): Pair<Int, Int> {
    val moveOptions = board.availableColumns()
    val childMovesWithScores = mutableListOf<Pair<Int, Int>>()
    for (childMove in moveOptions) {
        val result = miniMaxHelper(
            board,
            childMove,
            turn,
            level + 1,
            maxLevel,
            heuristics,
            isLogging
        )
        childMovesWithScores.add(result)
    }
    return when (turn) {
        1 -> childMovesWithScores.maxBy { it.second } ?: throw IllegalArgumentException("max is null")
        2 -> childMovesWithScores.minBy { it.second } ?: throw IllegalArgumentException("min is null")
        else -> throw IllegalArgumentException("Turn number $turn is out of range [1, 2]")
    }
}

