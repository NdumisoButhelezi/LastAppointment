package com.example.lastappointment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Spinner timeSlot;
    private Button btn, btnViewBookings;
    private EditText emailInput, symptomInput;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        datePicker = findViewById(R.id.datePicker);
        timeSlot = findViewById(R.id.spinnerTimeSlots);
        emailInput = findViewById(R.id.emailInput);
        symptomInput = findViewById(R.id.symptomInput);
        btn = findViewById(R.id.btnBook);
        btnViewBookings = findViewById(R.id.btnViewBookings);

        setupDatePicker();
        setupTimeSlots();
        buttonSaveEvent();
        buttonViewBookingsEvent();

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
    }

    private void buttonViewBookingsEvent() {
        btnViewBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AdminViewActivity
                Intent intent = new Intent(MainActivity.this, AdminView.class);
                startActivity(intent);
            }
        });
    }

    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Start from the next day

        // Set the minimum date to tomorrow
        datePicker.setMinDate(calendar.getTimeInMillis());

        // Disable weekends in the DatePicker
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datePicker.getMinDate());
        while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            datePicker.setMinDate(cal.getTimeInMillis());
        }

        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            if (newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                if (newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    newDate.add(Calendar.DAY_OF_MONTH, 2); // Skip to Monday
                } else {
                    newDate.add(Calendar.DAY_OF_MONTH, 1); // Skip to Monday
                }
                datePicker.updateDate(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH));
            }
        });
    }

    private void setupTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 9; hour < 16; hour++) { // Booking times from 9 AM to 3 PM
            slots.add(String.format("%02d:%02d", hour, 0));  // e.g., 09:00
            slots.add(String.format("%02d:%02d", hour, 30)); // e.g., 09:30
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, slots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlot.setAdapter(adapter);
    }

    public void buttonSaveEvent() {
        btn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String symptom = symptomInput.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(MainActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }
            if (symptom.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your symptom details", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
            String time = timeSlot.getSelectedItem().toString();

            // Save to Firebase
            Booking booking = new Booking(email, symptom, date, time);
            databaseReference.push().setValue(booking);
            Toast.makeText(MainActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    static class Booking {
        public String email;
        public String symptom;
        public String date;
        public String time;

        public Booking(String email, String symptom, String date, String time) {
            this.email = email;
            this.symptom = symptom;
            this.date = date;
            this.time = time;
        }
    }
}