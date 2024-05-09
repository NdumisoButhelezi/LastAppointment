package com.example.lastappointment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminView extends AppCompatActivity {

    private ListView listView;
    private Button btnBackToMain;
    private List<String> bookingList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        // Initialize components from the layout
        listView = findViewById(R.id.listView);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Setup the ListView
        bookingList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookingList);
        listView.setAdapter(adapter);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");

        // Load bookings from Firebase
        loadBookings();

        // Setup button click listener
        btnBackToMain.setOnClickListener(v -> {
            // Finish this activity and return to the previous one in the activity stack
            finish();
        });
    }

    private void loadBookings() {
        // Example: Fetch bookings for a specific date
        databaseReference.child("2024-05-10").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookingDetails = snapshot.getValue(String.class);
                    bookingList.add(bookingDetails);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log error
                System.out.println("Failed to read value." + databaseError.toException());
            }
        });
    }
}