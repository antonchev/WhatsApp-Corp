package com.ilocator;


import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class UsersPresenter {
  //  private static final String TAG = Debug;
    private static int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    UsersActivity view;



    public void auth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(view, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        view.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void attachView(UsersActivity usersActivity) {
        view = usersActivity;
    }



}


