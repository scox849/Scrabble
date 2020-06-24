package scrabble;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Solver class contains methods and constructor for solver object.
 * Also contains main method for solving boards from standard in.
 */
public class Solver {

    private Trie tree;
    private Score score;
    private List<Direction> directions = new LinkedList<>();
    private int emptyTiles = 1;
    private List<BoardCell> boardAnchors;
    private Board copyOfOriginal;

    /**
     * Solver constructor makes a solver with the given tree and
     * a new score. Also adds each direction to a list.
     * @param tree dictionary
     */
    public Solver(Trie tree){
        this.tree = tree;
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        this.score = new Score();
    }

    /**
     * Sets the copy of the original board.
     * @param board copy of original board
     */
    protected void setBoardCopy(Board board){
        this.copyOfOriginal = board;
    }

    /**
     * Returns the score object for the solver.
     * @return score
     */
    protected Score getScore(){
        return this.score;
    }

    /**
     * Sets the list of anchor points for each board.
     * @param anchors anchors on board
     */
    protected void setBoardAnchors(List<BoardCell> anchors){
        this.boardAnchors = anchors;
    }

    /**
     * Returns the score objects current total score.
     * @return current score
     */
    protected int getCurrentScore(){
        return this.score.getCurrentScore();
    }

    /**
     * Reads in a board from standard in.
     * @param scanner input reader
     * @param size size of board
     * @return new Board object
     * @throws IOException for scanner
     */
    private static Board readInBoard(Scanner scanner, int size)
            throws IOException {

        StringBuilder boardString = new StringBuilder();
        StringBuilder line = new StringBuilder();

        for(int i = 0; i < size+1; i++){
            line.replace(0,line.length(),scanner.nextLine());
            boardString.append(line);
            boardString.append("\n");
        }
        return new Board(size,size,boardString);

    }

    /**
     * Finds each anchor point on a given board.
     * @param board board to check
     * @return list of anchors
     */
    protected List<BoardCell> findAnchors(Board board){
        List<BoardCell> anchorCells = new LinkedList<>();
        for(BoardCell cell : board.getBoard()){
            if(cell.hasTile()){
                for(Direction d: directions){
                    BoardCell next = board.getNextCell(cell, d);
                    if(next != null && !next.hasTile()){
                        anchorCells.add(next);
                    }
                }
            }
        }
        return anchorCells;
    }

    /**
     * Builds the computers tray from a string and the board.
     * @param tray string of letters
     * @param board current board
     * @return new Tray object
     */
    private Tray buildTray(String tray, Board board){
        List<Tile> trayList = new LinkedList<>();
        for(int i = 0; i < tray.length(); i++){
            Tile tile = board.getBag().getTile(tray.charAt(i));
            tile.toggleInTray();
            board.getBag().removeTile(tile);
            trayList.add(tile);
        }
        return new Tray(trayList);
    }

    /**
     * Solves the given scrabble board for the highest scoring word.
     * @param board board to be solved
     * @param tray computers tray of letters
     * @return list of the letters to place.
     * @throws IOException because of copy board
     */
    protected List<BoardCell> solve(Board board,
                                    Tray tray) throws IOException {

        Board copy = board.copyBoard(board.toString());
        Direction direction;
        List<BoardCell> tilesPlaced = null;
        for(BoardCell cell: this.boardAnchors){
            StringBuilder partialWord = new StringBuilder();
            List<BoardCell> posWord = new LinkedList<>();
            tilesPlaced = new LinkedList<>();
            BoardCell cellCopy = cell.copyCell();
            direction = Direction.UP;
            buildLeft(cellCopy, copy, partialWord, posWord, direction, tray,
                    tilesPlaced);
            direction = Direction.LEFT;
            posWord.clear();
            tilesPlaced.clear();
            if(partialWord.length() > 0){
                partialWord.delete(0, partialWord.length());
            }
            cellCopy = cell.copyCell();
            buildLeft(cellCopy, copy, partialWord, posWord, direction, tray,
                    tilesPlaced);
        }
        if(tilesPlaced != null && score.getWordToPlay() != null){
            for(BoardCell placed: score.getWordToPlay()){
                if(!placed.getTile().getLetter().equals(placed
                        .getTile().getLetter().toLowerCase())){
                    tray.getTray().remove(tray.getTile("*"));
                }else{
                    tray.getTray().remove(placed.getTile());
                }

            }
        }else{
            return null;
        }

        return this.score.getWordToPlay();
    }


