/**
 * Реализует одну из стратегий подсчета количества животных - подсчет животных из коллекции.
 *
 * @author Bkn
 */
package ru.bknproj.animals.strategy.collectionstrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bknproj.animals.animal.Animal;
import ru.bknproj.animals.strategy.Animals;
import ru.bknproj.animals.strategy.ReadAnimals;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;


public class CollectionStrategy extends Animals implements ReadAnimals {
    /**
     * динамический массив всех животных.
     */
    public ArrayList<Animal> arrAnimals;

    public CollectionStrategy() {
        arrAnimals = new ArrayList<>();
    }

    private Logger log = LoggerFactory.getLogger(CollectionStrategy.class);

    /**
     * читает исходный файл с атрибутами животных и сохраняет его в коллекции cAnimals.
     * @param strFile - исходный файл
     */
    void readAllAnimals (final String strFile) {
        String txtLine;

        log.info("Read file: {}", strFile);
        File fileIn = new File(strFile);
        try (BufferedReader inputVar = Files.newBufferedReader(fileIn.toPath(), Charset.forName("Cp1251"))) {
            txtLine = inputVar.readLine();
            while (txtLine != null) {
                String[] aAttr = txtLine.split(","); // получаем массив атрибутов одного животного
                insertIntoCollection(aAttr);         // записываем атрибуты животного в динамический массив
                txtLine = inputVar.readLine();
                log.info(txtLine);
            }
        } catch (FileNotFoundException ex) {
            log.error("File: {} not found", strFile);
        } catch (IOException ex) {
            log.error("IOException ", ex);
        }
    }
    /**
     * записываем атрибуты животного из aAttr в динамический массив.
     * @param aAttr - массив с атрибутами животного
     */
    public void insertIntoCollection (final String[] aAttr) {

        Animal curAni = new Animal();
        curAni.addAll(aAttr);
        // сохраняем экземпляр животного в коллекции всех животных
        arrAnimals.add(curAni);

    }

    /**
     * читает файл с правилами и выполняет их по одному (одна строка - одно правило)
     * атрибуты с функцией "и" разделяются запятой
     * несколько атрибутов могут объединяться функцией "или" (|}
     * для отрицания атрибута используется символ "^".
     * @param inputVar - имя файла
     * @return String - строка результата подсчета
     */
    public String readRules (final BufferedReader inputVar) {
        String txtLine;
        String sOut = "";
        try {
            txtLine = inputVar.readLine();             // читаем очередную строку с правилом
            while (txtLine != null) {
                sOut = sOut + "\n\rRule: " + txtLine;

                if (checker.isRuleValid(txtLine)) {
                    int cnt;
                    cnt = calculate(txtLine);              // подсчет животных с нужными атрибутами
                    sOut = sOut + " Count: " + cnt;
                } else {
                    log.info ("Syntax ERROR of the rule:'{}' in the position: {} {}", txtLine, checker.getErrorPosition (), checker.getErrorMessage ());
                }
                txtLine = inputVar.readLine();          // читаем очередную строку с правилом
            }
        }
        catch (IOException ex) {
            log.error("IOException ", ex);
        }
        return sOut;
    }
    /**
     * подсчитывает количество животных в коллекции, удовлетворяющих правилу из aAttr
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
                .filter(selectedAnimal ->
                        arrRules.stream()                                                             // перебираем нормализованные "подправила" без скобок
                                .anyMatch(arrayRule -> selectedAnimal.isRuleMatch((String) arrayRule)))   // проверяем "подправила"
                .count();
        return cnt;
    }
    /**
     * Поиск лексемы в атрибутах конкретного животного.
     * @param sRule - текущая лексема текущего правила
     * @param selectedAnimal - элемент коллекции (животное)
     * @return tue - если лексема встречается в атрибутах животного,
     *         false - если лексема не встречается ни в одном атрибуте
     */
    public boolean executeRule (final String sRule, final Animal selectedAnimal) {
        boolean isExist;

       isExist = executeRule(sRule, selectedAnimal.toArray());

        return  isExist;
    }

    /**
     * Реализация интерфейса паттерна "Стратегия".
     * @param sFileAni - имя файла с атрибутами животных
     * @param sFileRules - имя файла с правилами
     * @return String - выходная строка результата
     */
    @Override
    public String ReadAnimals(final String sFileAni, final String sFileRules) {
        String sOut = "";
        File fIn = new File(sFileRules);
        try (BufferedReader inputVar = Files.newBufferedReader(fIn.toPath(), Charset.forName("Cp1251"))) {
            try {
                readAllAnimals(sFileAni);
                sOut = readRules(inputVar);
            } finally {
                inputVar.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sOut;
    }
}
