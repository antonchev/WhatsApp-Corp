package com.ilocator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yandex.mapkit.geometry.Point;

public class UsersModel {


    public String username ;
    public String email ;
    public String myLocation;



    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public UsersModel( ) {
         //   this.myLocation = point;

            this.username = getUsername();
            this.email = getEmail();
    }

    public String getUsername() {
       if (user!=null)
       { username = user.getDisplayName();
       return username;}
      return "";
    }

    public String getEmail(){
        if (user!=null)
        { username = user.getEmail();return username;}
        return "";
    }
}
