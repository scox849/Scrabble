package scrabble;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Game class extends Application for javafx. Contains methods
 * to create a GUI and play a game of scrabble against a computer.
 * @author Sam Cox
 * @version date 3/6/20
 */
public class Game extends Application {

    private static Board gameBoard;
    private static Trie tree;
    private static ComputerPlayer computerPlayer;
    private static HumanPlayer humanPlayer;
    private Map<Rectangle, BoardCell> boardMap = new HashMap<>();
    private Map<Rectangle, Canvas> trayMap = new HashMap<>();
    private Map<BoardCell, Canvas> tilesOnBoard = new HashMap<>();
    private Map<Canvas, Tile> tilesInTray = new HashMap<>();
    private Rectangle tileToBePlaced = null;
    private List<BoardCell> wordBeingBuilt = new LinkedList<>();
    private boolean humanTurn = true;
    private boolean exchangeMode = false;
    private List<Tile> tilesToExchange = new LinkedList<>();


    /**
     * Reads in a board from input and creates a new board
     * from the input.
     * @param scanner system in
     * @param size size of board
     * @return game board
     * @throws IOException because of input
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
     * Mouse behavior for when a spot on the board is pressed.
     * @param rect spot released on
     * @param board gridpane board
     */
    private void mousePressed(Rectangle rect, GridPane board){
        if(tileToBePlaced != null){
            int span = 1;
            BoardCell cell = boardMap.get(rect);
            if(cell.hasTile()){
                return;
            }
            Canvas tile = trayMap.get(tileToBePlaced);
            Tile backEndTile = tilesInTray.get(tile);
            if(!backEndTile.getLetter().equals("*")){
                tilesOnBoard.put(cell, tile);
                board.getChildren().remove(tile);

                cell.setTileInCell(backEndTile);
                wordBeingBuilt.add(cell);
                board.add(tile, cell.getXCell(), cell.getYCell(), span, span);
                tileToBePlaced.setStroke(Color.BLACK);
                tileToBePlaced = null;
            }

        }

    }

    /**
     * Draws a tile based on the letter and point value.
     * @param tile tile to be drawn
     * @return canvas with tile
     */
    private Canvas drawTile(Tile tile){
        double canvasSize = 50;
        double offset = 12;
        int textSize = 30;
        int numberSize = 15;

        String point = "" + tile.getPointVal();
        Canvas tileCanvas = new Canvas(canvasSize,canvasSize);
        GraphicsContext graphicsContext = tileCanvas.getGraphicsContext2D();
        graphicsContext.setFont(new Font(textSize));
        graphicsContext.setFill(Color.BLACK);
        if(tile.getLetter().equals("*")){
            graphicsContext.fillText(" ",
                    Math.round(canvasSize/2 - offset - 5),
                    Math.round(canvasSize/2 + offset));
        }else{
            if(tile.getPointVal() == 0){
                graphicsContext.setFill(Color.GREEN);
            }
            graphicsContext.fillText(tile.getLetter().toUpperCase(),
                    Math.round(canvasSize/2 - offset - 5),
                    Math.round(canvasSize/2 + offset));
        }
        graphicsContext.setFont(new Font(numberSize));
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText(point,
                Math.round(canvasSize/2 + offset - 3),
                Math.round(canvasSize/2 +  offset * 1.5));

        return tileCanvas;
    }

