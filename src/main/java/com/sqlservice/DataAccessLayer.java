package com.sqlservice;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

public class DataAccessLayer {


    public ResultSet getEmployeeResultSet() throws SQLException {
        Connection connection = ContosoConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Course;");
        ResultSet rs = statement.executeQuery();

        return rs;

    }

    public ResultSet getColumnsMetaData(String tableName) throws SQLException{
        DatabaseMetaData databaseMetaData = ContosoConnection.getConnection().getMetaData();
        ResultSet columns = databaseMetaData.getColumns(null,null,tableName,null);

        return columns;
    }

    public String printTableMetaData(String tableName) throws SQLException{
        Connection connection = ContosoConnection.getConnection();

        String SQL = "SELECT TOP 10 * FROM ?";
        PreparedStatement stmt = connection.prepareStatement(SQL);
        stmt.setString(1,tableName);
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        // Display the column name and type.
        String st = "";
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            st = st + "NAME: " + rsmd.getColumnName(i) + " " + "TYPE: " + rsmd.getColumnTypeName(i) + "\n";

        }
        rs.close();
        stmt.close();
        connection.close();
        return st;

    }

    public ResultSet getAllFromTable(String tableName) throws SQLException{
        Connection connection = ContosoConnection.getConnection();
        String table = "SELECT * FROM " + tableName + ";";
        PreparedStatement statement = connection.prepareStatement(table);
        //statement.setString(1, tableName);


        ResultSet rs = statement.executeQuery();

        return rs;
    }





}
