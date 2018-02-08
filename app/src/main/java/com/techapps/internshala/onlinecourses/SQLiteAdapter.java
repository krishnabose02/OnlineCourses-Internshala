package com.techapps.internshala.onlinecourses;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Aditya on 26-01-2018.
 */

public class SQLiteAdapter extends SQLiteOpenHelper {

public static final String DATABASE_NAME = "course.db";
public static final String STUDENT_TABLE_NAME = "student_table";
public static final String COURSE_TABLE_NAME = "course_table";

public static final String COL1_STUD = "id";
public static final String COL2_STUD = "email";
public static final String COL3_STUD = "password";
public static final String COL4_STUD = "courses";


public static final String COL1_COURSE = "id";
public static final String COL2_COURSE = "title";
public static final String COL3_COURSE = "description";



    public SQLiteAdapter(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE student_table (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, courses TEXT)");
        db.execSQL("CREATE TABLE course_table (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+STUDENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+COURSE_TABLE_NAME);
        onCreate(db);
    }


    public ArrayList<Pair<String, String>> getAllEmails()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select email, password from "+STUDENT_TABLE_NAME, null);
        res.moveToFirst();
        ArrayList<Pair<String, String>> accounts = new ArrayList<>();
        if(res.getCount() == 0)
            return accounts;
        do
        {
            accounts.add(new Pair<>(res.getString(res.getColumnIndex("email")),res.getString(res.getColumnIndex("password"))));
        }while (res.moveToNext());
        res.close();
        return accounts;
    }

    public boolean addEntry(String mailID, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("email",mailID);
        cv.put("password", pass);
        cv.put("courses", "");
        long row = db.insert(STUDENT_TABLE_NAME, null, cv);
        return row!=-1;
    }

    //this course can easily add new courses to the database of existing courses, though not implemented
    //as described in the problem statement
    public boolean addCourse(String title, String desc)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("title",title);
        cv.put("description", desc);

        long row = db.insert(COURSE_TABLE_NAME, null, cv);
        return row!=-1;
    }

    public void generateCourses()
    {
        for(int i=1;i<20;i++)
        {
            addCourse("Course Title #"+i, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed");
        }
    }

    public ArrayList<CourseView> getAllCourses(Context context)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+COURSE_TABLE_NAME,null);
        res.moveToFirst();
        ArrayList<CourseView> courses = new ArrayList<>();
        if(res.getCount()==0)
            return courses;
        do {
            CourseView view = new CourseView(context,res.getString(res.getColumnIndex("title")),res.getString(res.getColumnIndex("description")),res.getString(res.getColumnIndex("id")));
            courses.add(view);
        }
        while (res.moveToNext());
        return courses;
    }

    public User fetchUser(Context context, String email) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select courses from "+STUDENT_TABLE_NAME+" where email = \'"+email+"\'", null);
        if(res.getColumnCount() == 0)
        return null;
        User user = new User(email);
        res.moveToFirst();
        String dum = res.getString(res.getColumnIndex("courses"));
        ArrayList<CourseView> c = new ArrayList<>();
        user.setCourses(c);
        if(dum.equals("")){
            return user;
        }
        String courses[] = dum.split("%");
        for( String s: courses)
        {
            Cursor course = db.rawQuery("select * from "+COURSE_TABLE_NAME+" where id = "+s, null);
            course.moveToFirst();
            if(course.getCount() == 0)
                continue;
            CourseView view = new CourseView(context,course.getString(course.getColumnIndex("title")),course.getString(course.getColumnIndex("description")),course.getString(course.getColumnIndex("id")),3);
            c.add(view);
        }
        user.setCourses(c);
        return user;
    }

    public void addCourseForUser(Context context, String user, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select courses from "+STUDENT_TABLE_NAME+" where email = \'"+user+"\'", null);
        res.moveToFirst();
        String cc = res.getString(res.getColumnIndex("courses"));
        if(cc.equals(""))
            cc = id+"";
        else
            cc = cc+ "%"+id;
        ContentValues cv = new ContentValues();
        cv.put("email",user);
        cv.put("courses", cc);

        db.update(STUDENT_TABLE_NAME, cv, "email = ?",new String[]{user});
    }

    public boolean removeCourseForUser(String id, Context context) {
        User u = User.getCurrentUser(context);
        if(u == null) return false;
        String user = u.getUserid();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select courses from "+STUDENT_TABLE_NAME+" where email = \'"+user+"\'", null);
        res.moveToFirst();
        String cc = res.getString(res.getColumnIndex("courses"));
        if(!cc.contains("%"))
        {
            //user had only one course
            cc="";
        }
        else
        {
            cc = cc.replace("%"+id,"");
        }
        ContentValues cv = new ContentValues();
        cv.put("email",user);
        cv.put("courses", cc);

        db.update(STUDENT_TABLE_NAME, cv, "email = ?",new String[]{user});
        return true;
    }
}