    /**
     * Draws the board and adds event listeners to spots on the board
     * based on the size and multipliers on the board.
     * @param board gridpane to be added to
     */
    private void buildBoard(GridPane board){
        double rectSize = 50;
        int stroke = 2;
        for(int i = 0; i < gameBoard.getRows(); i++){
            for(int j = 0; j < gameBoard.getCols(); j++){
                Rectangle rect = new Rectangle(rectSize,rectSize);
                boardMap.put(rect, gameBoard.getCell(j,i));
                rect.setStroke(Color.BLACK);
                rect.setFill(Color.TRANSPARENT);
                switch(gameBoard.getCell(j,i).getLetterMultiplier()){
                    case 2:
                        rect.setFill(Color.LIGHTBLUE);
                        break;
                    case 3:
                        rect.setFill(Color.DEEPSKYBLUE);
                        break;
                    default:
                        break;
                }
                switch(gameBoard.getCell(j,i).getWordMultiplier()){
                    case 2:
                        rect.setFill(Color.PINK);
                        break;
                    case 3:
                        rect.setFill(Color.RED);
                        break;
                    default:
                        break;
                }
                rect.setStrokeWidth(stroke);
                rect.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
                        mousePressed(rect, board));
                board.add(rect, j, i);
            }
        }
        for(int k = 0; k < gameBoard.getCols(); k++){
            Rectangle rect = new Rectangle(rectSize,rectSize);
            rect.setFill(Color.TRANSPARENT);
            rect.setStroke(Color.TRANSPARENT);
            board.add(rect,k,15);
        }
    }

    /**
     * Marks a tile in the tray.
     * @param rect tile mouse released on
     */
    private void markTileInTray(Rectangle rect){
        if(exchangeMode){
            Tile tile = tilesInTray.get(trayMap.get(rect));
            if(tilesToExchange.contains(tile)){
                return;
            }
            tilesToExchange.add(tile);
            humanPlayer.getTray().getTray().remove(tile);
        }else{
            if (tileToBePlaced != null) {
                tileToBePlaced.setStroke(Color.BLACK);
            }
            tileToBePlaced = rect;
        }
        rect.setStroke(Color.RED);
    }

    /**
     * Clears the tile that was used out of the tray.
     * @param tile tile to be cleard
     */
    private void clearOutTileUsed(Canvas tile){
        Collection<Rectangle> trayCells = trayMap.keySet();
        for(Rectangle cell: trayCells){
            if(trayMap.get(cell) == tile){
                trayMap.remove(cell);
                return;
            }
        }
    }

    /**
     * Game behavior for when all tiles for a move have been placed.
     * @param board gridpane gameboard
     */
    private void playPressed(GridPane board) throws IOException {
        if(tileToBePlaced == null) {
            if(wordBeingBuilt.size() > 0){
                if(!humanPlayer.isLegal(wordBeingBuilt)){
                    for(BoardCell cell : wordBeingBuilt){
                        Canvas tile = tilesOnBoard.get(cell);
                        if(cell.getTile().getPointVal() == 0){
                            cell.getTile().setLetter("*");
                        }
                        board.getChildren().remove(tile);
                        tilesOnBoard.remove(cell);
                        cell.setTileInCell(null);
                        buildTray(board);
                    }
                }else{
                    humanTurn = false;
                    for(BoardCell cell: wordBeingBuilt){
                        Canvas tile = tilesOnBoard.get(cell);
                        clearOutTileUsed(tile);
                        humanPlayer.getTray().getTray().remove(cell.getTile());
                    }
                    gameBoard.getBag().fillTray(humanPlayer.getTray());
                    tilesInTray.clear();
                    buildTray(board);
                    if(!humanTurn){
                        List<BoardCell> compMove = computerPlayer.makeMove();
                        if(compMove != null){
                            this.placeCompTiles(board, compMove);
                        }
                        humanTurn = true;
                    }
                    if(gameOver()){
                        checkWin(board);
                    }
                }
            }

            wordBeingBuilt.clear();
        }else{
            tileToBePlaced.setStroke(Color.BLACK);
            tileToBePlaced = null;
        }
    }

    /**
     * Draws the tray and adds listeners where needed.
     * @param board gridpane to be drawn on
     */
    private void buildTray(GridPane board){
        int rectSize = 50;
        int stroke = 2;
        int xIndex = 3;
        int span = 1;
        Collection<Canvas> tiles = trayMap.values();
        if(tiles.size() > 0){
            for (Canvas tile : tiles) {
                board.getChildren().remove(tile);
            }
            trayMap.clear();
            tilesInTray.clear();
        }

        for(Tile tile : humanPlayer.getTray().getTray()){
            Canvas newTile = drawTile(tile);
            Rectangle rect = new Rectangle(rectSize,rectSize);
            trayMap.put(rect, newTile);
            tilesInTray.put(newTile, tile);
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(stroke);
            rect.setFill(Color.TRANSPARENT);

            rect.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
                    markTileInTray(rect));

            board.add(newTile, xIndex, gameBoard.getRows() + 1,
                    span, span);
            board.add(rect, xIndex, gameBoard.getRows() + 1,
                    span ,span);
            xIndex++;
        }
    }

    /**
     * Makes the button to exchange tiles with and adds listener.
     * @param board gridpane board to have button added
     */
    private void makeExchangeButton(GridPane board, Text computerScore){
        Button exchange = new Button("Exchange");
        int buttonX = 1;
        int span = 2;
        exchange.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if(exchangeMode){
                int numNeeded = tilesToExchange.size();
                if(gameBoard.getBagSize() < numNeeded){
                    tilesToExchange.clear();
                    exchangeMode = false;
                    return;
                }
                List<Tile> newTiles = gameBoard.getBag().takeFromBag(numNeeded);
                gameBoard.getBag().addToBag(tilesToExchange);
                tilesToExchange.clear();
                humanPlayer.getTray().getTray().addAll(newTiles);
                exchangeMode = false;
                buildTray(board);
                humanTurn = false;
                try {
                    List<BoardCell> compMove = computerPlayer.makeMove();
                    if(compMove != null){
                        this.placeCompTiles(board, compMove);
                        computerScore.setText("Computer Score: " +
                                computerPlayer.getCurrentScore());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(gameOver()){
                    checkWin(board);
                }
                humanTurn = true;

            }else{
                exchangeMode = true;
            }
        });
        board.add(exchange, buttonX, gameBoard.getRows() + 1,
                span ,span/2);
    }

    /**
     * Creates button for player to pass on their turn.
     * @param board gridpane to be added to
     * @param computerScore text on pane of computer score
     */
    private void makePassButton(GridPane board, Text computerScore){
        Button pass = new Button("Pass");
        int buttonX = 13;
        int span = 1;
        pass.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if(!humanTurn){
                return;
            }
            humanTurn = false;
            try {
                List<BoardCell> compMove = computerPlayer.makeMove();
                if(compMove != null){
                    this.placeCompTiles(board, compMove);
                    computerScore.setText("Computer Score: " +
                            computerPlayer.getCurrentScore());
                }else{
                    checkWin(board);
                }
                humanTurn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        board.add(pass, buttonX, gameBoard.getRows() + 1, span, span);
    }

    /**
     * Draws the computer move on the GUI
     * @param board gridPane
     * @param compMove cells of computer move
     */
    private void placeCompTiles(GridPane board, List<BoardCell> compMove){
        int span = 1;
        for(BoardCell cell: compMove){
            BoardCell boardCell = gameBoard.getCell(cell.getXCell(),
                    cell.getYCell());
            boardCell.setTileInCell(cell.getTile());
            Canvas tile = drawTile(cell.getTile());
            tilesOnBoard.put(boardCell, tile);
            board.add(tile, cell.getXCell(), cell.getYCell(), span, span);
        }
    }

    /**
     * Checks if the game is over.
     * @return true or false
     */
    private boolean gameOver(){
        if(gameBoard.getBag().getBagSize() == 0){
            return computerPlayer.getTray().getTray().size() == 0 ||
                    humanPlayer.getTray().getTray().size() == 0;
        }
        return false;
    }

    /**
     * Checks who won.
     * @param board gridPane
     */
    private void checkWin(GridPane board){
        int fontSize = 20;
        int remainingCompLetters = 0;
        int remainingHumanLetters = 0;
        int beforeCompScore = computerPlayer.getCurrentScore();
        int beforeHumanScore = humanPlayer.getCurrentScore();
        for(Tile tile: computerPlayer.getTray().getTray()){
            remainingCompLetters += tile.getPointVal();
        }
        for(Tile tile: humanPlayer.getTray().getTray()){
            remainingHumanLetters += tile.getPointVal();
        }
        humanPlayer.subtract(remainingHumanLetters);
        computerPlayer.subtract(remainingCompLetters);
        if(computerPlayer.getTray().getTray().size() == 0 &&
                humanPlayer.getTray().getTray().size() > 0){
            computerPlayer.addToScore(remainingHumanLetters);
        }else if(humanPlayer.getTray().getTray().size() == 0 &&
                    computerPlayer.getTray().getTray().size() > 0){
            humanPlayer.addToScore(remainingCompLetters);
        }
        Text winner = new Text();
        winner.setFont(new Font(fontSize));
        if(computerPlayer.getCurrentScore() > humanPlayer.getCurrentScore()){
            winner.setText("Computer Wins!");
        }else if(humanPlayer.getCurrentScore() >
                computerPlayer.getCurrentScore()){
            winner.setText("Human Wins!");
        }else if(humanPlayer.getCurrentScore() ==
                computerPlayer.getCurrentScore()){
            if(beforeCompScore > beforeHumanScore){
                winner.setText("Computer Wins!");
            }else if(beforeHumanScore > beforeCompScore){
                winner.setText("Human Wins!");
            }else{
                winner.setText("Tie!");
            }
        }
        board.add(winner, 5, gameBoard.getRows(),
                3,1);
    }


    /**
     * Starts the GUI. Adds a gridpane a button onto the gridpane
     * and text onto the gridpane. Also has listener for key pressed for
     * blanks.
     * @param primaryStage stage for scene.
     */
    @Override
    public void start(Stage primaryStage){

        GridPane board = new GridPane();
        board.setStyle("-fx-background-color:#ffffcc; -fx-opacity:1;");
        int sceneWidth = gameBoard.getCols() * 52;
        int sceneHeight = gameBoard.getRows() * 60;
        int span = 1;
        int buttonX = 12;
        int fontSize = 20;
        buildBoard(board);
        buildTray(board);

        Text humanScore = new Text("Human Score: " +
                                    humanPlayer.getCurrentScore());

        Text computerScore = new Text("Computer Score: " +
                                        computerPlayer.getCurrentScore());
        humanScore.setFont(new Font(fontSize));
        computerScore.setFont(new Font(fontSize));

        makeExchangeButton(board, computerScore);
        makePassButton(board, computerScore);

        board.add(humanScore, buttonX - 11, gameBoard.getRows(),
                 span + 2, span);
        board.add(computerScore, buttonX - 2, gameBoard.getRows(),
                span + 2, span);

        Button play = new Button("Play");
        play.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            try {
                playPressed(board);
            } catch (IOException e) {
                e.printStackTrace();
            }
            humanScore.setText("Human Score: " +
                    humanPlayer.getCurrentScore());
            computerScore.setText("Computer Score: " +
                    computerPlayer.getCurrentScore());

        });
        board.add(play, buttonX - 1, gameBoard.getRows()+1,
                span ,span);

        Scene root = new Scene(board, sceneWidth, sceneHeight);
        root.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(tileToBePlaced == null){
                return;
            }
            double canvasSize = 50;
            int offset = 12;
            Canvas tile = trayMap.get(tileToBePlaced);
            Tile backEndTile = tilesInTray.get(tile);
            if(backEndTile.getLetter().equals("*")){
                backEndTile.setLetter(event.getText());
                GraphicsContext graphicsContext = tile.getGraphicsContext2D();
                graphicsContext.setFont(new Font(fontSize + 10));
                graphicsContext.setFill(Color.GREEN);
                graphicsContext.fillText(backEndTile.getLetter(),
                        Math.round(canvasSize/2 - offset - 5),
                        Math.round(canvasSize/2 + offset));
            }

        });

        root.setFill(Color.CORNSILK);
        primaryStage.setTitle("Scrabble");
        primaryStage.setScene(root);
        primaryStage.show();
    }

    /**
     * Main starts GUI and reads necessary files.
     * @param args command line args
     * @throws IOException because of input
     */
    public static void main(String[] args) throws IOException {

        String file;
        if(args.length > 0){
            file = args[0];
        }else{
            System.out.println("Need input file!");
            return;
        }

        tree = new Trie(file);
        InputStream boardText = Game.class.getClassLoader().
                getResourceAsStream("scrabble_board.txt");
        assert boardText != null;
        Scanner scanner = new Scanner(boardText);

        while(scanner.hasNextLine()){
            int size = scanner.nextInt();
            gameBoard = readInBoard(scanner,size);
        }

        computerPlayer = new ComputerPlayer(tree, gameBoard,
                new Tray(new LinkedList<>()));
        humanPlayer = new HumanPlayer(gameBoard, tree,
                new Tray(new LinkedList<>()));

        launch(args);


    }

}
