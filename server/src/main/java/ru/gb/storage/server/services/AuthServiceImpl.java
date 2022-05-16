package ru.gb.storage.server.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthServiceImpl implements AuthService {

    private static Connection connection;
    private static PreparedStatement pr;

    public static class UserEntity {


        private final String nikeName;
        private final String login;
        private final String password;

        public UserEntity(String login, String password, String nikeName) {
            this.login = login;
            this.password = password;
            this.nikeName = nikeName;
        }
    }


    private List<UserEntity> userEntityList;


    public void AuthenticationServiceImpl() throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        pr = connection.prepareStatement("SELECT * FROM users;");
        ResultSet rs = pr.executeQuery();

        this.userEntityList = new ArrayList<>();

        while (rs.next()) {
            userEntityList.add(new UserEntity(rs.getString("login"), rs.getString("password"), rs.getNString("nikeName")));
        }

    }


    @Override
    public void start() throws SQLException {
        System.out.println("Authentication service start");

    }

    @Override
    public void stop() {
        System.out.println("Authentication service stop");
        disconnect();
    }

    public void disconnect() {
        try {
            if (pr !=null) {
                pr.close();
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            if (connection !=null) {
                connection.close();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public String getLoginPassword(String login, String password, String nikeName) {

        for (UserEntity user : userEntityList) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nikeName;
            }
        }

        return null;
    }


}
