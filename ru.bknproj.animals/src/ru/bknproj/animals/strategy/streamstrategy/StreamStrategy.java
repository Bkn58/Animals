package ru.bknproj.animals.strategy.streamstrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bknproj.animals.strategy.Animals;
import ru.bknproj.animals.strategy.CountingAnimals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
/**
 * Реализация стратегии подсчета количества животных напрямую из входного потока
 *
 *  @author Bkn
 */
public class StreamStrategy extends Animals implements CountingAnimals {
    private static final String CP1251 = "Cp1251";
    private static final String RULE = "\n\rRule: ";
    private static final String COUNT = " count: ";
    private static final String BLANK = " ";
    private Logger log = LoggerFactory.getLogger(StreamStrategy.class);

    /**
     * Реализация интерфейса паттерна "Стратегия"
     * ведет подсчет животных из входного потока на основании файла с правилами
     * @param fileNameAni - файл входного потока с животными
     * @param fileNameRules - файл с правилами
     * @return String - выходная строка результата
     * @throws IOException
     */
    @Override
    public String countingAnimals (String fileNameAni, String fileNameRules) throws IOException {
        BufferedReader inputAnimals = null;
        BufferedReader inputRules = null;
        StringBuilder  sOut = new StringBuilder ();
        try {
            String txtLineAni;
            String txtLineRule;

            File fInRules = new File(fileNameRules);
            inputRules = Files.newBufferedReader(fInRules.toPath(), Charset.forName(CP1251));
            txtLineRule = inputRules.readLine();                            // читаем из потока Правила

            File fInAnmals = new File(fileNameAni);
            inputAnimals = Files.newBufferedReader(fInAnmals.toPath(), Charset.forName(CP1251));
            while(txtLineRule != null){
                if (checker.isRuleValid(txtLineRule)) {
                    ArrayList ArrayRules = doNormalization (txtLineRule);

                    txtLineAni = inputAnimals.readLine();
                    int cnt = 0;
                    while (txtLineAni != null) {                                // читаем из потока животных
                        boolean isExist = false;
                        for (int i=0;i<ArrayRules.size();i++) {                 // выбираем нормализованное "подправило" без скобок
                            String sRule = (String)ArrayRules.get(i);
                            String[] sAttrRule = sRule.split(",");           // получаем массив атрибутов "подправила", необходимых для выборки животных;
                            String[] sAttrAni = txtLineAni.split(",");       // получаем массив атрибутов животных
                            for (String strLexema : sAttrRule) {                // перебираем все лексемы "подправила"
                                isExist = executeRule(strLexema, sAttrAni);
                                if (!isExist)
                                    break;                                      // лексема не встретилась в атрибутах животного - прерываем цикл просмотра лексем
                            }
                            if (isExist) {
                                cnt++;                                          // все лексемы "подправила" совпали - наращиваем счетчик
                                log.debug (txtLineAni);
                                break;                                          // нет необходимости дальше смотреть "подправила"
                            }
                        }
                        txtLineAni = inputAnimals.readLine();                   // читаем из потока животных
                    }
                    sOut.append (RULE).append (txtLineRule).append (COUNT).append (cnt).append (BLANK);
                    log.debug (txtLineRule+" COUNT: {}",cnt);
                }
                else {
                    log.error ("Syntax ERROR of the rule:'{}' in the position: {} {}", txtLineRule, checker.getErrorPosition (), checker.getErrorMessage ());
                }
                txtLineRule = inputRules.readLine();                        // читаем из потока Правила
                inputAnimals.close();
                inputAnimals = Files.newBufferedReader(fInAnmals.toPath(), Charset.forName(CP1251));
            }
        } catch (IOException ex) {
            log.error("IOException ", ex);
            ex.printStackTrace();
        } finally {
            try {
                inputAnimals.close();
                inputRules.close();
            } catch (IOException ex) {
                log.error("IOException ", ex);
                ex.printStackTrace();
            }
        }
        return sOut.toString ();
    }

}
