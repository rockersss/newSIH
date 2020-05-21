package com.example.sih;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class jobDetails extends AppCompatActivity {
    EditText title, description, experience, city, email;
    Button post;
    DatabaseReference reff;
    details details;
    int i;
    String phone, newPhone, S;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences1 = getSharedPreferences(S,i);
        phone = preferences1.getString("Phone","");

        setContentView(R.layout.activity_job_details);

        title = findViewById(R.id.editText1);
        description = findViewById(R.id.editText2);
        experience = findViewById(R.id.editText3);
        city = findViewById(R.id.editText4);
        email = findViewById(R.id.editText5);
        post = findViewById(R.id.button4);
        details = new details();
        reff = FirebaseDatabase.getInstance().getReference().child("Users");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details.setTitle(title.getText().toString().trim());
                details.setDescription(description.getText().toString().trim());
                details.setExperience(experience.getText().toString().trim());
                details.setCity(city.getText().toString().trim());
                details.setEmail(email.getText().toString().trim());

//                reff.child("Job Post").child(String.valueOf(title)).setValue(details);
                reff.child("Job Post").child(phone).setValue(details);
                Toast.makeText(jobDetails.this, "Uploaded Job Details Successfully",Toast.LENGTH_LONG).show();
            }
        });

    }
}
