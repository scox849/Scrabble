#Scrabble


##Introduction
This program has two different versions. The solver that finds the highest
possible scoring word for a given board. And a full scrabble game. The full
game utilizes the solver for the computer player. So, it's hard to beat!
There are 12 classes, 2 procedures and 11 objects. And there is one enum.

Objects: Board, BoardCell, ComputerPlayer, HumanPlayer, Solver, Tile, TilesBag,
Tray, Trie, and TrieNode.

Procedures: Game, Solver.

Enum: Direction.

The Game procedure plays a full dominoes game with GUI. The Solver procedure
solver boards from standard input. The ComputerPlayer uses an instance of Solver
to solver the Game board. 

##Usage
To run the solver on the command line use the command

java -jar solver.jar dictionary.txt < input.txt > output.txt

I have all of the dictionaries from the discussion board included in the jar
so you can replace dictionary.txt with any of the dictionaries form there.
input.txt can be whatever input file you want be it needs to be formatted the same
way as example_input.txt. i.e.: Board size on top, string representing board next,
string representing tray on bottom.

To run the full game use

java -jar game.jar dictionary.txt

This will run the full game with whatever dictionary you chose. I recommend
sowpods.txt.

###GUI controls
To place a tile on the board click a tile from your tray and then click
the spot on the board that you want to place it. Do this for each of the tiles
you wish to place, then click play. If the word you placed is a legal move 
the word will remain on the board and your tray will refill. If it is not legal
the letter you placed will return to you tray and you can try again.

THE HUMAN MAKES THE FIRST MOVE. MAKE SURE ONE OF YOUR TILES GOES THROUGH
THE CENTER SQUARE.

After the first move has been made the computer can now takes its turn.

If you wish to skip a turn simply click pass.

If you wish to use your turn to exchange some tiles click exchange, the 
all of the tiles you wish to exchange, the click exchange again.

If you are presented with a blank tile and want to place it, click on the tile
pressed the letter on the keyboard you with the tile to represent, then click
the location on the board you wish to place it. After you place all of your
tiles for that move, if the move you made was legal the tile will remain there
with a green letter. If the move is not legal the blank will be returned to your
tray and can be reassigned. 

##Project Assumptions
This project assumes that input from standard in is correct for the solver.
And for the GUI, if you press a button, it assumes you wanted to press that
button, there is no going back from a button press.

The tiles bag is made from a configuration file called letterVals.txt.
Each board is given a bag with 100 tiles for both the game and solver to
pull from. This means that trying to input a tray or board with a non-standard
number of certain tiles i.e. 3 blanks instead of two will cause the program
to crash, unless you go into the letterVals.txt file and adjust the number
of tiles that letter has.

The file is formatted letter, number of tiles it has, points it has. ex:

* 2 0

If you wanted to have three blanks instead of two you would just have to change
that line...

* 3 0

THE HUMAN MAKES THE FIRST MOVE. MAKE SURE ONE OF YOUR TILES GOES THROUGH
THE CENTER SQUARE.
Sorry for yelling...

##Design Choice
I chose to use a retrieval tree or trie to hold the words for the dictionary.
I used a resource I found online which is cited below. I included what I found
and I added a few other things that were necessary for solving the boards.

I used a recursive back tracking algorithm to solve each board. It first gets
the furthest left or up board cell it can build from, then for each tile
in the tray it places the tile at the cell moves to the next cell and places
another tile. If the word that is being made is not in the dictionary it returns
and uses another tile. That is to say if the word being made starts with "q"
and then the next tile has a "p" the word would now be "qp" and it would return
because there are no words that start with "qp". If a full word is made
it checks what score that word has and if it is higher that the previous
high score it makes that word the new high score word. If the words are tied
it takes the word that is ahead alphabetically.

##Versions
There are two versions, the solver and the full game.

##Docs
The design document is located in the docs directory as a pdf.

##Testing and Debugging
I used many many many print statements to debug this program.

##Known Bugs
The solver isn't very fast when it comes to solving boards with blanks.
Especially when there is more than one blank.

##Sources
Used this to help me figure out how to build the Trie:
https://medium.com/@amogh.avadhani/how-to-build-a-trie-tree-in-java-9d144aaa0d01