package com.sqlservice;

import java.sql.*;
import java.text.DecimalFormat;

@SuppressWarnings("GrazieInspection")
public class DataAccessLayer {
    //En metod som returnerar resultSet. Även throws SQLException. Startat en ny connection till vår databas.
    //En sträng med SQL-statement. Metoden tar in en sträng som är ett tablename. Course, Student etc.
    //preparedStatement connection, table där är strängen,
    //resultSet vi kör querien och returnerar det resultSet.
    // '?' efter FROM går endast att sätta när man gör conditions alltså efter ett WHERE statement.

    //Metod som hämtar all information från ett specifikt table.
    public ResultSet getAllFromTable(String tableName) throws SQLException{
        Connection connection = ContosoConnection.getConnectionLocalDB();
        String table = "SELECT * FROM " + tableName + ";";
        PreparedStatement statement = connection.prepareStatement(table);


        return statement.executeQuery();
    }


    //Skapar en ny student.
    public int createStudent(String studentID, String studentSSN, String studentName, String studentAddress) throws SQLException{
        Connection connection = ContosoConnection.getConnectionLocalDB();

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

    //Tar bort en specifik student.
    public int deleteStudent(String studentID) throws SQLException{ //studentID kommer från tableView Student.
        String query = "DELETE FROM Student WHERE studentID = ?"; //Vi vet inte studentID:t.
        Connection connection = ContosoConnection.getConnectionLocalDB();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID); //parameterindex 1 betyder att de första "?" är 1 (Alltså studentID).

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement); //Den stänger connection till databasen.
        return i; //returnerar x rader som har blivit uppdaterade.
    }

    //Skapar en ny kurs.
    public int createCourse(String courseCode, String courseName, double courseCredits) throws SQLException{
        Connection connection = ContosoConnection.getConnectionLocalDB();

        String query  = "INSERT INTO Course VALUES (?,?,?)"; //queryn för att skapa en kurs/lägga till en kurs

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        preparedStatement.setString(2,courseName);
        preparedStatement.setDouble(3,courseCredits);


        int i = preparedStatement.executeUpdate(); //returnerar en int på vilka rader den uppdaterat.
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    //Tar bort en specifik kurs.
    public int deleteCourse(String courseCode) throws SQLException{
        String query = "DELETE FROM Course WHERE courseCode = ?";
        Connection connection = ContosoConnection.getConnectionLocalDB();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    //Lägg till en student på en aktiv kurs.
    public int addToStudies(String studentID, String courseCode) throws SQLException{

        String query = "INSERT INTO Studies VALUES(?,?)";

        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    //Kontrollerar om en student studerar en specifik kurs.
    public boolean isStudentOnCourse(String studentID, String courseCode) throws SQLException{
        String query = "SELECT * FROM Studies WHERE studentID = ? AND courseCode = ?;"; //PK är stundetID och courseCode composite
        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        ResultSet resultSet = preparedStatement.executeQuery();
        int count = 0;
        while (resultSet.next()){
            count++;
        }
        ContosoConnection.connectionClose(resultSet);
        //studenten studerar kursen
        return count == 1;//studerar inte kursen
    }

    //Kontrollerar om en student har fått ett betyg tidigare i kursen.
    public boolean hasPreviousGrade(String studentID, String courseCode) throws SQLException{
        String query = "SELECT * FROM HasStudied WHERE studentID = ? AND courseCode = ?;"; //PK är stundetID och courseCode composite
        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        ResultSet resultSet = preparedStatement.executeQuery();
        int count = 0;
        while (resultSet.next()){
            count++;
        }
        ContosoConnection.connectionClose(resultSet);
        return count == 1;
    }



    //Metod som tar bort en student från en aktiv kurs.
    public int removeFromStudies(String studentID, String courseCode) throws SQLException{

        String query = "DELETE FROM Studies WHERE studentID = ? AND courseCode = ?;";
        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    //Lägger till studenter som har studerat färdigt en kurs.
    public int addToHasStudied(String studentID, String courseCode, String grade) throws SQLException{
        String query = "INSERT INTO HasStudied VALUES(?,?,?);";
        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);
        preparedStatement.setString(2,courseCode);
        preparedStatement.setString(3,grade);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    //Uppdaterar en students betyg
    public int updateGrade(String studentID, String courseCode, String grade) throws SQLException{
        String query = "UPDATE HasStudied SET grade = ? WHERE studentID = ? AND courseCode = ? ;";
        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,grade);
        preparedStatement.setString(2,studentID);
        preparedStatement.setString(3,courseCode);

        int i = preparedStatement.executeUpdate();
        ContosoConnection.connectionClose(preparedStatement);
        return i;
    }

    //Kontrollerar studenter som studerar kursen just nu.
    public ResultSet getStudentsOnCourse(String courseCode) throws SQLException{
        String query =
                "SELECT Student.studentID, Student.studentName FROM Student " +
                "JOIN Studies ON Student.studentID = Studies.studentID " +
                "WHERE Studies.courseCode = ?;";

        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);

        return preparedStatement.executeQuery();
    }

// Hittar aktiva kurser för en given student
    public ResultSet getActiveCoursesForStudent(String studentID) throws SQLException{
        String query =
                "SELECT Course.courseCode, Course.courseName FROM Course " +
                        "JOIN Studies ON Course.courseCode = Studies.courseCode " +
                        "WHERE Studies.studentID = ?;";

        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);

        return preparedStatement.executeQuery();
    }

    // Ger alla kurser en specifik student studerat (med betyg)
    public ResultSet getCompletedCoursesForStudent(String studentID) throws SQLException{
        String query =
                "SELECT Course.courseCode, Course.courseName, HasStudied.grade FROM Course " +
                        "JOIN HasStudied ON Course.courseCode = HasStudied.courseCode " +
                        "WHERE HasStudied.studentID = ?;";

        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,studentID);

        return preparedStatement.executeQuery();
    }

    //Hämtar studenter som har studerat färdigt en kurs.
    public ResultSet getStudentsWhoHaveStudied(String courseCode) throws SQLException{
        String query =
                        "SELECT Student.studentID, Student.studentName, HasStudied.grade FROM Student " +
                        "JOIN HasStudied ON Student.studentID = HasStudied.studentID " +
                        "WHERE HasStudied.courseCode = ?;";

        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        return preparedStatement.executeQuery();
    }

    //Räknar ut antal % som fått ett visst betyg.
    public String percentageOfGrade(String courseCode, String grade) throws SQLException{
        DecimalFormat df = new DecimalFormat("#.#"); //Formatering med 1 decimal.

        String query = "SELECT courseCode,grade, count(*) * 100.0 / (SELECT count(*) FROM HasStudied WHERE courseCode = ?) " +
                " FROM HasStudied " +
                " GROUP BY grade, courseCode " +
                " HAVING courseCode = ? AND grade = ?;";
        //efter "/" = hur många betyg som finns på denna kursen
        //till vänster om "/" = hur många som fått ut ett specifikt betyg

        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        preparedStatement.setString(2,courseCode);
        preparedStatement.setString(3,grade);
        //3 olika frågetecken i statement. Ett på första raden, två och tre är på sista raden.

        ResultSet resultSet = preparedStatement.executeQuery();

        double d = 0;
        while (resultSet.next()){ //En rad eller noll rader.
            d = Double.parseDouble(resultSet.getString(3)); // Är där 0 kommer värdet vara 0, då är de 0 % som fått betyget.
        }
        ContosoConnection.connectionClose(resultSet);

        return df.format(d); //Det är värdet vi får ut, formaterat med 1 decimal.
    }

    //Metod som returnerar antal credits för en specifik student inklusive den kurs som man försöker lägga till.Antalet maxcredits är 45.
    public double potentialCredits(String courseCode, String studentID) throws SQLException{
        String query = "SELECT SUM(courseCredits) + (SELECT courseCredits FROM course WHERE courseCode = ?) " +
                " FROM Studies " +
                " JOIN Course on Studies.courseCode = Course.courseCode " +
                " WHERE StudentID = ?";

        Connection connection = ContosoConnection.getConnectionLocalDB();
        PreparedStatement preparedStatement= connection.prepareStatement(query);
        preparedStatement.setString(1,courseCode);
        preparedStatement.setString(2,studentID);

        ResultSet resultSet = preparedStatement.executeQuery(); //KOLLA om överflödigt!!!
        double d = 0;
        while (resultSet.next()){//En rad eller noll rader.
            if(resultSet.getString(1) == null){
                d=0;
            }else{
                d = Double.parseDouble(resultSet.getString(1));
            }
             // Är där 0 kommer värdet vara 0, då är de 0 % som fått betyget.
        }

        ContosoConnection.connectionClose(resultSet);
        return d;
    }

    public ResultSet getTopThroughput() throws SQLException{
        Connection connection = ContosoConnection.getConnectionLocalDB();
        String query2 = "SELECT courseCode, courseName, FORMAT(throughput, '##.#') + '%' AS throughput FROM Throughput " +
                "WHERE throughput = (SELECT MAX(throughput) FROM Throughput)";
        PreparedStatement preparedStatement = connection.prepareStatement(query2);
        return preparedStatement.executeQuery(); //returnerar de kurser som har högst throughput (med kurskod, namn och throughput)
    }
}
