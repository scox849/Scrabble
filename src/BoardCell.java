package scrabble;

/**
 * BoardCell class contains methods and constructor for BoardCell object.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class BoardCell {

    private int wordMultipier;
    private int letterMultiplier;
    private Tile tileInCell;
    private final int x;
    private final int y;


    /**
     * BoardCell constructor makes new board cell.
     * with and x and y value sets everything else to
     * default.
     * @param x x coord
     * @param y y coord
     */
    public BoardCell(int x, int y){
        this.wordMultipier = 1;
        this.letterMultiplier = 1;
        this.tileInCell = null;
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the tile in the cell.
     * @param tile tile in cell
     */
    protected void setTileInCell(Tile tile){

        this.tileInCell = tile;
    }

    /**
     * Sets the word multiplier.
     * @param multipier word multiplier
     */
    protected void setWordMultipier(int multipier){

        this.wordMultipier = multipier;
    }

    /**
     * Copies the cell into a new cell.(Separate reference)
     * @return new BoardCell
     */
    protected BoardCell copyCell(){
        BoardCell cell = new BoardCell(this.x, this.y);
        cell.setWordMultipier(this.wordMultipier);
        cell.setLetterMultiplier(this.letterMultiplier);
        cell.setTileInCell(this.tileInCell);
        return cell;
    }

    /**
     * Sets the letter multiplier.
     * @param multiplier letter multiplier
     */
    protected void setLetterMultiplier(int multiplier){

        this.letterMultiplier = multiplier;
    }

    /**
     * Checks to see if the cell has a tile.
     * @return true or false
     */
    protected boolean hasTile(){

        return this.tileInCell != null;
    }

    /**
     * Gets the tile from the cell.
     * @return tile in cell
     */
    protected Tile getTile(){

        return this.tileInCell;
    }

    /**
     * Gets the word multiplier.
     * @return word multiplier
     */
    protected int getWordMultiplier(){

        return this.wordMultipier;
    }

    /**
     * Gets the letter multiplier.
     * @return letter multiplier
     */
    protected int getLetterMultiplier(){

        return this.letterMultiplier;
    }

    /**
     * Gets the x coordinate of the cell.
     * @return x
     */
    protected int getXCell(){

        return this.x;
    }

    /**
     * Gets the y coordinate of the cell.
     * @return y
     */
    protected int getYCell(){

        return this.y;
    }
}
