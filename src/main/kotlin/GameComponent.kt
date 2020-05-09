import algorithms.heuristics.possibleRcdHeuristics
import algorithms.heuristics.totalStrikeHeuristics
import game.BoardOwner
import game.board.Board
import game.board.utils.columns
import game.controllers.AlphaBetaAiPlayer
import game.controllers.IPlayerController
import game.controllers.MiniMaxAiPlayer
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledForm

interface GameState : RState {
    var currentBoard: Board
    var player1Value: String
    var player2Value: String
    var searchDepth1Value: String
    var searchDepth2Value: String
    var heuristics1Value: String
    var heuristics2Value: String
    var isGameInProgress: Boolean
    var winner: Int
    var turn: Int
    var player1: IPlayerController?
    var player2: IPlayerController?
}

class GameComponent : RComponent<RProps, GameState>(), BoardOwner {

    override fun GameState.init() {
        currentBoard = Board(Array(6) { Array(7) { 0 } })
        player1Value = "player"
        player2Value = "player"
        searchDepth1Value = "2"
        searchDepth2Value = "2"
        heuristics1Value = "possibleRcd"
        heuristics2Value = "totalStrike"
        isGameInProgress = false
        winner = 0
        turn = 1
    }

    private val handleSubmit = { event: Event ->
        setState {
            val player1Controller = when {
                player1Value == "player" -> null
                player1Value == "minimax" && heuristics1Value == "possibleRcd" -> MiniMaxAiPlayer(
                    1,
                    searchDepth1Value.toInt(),
                    { possibleRcdHeuristics() })
                player1Value == "minimax" && heuristics1Value == "totalStrike" -> MiniMaxAiPlayer(
                    1,
                    searchDepth1Value.toInt(),
                    { totalStrikeHeuristics() })
                player1Value == "alphabeta" && heuristics1Value == "possibleRcd" -> AlphaBetaAiPlayer(
                    1,
                    searchDepth1Value.toInt(),
                    { possibleRcdHeuristics() })
                player1Value == "alphabeta" && heuristics1Value == "totalStrike" -> AlphaBetaAiPlayer(
                    1,
                    searchDepth1Value.toInt(),
                    { totalStrikeHeuristics() })
                else -> throw IllegalArgumentException("Invalid value of player 1: $player1")
            }
            val player2Controller = when {
                player2Value == "player" -> null
                player2Value == "minimax" && heuristics2Value == "possibleRcd" -> MiniMaxAiPlayer(
                    2,
                    searchDepth2Value.toInt(),
                    { possibleRcdHeuristics() })
                player2Value == "minimax" && heuristics2Value == "totalStrike" -> MiniMaxAiPlayer(
                    2,
                    searchDepth2Value.toInt(),
                    { totalStrikeHeuristics() })
                player2Value == "alphabeta" && heuristics2Value == "possibleRcd" -> AlphaBetaAiPlayer(
                    2,
                    searchDepth2Value.toInt(),
                    { possibleRcdHeuristics() })
                player2Value == "alphabeta" && heuristics2Value == "totalStrike" -> AlphaBetaAiPlayer(
                    2,
                    searchDepth2Value.toInt(),
                    { totalStrikeHeuristics() })
                else -> throw IllegalArgumentException("Invalid value of player 2: $player2")
            }
            console.log(player1Controller)
            console.log(player2Controller)
            player1Controller?.register(this@GameComponent)
            player2Controller?.register(this@GameComponent)
            setState {
                currentBoard = Board(Array(6) { Array(7) { 0 } })
                player1 = player1Controller
                player2 = player2Controller
                winner = 0
                isGameInProgress = true
            }
        }
        event.preventDefault()
    }


    // Actual game loop
    override fun componentDidUpdate(prevProps: RProps, prevState: GameState, snapshot: Any) {
        val inProgress = state.isGameInProgress
        if (inProgress) {
            val availableColumns = state.currentBoard.availableColumns()
            val turn = state.turn

            // assess state
            val newWinner = when (state.currentBoard.assess()) {
                Int.MAX_VALUE -> 1
                Int.MIN_VALUE -> 2
                else -> 0
            }

            if (newWinner != 0 || availableColumns.isEmpty()) {
                setState {
                    winner = newWinner
                    isGameInProgress = false
                }
            } else {
                val currentPlayerController = if (turn == 1) state.player1 else state.player2

                // if player is controller is not null then make move on their behalf
                currentPlayerController?.makeMove()
                // else (if player controller is null) this will be handled by user's onclick
            }
        }
    }

    override fun provide(): Board {
        return state.currentBoard
    }

    override fun update(board: Board) {
        val t = state.turn
        setState {
            currentBoard = board
            turn = if (t == 1) 2 else 1
        }
    }

    private val playerOptions = mapOf(
        "player" to "Gracz",
        "minimax" to "Komputer minimax",
        "alphabeta" to "Komputer alfa - beta"
    )

    private val heuristicsOptions = mapOf(
        "possibleRcd" to "Liczba możliwych wierszy, kolumn i przekątnych",
        "totalStrike" to "Suma rokujących podciągów"
    )

