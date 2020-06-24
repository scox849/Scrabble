package scrabble;

/**
 * Tile class contains methods and constructor for tile object.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class Tile {

    private String letter;
    private  int pointVal;
    private boolean inTray = false;

    /**
     * Tile constructor makes new tile.
     * @param letter tile letter
     * @param pointVal tile points
     */
    public Tile(String letter, int pointVal){
        this.letter = letter;
        this.pointVal = pointVal;
    }

    /**
     * Makes a copy of the tile.
     * @return tile copy
     */
    protected Tile copyTile(){

        return new Tile(this.letter, this.pointVal);
    }

    /**
     * Returns the letter on the tile.
     * @return tile letter
     */
    protected String getLetter(){

        return this.letter;
    }

    /**
     * Gets tile point value.
     * @return point value
     */
    protected int getPointVal(){

        return this.pointVal;
    }

    /**
     * Sets the letter on the tile. If the tile was a blank
     * the letter is set to upper cased. Also resets tile to blank when
     * needed.
     * @param letter letter for tile
     */
    protected void setLetter(String letter){
        if(this.letter.equals("*")){
            this.letter = letter.toUpperCase();
        }else{
            this.letter = letter;
        }

    }

    /**
     * Toggles whether or not the tile is in a tray.
     */
    protected void toggleInTray(){

        this.inTray = !this.inTray;
    }

}
