package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Objects;

public class TeacherUsersList extends AppCompatActivity {

    private TeacherUserAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_user_rv);

        Toolbar toolbar = findViewById(R.id.toolbarTeachers);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        db = FirebaseFirestore.getInstance();

        getTeachers();

        if(!isConnectedtoInternet(TeacherUsersList.this)){
            Toast.makeText(TeacherUsersList.this, "Please connect to internet to see list", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTeachers() {

        CollectionReference userRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User");

        Query query = userRef.orderBy("teacher_id", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    Objects.requireNonNull(task.getResult());

                    //queryDocumentSnapshot.getString("student_id");
                }
                else {
                    Toast.makeText(TeacherUsersList.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        FirestoreRecyclerOptions<TeacherUsers> teachers = new FirestoreRecyclerOptions.Builder<TeacherUsers>()
                .setQuery(query, TeacherUsers.class)
                .build();


        adapter = new TeacherUserAdapter(teachers);
        RecyclerView recyclerView = findViewById(R.id.teacherUserRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherUsersList.this));
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
    protected void onStart() {
        super.onStart();
        if(isConnectedtoInternet(TeacherUsersList.this))
        {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
