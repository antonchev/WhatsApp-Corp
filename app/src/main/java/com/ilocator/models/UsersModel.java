package com.ilocator.models;



import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class UsersModel implements Serializable {
    String id, name, email, token;

    public UsersModel() {
    }

    public UsersModel(String id, String name, String email, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String id) {
        this.token = token;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