    /**
     * Checks if the letter in the given position is legal.
     * @param board board being solved
     * @param cell cell with tile being placed
     * @param buildDirection direction the word is being built in
     * @return true or false for pass or fail
     */
    private boolean crossCheck(Board board, BoardCell cell,
                              Direction buildDirection){
        Direction reverse = Direction.reverseDirection(buildDirection);
        StringBuilder word = new StringBuilder();
        word.append(cell.getTile().getLetter());
        boolean hasWord = false;
        for(Direction d : directions){
            if(d != reverse && d != buildDirection){
                BoardCell next = board.getNextCell(cell, d);
                while(next != null && next.hasTile()){
                    hasWord = true;
                    if(d == Direction.UP || d == Direction.LEFT){
                        word.insert(0, next.getTile().getLetter());
                    }else{
                        word.append(next.getTile().getLetter());
                    }
                    next = board.getNextCell(next,d);
                }
            }
        }
        if(hasWord){
            return this.tree.search(word.toString());
        }
        return true;

    }

    /**
     * Loops to the last cell with a tile before and empty or null cell.
     * @param oldCell original cell
     * @param nextCell next cell in the given direction
     * @param board board being solved
     * @param direction direction word is being build
     * @return Last cell with a tile
     */
    private BoardCell loopToNextEmpty(BoardCell oldCell, BoardCell nextCell,
                                 Board board, Direction direction){
        while (nextCell.hasTile()){
            oldCell = nextCell;
            nextCell = board.getNextCell(nextCell, direction);
            if(nextCell == null){
                break;
            }
        }
        return oldCell;
    }

    /**
     * Builds to the furthest possible left position based on the number of
     * empty and filled cells in a given row or column.
     * @param cell anchor
     * @param board board being solved
     * @param partialWord string being built to check if its in the dictionary
     * @param posWord cells of each letter of the full word that was found
     * @param direction direction to search, up or down
     * @param tray tray of tiles for computer
     * @param tilesPlaced actual tiles that the computer places
     */
    private void buildLeft(BoardCell cell, Board board,
                           StringBuilder partialWord, List<BoardCell> posWord,
                           Direction direction, Tray tray,
                           List<BoardCell> tilesPlaced) {

        this.emptyTiles = 1;
        int maxLength = 7;
        BoardCell next = board.getNextCell(cell,direction);
        if(next == null || !next.hasTile()){
            if(next != null){
                if(this.emptyTiles != maxLength){
                    this.emptyTiles++;
                    buildLeft(next, board, partialWord, posWord, direction,
                            tray, tilesPlaced);
                }else{
                    this.emptyTiles = 1;
                }
            }
            buildRight(cell, partialWord,
                    Direction.reverseDirection(direction),
                    board,posWord, tray, tilesPlaced);
        }else{
            this.emptyTiles = 1;
            BoardCell furthestLeft = loopToNextEmpty(cell, next,
                    board, direction);
            buildRight(furthestLeft, partialWord,
                    Direction.reverseDirection(direction),
                    board,posWord, tray, tilesPlaced);

        }
    }

