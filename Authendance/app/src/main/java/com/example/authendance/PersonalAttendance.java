package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

//Implements interface to pass data from this activity to both Attended and Absent fragments
public class PersonalAttendance extends AppCompatActivity implements PersonalAttFragInterface {

    private String module;
    private String studentID;

    TextView toolbarText;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_att_rv);

        //TabLayout allows the user to switch between the Attended and Absent lists using tabs
        TabLayout tabLayout = findViewById(R.id.personalAttTabs);

        //Sets up toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbarText = toolbar.findViewById(R.id.personalToolbarTV);
        setSupportActionBar(toolbar);

        //ViewPager allows the user to swipe to move from one tab to the other
        ViewPager viewPager = findViewById(R.id.personalVP);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        //Adds fragments. This splits the activity into two lists, Attended and Absent
        vpAdapter.addFragment(new PersonalAttendedFragment(), "Attended");
        vpAdapter.addFragment(new PersonalAbsentFragment(), "Absent");

        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);

        /*Retrieves the module and student ID from the StudentAttendanceScreen class
        This allows the correct attendance records to be retrieved from the Firestore database
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        module = bundle.getString("MOD_ID");
        studentID = bundle.getString("STU_ID");

        toolbarText.setText(module);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    /*These methods pass the module and student ID to the Attended and Absent fragments so the
    Firestore database can be queried and personal attendance record can be retrieved
     */
    @Override
    public String getModuleName() {
        return module;
    }

    @Override
    public String getStudentID() {
        return studentID;
    }
}
