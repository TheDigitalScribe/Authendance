//This fragment shows a list of the students who have NOT attended the selected module on the selected date

package com.example.authendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Objects;

public class AbsentFragment extends Fragment {

    private FirebaseFirestore db;
    private AttendanceAdapter attendAdapter;
    private RecyclerView recyclerView;

    private String module;
    private String date;

    public AbsentFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.absent_fragment, container, false);
        recyclerView = v.findViewById(R.id.absentRV);

        //Retrieves module and date from the AttendanceScreen class through an interface
        AttFragInterface activity = (AttFragInterface) getActivity();
        assert activity != null;
        module = activity.getModule();
        date = activity.getDate();

        getStudents();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    private void getStudents() {

        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(module)
                .collection("Date")
                .document(date)
                .collection("Students");


        Query query = moduleRef.whereEqualTo("attended", false);
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

        //Shows overall student attendance for that module
        attendAdapter.setOnItemClickListener(new AttendanceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                if(isConnectedtoInternet(Objects.requireNonNull(getActivity()))){
                    String studentID = documentSnapshot.getId();

                    Intent intent = new Intent(getActivity(), PersonalAttendance.class);
                    intent.putExtra("STU_ID", studentID);
                    intent.putExtra("MOD_ID", module);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity(), "Please connect to internet to see student attendance", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Allows the teacher to manually set the user as present and updates their attendance record
        attendAdapter.setOnItemLongClickListener(new AttendanceAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {

                if (isConnectedtoInternet(Objects.requireNonNull(getActivity()))) {


                    final String studentID = documentSnapshot.getId();

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    builder.setTitle("Set Attendance");
                    builder.setMessage("Do you want to set the student as present?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DocumentReference documentReference = db.collection("School")
                                    .document("0DKXnQhueh18DH7TSjsb")
                                    .collection("Attendance")
                                    .document(module)
                                    .collection("Date")
                                    .document(date)
                                    .collection("Students")
                                    .document(studentID);

                            documentReference.update("attended", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.d("ATT", "Success");

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error updating database", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            String docName = module.replaceAll("\\s+", "") + date.replaceAll("\\s+", "");

                            DocumentReference docRef = db.collection("School")
                                    .document("0DKXnQhueh18DH7TSjsb")
                                    .collection("AttendanceRecord")
                                    .document(studentID)
                                    .collection("Records")
                                    .document(docName);

                            docRef.update("attended", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ATT", "Personal attendance updated");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ATT", "Failed to update");
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    Toast.makeText(getActivity(), "Please connect to internet to set attendance", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            attendAdapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        attendAdapter.stopListening();
    }
}