    /**
     * Checks if the word that was built is anchored.
     * @param posWord word to check
     * @return true or false
     */
    private boolean anchored(List<BoardCell> posWord){
        for(BoardCell anchor: this.boardAnchors){
            for(BoardCell letter: posWord){
                if(letter.getXCell() == anchor.getXCell() &&
                   letter.getYCell() == anchor.getYCell()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks the score of the new word and if it is higher than the previous
     * word found it replaces it as the high score word.
     * @param posWord word that was built
     * @param tray tray of tiles
     * @param direction direction used to get the word score
     * @param tilesPlaced actual tiles placed by computer
     */
    private void checkScore(List<BoardCell> posWord, Tray tray,
                            Direction direction,
                            List<BoardCell> tilesPlaced){
        int wordScore = this.score.scoreWord(posWord, tray,
                direction, copyOfOriginal, tilesPlaced);
        if(wordScore > this.score.getWordHighScore()){
            List<BoardCell> newWord = new LinkedList<>(posWord);
            List<BoardCell> newTiles = new LinkedList<>(tilesPlaced);
            this.score.setHighScore(wordScore);
            this.score.setHighScoreWord(newWord);
            this.score.setWordToPlay(newTiles);
        }else if(wordScore == this.score.getWordHighScore()){
            StringBuilder newWord = new StringBuilder();
            StringBuilder currentWord = new StringBuilder();
            for(BoardCell cell: posWord){
                newWord.append(cell.getTile().getLetter());
            }
            for(BoardCell cell: this.score.getHighScoreWord()){
                currentWord.append(cell.getTile().getLetter());
            }
            if(newWord.toString().compareTo(currentWord.toString()) < 0){
                List<BoardCell> nextWord = new LinkedList<>(posWord);
                List<BoardCell> newTiles = new LinkedList<>(tilesPlaced);
                this.score.setHighScore(wordScore);
                this.score.setHighScoreWord(nextWord);
                this.score.setWordToPlay(newTiles);
            }
        }
    }

    /**
     * Builds the word starting from the left or up most tile and uses
     * recursive backtracking to build words based on they're existence
     * in the dictionary
     * @param cell Cell being built from
     * @param partialWord string builder being checked against dictionary
     * @param direction direction word is being built in, right or down
     * @param board board being solved
     * @param posWord each cell of the full word being checked
     * @param tray tray of tiles
     * @param tilesPlaced actual tiles that are placed and not already on the
     *                    board
     */
    private void buildRight(BoardCell cell, StringBuilder partialWord,
                            Direction direction, Board board,
                            List<BoardCell> posWord, Tray tray,
                            List<BoardCell> tilesPlaced) {
        if(!cell.hasTile()){
            for(Tile tile : tray.getTray()){
                Tray trayCopy = tray.copyTray();
                BoardCell cellCopy = cell.copyCell();
                if(tile.getLetter().equals("*")){
                    Set<Character> posNextLetters = this.tree.
                            getNodeKeys(partialWord.toString());
                    for(Character c: posNextLetters){
                        BoardCell megaCopy = cellCopy.copyCell();
                        Tile tileCopy = tile.copyTile();
                        tileCopy.setLetter(String.valueOf(c));
                        megaCopy.setTileInCell(tileCopy);
                        partialWord.append(tileCopy.getLetter());
                        posWord.add(megaCopy);
                        tilesPlaced.add(megaCopy);
                        BoardCell next = board.getNextCell(megaCopy, direction);
                        if((next == null || !next.hasTile()) &&
                        this.anchored(posWord) &&
                                this.tree.search(partialWord.toString())
                         && crossCheck(board, megaCopy, direction)){
                            trayCopy.getTray().remove(tile);
                            this.checkScore(posWord, trayCopy, direction,
                                    tilesPlaced);
                            trayCopy.getTray().add(tile);
                        }
                        if(this.tree.searchPartial(partialWord.toString()) &&
                                crossCheck(board, megaCopy, direction)){

                            tile.setLetter(String.valueOf(c));
                            trayCopy.getTray().remove(tile);

                            if(next != null){
                                buildRight(next, partialWord, direction, board,
                                        posWord, trayCopy, tilesPlaced);
                            }
                            tile.setLetter("*");
                        }
                        posWord.remove(megaCopy);
                        tilesPlaced.remove(megaCopy);
                        partialWord.deleteCharAt(partialWord.length() - 1);
                    }
                }else {
                    cellCopy.setTileInCell(tile);
                    partialWord.append(tile.getLetter());
                    posWord.add(cellCopy);
                    tilesPlaced.add(cellCopy);
                    BoardCell next = board.getNextCell(cellCopy, direction);
                    if ((next == null || !next.hasTile()) &&
                            this.anchored(posWord)
                            && this.tree.search(partialWord.toString()) &&
                            crossCheck(board, cellCopy, direction)) {
                        trayCopy.getTray().remove(tile);
                        this.checkScore(posWord, trayCopy, direction,
                                tilesPlaced);
                        trayCopy.getTray().add(tile);
                    }
                    if (this.tree.searchPartial(partialWord.toString()) &&
                            crossCheck(board, cellCopy, direction)) {
                        if (next != null) {
                            trayCopy.getTray().remove(tile);
                            buildRight(next, partialWord, direction, board,
                                    posWord, trayCopy, tilesPlaced);
                        }
                    }
                    posWord.remove(cellCopy);
                    tilesPlaced.remove(cellCopy);
                    partialWord.deleteCharAt(partialWord.length() - 1);
                }
            }

        }else{
            partialWord.append(cell.getTile().getLetter());
            posWord.add(cell);
            BoardCell next = board.getNextCell(cell, direction);
            if(next == null || !next.hasTile()){
                if(this.anchored(posWord) &&
                        this.tree.search(partialWord.toString())){
                    this.checkScore(posWord, tray, direction, tilesPlaced);
                }
            }
            if(this.tree.searchPartial(partialWord.toString())){
                if(next != null){
                    buildRight(next, partialWord, direction, board, posWord,
                            tray, tilesPlaced);
                }
            }
            posWord.remove(cell);
            partialWord.deleteCharAt(partialWord.length() - 1);
        }
    }

    /**
     * Places the tiles of the highest scoring word on the board.
     * @param board board that was solved
     */
    protected void placeWord(Board board){
        if(this.score.getWordToPlay() != null){
            BoardCell cellToPlay;
            for(BoardCell cell: this.score.getWordToPlay()){
                cellToPlay = board.getCell(cell.getXCell(), cell.getYCell());
                cellToPlay.setTileInCell(cell.getTile());
                cellToPlay.setLetterMultiplier(cell.getLetterMultiplier());
                cellToPlay.setWordMultipier(cell.getWordMultiplier());
            }
        }
    }

    /**
     * Returns the solution as a string.
     * @return solution
     */
    private String solutionToString(){
        StringBuilder word = new StringBuilder();
        for(BoardCell c: this.score.getHighScoreWord()){
            word.append(c.getTile().getLetter());
        }
        return word.toString();
    }

    /**
     * Gets the value of the high score.
     * @return high score
     */
    private int getHighScore(){

        return this.score.getWordHighScore();
    }

    /**
     * Main starts program builds a dictionary and reads in boards to be solved.
     * @param args command line arguments
     * @throws IOException because of scanners
     */
    public static void main(String[] args) throws IOException {
        String file;
        if(args.length > 0){
            file = args[0];
        }else{
            System.out.println("Need input file!");
            return;
        }
        Trie tree = new Trie(file);
        InputStream textBoard = System.in;
        assert textBoard != null;
        Scanner scanner = new Scanner(textBoard);

        while(scanner.hasNextLine()){
            int size = scanner.nextInt();
            Board board = readInBoard(scanner,size);
            String tray = scanner.nextLine();
            Solver solver = new Solver(tree);
            solver.setBoardCopy(board.copyBoard(board.toString()));
            solver.setBoardAnchors(solver.findAnchors(board));
            Tray rack = solver.buildTray(tray, board);
            solver.solve(board, rack);
            System.out.println("Input Board:");
            System.out.print(board.toString());
            System.out.println("Tray: " + tray);
            solver.placeWord(board);
            System.out.println("Solution " + solver.solutionToString() +
                    " has " + solver.getHighScore() + " points");
            System.out.println("Solution Board:");
            System.out.print(board.toString());
        }
    }

}
