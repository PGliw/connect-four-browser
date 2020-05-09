package game

import game.board.Board
import game.controllers.IPlayerController

interface BoardOwner {
    fun provide(): Board
    fun update(board: Board)
}


class GameEngine
    (
    private val player1Controller: IPlayerController,
    private val player2Controller: IPlayerController
) : BoardOwner {
    private var turn = 1
    private fun nextTurn() {
        turn = if (turn == 1) 2 else 1
    }

    var board = Board(Array(6) { Array(7) { 0 } })
    var availableMovements = listOf<Int>()

    override fun provide() = this.board

    override fun update(board: Board) {
        this.board = board
    }

    init {
        player1Controller.register(this)
        player2Controller.register(this)
    }

    // starts game loop
    fun start() {
        var winner = 0
        while (true) {


            // if the board is full or somebody won then break
            availableMovements = board.availableColumns()
            if (winner != 0 || availableMovements.isEmpty()) {
                // show winning board and exit
                println("\n------ Koniec gry ------")
                board.draw()
                break
            }

            val currentPlayerController = if (turn == 1) player1Controller else player2Controller
            currentPlayerController.makeMove()

            // assess state
            winner = when (board.assess()) {
                Int.MAX_VALUE -> 1
                Int.MIN_VALUE -> 2
                else -> 0
            }

            nextTurn()
        }

        // show result1s
        when (winner) {
            0 -> println("Brak zwycięzców")
            else -> println("Wygrywa: $winner")
        }
    }
}