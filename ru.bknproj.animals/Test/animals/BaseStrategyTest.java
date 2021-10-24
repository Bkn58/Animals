package ru.bknproj.animals;

import org.junit.Test;
import ru.bknproj.animals.strategy.BaseStrategy;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BaseStrategyTest {

    @Test
    public void isRuleValid() {
        BaseStrategy ex = new BaseStrategy ();
        boolean actual = ex.isRuleValid("");
        boolean expected = false;
        assertEquals(expected,actual);

        actual = ex.isRuleValid("(высокое)");
        expected = true;
        assertEquals(expected,actual);

        actual = ex.isRuleValid("(высокое|тяжелое)");
        expected = true;
        assertEquals(expected,actual);

        actual = ex.isRuleValid("(высокое)(^легкое)");
        expected = true;
        assertEquals(expected,actual);

        actual = ex.isRuleValid("(высокое,тяжелое)(^легкое)");
        expected = true;
        assertEquals(expected,actual);

        actual = ex.isRuleValid("(высокое,тяжелое)(легкое,)");
        expected = false;
        assertEquals(expected,actual);
    }

    @Test
    public void doNormalization() {
        BaseStrategy ex = new BaseStrategy ();
        ArrayList result;

        ArrayList actual = ex.doNormalization("(высокое,тяжелое)(^легкое)");
        ArrayList expected = new ArrayList();
        expected.add("высокое,тяжелое");
        expected.add("^легкое");
        assertEquals(expected,actual);

        String  aAttr = "(травоядное|плотоядное,маленькое)(тяжелое, высокое)";
        ArrayList expResult = new ArrayList();
        expResult.add("травоядное|плотоядное,маленькое");
        expResult.add("тяжелое, высокое");

        result = ex.doNormalization (aAttr);
        assertEquals (expResult,result);

        String  aAttr1 = "(травоядное|плотоядное,маленькое)";
        ArrayList expResult1 = new ArrayList();
        expResult1.add("травоядное|плотоядное,маленькое");

        result = ex.doNormalization (aAttr1);
        assertEquals (expResult1,result);


    }

    @Test
    public void executeRule() {
        boolean result;
        boolean expResult;
        String [] aAttr = {"курица", "травоядное", "маленькое", "легкое"};
        BaseStrategy instance = new BaseStrategy ();

        String sRule = "маленькое";
        expResult = true;

        result = instance.compareRule (sRule, aAttr);
        assertEquals(expResult, result);

        String sRule1 = "^маленькое";
        expResult = false;

        result = instance.compareRule (sRule1, aAttr);
        assertEquals(expResult, result);

    }
}