package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

public class PersonalAttendance extends AppCompatActivity implements PersonalAttFragInterface {

    private String module;
    private String studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_att_rv);

        TabLayout tabLayout = findViewById(R.id.personalAttTabs);
        ViewPager viewPager = findViewById(R.id.personalVP);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        //Adds fragments
        vpAdapter.addFragment(new PersonalAttendedFragment(), "Attended");
        vpAdapter.addFragment(new PersonalAbsentFragment(), "Absent");

        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        module = bundle.getString("MOD_ID");
        studentID = bundle.getString("STU_ID");

        Log.d("PERS", "Module: " + module + " " + "ID: " + studentID);
    }

    @Override
    public String getModuleName() {
        return module;
    }

    @Override
    public String getStudentID() {
        return studentID;
    }
}
