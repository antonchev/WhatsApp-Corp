package com.ilocator;
import com.google.firebase.auth.FirebaseUser;
import com.yandex.mapkit.geometry.Point;

public class UsersModel {

    public String username;
    public String email;
    public String myLocation;

    public UsersModel (){}

    public UsersModel(String point) {
        this.username = username;
        this.email = email;
        this.myLocation = point;
    }
}
