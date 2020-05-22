package com.example.sih;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    ImageView bgapp;
    Animation bganim;
    TextView GovText, NonText, TenderText, FreeText;
    Animation frombotton;
    ImageButton Gov, Non_Gov, Tenders, Free_Lancing;
    RelativeLayout menus;
    DatabaseReference reff;
    String phone, S, M, user_name, check, lang, X;
    Menu menu1;
    Boolean English = true;
    int i, j, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences1 = getSharedPreferences(M,j);
        check = preferences1.getString("Lang","Eng");
        SharedPreferences preferences = getSharedPreferences(S,i);
        phone = preferences.getString("Phone","");
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Firebase.setAndroidContext(this);
        frombotton = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        GovText = findViewById(R.id.govText);
        NonText = findViewById(R.id.nonText);
        TenderText = findViewById(R.id.tenderText);
        FreeText = findViewById(R.id.freeText);
        bgapp = findViewById(R.id.bgapp);
        menus = findViewById(R.id.menus);
        Gov = findViewById(R.id.gov);
        Non_Gov = findViewById(R.id.non);
        Tenders = findViewById(R.id.tenders);
        Free_Lancing = findViewById(R.id.free);
        if(check.equals("Hin")){
            English = false;
            toHin();
        } else{
            English = true;
            toEng();
        }

        Gov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent govIntent = new Intent(MainActivity.this, Government.class);
                startActivity(govIntent);
            }
        });
        Non_Gov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nonIntent = new Intent(MainActivity.this, Non_Government.class);
                startActivity(nonIntent);
            }
        });
        Tenders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tenderIntent = new Intent(MainActivity.this, Tenders.class);
                startActivity(tenderIntent);
            }
        });
        Free_Lancing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent freeIntent = new Intent(MainActivity.this, Free_Lancing.class);
                startActivity(freeIntent);
            }
        });
        //An animation for 2 seconds
        bganim = AnimationUtils.loadAnimation(this, R.anim.anim);
        bgapp.animate().translationY(-2000).setDuration(800).setStartDelay(900);
        menus.startAnimation(frombotton);
        try {
            reff = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
        } catch(Exception e){
            if(check.equals("Eng") || English) {
                Toast.makeText(this, "Your account has been deleted", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, getResources().getString(R.string.account_deleted1), Toast.LENGTH_SHORT).show();
            }
        }
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    user_name = dataSnapshot.child("Username").getValue().toString();
                    SharedPreferences.Editor editor = getSharedPreferences(S, i).edit();
                    editor.putString("UName", user_name);
                    editor.apply();
                    SharedPreferences.Editor editor2 = getSharedPreferences(X, y).edit();
                    editor2.putString("isDeleted", "No");
                    editor2.apply();
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        //currently I am writing nothing here, you can write whatever you want but just inform me.
                                        return;
                                    }
                                    //your tokens will be stored as soon as you open this activity
                                    String token = task.getResult().getToken();
                                    String user_token = getString(R.string.msg_token_fmt, token);
                                    Log.d("Token", user_token);
                                    Firebase reference = new Firebase("https://smart-e60d6.firebaseio.com/Users");
                                    reference.child(phone).child("Message Token").setValue(user_token);
                                }
                            });
                    FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                } catch(Exception e){
                    if(check.equals("Eng") && English){
                        Toast.makeText(MainActivity.this, "Your account has been deleted", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.account_deleted1), Toast.LENGTH_SHORT).show();
                    }
                    SharedPreferences.Editor editor = getSharedPreferences(X, y).edit();
                    editor.putString("isDeleted", "Yes");
                    editor.apply();
                    Intent logIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(logIntent);
                    finishAffinity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(check.equals("Hin")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error1), Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(MainActivity.this, "There is some error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu1 = menu;
        getMenuInflater().inflate(R.menu.option_menu,menu);
        if(check.equals("Hin")){
            optionHin();
        }else {
            optionEng();
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.switch1:
                if(English){
                    toHin();
                    optionHin();
                    English = false;
                }else{
                    toEng();
                    optionEng();
                    English = true;
                }
                return true;
            case R.id.logout: {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                SharedPreferences.Editor editor = getSharedPreferences(S, i).edit();
                editor.putString("Status", "Null");
                editor.apply();
                finishAffinity();
                break;
            }
            case R.id.rate_us:
                Intent rateIntent = new Intent(MainActivity.this, Rating.class);
                startActivity(rateIntent);
                return true;
            case R.id.contact_us:
                String recipient = "firstloveyourself1999@gmail.com";
                String subject = "Related to Rojgar App";
                Intent intent4 = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                intent4.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
                intent4.putExtra(Intent.EXTRA_SUBJECT, subject);
                startActivity(intent4);
                return true;

            case R.id.go_to_profile:
                Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                startActivity(profileIntent);
                return true;

                case R.id.create_your_job:
                Intent jCreateIntent = new Intent(MainActivity.this, jobPublish.class);
                startActivity(jCreateIntent);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
    public void toHin(){
        GovText.setText(R.string.government_jobs1);
        NonText.setText(R.string.non_government_jobs1);
        TenderText.setText(R.string.tenders1);
        FreeText.setText(R.string.freelancing1);
        lang = "Hin";
        SharedPreferences.Editor editor1 = getSharedPreferences(M,j).edit();
        editor1.putString("Lang",lang);
        editor1.apply();
    }
    public void toEng(){
        GovText.setText("Government Jobs");
        NonText.setText("Non-Government Jobs");
        TenderText.setText("Tenders");
        FreeText.setText("Freelancing");
        lang = "Eng";
        SharedPreferences.Editor editor1 = getSharedPreferences(M,j).edit();
        editor1.putString("Lang",lang);
        editor1.apply();
    }

    private void setOptionTitle(int id, String title)
    {
        MenuItem item = menu1.findItem(id);
        item.setTitle(title);
    }

    public void optionHin(){
        setOptionTitle(R.id.switch1, getResources().getString(R.string.switch1));
        setOptionTitle(R.id.rate_us, getResources().getString(R.string.rate1));
        setOptionTitle(R.id.logout, getResources().getString(R.string.logout1));
        setOptionTitle(R.id.contact_us, getResources().getString(R.string.contact_us1));
        setOptionTitle(R.id.go_to_profile, getResources().getString(R.string.go_to_profile1));
    }

    public void optionEng(){
        setOptionTitle(R.id.switch1, "Change Language");
        setOptionTitle(R.id.rate_us, "Rate Us");
        setOptionTitle(R.id.logout, "Logout");
        setOptionTitle(R.id.contact_us, "Contact Us");
        setOptionTitle(R.id.go_to_profile, "Go To Profile");
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences1 = getSharedPreferences(M,j);
        check = preferences1.getString("Lang","Eng");
        if(check.equals("Hin")){
            English = false;
            toHin();
            try{
                optionHin();
            } catch(Exception e){
            }
        } else{
            English = true;
            toEng();
            try{
                optionEng();
            } catch(Exception e){

            }
        }
    }
}
