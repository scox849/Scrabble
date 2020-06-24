package scrabble;
import java.util.*;

/**
 * Tray class contains methods and constructor for tray object.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class Tray {

    private List<Tile> tray;

    /**
     * Tray constructor makes new tray.
     * @param tray list of tiles
     */
    public Tray(List<Tile> tray){
        this.tray = tray;
    }

    /**
     * Returns the tile from the tray with the given letter.
     * @param letter letter on tile
     * @return tile with letter
     */
    protected Tile getTile(String letter){
        for(Tile t: this.tray){
            if(t.getLetter().equals(letter)){
                return t;
            }
        }
        return null;
    }

    /**
     * Copies the tray to a new reference.
     * @return new tray
     */
    protected Tray copyTray(){
        List<Tile> copy = new LinkedList<>(this.tray);
        return new Tray(copy);
    }

    /**
     * Returns the list of tiles.
     * @return tray list
     */
    protected List<Tile> getTray(){
        return this.tray;
    }
}
