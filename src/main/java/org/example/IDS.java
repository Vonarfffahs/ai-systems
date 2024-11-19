package org.example;

import java.util.List;
import static org.example.Main.*;

public class IDS {
    public static PuzzleState iterativeDeepeningSearch(PuzzleState initialState, long startTime, Runtime runtime) {
        for (int depth = 0; depth <= Integer.MAX_VALUE; depth++) {
            PuzzleState result = depthLimitedSearch(initialState, depth, startTime, runtime);
            if (result != null) {
                return result; // Якщо знайдено цільовий стан, повертаємо результат
            }
        }
        return null; // Якщо рішення не знайдено в межах глибини та обмежень
    }

    public static PuzzleState depthLimitedSearch(PuzzleState state, int limit, long startTime, Runtime runtime) {
        // Перевіряємо, чи не перевищено обмеження на час або пам'ять
        if (System.currentTimeMillis() - startTime > MAX_TIME) {
            System.out.println("Time limit exceeded.");
            return state;
        }
        if (runtime.totalMemory() - runtime.freeMemory() > MAX_MEMORY) {
            System.out.println("Memory limit exceeded.");
            return state;
        }

        // Перевірка, чи досягнуто цільового стану
        if (state.isGoal()) {
            return state;
        }

        // Якщо досягли максимального обмеження глибини, повертаємо null
        if (limit <= 0) {
            return null;
        }
        // Генеруємо наступників
        List<PuzzleState> successors = state.generateSuccessors();
        generatedNodes += successors.size(); // Збільшення кількості згенерованих вузлів

        // Додаємо вузол до пам'яті
        maxStoredNodes++;
        // Виконуємо DLS для кожного наступника
        for (PuzzleState child : successors) {
            PuzzleState result = depthLimitedSearch(child, limit - 1, startTime, runtime);
            if (result != null) {
                return result; // Якщо знайшли рішення, повертаємо результат
            }
        }
        return null; // Якщо рішення не знайдено, повертаємо null
    }
}
