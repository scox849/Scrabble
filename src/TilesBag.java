package scrabble;
import java.io.*;
import java.util.*;

/**
 * TilesBag class contains methods and constructor
 * to created a bag of tiles with the number of tiles
 * and the point value associated with each gathered from
 * a text file.
 * @author Sam Cox
 * @version date 2/20/20
 */
public class TilesBag {

    private List<Tile> bag;

    /**
     * TilesBag constructor initializes bag as a linked list
     * and fills the bag with the proper tiles.
     * @throws IOException if text file is null
     */
    public TilesBag() throws IOException {
        bag = new LinkedList<>();
        readFile();
    }

    /**
     * Takes a letter and point value and puts the number of tiles
     * with that letter and point value into the bag.
     * @param letter letter on tile
     * @param frequency number of that tile in the bag
     * @param points number of points for tile
     */
    private void fillBag(String letter, int frequency ,int points){
        for(int i = 0; i < frequency; i++){
            bag.add(new Tile(letter,points));
        }
    }

    /**
     * Returns the size of the bag.
     * @return size of bag
     */
    protected int getBagSize(){
        return this.bag.size();
    }

    /**
     * Removes a tile from the bad.
     * @param tile tile to be removed.
     */
    protected void removeTile(Tile tile){
        this.bag.remove(tile);
    }

    /**
     * Fills the tray up to 7 tiles.
     * @param tray tray to be filled.
     */
    protected void fillTray(Tray tray){
        Random randGenerator = new Random();
        int randIndex;
        int bagSize = this.bag.size();
        int trayMaxSize = 7;
        int currentTraySize = tray.getTray().size();
        int limit = Math.min(bagSize, trayMaxSize);

        for(int i = currentTraySize; i < limit; i++){
            randIndex = randGenerator.nextInt(this.bag.size());
            Tile tile = this.bag.get(randIndex);
            tray.getTray().add(tile);
            this.bag.remove(tile);
        }
    }


    /**
     * Returns the number tiles needed from an exchange.
     * @param numNeeded number of tiles needed.
     * @return list of tiles.
     */
    protected List<Tile> takeFromBag(int numNeeded){
        List<Tile> tiles = new LinkedList<>();
        for(int i = 0; i < numNeeded; i++){
            Random randIndex = new Random();
            Tile newTile = this.bag.get(randIndex.nextInt(this.bag.size()));
            tiles.add(newTile);
            this.bag.remove(newTile);
        }
        return tiles;
    }

    /**
     * Adds the tiles the user wanted to exchange back
     * into the bag.
     * @param tiles unwanted tiles
     */
    protected void addToBag(List<Tile> tiles){
        this.bag.addAll(tiles);
    }

    /**
     * Reads in the file to get the correct values for tiles.
     * @throws IOException if file cannot be found
     */
    private void readFile() throws IOException {
        String file = "letterVals.txt";
        InputStream tileVals = getClass().getClassLoader().
                getResourceAsStream(file);
        assert tileVals != null;
        InputStreamReader reader = new InputStreamReader(tileVals);
        BufferedReader lineReader = new BufferedReader(reader);
        String line;
        while((line = lineReader.readLine()) != null){
            this.makeTileVals(line);
        }
        lineReader.close();
    }

    /**
     * Prints each tiles letter and point value.
     * Used for debugging.
     */
    private void printTiles(){
        for(Tile t : this.bag){
            System.out.println("Letter = " + t.getLetter()
                    + " Points = " + t.getPointVal());
        }
    }

    /**
     * Gets the a tile from the bad associated the given letter.
     * If the letter on the board is and uppercase letter it
     * removes a blank from the bag.
     * @param letter of tile to be returned
     * @return tile with letter on it.
     */
    protected Tile getTile(char letter){
        if(Character.getType(letter) == Character.UPPERCASE_LETTER){
            for(Tile t : this.bag){
                if(t.getLetter().equals("*")){
                    t.setLetter(String.valueOf(letter));
                    this.bag.remove(t);
                    return t;
                }
            }
        }else{
            for(Tile t : this.bag){
                if(t.getLetter().equals(String.valueOf(letter))){
                    this.bag.remove(t);
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Gets each the value of letter points and frequency from a line of
     * the text file.
     * @param line line from file
     */
    private void makeTileVals(String line){
        String letter = "";
        String frequency = "";
        String points = "";
        int numSpaces = 0;
        for(int i = 0; i < line.length(); i++){
            if(line.charAt(i) == ' '){
                numSpaces++;
                continue;
            }
            if(numSpaces == 0){
                letter = letter.concat(String.valueOf(line.charAt(i)));
            }else if(numSpaces == 1){
                frequency = frequency.concat(String.valueOf(line.charAt(i)));
            }else if(numSpaces == 2){
                points = points.concat(String.valueOf(line.charAt(i)));
            }
            if(i == line.length() - 1){
                int tileFrequency = Integer.parseInt(frequency);
                int tilePoints = Integer.parseInt(points);
                fillBag(letter,tileFrequency,tilePoints);
                letter = "";
                frequency = "";
                points = "";
                numSpaces = 0;
            }
        }
    }

}
