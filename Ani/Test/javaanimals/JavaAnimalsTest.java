package javaanimals;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class JavaAnimalsTest {

    @Test
    public void isRuleValid() {
        JavaAnimals ex = new JavaAnimals();
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
        JavaAnimals ex = new JavaAnimals();
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
        JavaAnimals instance = new JavaAnimals();

        String sRule = "маленькое";
        expResult = true;

        result = instance.executeRule(sRule, aAttr);
        assertEquals(expResult, result);

        String sRule1 = "^маленькое";
        expResult = false;

        result = instance.executeRule(sRule1, aAttr);
        assertEquals(expResult, result);

    }
}