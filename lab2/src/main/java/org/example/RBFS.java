package org.example;

import java.util.Comparator;
import java.util.List;
import static org.example.Main.*;

public class RBFS {
    // Recursive Best-First Search (RBFS)
    public static PuzzleState recursiveBestFirstSearch(PuzzleState initialState, long startTime, Runtime runtime) {
        return rbfs(initialState, Integer.MAX_VALUE, startTime, runtime).result;
    }

    // Допоміжний метод RBFS
    private static RBFSResult rbfs(PuzzleState node, int fLimit, long startTime, Runtime runtime) {
        // Перевірка обмежень на час виконання
        if (System.currentTimeMillis() - startTime > MAX_TIME) {
            System.out.println("Time limit exceeded.");
            return new RBFSResult(node, Integer.MAX_VALUE);
        }

        // Перевірка обмежень на використання пам'яті
        if (runtime.totalMemory() - runtime.freeMemory() > MAX_MEMORY) {
            System.out.println("Memory limit exceeded.");
            return new RBFSResult(node, Integer.MAX_VALUE);
        }

        // Якщо це цільовий вузол, повертаємо його
        if (node.isGoal()) {
            return new RBFSResult(node, 0);
        }

        // Розширення вузла для отримання його нащадків
        List<PuzzleState> successors = node.generateSuccessors();
        generatedNodes += successors.size(); // Оновлення максимального числа вузлів

        // Додаємо вузол до пам'яті
        maxStoredNodes++;

        // Якщо немає нащадків, повертаємо невдачу з нескінченним f-лімітом
        if (successors.isEmpty()) {
            return new RBFSResult(null, Integer.MAX_VALUE);
        }

        // Розрахунок початкових f-значень для кожного нащадка
        for (PuzzleState s : successors) {
            s.fValue = Math.max(s.depth + s.h1(), node.fValue);
        }

        // Основний цикл RBFS
        while (true) {
            // Вибір нащадка з найменшим f-значенням
            successors.sort(Comparator.comparingInt(s -> s.fValue));
            PuzzleState best = successors.get(0);

            // Якщо f-значення найкращого вузла перевищує f-ліміт, повертаємо null та f-значення
            if (best.fValue > fLimit) {
                return new RBFSResult(null, best.fValue);
            }

            // Альтернативний f-ліміт для другого найменшого f-значення
            int alternative = (successors.size() > 1) ? successors.get(1).fValue : Integer.MAX_VALUE;

            // Рекурсивний виклик RBFS для найкращого вузла з новим f-лімітом
            RBFSResult result = rbfs(best, Math.min(fLimit, alternative), startTime, runtime);

            // Якщо знайдено рішення, повертаємо його
            if (result.result != null) {
                return result;
            }

            // Оновлюємо f-значення найкращого вузла
            best.fValue = result.fLimit;
        }
    }

    // Клас для зберігання результату RBFS
    static class RBFSResult {
        PuzzleState result;
        int fLimit;

        RBFSResult(PuzzleState result, int fLimit) {
            this.result = result;
            this.fLimit = fLimit;
        }
    }
}
