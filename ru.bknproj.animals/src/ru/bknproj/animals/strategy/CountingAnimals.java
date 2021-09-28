package ru.bknproj.animals.strategy;

import java.io.IOException;

/**
 * Общий интерфейс для всех стратегий подсчета животных паттерна "Стратегия"
 * @author Bkn
 */
public interface CountingAnimals {
    String countingAnimals (String sFileAni, String sFileRules) throws IOException;
}
