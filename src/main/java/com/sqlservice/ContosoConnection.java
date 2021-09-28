package com.sqlservice;


import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class ContosoConnection {
    //öppnar och stänger connections

    public static final String URL = "jdbc:sqlserver://localhost:1433;" +
            "database=ProgrammingAssignment;" +
            "user=grupp7;" +
            "password=G00dPassword;";
    //URL för databasen och servern

    public static final String URLAdventureWorks = "jdbc:sqlserver://uwdb18.srv.lu.se\\icssql001;database=AdventureWorks;user=awreader;password=aw2021";

    //@return Connection object
    
    //En funktion som returnerar en connection till URL.
    //throws = om det blir problem när vi ska göra connection(uppdatera en person), då kommer denna funktion
    //kasta ett SQLException och vi catchar det i en try catch funktion. (Connection slänger det till DAL).

    public static Connection getConnectionAdventureWorks() throws SQLException{
        return getConnection(URLAdventureWorks);
    }

    private static Connection getConnection(String urlAdventureWorks) throws SQLException {
        DriverManager.registerDriver(new Driver() {
            @Override
            public Connection connect(String url, Properties info) throws SQLException {
                return null;
            }


            @Override
            public boolean acceptsURL(String url) throws SQLException {
                return false;
            }

            @Override
            public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
                return new DriverPropertyInfo[0];
            }

            @Override
            public int getMajorVersion() {
                return 0;
            }

            @Override
            public int getMinorVersion() {
                return 0;
            }

            @Override
            public boolean jdbcCompliant() {
                return false;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }
        });
        return DriverManager.getConnection(urlAdventureWorks);
    }

    public static Connection getConnectionLocalDB() throws SQLException{


        return getConnection(URL);
    }

    //Metod som...
    public static void connectionClose(ResultSet rs) throws SQLException{ //stänger Connection för den ResultSet man stoppar in när man anropar.
        Statement statement = rs.getStatement();
        Connection connection = statement.getConnection();
        //inbyggda funktioner från JBDC-library
        rs.close();
        statement.close();
        connection.close();
        //Vi stänger i den ordning som Björn har rekommenderat.
        //När man öppnar sker det i motsatt ordning.
    }

    //Metod som...
    public static void connectionClose(Statement statement) throws SQLException{
        Connection connection = statement.getConnection();
        statement.close();
        connection.close();
    }

    //Test Connection
    public static void main(String[] args) { //Det är för att testa connection med det URL:et. Funkar det inte så får man ett error.
        try(Connection connection = getConnectionAdventureWorks()){
            System.out.println("connection worked");

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }
}
