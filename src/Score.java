package scrabble;
import java.util.*;

/**
 * Score class contains methods and constructor for needed for scoring.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class Score {

    private List<BoardCell> wordToPlay;
    private List<BoardCell> highScoreWord;
    private int wordHighScore;
    private int currentScore = 0;
    private List<Direction> directions = new LinkedList<>();

    /**
     * Score constructor adds directions to a list.
     */
    public Score(){
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
    }

    /**
     * Sets the full word that is the highest scoring.
     * @param word new high score word
     */
    protected void setHighScoreWord(List<BoardCell> word){

        this.highScoreWord = word;
    }

    /**
     * Subtracts the given number of points from the current score.
     * @param points to subtract
     */
    protected void subtractFromScore(int points){
        this.currentScore -= points;
    }

    /**
     * Sets the actual tiles placed by the computer so that they can
     * be retrieved and placed later on.
     * @param tilesPlaced tiles that were placed
     */
    protected void setWordToPlay(List<BoardCell> tilesPlaced){

        this.wordToPlay = tilesPlaced;
    }

    /**
     * Returns the full word of the high score word.
     * @return high score word
     */
    protected List<BoardCell> getHighScoreWord(){

        return this.highScoreWord;
    }

    /**
     * Returns the tiles placed by the computer.
     * @return word to play
     */
    protected List<BoardCell> getWordToPlay(){

        return this.wordToPlay;
    }

    /**
     * Returns the value of the word high score.
     * @return word high score
     */
    protected int getWordHighScore(){

        return this.wordHighScore;
    }

    /**
     * Sets the value of the new high score.
     * @param score new high score
     */
    protected void setHighScore(int score){

        this.wordHighScore = score;
    }

    /**
     * Adds the given value to the player score.
     * @param score value to be added to current score.
     */
    protected void addToScore(int score){

        this.currentScore += score;
    }

    /**
     * Returns the players current score.
     * @return current score
     */
    protected int getCurrentScore(){

        return this.currentScore;
    }

    /**
     * Scores the word placed by the human.
     * @param word full word that was made
     * @param tray player tray
     * @param direction direction word was built in
     * @param board board
     * @param lettersPlaced actual tiles placed
     */
    protected int scoreWord(List<BoardCell> word, Tray tray,
                            Direction direction, Board board,
                            List<BoardCell> lettersPlaced){
        int wordScore = 0;
        int wordMulti = 1;
        int bingo = 50;
        for(BoardCell cell: word){
            int letterScore;
            letterScore = cell.getTile().getPointVal();
            letterScore *= cell.getLetterMultiplier();
            wordScore += letterScore;
            wordMulti *= cell.getWordMultiplier();
            board.getCell(cell.getXCell(),cell.getYCell()).setWordMultipier(1);
            board.getCell(cell.getXCell(),
                    cell.getYCell()).setLetterMultiplier(1);
        }
        wordScore *= wordMulti;
        if(tray.getTray().size() == 0){
            wordScore += bingo;
        }
        wordScore += addCrossWords(board, direction, lettersPlaced, word);
        return wordScore;
    }

    /**
     * Adds the value of the letters made from crosswords
     * @param board board
     * @param direction direction of main word being built
     * @param lettersPlaced letters placed by the player
     * @param fullWord full word made
     */
    protected int addCrossWords(Board board, Direction direction,
                                 List<BoardCell> lettersPlaced,
                                 List<BoardCell> fullWord){
        int crossScore = 0;
        boolean addedSameTile = false;
        Direction reverse = Direction.reverseDirection(direction);
        for(BoardCell letter: lettersPlaced){
            for(Direction d : this.directions){
                if(d != reverse && d != direction){
                    BoardCell next = board.getNextCell(letter, d);
                    if(next != null && next.hasTile() && !addedSameTile){
                        crossScore += letter.getTile().getPointVal() *
                                letter.getLetterMultiplier();
                        addedSameTile = true;
                    }
                    while(next != null &&
                            !fullWord.contains(next) && next.hasTile()){
                        crossScore += next.getTile().getPointVal();
                        next = board.getNextCell(next,d);
                    }
                }
            }
            addedSameTile = false;
        }
        return crossScore;
    }


}
