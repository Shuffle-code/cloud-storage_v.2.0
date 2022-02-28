package org.example.server;

import javafx.scene.shape.Path;

import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class AuthServer {
    private static Connection connection;
    private String login;

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public static Connection connectionM(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authorization(String login, String password) {
        try {
            Connection connection = connectionM();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE login = ? AND password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            String dbLogin = resultSet.getString("login");
            String dbPassword = resultSet.getString("password");
            setLogin(dbLogin);

            if(dbLogin.equals(login) && dbPassword.equals(password)) {
                connection.close();
                return true;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
//    public String authorizationGetLogin(String login, String password) throws SQLException {
//        Connection connection = connectionM();
//        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE login = ? AND password = ?");
//        preparedStatement.setString(1, login);
//        preparedStatement.setString(2, password);
//        ResultSet resultSet = preparedStatement.executeQuery();
//        String dbLogin = resultSet.getString("login");
//        return dbLogin;
//    }


    private boolean userExists(String login, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE login = ?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            String dbLogin = resultSet.getString("login");
            if(dbLogin.equals(login)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean registration(String name, String login, String password) {
        try {
            Connection connection = connectionM();

            if (userExists(login, connection) == false){
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user (name, login, password) VALUES (?, ?, ?)");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, login);
                preparedStatement.setString(3, password);
                preparedStatement.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("save error", e);
        } finally {
            close(connection);
        } return false;
    }

}
