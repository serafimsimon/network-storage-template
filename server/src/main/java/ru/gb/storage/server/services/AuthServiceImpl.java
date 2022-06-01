package ru.gb.storage.server.services;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthServiceImpl implements AuthService {

    private static Connection connection;
    private static PreparedStatement pr;

    public static class UserEntity {

        private final String login;
        private final String password;

        public UserEntity(String login, String password) {
            this.login = login;
            this.password = password;

        }
    }

    private List<UserEntity> userEntityList;

    public AuthServiceImpl() throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        pr = connection.prepareStatement("SELECT * FROM users;");
        ResultSet rs = pr.executeQuery();

        this.userEntityList = new ArrayList<>();

        while (rs.next()) {
            userEntityList.add(new UserEntity(rs.getString("login"), rs.getString("password")));
        }

    }

    @Override
    public void start() throws IOException {
        System.out.println("Start authentication service");

    }

    @Override
    public void stop() {
        System.out.println("Stop authentication service");
        disconnect();
    }

    public void disconnect() {
        try {
            if (pr != null) {
                pr.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override

    public String getLoginBD(String login) {
        for (UserEntity user : userEntityList) {

            if (user.login.equals(login))
                return user.login;
        }
        return null;
    }

    @Override
    public java.lang.String getPasswordBD(String password) {
        for (UserEntity user : userEntityList) {

            if (user.password.equals(password))
                return user.password;
        }
        return null;

    }




        }


      







