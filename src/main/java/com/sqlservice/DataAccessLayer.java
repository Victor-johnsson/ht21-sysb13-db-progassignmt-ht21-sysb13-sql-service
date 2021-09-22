package com.sqlservice;

import java.sql.*;

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
    //En metod som returnerar resultSet. Även throws SQLException. Startat en ny connection till vår databas.
    //En sträng med SQL-statement. Metoden tar in en sträng som är ett tablename. Course, Student etc.
    //preparedStatement connection, table där är strängen,
    //resultSet vi kör querien och returnerar det resultSet.
    // '?' efter FROM går endast att sätta när man gör conditions alltså efter ett WHERE statement.

    public ResultSet getAllFromTable(String tableName) throws SQLException{
        Connection connection = ContosoConnection.getConnection();
        String table = "SELECT studentID AS ID,studentName AS Name,studentAddress AS Address,studentSSN AS SSN FROM " + tableName + ";";
        PreparedStatement statement = connection.prepareStatement(table);
        //statement.setString(1, tableName); //test med '?'


        ResultSet rs = statement.executeQuery();

        return rs;
    }


    //NY KOD!!!
    public int createStudent(String studentID, String studentSSN, String studentName, String studentAddress) throws SQLException{
        Connection connection = ContosoConnection.getConnection();

        String query  = "INSERT INTO Student VALUES (?,?,?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,studentSSN);
        preparedStatement.setString(3,studentName);
        preparedStatement.setString(4,studentAddress);

        int i = preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
        return i;
    }

    public int deleteStudent(String studentID) throws SQLException{ //studentID kommer från tableView Student
        String query = "DELETE FROM Student WHERE studentID = ?";
        Connection connection = ContosoConnection.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    public int createCourse(String courseCode, String courseName, double courseCredit) throws SQLException{
        Connection connection = ContosoConnection.getConnection();

        String query  = "INSERT INTO Course VALUES (?,?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        preparedStatement.setString(2,courseName);
        preparedStatement.setDouble(3,courseCredit);


        int i = preparedStatement.executeUpdate(); //returnerar en int på vilka rader den uppdaterat.
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    public int deleteCourse(String courseCode) throws SQLException{
        String query = "DELETE FROM Course WHERE courseCode = ?";
        Connection connection = ContosoConnection.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }










}
