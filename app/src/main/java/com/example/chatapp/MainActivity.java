package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private final String CHANNEL_ID = "Notification";
    private final int NOTIFICATION_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Creating Messages section in database
        myDatabase = FirebaseDatabase.getInstance().getReference("Message");


        // Casting  TextView
        final TextView myText = findViewById(R.id.textbox1);


        //Scrolling enable
        myText.setMovementMethod(new ScrollingMovementMethod());



        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String[] Messages = dataSnapshot.getValue().toString().split(",");
                myText.setText(""); //For clearing the display

                //Sorting the messages according to time
                Arrays.sort(Messages);

                for (int i=0; i<Messages.length;i++)
                {
                    //Splitting using "=" sign
                    String[] finalmsg = Messages[i].split("=");

                    //Displaying date and time along with message
                    myText.append(java.time.LocalDateTime.now() +":   " +finalmsg[1] + "\n \n");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //If error happens display "Cancelled"
                myText.setText("Cancelled");
            }
        });
    }

    public void sendMessage(View view)
    {

        EditText myEditText = findViewById(R.id.editText);
        Date date = new Date();
        Notify(myEditText.getText().toString());
        myDatabase.child(Long.toString(date.getTime())).setValue(myEditText.getText().toString());
        myEditText.setText("");


    }

    public void Notify(String incoming)
    {
        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_message)
        .setContentTitle("New Message")
        .setAutoCancel(true)
        .setContentText(incoming)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

}
