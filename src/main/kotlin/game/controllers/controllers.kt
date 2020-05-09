package game.controllers

import algorithms.alphaBeta
import algorithms.heuristics.possibleRcdHeuristics
import algorithms.miniMax
import game.BoardOwner
import game.board.Board

interface IPlayerController {
    val number: Int
    fun register(boardOwner: BoardOwner)
    fun makeMove()
}

class ReactPhysicalPlayer(override val number: Int) : IPlayerController {
    private var boardOwner: BoardOwner? = null

    override fun register(boardOwner: BoardOwner) {
        this.boardOwner = boardOwner
    }

    override fun makeMove() {
        val boardOwner =
            boardOwner ?: throw NullPointerException("Game engine not registered (id null) in player $number")
        val board = boardOwner.provide()
        board.draw()
        println("Heurystyka: ${board.possibleRcdHeuristics()}")
        print("Ruch $number: ${board.availableColumns()}> ")

//        // make move
//        val column = readLine()!!.toInt() // TODO
//        boardOwner.update(board.insert(column, number)) // TODO
    }
}

class MiniMaxAiPlayer(
    override val number: Int,
    private val searchDepth: Int = 0,
    private val heuristics: Board.() -> Int,
    private val isLogging: Boolean = false
) : IPlayerController {

    private var boardOwner: BoardOwner? = null

    override fun register(boardOwner: BoardOwner) {
        this.boardOwner = boardOwner
    }

    override fun makeMove() {
        val boardOwner =
            boardOwner ?: throw NullPointerException("Game engine not registered (id null) in player $number")
        val board = boardOwner.provide()
        val bestMoveWithScore =
            miniMax(board, number, 0, searchDepth, heuristics, isLogging)
        boardOwner.update(board.insert(bestMoveWithScore.first, number))
    }
}

class AlphaBetaAiPlayer(
    override val number: Int,
    private val searchDepth: Int = 0,
    private val heuristics: Board.() -> Int,
    private val isLogging: Boolean = false
) : IPlayerController {

    private var boardOwner: BoardOwner? = null

    override fun register(boardOwner: BoardOwner) {
        this.boardOwner = boardOwner
    }

    override fun makeMove() {
        val boardOwner =
            boardOwner ?: throw NullPointerException("Game engine not registered (id null) in player $number")
        val board = boardOwner.provide()
        val bestMoveWithScore =
            alphaBeta(
                board,
                number,
                0,
                searchDepth,
                Int.MIN_VALUE,
                Int.MAX_VALUE,
                heuristics,
                isLogging
            )
        boardOwner.update(board.insert(bestMoveWithScore.first, number))
    }
}