# connect-four-browser
Browswer implementation of Connect Four game written in Kontlin-React for artiffical intelligence classes

The player wins if they set 4 tokens in a row, column or diagonal of the board.

![gameplay](gameplay.gif)

You can play:
* player vs player,
* player vs AI,
* AI vs AI.

The AI player can be customized by choosing:
* algorithm - min-max or alpha-beta,
* heuristics - used for assessment of the situation on the board,
* search depth.

**Caution**: The app was ported from console version to browser version. Higher values of search-depth (eg. 4 or 5) can cause app to freeze, since heavy AI computation runs sequentially on browser amain thread.

