# Grid Games

Frame work and simple grid based games and writing AIs for those games

## Flood it

Toggle between colors to convert the entire board to a single color

### Max Area AI

Each turn tries to convert the most number of cells

### Max Perimeter AI

Tries to maximize the perimeter. This gets in an infinite 
loop because it favors a fragmented board.

## Lights out

When you every click toggles the block and the surrounding blocks

STATUS: Brute force AI

## Minesweeper

It's minesweeper! try to not click a bomb

## Pop it

"pop" a cell and remaining cells will drop down and to the left.
You can only pop cells of 2 or more.

### First Path Non Recursive

Returns the first valid path it can find. It favors moves that maximize possible
future moves. The non recursive bit was me playing around with performance.

https://youtu.be/4NlQJ1bQlIc

### Recursive Board Heuristic

Returns the first valid path it can find. It favors moves that maximize possible
future moves. 

The board heuristic could use some tweaking, to instead favor higher scoring moves.

https://youtu.be/bRS_RnodlTI

### Recursive First Path

Does not favor any moves and just tries any possibility it can

https://youtu.be/gAUn8hRk9hc

### Exhaustive AIs

The goal of these AIs is to determine the best possible game. Each version
takes a stab at doing this in a reasonable time.

#### Exhaustive Recursive AI

This one is pretty bad, it saves all the possible states and then picks the best one.

This has never successfully solved a full board.

#### Exhaustive Iterative AI

This does a depth first search only saving the best resulting game. It will discard any
moves that don't, or can't beat the current best game.

On a full board, this took about 3 hours.

#### Exhaustive Threaded AI

This is an attempt to thread a depth first search that discards results if they cannot beat
the best possible game. This has a performance boost for small games, but on a full board,
the iterative AI still has better performance.

Tests on a full board were stopped after 3 hours.
