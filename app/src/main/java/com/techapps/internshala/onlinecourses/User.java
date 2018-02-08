package com.techapps.internshala.onlinecourses;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Aditya on 25-01-2018.
 */

public class User {
private static User cur;

    public static User getCurrentUser(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String user = pref.getString("user","qqq");
        if(user.equals("qqq"))
            return null;
        return new SQLiteAdapter(context).fetchUser(context, user);
    }


public String userid;
public ArrayList<CourseView> courses;
    public User(String name)
    {
        userid = name;
    }

    public ArrayList<CourseView> getCourses() {
        return courses;
    }

    public String getUserid() {
        return userid;
    }

    public void setCourses(ArrayList<CourseView> courses) {
        this.courses = courses;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
