package com.techapps.internshala.onlinecourses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Aditya on 25-01-2018.
 */

public class CourseView extends LinearLayout {
public String title, details, id;
int tick;
TextView titleview, detailsview;
ImageView image;
FloatingActionButton fab;
AppCompatActivity ac;

    public CourseView(Context context) {
        super(context);
    }


    public CourseView(Context context, String courseTitle, String courseDetails, int courseId, int tick)
    {
        this(context, courseTitle, courseDetails,""+courseId,2);
    }


    public CourseView(Context context, String courseTitle, String courseDetails, int courseId)
    {
        this(context, courseTitle, courseDetails,""+courseId,2);
    }

    public CourseView(Context context, String courseTitle, String courseDetails, String courseId)
    {
        this(context, courseTitle, courseDetails,courseId,2);
    }

    //tickID is 1 for a tick symbol on FAB, 2 for a add symbol, else its add, 3 for delete
    public CourseView(Context context, String courseTitle, String courseDetails, String courseId, int tickID)
    {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null)
            inflater.inflate(R.layout.course_card, this);

        title = courseTitle;
        details = courseDetails;
        id = courseId;

        if(tickID<1 || tickID>3)
            tickID = 2;
        tick = tickID;

        titleview = this.findViewById(R.id.titleview);
        detailsview = this.findViewById(R.id.detailsview);
        image = this.findViewById(R.id.image);
        fab = this.findViewById(R.id.fab);

        fillViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,8,10,2);
        this.setLayoutParams(params);
    }

    private void fillViews() {
        titleview.setText(title);
        detailsview.setText(details);

        int photo = (int)(Math.random()*10);
        if(photo>9) photo=9;

        ArrayList<Integer> img = new ArrayList<>();
        img.add(R.drawable.onlinecourse0);
        img.add(R.drawable.onlinecourse1);
        img.add(R.drawable.onlinecourse2);
        img.add(R.drawable.onlinecourse3);
        img.add(R.drawable.onlinecourse4);
        img.add(R.drawable.onlinecourse5);
        img.add(R.drawable.onlinecourse6);
        img.add(R.drawable.onlinecourse7);
        img.add(R.drawable.onlinecourse8);
        img.add(R.drawable.onlinecourse9);


        image.setImageResource(img.get(photo));
        if(tick == 1)
            fab.setImageResource(R.drawable.ic_done);
        else if(tick == 2)
            fab.setImageResource(R.drawable.ic_add);
        else if(tick == 3)
            fab.setImageResource(R.drawable.ic_delete);

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourseToList();
            }
        });
    }

    private void addCourseToList() {
        if(tick == 1)
        {
            Toast.makeText(getContext(), "This course is already on your list", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(tick == 3)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Remove Course?");
            builder.setMessage("Do you want to remove the following?\n"+title);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    removeIt();
                }
            }).setNegativeButton("No", null);
            builder.create();
            builder.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Course?");
        builder.setMessage("Do you want to add the following?\n"+title);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addIt();
            }
        }).setNegativeButton("No", null);
        builder.create();
        builder.show();
    }

    private void removeIt() {
        DatabaseHandler.removeCourse(id, getContext());
        //Toast.makeText(getContext(), "Please reload the page to update contents", Toast.LENGTH_SHORT).show();
        tick = 1;
        if(ac != null)
        {
            ac.startActivity(new Intent(ac, DashboardActivity.class));
            ac.finish();
        }
    }

    private void addIt() {
        if(DatabaseHandler.addCourse(id, getContext()))
            fab.setImageResource(R.drawable.ic_done);
        tick = 1;
    }

    public void setTick(int tick)
    {
        this.tick = tick;
        if(tick == 1)
            fab.setImageResource(R.drawable.ic_done);
        else if(tick == 2)
            fab.setImageResource(R.drawable.ic_add);
        else if(tick == 3)
            fab.setImageResource(R.drawable.ic_delete);
    }

    public void setActivityReference(AppCompatActivity activity)
    {
        ac = activity;
    }
}
