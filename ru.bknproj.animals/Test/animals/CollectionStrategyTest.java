package ru.bknproj.animals;

import org.junit.Test;
import ru.bknproj.animals.Strategy.CollectionStrategy.CollectionStrategy;

import static org.junit.Assert.assertEquals;

public class CollectionStrategyTest {

    @Test
    public void readAllAnimals() {
    }

    @Test
    public void insertIntoCollection() {
    }

    @Test
    public void readRules() {
    }

    @Test
    public void calculate() {
        CollectionStrategy ex = new CollectionStrategy();
        int result;
        int expResult;


        String [] aAttr = {"курица", "травоядное", "маленькое", "легкое"};
        ex.insertIntoCollection(aAttr);
        String [] aAttr1 = {"слон","тяжелое","высокое","травоядное"};
        ex.insertIntoCollection(aAttr1);
        String [] aAttr2 = {"коза","травоядное","невысокое","среднее"};
        ex.insertIntoCollection(aAttr2);
        String [] aAttr3 = {"тигр","плотоядное","невысокое","тяжелое","злое"};
        ex.insertIntoCollection(aAttr3);
        String [] aAttr4 = {"лиса","плотоядное","маленькое","среднее"};
        ex.insertIntoCollection(aAttr4);
        String [] aAttr5 = {"свинья","всеядное","невысокое","среднее"};
        ex.insertIntoCollection(aAttr5);
        String [] aAttr6 = {"еж","всеядное","маленькое","легкоe"};
        ex.insertIntoCollection(aAttr6);
        String [] aAttr7 = {"корова","травоядное","невысокое","тяжелое","желтое"};
        ex.insertIntoCollection(aAttr7);

        String sRule = "(травоядное|плотоядное,маленькое)";
        expResult = 2;
        result = ex.calculate(sRule);
        assertEquals(expResult, result);

        String sRule1 = "(травоядное)";
        expResult = 4;
        result = ex.calculate(sRule1);
        assertEquals(expResult, result);

        String sRule2 = "(всеядное,^высокое)";
        expResult = 2;
        result = ex.calculate(sRule2);
        assertEquals(expResult, result);

        String sRule3 = "(травоядное|плотоядное,маленькое)(тяжелое, высокое)";
        expResult = 3;
        result = ex.calculate(sRule3);
        assertEquals(expResult, result);

        String sRule4 = "(^высокое)";
        expResult = 7;
        result = ex.calculate(sRule4);
        assertEquals(expResult, result);

    }

    @Test
    public void executeRule() {
        boolean result;
        boolean expResult;
        String [] aAttr = {"курица", "травоядное", "маленькое", "легкое"};
        CollectionStrategy instance = new CollectionStrategy();

        String sRule = "маленькое";
        instance.insertIntoCollection(aAttr);
        expResult = true;
        result = instance.executeRule(sRule, instance.Animals.get(0));
        assertEquals(expResult, result);

        result = instance.executeRule(sRule, aAttr);
        assertEquals(expResult, result);

        instance.Animals.clear();
        sRule = "плотоядное";
        instance.insertIntoCollection(aAttr);
        expResult = false;
        result = instance.executeRule(sRule, instance.Animals.get(0));
        assertEquals(expResult, result);

        result = instance.executeRule(sRule, aAttr);
        assertEquals(expResult, result);

        instance.Animals.clear();
        sRule = "травоядное|плотоядное";
        instance.insertIntoCollection(aAttr);
        expResult = true;
        result = instance.executeRule(sRule, instance.Animals.get(0));
        assertEquals(expResult, result);

        result = instance.executeRule(sRule, aAttr);
        assertEquals(expResult, result);


        instance.Animals.clear();
        sRule = "^невысокое";
        instance.insertIntoCollection(aAttr);
        //instance.displayAll(instance.Animals);
        expResult = true;
        result = instance.executeRule(sRule, aAttr);
        assertEquals(expResult, result);

        result = instance.executeRule(sRule, instance.Animals.get(0));
        assertEquals(expResult, result);
    }

    @Test
    public void readAndExecute() {
    }
}