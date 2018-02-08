package com.techapps.internshala.onlinecourses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
LinearLayout leftpanel, rightpanel;



    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
        String name = pref.getString("user", "User");
        if(name.contains("@")) name = name.substring(0,name.indexOf("@"));
        ((TextView)findViewById(R.id.username)).setText(name);

        leftpanel = findViewById(R.id.coursesleft);
        rightpanel = findViewById(R.id.coursesright);

        User currentUser = User.getCurrentUser(this);
        if(currentUser == null)
        {
            Toast.makeText(this, "unknown Error", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<CourseView> courseViews = DatabaseHandler.getCoursesForUser(currentUser.getUserid(), this);
        if(courseViews.size() == 0)
        {
            Toast.makeText(this, "You have no ongoing courses", Toast.LENGTH_SHORT).show();
        }
        int c = 0;
        for(CourseView view : courseViews)
        {
            view.setActivityReference(this);
            if(c%2==0)
                leftpanel.addView(view);
            else
                rightpanel.addView(view);
            c++;
        }
    }

    public void logOut(View view) {
        SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user","qqq");
        editor.commit();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
