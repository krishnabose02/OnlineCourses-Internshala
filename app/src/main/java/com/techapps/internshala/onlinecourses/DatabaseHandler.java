package com.techapps.internshala.onlinecourses;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Aditya on 25-01-2018.
 */

public class DatabaseHandler {


    public static boolean isRegistered(Context context, String email)
    {
        //Database integrated
        SQLiteAdapter db = new SQLiteAdapter(context);
        ArrayList<Pair<String, String>> accounts = db.getAllEmails();
        if(accounts.size() == 0) {
            Toast.makeText(context, "No registered users found, please register", Toast.LENGTH_SHORT).show();
            return false;
        }
        for( Pair<String, String> pair : accounts)
        {
            if(pair.first.equals(email))
                return true;
        }

        return false;
    }


    public static String loginTry(Context context, String email, String pass)
    {

        //Database Integrated

        if(!isRegistered(context, email)) return "Unrecognized email";

        SQLiteAdapter db = new SQLiteAdapter(context);
        ArrayList<Pair<String, String>> accounts = db.getAllEmails();
        for(Pair<String, String> pair: accounts)
        {
            if(pair.first.equals(email))
            {
                if(!pair.second.equals(pass))
                    return "Invalid password";
                else
                    break;
            }
        }

        return null;
    }
    public static User login(Context context, String email, String pass)
    {
        if(loginTry(context, email,pass) != null)
            return null;

        // Database Integration done
        SQLiteAdapter db = new SQLiteAdapter(context);
        return db.fetchUser(context, email);
        //return new User("krishna.bose02@gmail.com");

        //return null;
    }

    public static User createUser(Context context, String mailID, String pass) {

        SQLiteAdapter adapter = new SQLiteAdapter(context);
        //Database integrated
        if(adapter.addEntry(mailID, pass))
        return new User(mailID);
        return null;
    }

    //TODO this method is hardcoded, change it
    public static ArrayList<CourseView> getAvailableCourses(Context context)
    {
        SQLiteAdapter db = new SQLiteAdapter(context);
        return db.getAllCourses(context);
        /*
        ArrayList<CourseView> courses = new ArrayList<>();
        int counter =1;
        CourseView view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter++)+"");
        courses.add(view);
        view = new CourseView(context, "Random Title", "Lorem Ipsum Description", (counter)+"");
        courses.add(view);


        return courses;

       */
    }

    public static boolean removeCourse(String id, Context context) {

        SQLiteAdapter adapter = new SQLiteAdapter(context);
        return adapter.removeCourseForUser(id, context);
    }


    public static boolean addCourse(String id, Context context) {

        SharedPreferences pref = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String user = pref.getString("user", "qqq");
        if(user.equals("qqq")) {
            //user not signed in, redirecting to login page
            context.startActivity(new Intent(context, LoginActivity.class));
            Toast.makeText(context, "You must log in to add a course", Toast.LENGTH_SHORT).show();
            //adding a pending task
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("pending", Integer.parseInt(id));
            editor.commit();
            return false;
        }
        else {
            //Database integrated
            SQLiteAdapter adapter = new SQLiteAdapter(context);
            adapter.addCourseForUser(context, user, ""+id);
            Toast.makeText(context, "Course added to your curriculum", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static ArrayList<CourseView> getCoursesForUser(String email, Context context)
    {
        SQLiteAdapter adapter = new SQLiteAdapter(context);
        User user = adapter.fetchUser(context, email);
        return user.getCourses();
    }
}
