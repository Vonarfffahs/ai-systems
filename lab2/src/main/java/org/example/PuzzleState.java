package org.example;

import java.util.ArrayList;
import java.util.List;
import static org.example.Main.*;

// Класс пазлу
public class PuzzleState {
    int[][] board;
    PuzzleState parent;
    String move;
    int depth;
    int fValue;

    PuzzleState(int[][] board) {
        this.board = board;
        this.parent = null;
        this.move = "";
        this.depth = 0;
        this.fValue = h1();
    }

    PuzzleState(int[][] board, PuzzleState parent, String move, int depth) {
        this.board = board;
        this.parent = parent;
        this.move = move;
        this.depth = depth;
    }

    boolean isGoal() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] != GOAL_STATE[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    int h1() {
        int misplacedTiles = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] != 0 && this.board[i][j] != GOAL_STATE[i][j]) {
                    misplacedTiles++;
                }
            }
        }
        return misplacedTiles;
    }

    List<PuzzleState> generateSuccessors() {
        List<PuzzleState> successors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0}; // left, right
        int[] dy = {0, 0, -1, 1}; // up, down

        int zeroPosX = -1, zeroPosY = -1;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    zeroPosX = i;
                    zeroPosY = j;
                }
            }
        }

        for (int dir = 0; dir < 4; dir++) {
            int newX = zeroPosX + dx[dir];
            int newY = zeroPosY + dy[dir];
            if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                int[][] newBoard = cloneBoard();
                newBoard[zeroPosX][zeroPosY] = newBoard[newX][newY];
                newBoard[newX][newY] = 0;
                PuzzleState child = new PuzzleState(newBoard, this, "", depth + 1);
                successors.add(child);
                generatedNodes++; // Збільшення кількості створених вузлів
            }
        }

        maxStoredNodes = Math.max(maxStoredNodes, successors.size()); // Максимальна кількість вузлів у пам'яті одночасно
        return successors;
    }

    int[][] cloneBoard() {
        int[][] newBoard = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(this.board[i], 0, newBoard[i], 0, SIZE);
        }
        return newBoard;
    }
}

