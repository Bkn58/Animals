package ru.bknproj.animals.strategy;

import java.io.IOException;

/**
 * Контекст для паттерна "Стратегия"
 */
public class ContextCalc {
    private CountingAnimals strategy;

    public void setStrategy(CountingAnimals strategy) {

        this.strategy = strategy;
    }

    public String executeStrategy(String sFileAni, String cFileRules) {
        String s="";
        try {
            s= strategy.countingAnimals (sFileAni, cFileRules);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return s;
    }
}
