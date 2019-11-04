package com.ilocator;


import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class UsersPresenter {
  //  private static final String TAG = Debug;

    UsersActivity view;






    public void attachView(UsersActivity usersActivity) {
        view = usersActivity;
    }



}


