package ru.bknproj.animals.strategy;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bknproj.animals.DemoAnimals;

import java.io.IOException;

/**
 * Контекст для паттерна "Стратегия"
 */
public class ContextStrategy {
    private CountingAnimals strategy;

    public void setStrategy(CountingAnimals strategy) {

        this.strategy = strategy;
    }

    public String executeStrategy(String sFileAni, String cFileRules) {
        String s="";
        PropertyConfigurator.configure("log4j.properties");
        Logger log = LoggerFactory.getLogger(DemoAnimals.class);
        try {
            s= strategy.countingAnimals (sFileAni, cFileRules);
        } catch (IOException e) {
            e.printStackTrace ();
            log.error("IOException ", e);
        }
        return s;
    }
}
