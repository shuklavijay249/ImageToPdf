package com.example.imagetopdfexample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import vijay.createpdf.activity.FirstActivity;
import vijay.createpdf.activity.ImgToPdfActivity;

public class MainActivity extends AppCompatActivity {

    TextView textView1;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        textView1 = (TextView) findViewById(R.id.textView1);
//        button1 = (Button) findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, ImgToPdfActivity.class);
                startActivityForResult(intent, 2);// Activity is started with requestCode 2
            }
        });
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            String message = data.getStringExtra("PATH");
            textView1.setText(message);

        }
        if (requestCode == 1) {
            String message = data.getStringExtra("BACK");
            textView1.setText(message);
        }

    }

}
