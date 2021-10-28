package ru.bknproj.animals.strategy.collectionstrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bknproj.animals.animal.Animal;
import ru.bknproj.animals.strategy.BaseStrategy;
import ru.bknproj.animals.strategy.CountingAnimals;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 *  Реализация стратегии подсчета количества животных из коллекции.
 *
 * @author Bkn
 */

public class CollectionStrategy extends BaseStrategy implements CountingAnimals {
    private static final String CP1251 = "Cp1251";
    private static final String COUNT = " Count: ";
    private static final String COLON = ",";
    private static final String RULE = "\n\rRule: ";
    /**
     * Динамический массив всех животных.
     */
    ArrayList<Animal> arrAnimals;

    public CollectionStrategy() {
        arrAnimals = new ArrayList<>();
    }

    private Logger log = LoggerFactory.getLogger(CollectionStrategy.class);

    /**
    * Возвращает i-тый элемент коллекции.
     *
    * @param  i - номер элемента коллекции
    */
    public Animal getAnimal (int i) {
        return arrAnimals.get (i);
    }

    /**
     * Очищает коллекцию животных.
     */
    public void clearAnimals () {
        arrAnimals.clear();
    }

    /**
     * Читает файл с атрибутами животных во внутреннее представление.
     *
     * @param fileName - файл с описанием атрибутов животных.
     * @throws IOException
     */
    public void readAllAnimals (final String fileName) throws IOException {
        String txtLine;

        log.debug ("Read file: {}", fileName);
        File fileIn = new File(fileName);
        try (BufferedReader inputVar = Files.newBufferedReader(fileIn.toPath(), Charset.forName(CP1251))) {
            txtLine = inputVar.readLine();
            while (txtLine != null) {
                insertIntoCollection(txtLine.split(COLON));         // записываем атрибуты животного в динамический массив
                txtLine = inputVar.readLine();
                log.debug(txtLine);
            }
        } catch (FileNotFoundException ex) {
            log.error("File: {} not found", fileName);
        } catch (IOException ex) {
            log.error("IOException ", ex);
        }
    }
    /**
     * Записывает атрибуты животного из aAttr в динамический массив.
     *
     * @param aAttr - массив с атрибутами животного
     */
    public void insertIntoCollection (final String[] aAttr) {

        Animal curAni = new Animal();
        curAni.addAll(aAttr);
        // сохраняем экземпляр животного в коллекции всех животных
        arrAnimals.add(curAni);

    }

    /**
     * Выполняет правила из файла по одному (одна строка - одно правило)
     * атрибуты с функцией "и" разделяются запятой
     * несколько атрибутов могут объединяться функцией "или" - символ "|"
     * для отрицания атрибута используется символ "^"
     * если в строке правила синтаксическая ошибка, то обработка этого правила прерывается
     * и в log выводится диагностическое сообщение об ошибке со статусом ERROR.
     *
     * @param fileName - имя файла
     * @return String - строка результата подсчета
     * @throws IOException
     */
    public String readRules (final BufferedReader fileName) throws IOException {
        String txtLine;
        StringBuilder  sOut = new StringBuilder ();
        try {
            txtLine = fileName.readLine();             // читаем очередную строку с правилом
            while (txtLine != null) {
                sOut.append (RULE).append (txtLine);

                if (checker.isRuleValid(txtLine)) {
                    int cnt;
                    cnt = calculate(txtLine);              // подсчет животных с нужными атрибутами
                    log.debug (txtLine+" COUNT={}",cnt);
                    sOut.append (COUNT).append (cnt);
                } else {
                    log.error ("Syntax ERROR of the rule:'{}' in the position: {} {}", txtLine, checker.getErrorPosition (), checker.getErrorMessage ());
                }
                txtLine = fileName.readLine();          // читаем очередную строку с правилом
            }
        }
        catch (IOException ex) {
            log.error("IOException ", ex);
        }
        return sOut.toString ();
    }

    /**
     * Подсчитывает количество животных в коллекции, удовлетворяющих правилу из aAttr.
     *
     * @param txtRules - нормализованное правило, состоящее из лексем вида:
     * (лексема1,лексема2,лексема3|лексема4)|(лексема5,^лексема6).....
     * лексемы, перечисленные через запятую внутри скобок, являются конъюнкцией
     * знак "^" перед лексемой - отрицание (этого атрибута не должно быть у животного)
     * лексемы внутри скобок - "подправило", скобки между собой являются дизъюнкцией.
     * @return - cnt количество животных, удовлетворяющих входному правилу
     */
    public int calculate (final String txtRules) {
        ArrayList <String> arrRules = doNormalization(txtRules);
        int cnt = (int) arrAnimals.stream()                                                                    // перебираем всех животных из коллекции
                .filter(selectedAnimal -> arrRules.stream()                                                             // перебираем нормализованные "подправила" без скобок
                .anyMatch(arrayRule -> selectedAnimal.isRuleMatch((String) arrayRule)))   // проверяем "подправила"
                .count();
        return cnt;
    }

    /**
     * Поиск лексемы в атрибутах конкретного животного.
     *
     * @param sRule - текущая лексема текущего правила
     * @param selectedAnimal - элемент коллекции (животное)
     * @return tue - если лексема встречается в атрибутах животного,
     *         false - если лексема не встречается ни в одном атрибуте
     */
/*
    public boolean executeRule (final String sRule, final Animal selectedAnimal) {
        boolean isExist;

       isExist = super.executeRule(sRule, selectedAnimal.toArray());

        return  isExist;
    }
*/
    /**
     * Реализация интерфейса CountingAnimals паттерна "Стратегия".
     *
     * @param animalsFileName - имя файла с атрибутами животных
     * @param rulesFileName - имя файла с правилами
     * @return String - выходная строка результата
     * @throws IOException
     */
    @Override
    public String countingAnimals (final String animalsFileName, final String rulesFileName) throws IOException {
        String sOut = "";
        File rulesFILE = new File(rulesFileName);
        try (BufferedReader inputVar = Files.newBufferedReader(rulesFILE.toPath(), Charset.forName(CP1251))) {
            try {
                readAllAnimals(animalsFileName);
                sOut = readRules(inputVar);
            } finally {
                inputVar.close();
            }
        } catch (IOException e) {
            log.error("IOException ", e);
        }
        return sOut;
    }
}
