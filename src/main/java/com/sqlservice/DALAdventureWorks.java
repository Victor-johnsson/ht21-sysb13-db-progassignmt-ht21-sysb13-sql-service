package com.sqlservice;

import java.sql.*;

public class DALAdventureWorks {
    public ResultSet getRS(String query) throws SQLException{
        Connection connection = ContosoConnection.getConnectionAdventureWorks();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    public ResultSet getAllKeys() throws SQLException{
        String query = "SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE";
        return getRS(query);
    }

    public ResultSet getCustomerContent() throws SQLException {
        String query = "SELECT * FROM SalesLT.Customer c " +
                "JOIN SalesLT.CustomerAddress a ON c.CustomerID = a.CustomerID " +
                "JOIN SalesLT.SalesOrderHeader h ON c.CustomerID = h.CustomerID";
        return getRS(query);
    }

    public ResultSet getTableConstraints() throws SQLException{
        String query = "SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS";
        return getRS(query);
    }

    public ResultSet getAllTables() throws SQLException{
        String query = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE  = 'Base Table'";
        return getRS(query);
    }

    public ResultSet getCustomerColumns() throws SQLException{
        String query = "SELECT *\n" +
                "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                "WHERE TABLE_SCHEMA = 'SalesLT'\n" +
                "AND TABLE_NAME = 'Customer'";
        return getRS(query);
    }

    public ResultSet getTableWithMostRows() throws SQLException{
        String query = "SELECT TOP(1) t.name AS TableName,\n" +
                "SUM(p.rows) AS TotalRowCount\n" +
                "FROM sys.tables  t\n" +
                "JOIN sys.partitions p\n" +
                "ON t.object_id = p.object_id\n" +
                "AND p.index_id IN ( 0, 1 )\n" +
                "GROUP BY  t.name\n" +
                "ORDER BY TotalRowCount DESC";
        return getRS(query);
    }
}
