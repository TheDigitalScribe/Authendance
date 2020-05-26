//This fragment shows a list of the students who HAVE attended the selected module on the selected date

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

public class AttendedFragment extends Fragment {

    private FirebaseFirestore db;
    private AttendanceAdapter attendAdapter;
    private RecyclerView recyclerView;
    private String module;
    private String date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.attended_fragment, container, false);
        recyclerView = v.findViewById(R.id.attendedRV);

        db = FirebaseFirestore.getInstance();

        //Module and date retrieved from AttendanceScreen class using an interface
        AttFragInterface activity = (AttFragInterface) getActivity();
        assert activity != null;
        module = activity.getModule();
        date = activity.getDate();

        getStudents();

        return v;
    }

    private void getStudents() {

        //Determines document path for student attendance on the selected module and date
        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(module)
                .collection("Date")
                .document(date)
                .collection("Students");


        //Looks for the students who HAVE attended the module
        Query query = moduleRef.whereEqualTo("attended", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        queryDocumentSnapshot.getId();
                    }
                } else {
                    Toast.makeText(getContext(), "Query failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Builds the RecyclerView
        FirestoreRecyclerOptions<Student> students = new FirestoreRecyclerOptions.Builder<Student>()
                .setQuery(query, Student.class)
                .build();

        attendAdapter = new AttendanceAdapter(students);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(attendAdapter);
        attendAdapter.notifyDataSetChanged();

        //Goes to student's attendance record for that module
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

        //Allows the teacher to set the selected student as absent and updates the student's attendance record
        attendAdapter.setOnItemLongClickListener(new AttendanceAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {

                if (isConnectedtoInternet(Objects.requireNonNull(getActivity()))) {


                    final String studentID = documentSnapshot.getId();

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    builder.setTitle("Set Attendance");
                    builder.setMessage("Do you want to set the student as absent?");
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

                            documentReference.update("attended", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.d("ATT", "Attendance updated");

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ATT", "Error updating field");

                                        }
                                    });

                            //Removes spaces when adding document to the database so it can be queried properly
                            String docName = module.replaceAll("\\s+", "") + date.replaceAll("\\s+", "");

                            DocumentReference docRef = db.collection("School")
                                    .document("0DKXnQhueh18DH7TSjsb")
                                    .collection("AttendanceRecord")
                                    .document(studentID)
                                    .collection("Records")
                                    .document(docName);

                            docRef.update("attended", false).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                } else {
                    Toast.makeText(getActivity(), "Please connect to internet to set attendance", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isConnectedtoInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    //Starts listening for changes to the RecyclerView when Activity starts
    @Override
    public void onStart() {
        super.onStart();

        if (isConnectedtoInternet(Objects.requireNonNull(getActivity()))) {
            attendAdapter.startListening();
        }

    }

    //Stops listening for changes to the RecyclerView when Activity stops
    @Override
    public void onStop() {
        super.onStop();
        attendAdapter.stopListening();
    }
}