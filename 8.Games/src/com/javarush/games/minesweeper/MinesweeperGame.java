package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
            }
        }
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (gameField[y][x].isMine == false) {
                    for (GameObject neighbor : getNeighbors(gameField[y][x])) {
                        if (neighbor.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }

                    }

                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void openTile(int x, int y) {
        if (doesThisTileCouldBeOpen(x, y)) return;
        gameField[y][x].isOpen = true;

        if (gameField[y][x].isOpen) {
            countClosedTiles--;
        }
        if (gameField[y][x].isOpen && !gameField[y][x].isMine) {
            score += 5;
        }
        setScore(score);
        if (countClosedTiles == countMinesOnField && !gameField[y][x].isMine) {
            win();
        }
        setCellColor(x, y, Color.GREEN);

        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0) {

            for (GameObject neib : getNeighbors(gameField[y][x])) {
                if (!neib.isOpen) {
                    openTile(neib.x, neib.y);
                }
            }
            setCellValue(x, y, "");
        }
        if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors != 0) {
            {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            }
        }

    }

    private boolean doesThisTileCouldBeOpen(int x, int y) {
        return gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped;
    }

    private void markTile(int x, int y) {
        if (countFlags > 0 && !gameField[y][x].isOpen && !gameField[y][x].isFlag && !isGameStopped) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else if (gameField[y][x].isFlag && !isGameStopped) {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellColor(x, y, Color.ORANGE);
            setCellValue(x, y, "");
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "GAME OVER", Color.RED, 50);

    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "WiNNING", Color.GREEN, 50);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        createGame();

    }


    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
        } else openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }
}