    private fun handleColumnClick(columnId: Int) {
        val inProgress = state.isGameInProgress
        if (inProgress) {
            val t = state.turn
            val newBoard = state.currentBoard.insert(columnId, t)
            setState {
                currentBoard = newBoard
                turn = if (t == 1) 2 else 1
            }
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                background = "rgb(242,205,221)"
                background = "radial-gradient(circle, rgba(242,205,221,1) 0%, rgba(203,234,245,1) 100%)"
                width = LinearDimension.fillAvailable
                height = LinearDimension.fillAvailable
                textAlign = TextAlign.center
            }

            h2 {
                +"Connect 4"
            }

            if (!state.isGameInProgress) {

                styledForm {
                    css {
                        textAlign = TextAlign.center
                    }

                    attrs.onSubmitFunction = handleSubmit

                    styledDiv {

                        label {
                            +"Gracz czerwony: "
                            select {
                                attrs {
                                    value = state.player1Value
                                    onChangeFunction = {
                                        val target = it.target as HTMLSelectElement
                                        setState {
                                            player1Value = target.value
                                        }
                                    }
                                }
                                for (playerOption in playerOptions) {
                                    option {
                                        +playerOption.value
                                        attrs {
                                            value = playerOption.key
                                        }
                                    }
                                }
                            }
                        }
                        label {
                            +"Głębokośc przszukiwania: "
                            input {
                                attrs {
                                    min = "1"
                                    max = "5"
                                    value = state.searchDepth1Value
                                    onChangeFunction = {
                                        val target = it.target as HTMLInputElement
                                        setState {
                                            state.searchDepth1Value = target.value
                                        }
                                    }
                                }
                            }
                        }
                        label {
                            +"Heurystyka: "
                            select {
                                attrs {
                                    value = state.heuristics1Value
                                    onChangeFunction = {
                                        val target = it.target as HTMLSelectElement
                                        setState {
                                            heuristics1Value = target.value
                                        }
                                    }
                                }
                                for (heuristicsOption in heuristicsOptions) {
                                    option {
                                        +heuristicsOption.value
                                        attrs {
                                            value = heuristicsOption.key
                                        }
                                    }
                                }
                            }
                        }
                    }

                    br {}

                    styledDiv {
                        label {
                            +"Gracz żółty: "
                            select {
                                attrs {
                                    value = state.player2Value
                                    onChangeFunction = {
                                        val target = it.target as HTMLSelectElement
                                        setState {
                                            player2Value = target.value
                                        }
                                    }
                                }
                                for (playerOption in playerOptions) {
                                    option {
                                        +playerOption.value
                                        attrs {
                                            value = playerOption.key
                                        }
                                    }
                                }
                            }
                        }
                        label {
                            +"Głębokośc przszukiwania: "
                            input {
                                attrs {
                                    type = InputType.number
                                    min = "1"
                                    max = "5"
                                    value = state.searchDepth2Value
                                    onChangeFunction = {
                                        val target = it.target as HTMLInputElement
                                        setState {
                                            state.searchDepth2Value = target.value
                                        }
                                    }
                                }
                            }
                        }
                        label {
                            +"Heurystyka: "
                            select {
                                attrs {
                                    value = state.heuristics2Value
                                    onChangeFunction = {
                                        val target = it.target as HTMLSelectElement
                                        setState {
                                            heuristics2Value = target.value
                                        }
                                    }
                                }
                                for (heuristicsOption in heuristicsOptions) {
                                    option {
                                        +heuristicsOption.value
                                        attrs {
                                            value = heuristicsOption.key
                                        }
                                    }
                                }
                            }
                        }
                    }

                    br {}

                    input {
                        attrs {
                            type = InputType.submit
                            value = "Rozpocznij grę"
                            onSubmitFunction = handleSubmit
                        }
                    }

                }
            }

            if (state.isGameInProgress || state.winner != 0) {
                if (state.winner != 0) {
                    h2 {
                        +"Wygrywa ${state.winner}"
                    }
                }

                styledDiv {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        justifyContent = JustifyContent.center
                    }
                    for ((columnId, column) in state.currentBoard.fields.columns().withIndex()) {
                        styledDiv {
                            css {
                                marginTop = 100.px
                                display = Display.flex
                                flexDirection = FlexDirection.column
                                if (state.currentBoard.isColumnAvailable(columnId)) {
                                    backgroundColor = Color.white
                                    cursor = Cursor.pointer
                                }
                            }
                            attrs {
                                onClickFunction = {
                                    handleColumnClick(columnId)
                                }
                            }

                            column.forEach { field ->
                                styledDiv {
                                    css {
                                        width = 100.px
                                        height = 100.px
                                        alignContent = Align.center
                                        backgroundImage = Image("url(connect4_field.png)")
                                        backgroundSize = "contain"
                                        if (field == 1) backgroundColor = Color.red
                                        if (field == 2) backgroundColor = Color.yellow
                                    }
                                    +field.toString()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}