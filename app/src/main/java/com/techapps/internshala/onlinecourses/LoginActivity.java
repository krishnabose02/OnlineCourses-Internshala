package com.techapps.internshala.onlinecourses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
boolean login = true;
EditText email, password, confirmpass;
CardView confirmButton;
TextView buttonText;
ConstraintLayout root;
CardView passcard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar = getActionBar();
        if(bar != null)
        bar.hide();
        // Set up the login form.

        root = findViewById(R.id.loginroot);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpass = findViewById(R.id.confirmpassword);
        passcard = findViewById(R.id.confirm_password_card);

        confirmButton = findViewById(R.id.signincard);
        buttonText = findViewById(R.id.signinbutton);
    }

    @Override
    public void onBackPressed()
    {
        SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("pending", -1);
        editor.commit();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void showRegister(View view) {
        passcard.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        (findViewById(R.id.loginText)).setVisibility(View.VISIBLE);
        buttonText.setText("Register me");
        login = false;
    }

    public void showLogin(View view) {
        passcard.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        (findViewById(R.id.registerText)).setVisibility(View.VISIBLE);
        buttonText.setText("Sign in");
        login = true;
    }

    public void proceed(View view) {

        //these are checking common to both login and sign up
        String mailID = email.getText().toString().trim();
        if(!mailID.contains("@") && !mailID.contains(".")) {
            email.setError("Invalid Email");
            return;
        }

        String pass = password.getText().toString();
        if(pass.isEmpty())
        {
            password.setError("This field is required");
            return;
        }

        if(login)
        {
            //proceed to log in existing user

            //verify credentials
            String error = DatabaseHandler.loginTry(this,mailID, pass);
            if(error != null)
            {
                snackIt(error);
                return;
            }
            User user = DatabaseHandler.login(this, mailID,pass);
            if(user == null)
            {
                snackIt("Login failed, try again");
                return;
            }

            SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user",mailID);
            editor.commit();

            int pendingID = pref.getInt("pending",-1);
            if(pendingID != -1)
            {
                DatabaseHandler.addCourse(pendingID+"", this);
                editor.putInt("pending", -1);
                editor.commit();
            }
            startActivity(new Intent(this, DashboardActivity.class));
            finish();

        }
        else
        {
            //register new user
            String repass = confirmpass.getText().toString();
            if(!pass.equals(repass))
            {
                confirmpass.setError("Password doesn't match");
                return;
            }

            //so password matches

            //if user have already registered and trying to re-register
            if(DatabaseHandler.isRegistered(this, mailID))
            {
                snackIt("user already exists, try logging in");
                return;
            }

            //try to make a new entry
            User current = DatabaseHandler.createUser(this, mailID, pass);
            if(current == null)
            {
                snackIt("Internal Error, please retry");
                return;
            }

            //adding the user to sharedpreference memory
            SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user",mailID);
            editor.commit();


            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }

    private void snackIt(String s) {
        Snackbar.make(root, s, Snackbar.LENGTH_SHORT).show();
    }

}

