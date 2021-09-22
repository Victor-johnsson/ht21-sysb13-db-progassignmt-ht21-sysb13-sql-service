package com.sqlservice;

import java.sql.*;

public class DataAccessLayer {

    //En metod som returnerar resultSet. Även throws SQLException. Startat en ny connection till vår databas.
    //En sträng med SQL-statement. Metoden tar in en sträng som är ett tablename. Course, Student etc.
    //preparedStatement connection, table där är strängen,
    //resultSet vi kör querien och returnerar det resultSet.
    // '?' efter FROM går endast att sätta när man gör conditions alltså efter ett WHERE statement.

    public ResultSet getAllFromTable(String tableName) throws SQLException{
        Connection connection = ContosoConnection.getConnection();
        String table = "SELECT * FROM " + tableName + ";";
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

    public int addToStudies(String studentID, String courseCode) throws SQLException{

        String query = "INSERT INTO Studies VALUES(?,?)";
        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }
    public boolean isStudentOnCourse(String studentID, String courseCode) throws SQLException{
        String query = "SELECT * FROM Studies WHERE studentID = ? AND courseCode = ?;";
        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        ResultSet resultSet = preparedStatement.executeQuery();
        int count =0;
        while (resultSet.next()){
            count++;
        }
        ContosoConnection.connectionClose(resultSet);
        if(count == 1){
            return true;
        }
        return false;
    }

    public int removeFromStudies(String studentID, String courseCode) throws SQLException{

        String query = "DELETE FROM Studies WHERE studentID = ? AND courseCode = ?;";
        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    public int addToHasStudied(String studentID, String courseCode, String grade) throws SQLException{
        String query = "INSERT INTO HasStudied VALUES(?,?,?);";
        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);
        preparedStatement.setString(3,grade);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;

    }

    public ResultSet getStudentsOnCourse(String courseCode) throws SQLException{
        String query = "SELECT * FROM Student " +
                "JOIN Studies ON Student.studentID = Studies.studentID " +
                "WHERE Studies.courseCode = ?;";

        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
}
