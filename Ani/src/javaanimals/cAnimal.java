package javaanimals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Bkn
 */
public class cAnimal {
     ArrayList <String> propAni;   // динамический массив свойств одного животного
    
     cAnimal (){
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
     * @param sLexem - текущая лексема текущего правила
     * @return  tue - если лексема встречается в атрибутах животного,
     *          false - если лексема не встречается ни в одном атрибуте
     */
    boolean isRuleMatch(String sLexem){
        boolean ret=false;
        String sRule = (String) sLexem;
        String[] aAttr = sRule.split(",");                   // получаем из "подправила" массив лексем, необходимых для выборки животных
        if (Arrays.stream(aAttr).allMatch(sAttr -> this.isAttribMatch(sAttr)))
            return true;
        return  ret;
    }

}
