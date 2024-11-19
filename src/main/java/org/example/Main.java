package org.example;
import java.util.*;

public class Main {

    static final int SIZE = 3; // Розмір пазла (3х3)
    static final int MAX_TIME = 2 * 60 * 1000; // Максимальний час у мілісекундах (2 хвилини)
    static final long MAX_MEMORY = 512 * 1024 * 1024; // Макс пам'яті в байтах (512 МБ)

    static final int[][] GOAL_STATE = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
    };

    // Лічильники для вузлів
    static int generatedNodes = 0;
    static int maxStoredNodes = 0;

    public static void main(String[] args) {
        int moves = 1;  // Кількість ходів для створення початкового стану, який можна розв’язати
        int[][] initialState = generateSolvableInitialState(GOAL_STATE, moves);
//        int[][] initialState = {
//                {1, 2, 0},
//                {4, 6, 3},
//                {7, 5, 8}
//        };

        long startTime, endTime;
        System.gc();
        Runtime runtime = Runtime.getRuntime();

        // Скинути кількість згенерованих вузлів перед пошуком IDS
        generatedNodes = 0;
        maxStoredNodes = 0;
        // Запуск IDS
        System.out.println("Starting IDS search...");
        startTime = System.currentTimeMillis();  // Час початку IDS
        PuzzleState result = IDS.iterativeDeepeningSearch(new PuzzleState(initialState), startTime, runtime);
        endTime = System.currentTimeMillis();    // Час завершення IDS
        printResult(result, startTime, endTime, runtime, "IDS");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Скинути кількість згенерованих вузлів перед пошуком RBFS
        generatedNodes = 0;
        maxStoredNodes = 0;
        System.gc();
        Runtime runtime1 = Runtime.getRuntime();
        // Запуск RBFS
        System.out.println("Starting RBFS search...");
        startTime = System.currentTimeMillis();  // Час початку RBFS
        result = RBFS.recursiveBestFirstSearch(new PuzzleState(initialState), startTime, runtime1);
        endTime = System.currentTimeMillis();    // Час завершення RBFS
        printResult(result, startTime, endTime, runtime1, "RBFS");
    }

    static int[][] generateSolvableInitialState(int[][] goalState, int moves) {
        int[][] initialState = cloneBoard(goalState);
        Random random = new Random();
        int[] dx = {-1, 1, 0, 0}; // Переміщення для рядків
        int[] dy = {0, 0, -1, 1}; // Переміщення для стовпців

        int[] zero = new int[2];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (goalState[i][j] == 0) {
                    zero[0]= i;
                    zero[1] = j; // позиція нулю у цільовому стані
                }
            }
        }
        int zeroX =zero[0], zeroY = zero[1];

        for (int i = 0; i < moves; i++) {
            List<int[]> possibleMoves = new ArrayList<>();
            for (int dir = 0; dir < 4; dir++) {
                int newX = zeroX + dx[dir];
                int newY = zeroY + dy[dir];
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                    possibleMoves.add(new int[]{newX, newY});
                }
            }

            int[] move = possibleMoves.get(random.nextInt(possibleMoves.size()));
            int newX = move[0], newY = move[1];

            // Поміняти місцями нуль і вибраний елемент
            initialState[zeroX][zeroY] = initialState[newX][newY];
            initialState[newX][newY] = 0;

            // Оновити позицію нулю
            zeroX = newX;
            zeroY = newY;
        }
        return initialState;
    }

    // Допоміжний метод для клонування пазлу
    static int[][] cloneBoard(int[][] board) {
        int[][] newBoard = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, SIZE);
        }
        return newBoard;
    }

    // Виведення шляху рішення та результатів
    public static void printResult(PuzzleState solution, long startTime, long endTime, Runtime runtime, String algorithm) {
        if (solution == null) {
            System.out.println("No solution found.");
            return;
        }

        double timeTaken = endTime - startTime;

        // Виведення шляху рішення
        List<PuzzleState> path = new ArrayList<>();
        for (PuzzleState state = solution; state != null; state = state.parent) {
            path.add(state);
        }
        Collections.reverse(path);
        for (PuzzleState state : path) {
            printBoard(state.board);
        }

        // Виведення результатів
        double usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("\n" + algorithm + " solution found in " + (timeTaken / 1000) + " s.");
        System.out.println("Memory used: " + String.format("%.3f", (usedMemory / (1024 * 1024))) + " MB.");
        System.out.println("Heuristic (H1) value: " + solution.h1());
        System.out.println("Total generated nodes: " + generatedNodes);
        System.out.println("Max nodes in memory simultaneously: " + maxStoredNodes);
    }

    // Роздрукуйте пазл
    public static void printBoard(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
