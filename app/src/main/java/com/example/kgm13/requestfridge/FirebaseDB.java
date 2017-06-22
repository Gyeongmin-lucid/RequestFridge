package com.example.kgm13.requestfridge;

/**
 * Created by kgm13 on 2017-06-09.
 */

public class FirebaseDB {
    private String userName;
    private String message;

    public FirebaseDB() {
    }

    public FirebaseDB(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
