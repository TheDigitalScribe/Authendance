package com.example.authendance;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class PersonalAbsentFragment extends Fragment {

    private String module;
    private String studentID;
    private PersonalAttendanceAdapter adapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    public PersonalAbsentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.personal_absent_fragment, container, false);
        recyclerView = v.findViewById(R.id.personalAbsentRV);

        db = FirebaseFirestore.getInstance();

        PersonalAttFragInterface activity = (PersonalAttFragInterface) getActivity();
        assert activity != null;
        module = activity.getModuleName();
        studentID = activity.getStudentID();

        getDates();

        return v;
    }

    private void getDates() {

        //Determines database path for student's personal attendance records
        CollectionReference recordRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("AttendanceRecord")
                .document(studentID)
                .collection("Records");

        final Query query = recordRef.whereEqualTo("module", module).whereEqualTo("attended", false);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        String date = queryDocumentSnapshot.getString("date");

                    }
                }
                else {
                    Toast.makeText(getActivity(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Builds RecyclerView
        FirestoreRecyclerOptions<Date> dates = new FirestoreRecyclerOptions.Builder<Date>()
                .setQuery(query, Date.class)
                .build();

        adapter = new PersonalAttendanceAdapter(dates);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static boolean isConnectedtoInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
        {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onStart() {
        super.onStart();

        if(isConnectedtoInternet(Objects.requireNonNull(getActivity()))){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
