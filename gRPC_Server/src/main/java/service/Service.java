package service;

import com.student.grpc.Student;
import com.student.grpc.studentGrpc;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.DriverManager.getConnection;

public class Service extends studentGrpc.studentImplBase {

    String url = "jdbc:mysql://localhost:3306/grpc_project";
    String user = "root";
    String pass = "";

    @Override
    public void login(Student.LoginRequest request, StreamObserver<Student.Log_Response> responseObserver) throws SQLException {

        long  userName = request.getUsername();
        String password = request.getPassword();

        ResultSet resultSet = checkLoginInfo(userName, password);
        resultSet.next();

        int exists = resultSet.getInt(1);

        Student.Log_Response.Builder response = new Student.Log_Response.Builder();

        if (exists == 1)
        {
            response.setResponseCode(200).setResponse("Log in successful");
            System.out.println("Login Successful for user with registrationID " + userName);
        }
        else
        {
            response.setResponseCode(401).setResponse("Invalid username or password. Authentication failed. Try again.");
            System.out.println("Login failed for user with registrationID " + userName);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();

    }

    private ResultSet checkLoginInfo(long userName, String password) throws SQLException
    {
        Connection connection = getConnection(url, user, pass);
        Statement statement = connection.createStatement();

        String query = " SELECT EXISTS( SELECT registrationID, password FROM tbl_data WHERE registrationID = '"+userName+"' AND password = '"+password+"');" ;

        return statement.executeQuery(query);
    }

    @Override
    public void register(Student.RegisterRequest request, StreamObserver<Student.Reg_Response> responseObserver) throws SQLException {

        long regID = request.getRegistrationID();
        String studentName = request.getStudentName();

        String email = request.getEmail();
        String phone = request.getPhoneNumber();
        String password = request.getPassword();

        ResultSet resultSet = checkRegInfo(regID);
        resultSet.next();

        int exists = resultSet.getInt(1);

        Student.Reg_Response.Builder regResponse = new Student.Reg_Response.Builder();

            if(exists == 1)
            {
                regResponse.setResponse("User with registrationID " + regID + " is already registered.").setResponseCode(208);
                System.out.println("User with registrationID " +regID + " is already registered. ");
            }

            else
            {
                Connection connection = getConnection(url, user, pass);

                Statement statement = connection.createStatement();
                String query = "INSERT INTO tbl_data VALUES ('"+regID+"', '"+studentName+"', '"+email+"', '"+phone+"', '"+password+"'); ";

                statement.executeUpdate(query);

                regResponse.setResponse(studentName + " with registration ID " + regID + " registered successfully").setResponseCode(201);
                System.out.println("Registration Successful for new user with registrationID " + regID);
            }

        responseObserver.onNext(regResponse.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkRegInfo(long regID) throws SQLException {

        Connection connection = getConnection(url, user, pass);
        Statement statement = connection.createStatement();

        String query = " SELECT EXISTS( SELECT registrationID FROM tbl_data WHERE registrationID = '"+regID+"'); " ;

        return statement.executeQuery(query);
    }

    @Override
    public void logout(Student.LogoutRequest request, StreamObserver<Student.Log_Response> responseObserver) {
    }
}
