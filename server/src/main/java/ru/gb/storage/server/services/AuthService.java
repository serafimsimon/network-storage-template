package ru.gb.storage.server.services;

import java.sql.SQLException;

public interface AuthService {
    void start() throws SQLException;
    void stop();

    String getLoginPassword(String login, String password, String nikeName);
}
