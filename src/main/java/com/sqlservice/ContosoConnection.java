package com.sqlservice;


import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class ContosoConnection {
    public static final String URL = "jdbc:sqlserver://localhost:1433;" +
            "database=ProgrammingAssignment;" +
            "user=grupp7;" +
            "password=G00dPassword;";


    /**
     * Get a connection to database
     * @return Connection object
     */
    public static Connection getConnection() throws SQLException{

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
        return DriverManager.getConnection(URL);

    }

    public static void ConnectionClose(ResultSet rs) throws SQLException{
        Statement statement = rs.getStatement();
        Connection connection = statement.getConnection();
        rs.close();
        statement.close();
        connection.close();
    }

    /**
     * Test Connection
     */
    public static void main(String[] args) {
        try(Connection connection = getConnection()){

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }


}
