package scrabble;
import java.io.IOException;
import java.util.*;

/**
 * Board class contains methods and constructor for board object.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class Board {

    private int rows;
    private int cols;
    private List<BoardCell> gameBoard;
    private StringBuilder textBoard;
    private TilesBag bag;


    /**
     * Board constructor makes a new board.
     * @param rows number of rows
     * @param cols number of cols
     * @param textBoard string to base the board off
     * @throws IOException because string is read from input
     */
    public Board(int rows, int cols, StringBuilder textBoard)
            throws IOException{
        this.gameBoard = new LinkedList<>();
        this.rows = rows;
        this.cols = cols;
        this.textBoard = textBoard;
        this.bag = new TilesBag();
        makeBoard();

    }

    /**
     * Returns number of rows
     * @return number of rows
     */
    protected int getRows(){

        return this.rows;
    }

    /**
     * Returns number of cols
     * @return number of cols
     */
    protected int getCols(){

        return this.cols;
    }

    /**
     * Returns the size of the tiles bag.
     * @return bag size
     */
    protected int getBagSize(){

        return this.bag.getBagSize();
    }

    /**
     * Builds a board based on a string given from input.
     */
    private void makeBoard(){
        int wordMulti = 0;
        int letterMulti = 1;
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++){
                BoardCell boardCell = new BoardCell(j,i);
                this.gameBoard.add(boardCell);
            }
        }
        Scanner scanner = new Scanner(this.textBoard.toString());
        for(BoardCell cell : this.gameBoard){
            String cellValue = scanner.next();
            if(!cellValue.equals("..")){
                if(cellValue.length() > 1){
                    if(cellValue.charAt(wordMulti) != '.'){
                        int num = Integer.parseInt(
                                String.valueOf(cellValue.charAt(wordMulti)));
                        cell.setWordMultipier(num);
                    }else if(cellValue.charAt(letterMulti) != '.'){
                        int num = Integer.parseInt(
                                String.valueOf(cellValue.charAt(letterMulti)));
                        cell.setLetterMultiplier(num);
                    }

                }else{
                    int letterIndex = 0;
                    int noMulti = 1;
                    Tile tile = this.bag.getTile(cellValue.charAt(letterIndex));
                    cell.setTileInCell(tile);
                    cell.setLetterMultiplier(noMulti);
                    cell.setWordMultipier(noMulti);
                }
            }
        }
    }

    /**
     * Gets the next cell in a given direction
     * @param cell current cell
     * @param direction direction to look in
     * @return next cell
     */
    protected BoardCell getNextCell(BoardCell cell, Direction direction){
        int x = cell.getXCell();
        int y = cell.getYCell();
        switch (direction){
            case UP:
                y -= 1;
                break;
            case LEFT:
                x -= 1;
                break;
            case DOWN:
                y += 1;
                break;
            case RIGHT:
                x += 1;
                break;
            default:
                break;
        }
        if(x < this.cols && y < this.rows){
            return this.getCell(x,y);
        }else{
            return null;
        }

    }

    /**
     * Returns a copy of the board(not the same reference)
     * @param board string of the board being copied
     * @return new board
     * @throws IOException because board is read from input
     */
    protected Board copyBoard(String board) throws IOException {
        StringBuilder boardBuild = new StringBuilder(board);
        int rows = this.rows;
        int cols = this.cols;

        return new Board(rows,cols,boardBuild);
    }

    /**
     * Gets the cell from the given x and y coordinate.
     * @param x coord
     * @param y coord
     * @return cell at x y
     */
    protected BoardCell getCell(int x, int y){
        for(BoardCell cell : this.gameBoard){
            if(cell.getXCell() == x && cell.getYCell() == y){
                return cell;
            }
        }
        return null;
    }

    /**
     * Returns the board
     * @return list of board cells
     */
    protected List<BoardCell> getBoard(){

        return this.gameBoard;
    }

    /**
     * Gets the bag of tiles for the game.
     * @return tiles bag
     */
    protected TilesBag getBag(){

        return this.bag;
    }

    /**
     * Returns a string representation of the board.
     * @return string
     */
    @Override
    public String toString(){

        StringBuilder board = new StringBuilder();
        for(BoardCell cell : this.gameBoard){

            if(cell.hasTile()){
                board.append(" ").append(cell.getTile().getLetter());
            }else{
                if(cell.getWordMultiplier() == 1){
                    board.append(".");
                }else{
                    board.append(cell.getWordMultiplier());
                }
                if(cell.getLetterMultiplier() == 1){
                    board.append(".");
                }else{
                    board.append(cell.getLetterMultiplier());
                }
            }
            if(cell.getXCell() == this.cols - 1){
                board.append("\n");
            }else{
                board.append(" ");
            }

        }
        return board.toString();
    }
}
