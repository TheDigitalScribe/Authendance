package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class AttendanceScreen extends AppCompatActivity implements AttFragInterface {

    String module;
    String date;

    Toolbar toolbar;
    TextView toolbarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_recyclerview);

        toolbar = findViewById(R.id.toolbar);
        toolbarText = toolbar.findViewById(R.id.attendanceToolbarTV);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new AttendedFragment(), "Attended");
        vpAdapter.addFragment(new AbsentFragment(), "Absent");

        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        module = bundle.getString("MOD_ID");
        date = bundle.getString("DATE_PICKED");

        toolbarText.setText(module);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String getDate() {
        return date;
    }
}
