package ru.bknproj.animals.strategy;

import ru.bknproj.animals.rulecheker.Validator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Базовый Класс для классов конкретных Стратегий
 *
 * @author Bkn
 */
public class BaseStrategy {

    public static final String PARENTHESIS = "[(].+?[)]"; //шаблон regx поиска парных скобок
    public Validator checker = new Validator();           // автомат проверки синтаксиса строк правила

    /**
     * Проверяет строку на корректный синтаксис.
     *
     * @param strRule - исходная строка с правилом
     * @return true - если синтаксис верный
     */
    public boolean isRuleValid(String strRule) {

        return this.checker.isRuleValid(strRule);
        
    }

    /**
     * Нормализует входную строку
     * ищет парные скобки и представляет результирующую последовательность
     * в виде массива строк "подправил" без скобок.
     *
     * @param inRule - входная строка
     * @return outSubRules - массив выходных строк "подправил"
     */
    
    public ArrayList doNormalization (String inRule) {
        ArrayList outSubRules = new ArrayList();
        
                boolean hasBrackets = inRule.matches(PARENTHESIS);
                if (hasBrackets){
                    // ищем парные скобки
                    Pattern patBrackets = Pattern.compile(PARENTHESIS);
                    Matcher matBrackets = patBrackets.matcher(inRule);
                    while (matBrackets.find()) {
                        int start = matBrackets.start()+1;
                        int end   = matBrackets.end()-1;
                        if (start!=end) outSubRules.add(inRule.substring(start, end));// записываем содержимое парных скобок в результат
                     }
                }
                else {
                    // парных скобок не обнаружено
                    outSubRules.add(inRule);
                }

        
        return outSubRules;
    }
    /**
     * Поиск лексемы в атрибутах конкретного животного.
     *
     * @param sLexem - текущая лексема текущего правила
     * @param selectedAnimal - массив с атрибутами животного
     * @return  tue - если лексема встречается в атрибутах животного,
     *          false - если лексема не встречается ни в одном атрибуте
     */
    public boolean compareRule (String sLexem, String[] selectedAnimal) {
        boolean isExist=false;
        String str = sLexem.trim();
        // перебираем все атрибуты животного и сравниваем ее с текущей лексемой
            for (String propAni : selectedAnimal) {
                if (str.indexOf("^")==0) { //если лексема отрицание
                    isExist = str.substring(1).contains(propAni);
                    if (isExist)
                        // содержит атрибут, которого не должно быть, дальше не смотрим
                        return !isExist;
                    else
                        // атрибут не совпал - смотрим дальше
                        isExist = true;
                }
                else {
                    isExist = str.contains(propAni);
                    if (isExist)
                        // содержит нужный атрибут, дальше не смотрим
                        return isExist;
                }
            }
            return isExist;
    }
}

