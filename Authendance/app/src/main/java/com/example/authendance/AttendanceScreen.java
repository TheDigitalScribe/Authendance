package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

public class AttendanceScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_recyclerview);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new AttendedFragment(), "Attended");
        vpAdapter.addFragment(new AbsentFragment(), "Absent");

        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Retrieves module that teacher selects and allows fragments to access it
    public String getModuleName() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        return bundle.getString("MOD_ID");
    }

    //Retrieves date that teacher selects and allows fragments to access it
    public String getDate() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        return bundle.getString("DATE_PICKED");
    }
}
