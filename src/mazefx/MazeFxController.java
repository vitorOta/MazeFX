/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazefx;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mazefx.enums.Field;
import mazefx.enums.Movement;

/**
 *
 * @author VitorOta
 */
public class MazeFxController implements Initializable {

    @FXML
    private VBox vBox;

    @FXML
    private Button bNewGame;
    
    
    //TODO refactor
    int mazeSize = 20;
    Field[][] maze = new Field[mazeSize][mazeSize];
    Button[][] buttons = new Button[maze.length][maze[0].length];

    boolean gameStarted = false;

    Random random;

    final List<Movement> possibleMovements = Arrays.asList(Movement.values());

    Movement newGameMoveTo;
    Movement newGameLastMove;
    int[] newGameCurrents = new int[]{0, 0};
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        random = new Random();
        initComponents();
        bNewGame.setVisible(false);
        newGame();
    }

    void initComponents() {
        double bHeight = vBox.getHeight() / buttons.length;
        double bWidht = vBox.getWidth() / buttons[0].length;
        for (int row = 0; row < buttons.length; row++) {
            for (int column = 0; column < buttons[row].length; column++) {
                Button b = new Button();
                b.setPrefSize(bHeight, bWidht);
                buttons[row][column] = b;
            }
            HBox box = new HBox(buttons[row]);
            vBox.getChildren().add(box);
        }
    }

    @FXML
    void move(KeyEvent event) {
        move(event.getCode());
    }

    void move(KeyCode keyCode) {
        if (!gameStarted) {
            return;
        }
        if (MazeFxController.this.isGameEnd()) {
            gameStarted = false;
            return;
        }

        switch (keyCode) {
            case UP:
            case W:
                newGameMoveTo = Movement.UP;
                break;
            case RIGHT:
            case D:
                newGameMoveTo = Movement.RIGHT;
                break;
            case DOWN:
            case S:
                newGameMoveTo = Movement.DOWN;
                break;
            case LEFT:
            case A://'a':
                newGameMoveTo = Movement.LEFT;
                break;
        }

        
        boolean moved = moveOnMaze(newGameMoveTo, Field.PLAYER, newGameCurrents, true);
        newGameLastMove = newGameMoveTo;
        showMaze();

        if (MazeFxController.this.isGameEnd()) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Parabéns ! \n\nImplementar iniciar novo jogo ao finalizar um");
            a.show();
            return;
        }

    }

    void showMaze() {
        for (int row = 0; row < maze.length; row++) {
            for (int column = 0; column < maze[row].length; column++) {
                Field field = maze[row][column];
                buttons[row][column].setBackground(new Background(new BackgroundFill(field.color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    boolean isPossibleMoveTo(Movement moveTo, int currentRow, int currentColumn) {
        boolean isPossibleMoveTo = false;

        //TODO add the check to see if the adjacents positions are walls
        switch (moveTo) {
            case RIGHT:
                isPossibleMoveTo = currentColumn < maze[currentRow].length - 1 && maze[currentRow][currentColumn + 1] != Field.WALL;
                break;
            case DOWN:
                isPossibleMoveTo = currentRow < maze.length - 1 && maze[currentRow + 1][currentColumn] != Field.WALL;
                break;
            case LEFT:
                isPossibleMoveTo = currentColumn > 0 && maze[currentRow][currentColumn - 1] != Field.WALL;
                break;
            case UP:
                isPossibleMoveTo = currentRow > 0 && maze[currentRow - 1][currentColumn] != Field.WALL;
                break;
        }

        return isPossibleMoveTo;
    }

    Movement randomNextMove(Movement lastMove, int currentRow, int currentColumn) {
        //right or down ++ // left or up --
        Movement moveTo = null;
        boolean canMove = true;

        do {
            //4 possible movements
            moveTo = possibleMovements.get(random.nextInt(2)); //I see a bug here when moving up //TODO fix this bug

            // after a move, you CAN'T move to his oposite direction (example, go to down, after up)
            boolean isOpositeMove = moveTo.getOposte() == lastMove;

            canMove = !isOpositeMove && isPossibleMoveTo(moveTo, currentRow, currentColumn);

        } while (!canMove);

        return moveTo;
    }

    boolean moveOnMaze(Movement moveTo, Field field, int[] currents, boolean clearCurrent) {
        int currentRow = currents[0];
        int currentColumn = currents[1];

        if (!isPossibleMoveTo(moveTo, currentRow, currentColumn)) {
            return false;
        }

        boolean moved = possibleMovements.contains(moveTo);

        if (moved) {
            if (clearCurrent) {
                maze[currentRow][currentColumn] = Field.EMPTY;
            }
            switch (moveTo) {
                case RIGHT:
                    currentColumn++;
                    break;
                case DOWN:
                    currentRow++;
                    break;
                case LEFT:
                    currentColumn--;
                    break;
                case UP:
                    currentRow--;
                    break;
            }
            maze[currentRow][currentColumn] = field;
            currents[0] = currentRow;
            currents[1] = currentColumn;
        }
        return moved;
    }

    void generateMaze() {
        Field markAsGenerated = Field.EMPTY;// 99999999;

        Movement lastMove = null;
        int currents[] = new int[]{0, 0};
        
        //initial
        maze[currents[0]][currents[1]] = markAsGenerated;

        while (!(currents[0] == maze.length - 1 && currents[1] == maze[0].length - 1)) {
            //TODO dude, you was almost sleeping, the ugly code has a reason
            
            Movement moveTo = randomNextMove(lastMove, currents[0], currents[1]);
            moveOnMaze(moveTo, markAsGenerated, currents, false);
            lastMove = moveTo;
        }

        //populating maze
        for (int row = 0; row < mazeSize; row++) {
            for (int column = 0; column < mazeSize; column++) {
                Field field = Field.EMPTY;
                if (maze[row][column] != markAsGenerated && random.nextInt(3) > 0) {
                    field = Field.WALL;
                }
                maze[row][column] = field;
            }
        }
    }

    boolean isGameEnd() {
        return maze[maze.length - 1][maze[0].length - 1] == Field.PLAYER;
    }

    void initNewGameVars() {
        gameStarted = true;

        newGameMoveTo = null;
        newGameLastMove = null;
        newGameCurrents[0] = 0;
        newGameCurrents[1] = 0;
    }

    @FXML
    void newGame(ActionEvent event) {
        newGame();
    }

    void newGame() {
        initNewGameVars();
        generateMaze();
        maze[newGameCurrents[0]][newGameCurrents[1]] = Field.PLAYER;
        showMaze();
    }

}
