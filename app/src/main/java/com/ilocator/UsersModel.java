package com.ilocator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersModel {


    public String username ;
    public String email ;
    public String myLocation;
    public String userId;
    public DatabaseReference mDatabase;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public UsersModel( ) {
         //   this.myLocation = point;

            this.username = getUsername();
            this.email = getEmail();
             this.userId = userId();
    }

    public String getUsername() {
       if (user!=null)
       { username = user.getDisplayName();
       return username;}
      return "";
    }

    public String userId(){
        if (user!=null)
        {

            String userId =  user.getUid();
            return userId;
        }
        return "";
    }

    public String getEmail(){
        if (user!=null)
        { username = user.getEmail();return username;}
        return "";
    }

    public void addNewUser () {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(userId).setValue(new UsersModel());
    }
}
