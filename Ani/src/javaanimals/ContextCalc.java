package javaanimals;

/**
 * Контекст для паттерна "Стратегия"
 */
public class ContextCalc {
    private ReadAndExecute strategy;

    void setStrategy(ReadAndExecute strategy) {

        this.strategy = strategy;
    }

    String executeStrategy (String sFileAni, String cFileRules) {

        return strategy.readAndExecute(sFileAni,cFileRules);
    }
}
