package ru.bknproj.animals.RuleCheker;

import java.util.ArrayList;
/**
 * Автомат проверки синтаксиса строки правила
 *
 * @author Bkn
 */

public class Validator {
    
    private final ArrayList <State> programm = new ArrayList <> (); // программа автомата
    private int numCurState;                                        //номер текущего состояния автомата
    private String errorMsg;                                        // сообщение об ошибке
    private int curErrorPosition;                                   // позиция в строке, где произошла ошибка
    
    public Validator() {

        numCurState = 0;
        // программа автомата проверки синтаксиса правил
        addCondition(0, '(', 1, -1, false, false, "Ожидается открывающая скобка '('");
        addCondition(1, (char)0x16, 2, 6, false, true, "");
        addCondition(2, (char)0x16, 2, 3, false, true, "");
        addCondition(3, ')', 0, 4, false, true, "");
        addCondition(4, '|', 1, 5, false, true, "");
        addCondition(5, ',', 1, -1, false, false, "Ожидается закрывающая скобка ')',или '|', или буква или цифра");
        addCondition(6, '^', 1, -1, false, false, "Ожидается буква или цифра или знак отрицания '^");

    }
    
    public int getErrorPosition (){
        return curErrorPosition;
    }
    
    public String getErrorMessage (){
        return errorMsg;
    }
    /**
     * Добавляет в программу автомата инструкцию (состояние с условиями перехода)
     * @param condition - условие типа State
     */
    void addCondition (State condition) {
       programm.add(condition);
    }

    /**
     * Добавляет в программу автомата инструкцию (состояние с условиями перехода)
     * @param nState - номер сотояния
     * @param chWait - ожидаемый в этом состоянии символ (символ с кодом 0x16 означает любой алфавитно-цифровой)
     * @param nextIfEq - номер следующего состояния, если входной символ совпал с ожидаемым 
     * @param nextIfNotEq - номер следующего состояния, если входной символ НЕ совпал с ожидаемым ("-1" - конец работы - зафиксирована ОШИБКА)
     * @param sndToNextIfEQ - необходимость передать входной символ в следующее состояние, если символ совпал
     * @param sndToNextIfNotEQ- необходимость передать входной символ в следующее состояние, если символ НЕ совпал
     * @param ErrorMsg - сообщение об ошибке
     */
    void addCondition (int nState, char chWait, int nextIfEq, int nextIfNotEq, boolean sndToNextIfEQ, boolean sndToNextIfNotEQ, String ErrorMsg) {
       State condition = new State(nState,chWait,nextIfEq,nextIfNotEq,sndToNextIfEQ,sndToNextIfNotEQ,ErrorMsg);
       programm.add(condition);
    }

    
    /**
     * Проверяет строку на корректный синтаксис
     * @param strRule - исходная строка с правилом
     * @return true - если синтаксис верный
     */
    public boolean isRuleValid (String strRule) {
        boolean isValid=false;
        char [] chArray = strRule.toCharArray();
        curErrorPosition=0;
        for (char curCh : chArray) {
            if (!toRealizeChange(curCh)) {
                isValid=false;
                break;
            }
            isValid=true;
            curErrorPosition++;
        }
        return isValid;
    }
    
    /**
     * Реализует переход автомата в новое состояние
     * @param inChar - входной символ
     * @return true - если нет ошибки
     */
    boolean toRealizeChange (char inChar) {
        boolean isValid;
        boolean isEQ;
        State curState = programm.get(numCurState);
            if (inChar == ' ') return true;
            if (curState.charWait == (char)0x16){ // если ожидается любой алфавитно-цифровой символ
                isEQ = Character.isAlphabetic(inChar) || Character.isDigit(inChar); 
            }
            else isEQ = curState.charWait==inChar;
        
            if (isEQ) {
                numCurState=curState.nextIfEQ;
                if (curState.sndToNextIfEQ) return toRealizeChange(inChar);
                else                        isValid = true;
            }
            else {
                if (curState.nextIfNotEQ !=-1){
                    numCurState=curState.nextIfNotEQ;
                    if (curState.sndToNextIfNotEQ)  return toRealizeChange(inChar);
                    else                            isValid = true;
                }
                else {
                    errorMsg = curState.getErrorMsg();
                    numCurState = 0;
                    isValid=false;
                }
            }
        return isValid;
    }

}
