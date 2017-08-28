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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import mazefx.enums.Field;
import mazefx.enums.Movement;

/**
 *
 * @author VitorOta
 */
public class MazeFxController implements Initializable {

    @FXML
    Canvas canvas;

    GraphicsContext gc;

    //TODO refactor
    int mazeSize = 20;
    Field[][] maze = new Field[mazeSize][mazeSize];

    boolean gameStarted = false;

    Random random;

    Movement newGameMoveTo;
    Movement newGameLastMove;
    int[] newGameCurrents = new int[]{0, 0};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        random = new Random();
        canvas.focusTraversableProperty().set(true);
        gc = canvas.getGraphicsContext2D();
        //bNewGame.setVisible(false);
        newGame();
    }

    @FXML
    void move(KeyEvent event) {
        if (event.getCode() == KeyCode.N) {
            newGame();
            return;
        }
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

        moveOnMaze(newGameMoveTo, Field.PLAYER, newGameCurrents, true);
        newGameLastMove = newGameMoveTo;
        showMaze();

        if (MazeFxController.this.isGameEnd()) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Parabéns ! \n\nImplementar iniciar novo jogo ao finalizar um");
            a.show();
            a.setOnCloseRequest((event) -> {
                newGame();
            });

            return;
        }

    }

    void showMaze() {
        double bHeight = canvas.getHeight() / mazeSize;
        double bWidth = canvas.getWidth() / mazeSize;

        for (int row = 0; row < mazeSize; row++) {
            for (int column = 0; column < mazeSize; column++) {
                Field field = maze[row][column];
                gc.setFill(field.color);
                gc.fillRect(column * bWidth, row * bHeight, bWidth, bHeight);
            }
        }
    }

    boolean isPossibleMoveTo(Movement moveTo, int currentRow, int currentColumn) {
        boolean isPossibleMoveTo = false;

        //TODO add the check to see if the adjacents positions are walls
        switch (moveTo) {
            case RIGHT:
                isPossibleMoveTo = currentColumn < mazeSize - 1 && maze[currentRow][currentColumn + 1] != Field.WALL;
                break;
            case DOWN:
                isPossibleMoveTo = currentRow < mazeSize - 1 && maze[currentRow + 1][currentColumn] != Field.WALL;
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

    Movement generateMove(final Movement lastMove, int currentRow, int currentColumn) {
        Movement moveTo = null;
        boolean canMove = true;

        
        //TODO use a list and Collections.shuffle instead of sorting a random number. Each time you get a movement, take away from array, this mode I'll made the control of movements trieds
        boolean[] trieds = new boolean[3]; //TODO back to 4 after fix
        List<Movement> movements = Arrays.asList(Movement.values());
        do {
            boolean allTried = true;
            for (boolean tried : trieds) {
                if (!tried) {
                    allTried = false;
                    break;
                }
            }
            if (allTried) {
                throw new RuntimeException("Bad programmed");
            }

            int r = random.nextInt(3); //TODO back to 4 after fix
            if (trieds[r]) {
                continue;
            }

            moveTo = movements.get(r);
            trieds[r] = true;

            // after a move, you CAN'T move to his oposite direction (example, go to down, after up)
            boolean isOpositeMove = moveTo.getOposte() == lastMove;

            canMove = !isOpositeMove && isPossibleMoveTo(moveTo, currentRow, currentColumn);

            boolean willTouchGenerated = false;
            boolean hasRestrictions = false;

            if (canMove) {
                //hasRestrictions
                switch (moveTo) {
                    case LEFT:
                        hasRestrictions = currentRow > mazeSize - 2 && currentRow > 1;
                        break;
                    case UP:
                        hasRestrictions = currentColumn > mazeSize - 2 && currentColumn > 1;
                        break;

                }

//                //hasAdjacentsGen
//                if (!hasRestrictions) {
//
//                    switch (moveTo) {
//                        case RIGHT:
//                            willTouchGenerated
//                                    = (currentColumn < mazeSize - 2 ? maze[currentRow][currentColumn + 2] : null) == Field.GENERATED
//                                    || (currentRow > 0 && currentColumn < mazeSize - 1 ? maze[currentRow - 1][currentColumn + 1] : null) == Field.GENERATED
//                                    || (currentRow < mazeSize - 1 && currentColumn < mazeSize - 1 ? maze[currentRow + 1][currentColumn + 1] : null) == Field.GENERATED;
//                            break;
//                        case DOWN:
//                            willTouchGenerated
//                                    = (currentRow < mazeSize - 2 ? maze[currentRow + 2][currentColumn] : null) == Field.GENERATED
//                                    ||
//                                    ||;
//                            break;
//                        case LEFT:
//                            willTouchGenerated
//                                    = (currentColumn < 1 ? maze[currentRow][currentColumn + 2] : null) == Field.GENERATED
//                                    || (currentRow > 0 && currentColumn > 0 ? maze[currentRow - 1][currentColumn - 1] : null) == Field.GENERATED
//                                    || (currentRow < mazeSize - 1 && currentColumn > 0 ? maze[currentRow + 1][currentColumn - 1] : null) == Field.GENERATED;
//                            break;
//                        case UP:
//                            willTouchGenerated
//                                    = (currentRow > 1 ? maze[currentRow - 2][currentColumn] : null) == Field.GENERATED
//                                    ||
//                                    ||;
//                            break;
//                    }
//                }

            }

            canMove &= !hasRestrictions && !willTouchGenerated;

        } while (!canMove);

        return moveTo;
    }

    void moveOnMaze(Movement moveTo, Field field, int[] currents, boolean clearCurrent) {
        int currentRow = currents[0];
        int currentColumn = currents[1];

        if (!isPossibleMoveTo(moveTo, currentRow, currentColumn)) {
            return;
        }

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

    void clearMaze() {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                maze[i][j] = null;
            }

        }
    }

    void generateMaze() {
        clearMaze();

        Field markAsGenerated = Field.GENERATED;// Field.GENERATED;

        Movement lastMove = null;
        int currents[] = new int[]{0, 0};

        //initial
        maze[currents[0]][currents[1]] = markAsGenerated;

        while (!(currents[0] == mazeSize - 1 && currents[1] == mazeSize - 1)) {
            //TODO dude, you was almost sleeping, the ugly code has a reason

            Movement moveTo = generateMove(lastMove, currents[0], currents[1]);
            moveOnMaze(moveTo, markAsGenerated, currents, false);
            lastMove = moveTo;
        }

        int i = 0;
        //populating maze
        for (int row = 0; row < mazeSize; row++) {
            for (int column = 0; column < mazeSize; column++) {
                Field field = Field.EMPTY;
                if (maze[row][column] == markAsGenerated) {
//                    field = Field.GENERATED; //TODO take this off when finish your debug
                } else if (random.nextInt(5) > 0) {
                    field = Field.WALL;
                }

//                if (maze[row][column] == null) {
                    maze[row][column] = field;
//                }
            }
        }
    }

    boolean isGameEnd() {
        return maze[mazeSize - 1][mazeSize - 1] == Field.PLAYER;
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
