package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentModules extends AppCompatActivity {
    private ModuleAdapter moduleAdapter;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules_recyclerview);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getModules();
    }

    private void getModules() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        Log.d("MOD_SEE", "UID: " + uid);

        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        final Query query = moduleRef.orderBy("module", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        //queryDocumentSnapshot.getId();
                        queryDocumentSnapshot.getData();
                    }
                }
            }
        });

        FirestoreRecyclerOptions<StudentModuleItem> modules = new FirestoreRecyclerOptions.Builder<StudentModuleItem>()
                .setQuery(query, StudentModuleItem.class)
                .build();

        moduleAdapter = new ModuleAdapter(modules);
        RecyclerView recyclerView = findViewById(R.id.classRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentModules.this));
        recyclerView.setAdapter(moduleAdapter);
        moduleAdapter.notifyDataSetChanged();
    }

    void show()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("사과");
        ListItems.add("배");
        ListItems.add("귤");
        ListItems.add("바나나");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                Toast.makeText(StudentModules.this, selectedText, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        moduleAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        moduleAdapter.stopListening();
    }
}

