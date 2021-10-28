package ru.bknproj.animals.strategy.dbstrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bknproj.animals.strategy.BaseStrategy;
import ru.bknproj.animals.strategy.CountingAnimals;
import ru.bknproj.animals.strategy.streamstrategy.StreamStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *  Реализация стратегии подсчета количества животных из базы данных.
 * Для хранения свойств животных возможны 2 варианта:
 * 1. На основе таблиц в 3НФ (Zoo, Animal, Propertis, Atribut)
 * 2. На основе таблиц Animal и Features.
 * В таблице Features свойства животного хранятся в одной строке через запятую, что
 * позволяет добавлять новые свойства не используя содержимое справочников.
 *
 * @author Bkn
 */

public class DBStrategy extends BaseStrategy implements CountingAnimals  {
    private Logger log = LoggerFactory.getLogger(StreamStrategy.class);
    // Declare the JDBC objects.
    private static final String CONNECTION_URL = "jdbc:sqlserver://localhost;database=animals;integratedSecurity=true;";
    private static final String CLASS_FOR_NAME ="com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CP1251 = "Cp1251";
    private static final String RULE = "\n\rRule: ";
    private static final String COUNT = " count: ";
    private static final String BLANK = " ";
    /* скрипт при использовании таблицы Features для хранения свойств животных в строке через запятую (а-ля JSON) */
    private static final String SQL = "select an.Animal_Name+','+f.Features_Name from Animal an, Features f where an.Animal_ID=f.Animal_ID";
    /* скрипт при использовании таблиц в 3НФ */
    /*
    private static final String SQL = "select an.Animal_Name+','+ p.Proper_Name+','+ p2.Proper_Name+','+ p3.Proper_Name\n"+
    "from Zoo z,Zoo z2,Zoo z3,Animal an, Propertis p,Propertis p2,Propertis p3\n"+
    "where (an.Animal_ID=z.Animal_ID and z.Proper_ID=p.Proper_ID and p.Atribut_ID = 1) and\n"+
    "(an.Animal_ID=z2.Animal_ID and z2.Proper_ID=p2.Proper_ID and p2.Atribut_ID = 2 ) and\n"+
            "(an.Animal_ID=z3.Animal_ID and z3.Proper_ID=p3.Proper_ID and p3.Atribut_ID = 3)";
    */
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public DBStrategy ()  throws SQLException {
        // Establish the connection.
        try {
            Class.forName(CLASS_FOR_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace ();
        }
        try {
            con = DriverManager.getConnection(CONNECTION_URL);
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }
    }
    private void createResultSet () throws SQLException {
        // Create and execute an SQL statement that returns some data.
        try {
            stmt = con.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }
        try {
            rs = stmt.executeQuery(SQL);
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }
    }
    /**
     * Реализация интерфейса паттерна "Стратегия"
     * ведет подсчет животных из базы данных на основании файла с правилами.
     *
     * @param fileNameRules - файл с правилами
     * @return String - выходная строка результата
     * @throws IOException
     */
    @Override
    public String countingAnimals (String sFileAni, String fileNameRules) throws IOException {
        BufferedReader inputRules = null;
        String txtLineRule;
        String txtLineAni;
        StringBuilder  sOut = new StringBuilder ();

        File fInRules = new File(fileNameRules);
        inputRules = Files.newBufferedReader(fInRules.toPath(), Charset.forName(CP1251));
        txtLineRule = inputRules.readLine();                            // читаем из потока очередное Правило
        while(txtLineRule != null) {
            if (checker.isRuleValid(txtLineRule)) {
                ArrayList<String> ArrayRules = doNormalization (txtLineRule);
                int cnt = 0;
                // Create and execute an SQL statement that returns some data.
                try {
                    createResultSet ();
                } catch (SQLException throwables) {
                    throwables.printStackTrace ();
                }
                // Iterate through the data in the result set and display it.
                while (true) {
                    try {
                        if (!rs.next ()) break;                                 // чтение атрибутов животного из базы
                    } catch (SQLException throwables) {
                        throwables.printStackTrace ();
                    }
                    try {
                        txtLineAni = rs.getString (1);
                        String finalTxtLineAni = txtLineAni;
                        cnt += ArrayRules.stream ().filter(sRule->calculate (finalTxtLineAni, sRule)>0).count ();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace ();
                    }
                }
                sOut.append (RULE).append (txtLineRule).append (COUNT).append (cnt).append (BLANK);
                try {
                    rs.close (); // перематываем result set с животными в начало
                    rs = stmt.executeQuery (SQL);
                } catch (SQLException throwables) {
                    throwables.printStackTrace ();
                }
                log.debug (txtLineRule+" COUNT: {}",cnt);
            }
            else {
                log.error ("Syntax ERROR of the rule:'{}' in the position: {} {}", txtLineRule, checker.getErrorPosition (), checker.getErrorMessage ());
            }
            txtLineRule = inputRules.readLine();                        // читаем из потока очередное Правило
        }
        try {
            rs.close ();
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }
        inputRules.close();
        return sOut.toString ();
    }
    private int calculate (String txtLineAni, String txtRules) {
        ArrayList <String> arrRules = doNormalization(txtRules);   // получаем список строк с подправилами
        String[] sAttrAni = txtLineAni.split(",");              // получаем массив атрибутов животного
        int cnt = (int) arrRules.stream()                                                                    // перебираем всех животных из коллекции
                .filter((arrayRule ->
                    {String[] sAttrRule = arrayRule.split(",");
                     return Arrays.stream (sAttrRule).allMatch (sLexem-> compareRule (sLexem, sAttrAni));
                    }))   // проверяем лексемы (подмножества атрибутов правил, которые должны встретиться у животного)
                .count();
        return cnt;
    }

}
