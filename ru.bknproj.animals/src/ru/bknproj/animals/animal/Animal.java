package ru.bknproj.animals.animal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Базовый класс одного животного
 *
 * @author Bkn
 */
public class Animal {
     ArrayList <String> propAni;   // динамический массив свойств одного животного
    
     public Animal(){
        propAni = new ArrayList <> (); 
     }
    /**
     * Поиск конкретного атрибута из лексемы в атрибутах конкретного животного
     * @param sAttrib - текущий атрибут текущего правила
     * @return  tue - если атрибут встречается в атрибутах животного,
     *          false - если атрибут не встречается ни в одном атрибуте
     */
    boolean isAttribMatch (String sAttrib){
        AtomicBoolean isExist= new AtomicBoolean(false);
        // перебираем все атрибуты животного и сравниваем ее с текущей лексемой
        String[] aSubLexeme = sAttrib.split("\\|");  //если в лексеме есть дизъюнкция sub-лексем (символ "|")
        Arrays.stream(aSubLexeme).forEach(str -> {
            str = str.trim();
            for (String spropAni : this.propAni) {
                if (str.indexOf("^") == 0) { //если лексема отрицание
                    if (str.substring(1).equals(spropAni)) {
                        // содержит атрибут, которого не должно быть, дальше не смотрим
                        isExist.set(false);
                        return;
                    } else
                        // атрибут не совпал - смотрим дальше
                        isExist.set(true);
                } else {
                    if (str.equals(spropAni)) {
                        // содержит нужный атрибут, дальше не смотрим
                        isExist.set(true);
                        return;
                    }
                }
            }
        });
        return isExist.get();
    }
    /**
     * Поиск лексемы в атрибутах конкретного животного
     * @param sLexeme - текущая лексема текущего правила
     * @return  tue - если лексема встречается в атрибутах животного,
     *          false - если лексема не встречается ни в одном атрибуте
     */
    public boolean isRuleMatch(String sLexeme){
        boolean ret=false;
        String sRule = sLexeme;
        String[] aAttr = sRule.split(",");                   // получаем из "подправила" массив лексем, необходимых для выборки животных
        if (Arrays.stream(aAttr).allMatch(sAttr -> this.isAttribMatch(sAttr)))
            return true;
        return  ret;
    }
    /**
     * записывает атрибуты животного из aAttr в динамический массив propAni
     * @param aAttr - массив с атрибутами животного
     */

    public void addAll (String [] aAttr) {
        propAni.addAll(Arrays.asList(aAttr));
    }

    public String[] toArray() {
        return propAni.toArray(new String [0]);
    }

}
