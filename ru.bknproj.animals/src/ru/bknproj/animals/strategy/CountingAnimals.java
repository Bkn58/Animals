package ru.bknproj.animals.strategy;

import java.io.IOException;

/**
 * Общий интерфейс для всех стратегий подсчета животных паттерна "Стратегия".
 * @author Bkn
 *
 * @param sFileAni - файл входного потока с животными
 * @param sFileRules - файл с правилами
 * @return String - выходная строка результата
 * @throws IOException
 */
public interface CountingAnimals {
    String countingAnimals (String sFileAni, String sFileRules) throws IOException;
}
