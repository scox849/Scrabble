package scrabble;


import java.io.IOException;
import java.util.List;

/**
 * ComputerPlayer class has methods and constructor for computer player.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class ComputerPlayer{


    private Solver solver;
    private Board board;
    private Tray tray;

    /**
     * Computer Player constructor makes a new computer player.
     * Initializes reference to a new solver to make moves.
     * @param tree reference to dictionary
     * @param board reference to board
     * @param tray reference to tray
     */
    public ComputerPlayer(Trie tree, Board board, Tray tray){
        this.solver = new Solver(tree);
        this.board = board;
        this.tray = tray;
        this.board.getBag().fillTray(this.tray);

    }

    /**
     * Gets the computers current score.
     * @return current score
     */
    protected int getCurrentScore(){

        return solver.getCurrentScore();
    }

    /**
     * Returns the computers tray.
     * @return tray
     */
    protected Tray getTray(){
        return this.tray;
    }

    /**
     * Subtracts from the computers current score.
     * @param points to subtract
     */
    protected void subtract(int points){
        this.solver.getScore().subtractFromScore(points);
    }

    /**
     * Adds to the humans score.
     * @param points to add
     */
    protected void addToScore(int points){
        this.solver.getScore().addToScore(points);
    }

    /**
     * Calls the solver to make the move for the computer.
     * @return List of cells to be replaced on the board
     * @throws IOException because of input
     */
    public List<BoardCell> makeMove() throws IOException {
        this.solver.getScore().setHighScore(0);
        this.solver.getScore().setHighScoreWord(null);
        this.solver.getScore().setWordToPlay(null);
        solver.setBoardAnchors(solver.findAnchors(this.board));
        solver.setBoardCopy(this.board.copyBoard(this.board.toString()));
        solver.solve(this.board, this.tray);
        this.solver.getScore().addToScore(solver.getScore().getWordHighScore());
        if(solver.getScore().getWordToPlay() != null){
            this.board.getBag().fillTray(this.tray);
            return solver.getScore().getWordToPlay();
        }

        return null;
    }
}
