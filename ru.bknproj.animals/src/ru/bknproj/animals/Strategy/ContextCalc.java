package ru.bknproj.animals.Strategy;

/**
 * Контекст для паттерна "Стратегия"
 */
public class ContextCalc {
    private ReadAnimals strategy;

    public void setStrategy(ReadAnimals strategy) {

        this.strategy = strategy;
    }

    public String executeStrategy(String sFileAni, String cFileRules) {

        return strategy.ReadAnimals(sFileAni,cFileRules);
    }
}
