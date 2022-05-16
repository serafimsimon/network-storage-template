package ru.gb.storage.commons.message;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthMessage extends Message {

    private  String nikeName;
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

}
