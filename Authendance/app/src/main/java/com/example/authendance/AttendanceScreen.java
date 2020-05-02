package com.example.authendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.Objects;

public class AttendanceScreen extends AppCompatActivity implements AttFragInterface {

    private String module;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_recyclerview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarText = toolbar.findViewById(R.id.attendanceToolbarTV);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        vpAdapter.addFragment(new AttendedFragment(), "Attended");
        vpAdapter.addFragment(new AbsentFragment(), "Absent");

        viewPager.setAdapter(vpAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //Retrieves module and date from AttendanceSelect
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        module = bundle.getString("MOD_ID");
        date = bundle.getString("DATE_PICKED");

        toolbarText.setText(module);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //FloatingActionButton functions as an info box
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceScreen.this);
                builder.setTitle("Handling Clicks");
                builder.setMessage("Click on a student to see their attendance for the selected module. Hold down on a student to set them as present/absent.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    //Retrieves module and date and passes them over to the fragments using an interface
    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String getDate() {
        return date;
    }
}
