package com.ilocator.models;



import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class Contact implements Serializable {
    String id, name, email, number;

    public Contact() {
    }

    public Contact(String id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.name = number;
    }
}

