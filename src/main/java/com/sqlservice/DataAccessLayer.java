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

    public int deleteStudent(String studentID) throws SQLException{ //studentID kommer från tableView Student.
        String query = "DELETE FROM Student WHERE studentID = ?"; //Vi vet inte studentID:t.
        Connection connection = ContosoConnection.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID); //parameterindex 1 betyder att de första "?" är 1 (Alltså studentID).

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement); //Den stänger connection till databasen.
        return i; //returnerar x rader som har blivit uppdaterade.
    }

    public int createCourse(String courseCode, String courseName, double courseCredits) throws SQLException{
        Connection connection = ContosoConnection.getConnection();

        String query  = "INSERT INTO Course VALUES (?,?,?)"; //skapar värde till course table på index visst index.

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        preparedStatement.setString(2,courseName);
        preparedStatement.setDouble(3,courseCredits);


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
        String query = "SELECT * FROM Studies WHERE studentID = ? AND courseCode = ?;"; //PK är stundetID och courseCode composite
        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        ResultSet resultSet = preparedStatement.executeQuery();
        int count = 0;
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
        String query =
                "SELECT Student.studentID, Student.studentName FROM Student " +
                "JOIN Studies ON Student.studentID = Studies.studentID " +
                "WHERE Studies.courseCode = ?;";

        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    public ResultSet getStudentsWhoHaveStudied(String courseCode) throws SQLException{
        String query =
                        "SELECT Student.studentID, Student.studentName, HasStudied.grade FROM Student " +
                        "JOIN HasStudied ON Student.studentID = HasStudied.studentID " +
                        "WHERE HasStudied.courseCode = ?;";

        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    public int totalGrades(String courseCode) throws SQLException {
        String query = "SELECT COUNT(*)" + //returnerar alltid ett resultset
                "FROM HasStudied " +
                "GROUP BY HasStudied.courseCode " +
                "HAVING HasStudied.courseCode = ?;";

        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, courseCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        int i = 0;
        while (resultSet.next()){
            i =Integer.valueOf(resultSet.getString(1));
            System.out.println(i);
        }
        ContosoConnection.connectionClose(resultSet);
        return i;
    }

    public int specificGrades(String courseCode, String grade) throws SQLException {
        String query = "SELECT COUNT(*)" + //returnerar alltid ett resultset
                "FROM HasStudied " +
                "GROUP BY HasStudied.courseCode, HasStudied.grade " +
                "HAVING HasStudied.courseCode = ? " +
                "AND grade = ?";

        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, courseCode);
        preparedStatement.setString(2, grade);
        ResultSet resultSet = preparedStatement.executeQuery();
        int i = 0;
        while (resultSet.next()){
            i =Integer.valueOf(resultSet.getString(1));
            System.out.println(i);
        }
        ContosoConnection.connectionClose(resultSet);
        return i;
    }

    public double percentageOfA(String courseCode, String grade) throws SQLException{
        String query = "SELECT courseCode,grade, count(*) * 100.0 / (SELECT count(*) FROM HasStudied WHERE courseCode = ?) " +
                " FROM HasStudied " +
                " GROUP BY grade, courseCode " +
                " HAVING courseCode=? AND grade = ?;";

        Connection connection = ContosoConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        preparedStatement.setString(2,courseCode);
        preparedStatement.setString(3,grade);

        ResultSet resultSet = preparedStatement.executeQuery();

        double d = 0;
        while (resultSet.next()){
            d = Double.valueOf(resultSet.getString(3));
        }

        return d;
    }
}
