package ru.gb.storage.server.services;

import java.io.IOException;

public interface AuthService {
    void start() throws IOException;
    void stop();

    String getLoginBD(String login);

    String getPasswordBD(String password);

}
