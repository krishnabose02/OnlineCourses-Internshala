package com.techapps.internshala.onlinecourses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.LineNumberReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
LinearLayout right, left;
SQLiteAdapter db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new SQLiteAdapter(this);

        right = findViewById(R.id.rightpanelcourse);
        left = findViewById(R.id.leftpanelcourse);
        init();

        populateList();
    }

    private void init() {
        SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
        String user = pref.getString("user", "qqq");


        findViewById(R.id.profiler).setVisibility(View.VISIBLE);
        findViewById(R.id.signuplogo).setVisibility(View.GONE);

        if(user.equals("qqq"))
        {
            user = "user";
            findViewById(R.id.profiler).setVisibility(View.GONE);
            findViewById(R.id.signuplogo).setVisibility(View.VISIBLE);
        }
        if(user.contains("@")) user = user.substring(0,user.indexOf("@"));
        user = "Welcome, "+user+"!";
        ((TextView)findViewById(R.id.welcometext)).setText(user);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        init();
    }

    private void populateList() {
        int c = 0;
        ArrayList<CourseView> list = DatabaseHandler.getAvailableCourses(this);
        if(list.size() == 0)
        {
            SQLiteAdapter db = new SQLiteAdapter(this);
            db.generateCourses();
            list = DatabaseHandler.getAvailableCourses(this);
        }
        User user = User.getCurrentUser(this);
        ArrayList<CourseView> subscribed = new ArrayList<>();
        if(user != null)
        {
            subscribed = DatabaseHandler.getCoursesForUser(user.getUserid(), this);
        }
        for(CourseView view : list)
        {
            if(user != null)
            {
                boolean b = false;
                for(CourseView sb: subscribed)
                {
                    if(sb.id.equals(view.id)) b = true;
                }
                if(b){
                    view.setTick(1);
                }
            }
            if(c%2==1)
                left.addView(view);
            else
                right.addView(view);
            c++;
        }
    }

    public void goDash(View view) {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    public void goLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
