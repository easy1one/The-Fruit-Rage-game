# [AI]The-Fruit-Rage-game
 A game agent implemented by the minimax algorithm with alpha-beta pruning to win the Fruit Range game that captures the nature of a zero sum two player game with strict limitation on allocated time

# Highlevel Description
The Fruit Rage is a two player game in which each player tries to maximize his/her share from a batch of fruits randomly placed in a box. The box is divided into cells and each cell is either empty or filled with one fruit of a specific type.
<br />
At the beginning of each game, all cells are filled with fruits. Players play in turn and can pick a cell of the box in their own turn and claim all fruit of the same type, in all cells that are connected to the selected cell through horizontal and vertical paths. For each selection or move the agent is rewarded a numeric value which is the square of the number of fruits claimed in that move. Once an agent picks the fruits from the cells, their empty place will be filled with other fruits on top of them (which fall down due to gravity), if any. In this game, no fruit is added during game play. Hence, players play until all fruits have been claimed.
<br />
Another big constraint of this game is that every agent has a limited amount of time to spend for thinking during the whole game. Spending more than the original allocated time will be penalized harshly.
<br />
The overall score of each player is the sum of rewards gained for every turn. The game will terminate when there is no fruit left in the box or when a player has run out of time.

# Code
## input.txt
> __First line:__ integer n, the width and height of the square board (0 < n <= 26)
> __Second line:__ integer p, the number of fruit types (0 < p <= 9)
> __Third line:__ strictly positive floating point number, your remaining time in seconds
> __Next n lines:__ the n x n board, with one board row per input file line, and n characters (plus end- of-line marker) on each line. Each character can be either a digit from 0 to p-1, or a * to denote an empty cell. Note: for ease of parsing, the extra horizontal and vertical lines shown in figures 1 – 5 will not be present in the actual input.txt (see below for examples).

## fruitGame.java
- My agent should implement the minimax algorithm with alpha-beta pruning. 
```
public static Position decideNextMove(int N, int P, char[][] board)
public static Stack<Position> makeChildrenStack(Node n, int N)
public static char[][] updateBoard_star(char[][] board,int N)
```

## output.txt
> __First line:__ your selected move, represented as two characters:
  * A letter from A to Z representing the column number (where A is the leftmost column, B is the next one to the right, etc), and
  * A number from 1 to 26 representing the row number (where 1 is the top row, 2 is the row below it, etc).
> __Next n lines:__ the n x n board just after your move and after gravity has been applied to make any fruits fall into holes created by your move taking away some fruits (like in figure 3).


