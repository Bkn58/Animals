package ru.bknproj.animals;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bknproj.animals.Strategy.CollectionStrategy.CollectionStrategy;
import ru.bknproj.animals.Strategy.ContextCalc;
import ru.bknproj.animals.Strategy.StreamStrategy.StreamStrategy;

public class DemoAnimals {
    /**
     * @param args the command line arguments
     * args[0] - файл с атрибутами животных (одна строка - одно животное, атрибуты разделяются запятыми)
     * args[1] - файл с правилами (одна строка - одно правило, лексемы внутри строки разделяются запятыми)
     */
    public static void main(String[] args) {
        String str; // строка результата

        PropertyConfigurator.configure("log4j.properties");
        Logger log = LoggerFactory.getLogger(DemoAnimals.class);
        log.info("--Start program---");

        if(args.length==0)
            // Задайте в командной строке в качестве параметров имя входного файла с животными и имя файла с правилами
            log.info("Set the name of the input file with animals and the name of the file with rules as parameters on the command line.");
        else {
            /* подсчет через коллекцию животных используя интерфейс из класса*/
            log.info("counting through a collection of animals using the interface from the class");
            CollectionStrategy cAnimalColl = new CollectionStrategy();
            str = cAnimalColl.ReadAnimals(args[0],args[1]);
            log.info(str);
            /* подсчет напрямую в потоке чтения  используя интерфейс из класса*/
            log.info("counting directly in the read stream using the interface from the class");
            StreamStrategy cAmimalstr = new StreamStrategy();
            str = cAmimalstr.ReadAnimals(args[0],args[1]);
            log.info(str);

            /* то же, но с использованием паттерна "стратегия" */
            ContextCalc context = new ContextCalc();

            /* стратегия подсчета через коллекцию животных с использованием паттерна "Стратегия"*/
            log.info("counting through a collection of animals using the\"Strategy\" pattern");
            context.setStrategy(new CollectionStrategy());
            str = context.executeStrategy(args[0],args[1]);
            log.info(str);

            /* стратегия подсчета напрямую в потоке чтения с использованием паттерна "Стратегия"*/
            log.info("counting directly in the read stream using the \"Strategy\" pattern");
            context.setStrategy(new StreamStrategy());
            str = context.executeStrategy(args[0],args[1]);
            log.info(str);
            log.info("--End program---");
        }
    }

}
