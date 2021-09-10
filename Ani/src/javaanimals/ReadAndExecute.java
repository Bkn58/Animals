package javaanimals;

/**
 * Общий интерфейс для всех стратегий паттерна "Стратегия"
 * @author Bkn
 */
public interface ReadAndExecute {
    String readAndExecute(String sFileAni, String sFileRules);
}
