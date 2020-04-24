//This fragment shows a list of the students who have not attended the selected module on the selected date

package com.example.authendance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AttendedFragment extends Fragment {

    private FirebaseFirestore db;
    private AttendanceAdapter attendAdapter;
    private RecyclerView recyclerView;

    public AttendedFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.attended_fragment, container, false);
        recyclerView = v.findViewById(R.id.attendedRV);

        getStudents();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    private void getStudents() {

        AttendanceScreen attendanceScreen = (AttendanceScreen) getActivity();
        assert attendanceScreen != null;
        String module = attendanceScreen.getModuleName();
        String date = attendanceScreen.getDate();

        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(module)
                .collection("Date")
                .document(date)
                .collection("Students");


        Query query = moduleRef.whereEqualTo("attended", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        queryDocumentSnapshot.getId();
                    }
                } else {
                    Log.d("ATT_SCREEN", "Something went wrong");
                }
            }
        });


        FirestoreRecyclerOptions<Student> students = new FirestoreRecyclerOptions.Builder<Student>()
                .setQuery(query, Student.class)
                .build();

        attendAdapter = new AttendanceAdapter(students);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(attendAdapter);
        attendAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        attendAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        attendAdapter.stopListening();
    }
}
