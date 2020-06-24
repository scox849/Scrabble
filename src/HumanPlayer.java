package scrabble;
import java.util.*;
public class HumanPlayer{


    private Tray tray;
    private Board board;
    private Trie tree;
    private Score score;
    private boolean firstTurn = true;
    private final static List<Direction> directions = new LinkedList<>();

    /**
     * Human player constructor makes new human player.
     * @param board reference to gameboard
     * @param tree reference to dictionary
     * @param tray reference to tray
     */
    public HumanPlayer(Board board, Trie tree, Tray tray){
        this.board = board;
        this.tree = tree;
        this.tray = tray;
        this.score = new Score();
        this.board.getBag().fillTray(this.tray);
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);

    }

    /**
     * Gets the humans current score.
     * @return humans score
     */
    protected int getCurrentScore(){

        return this.score.getCurrentScore();
    }

    /**
     * Checks if the word placed on the board was legal.
     * @param wordPlaced word placed
     * @return true or false for legal or illegal
     */
    protected boolean isLegal(List<BoardCell> wordPlaced){
        Direction buildDirection = orderWord(wordPlaced);
        if(buildDirection == null){
            return false;
        }
        StringBuilder word = new StringBuilder();
        BoardCell next = wordPlaced.get(0);
        BoardCell previous = next.copyCell();
        List<BoardCell> fullWord = new LinkedList<>();
        boolean anchored = false;
        if(firstTurn){
            if(wordPlaced.size() < 2){
                return false;
            }
            boolean atCenter = false;
            for(BoardCell cell: wordPlaced){
                if (cell.getXCell() == (this.board.getCols() - 1) / 2 &&
                        cell.getYCell() == (this.board.getRows() - 1) / 2) {
                    atCenter = true;
                    break;
                }
            }
            if(!atCenter){
                return false;
            }
        }
        if(board.getNextCell(next,
                Direction.reverseDirection(buildDirection)).hasTile()){
            while(next.hasTile()){
                previous = next.copyCell();
                next = board.getNextCell(next,
                        Direction.reverseDirection(buildDirection));
                if(next == null){
                    break;
                }

            }
        }
        for(BoardCell cell : wordPlaced){
            for(Direction d : directions){
                BoardCell nextCell = board.getNextCell(cell, d);
                if(nextCell != null &&
                        nextCell.hasTile() && !wordPlaced.contains(nextCell)){
                    anchored = true;
                }
            }
        }
        if(!firstTurn && !anchored){
            return false;
        }
        while(previous.hasTile()){
            word.append(previous.getTile().getLetter());
            fullWord.add(previous);
            previous = board.getNextCell(previous, buildDirection);
        }
        if(word.length() < 2){
            return false;
        }
        if(this.tree.search(word.toString())){
            for(BoardCell cell: wordPlaced){
                if(!crossCheck(this.board, cell, buildDirection)){
                    return false;
                }
            }
            if(firstTurn){
                firstTurn = false;
            }
            this.score.addToScore(this.score.scoreWord(fullWord, this.tray,
                    buildDirection, board, wordPlaced));
            return true;
        }
        return false;
    }

    /**
     * Returns the tray.
     * @return tray
     */
    protected Tray getTray(){

        return this.tray;
    }

    /**
     * Adds to the humans score.
     * @param points to add
     */
    protected void addToScore(int points){
        this.score.addToScore(points);
    }

    /**
     * Subtracts from the humans current score.
     * @param points to subtract
     */
    protected void subtract(int points){
        this.score.subtractFromScore(points);
    }

    /**
     * Puts a list of board cells in order to make a coherent word.
     * @param wordPlaced letters placed on the board
     * @return a direction in which the word was built.
     */
    private Direction orderWord(List<BoardCell> wordPlaced){
        boolean swapped = true;
        Direction buildDirection = null;
        if(wordPlaced.size() == 1){
            for(Direction direction: directions){
                BoardCell next = this.board.getNextCell(wordPlaced.get(0),
                        direction);
                if(next != null && next.hasTile()){
                    if(direction == Direction.LEFT || direction == Direction.UP){
                        direction = Direction.reverseDirection(direction);
                    }
                    return direction;
                }
            }
        }
        while(swapped){
            swapped = false;
            for(int i = 0; i < wordPlaced.size(); i++){
                if(i < wordPlaced.size() - 1){
                    BoardCell first = wordPlaced.get(i);
                    BoardCell second = wordPlaced.get(i + 1);
                    if(first.getXCell() > second.getXCell()){
                        swap(first, second, wordPlaced);
                        swapped = true;
                        buildDirection = Direction.RIGHT;
                    }else if(first.getXCell() == second.getXCell()){
                        buildDirection = Direction.DOWN;
                    }
                    if(first.getYCell() > second.getYCell()){
                        swap(first, second, wordPlaced);
                        swapped = true;
                        buildDirection = Direction.DOWN;
                    }else if(first.getYCell() == second.getYCell()){
                        buildDirection = Direction.RIGHT;
                    }
                }
            }
        }
        return buildDirection;
    }

    /**
     * Swaps the order of two boardcells
     * @param first first cell
     * @param second second cell
     * @param word list cells for word
     */
    private void swap(BoardCell first, BoardCell second, List<BoardCell> word){
        int indexFirst = word.indexOf(first);
        int indexSecond = word.indexOf(second);
        word.set(indexSecond, first);
        word.set(indexFirst, second);
    }

    /**
     * Crosschecks the letters placed on the board to make sure the
     * tile placed was legal.
     * @param board board
     * @param cell cell being checked
     * @param buildDirection direction that the word was being built in
     * @return true or false
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
}
