package com.example.mobile3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobile3.Singleton.ProjectMap;

public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        final TextView message =  (TextView) findViewById(R.id.information);
        final Button yesButton = (Button) findViewById(R.id.yesButton);
        final Button noButton = (Button) findViewById(R.id.noButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            message.setText(extras.getString("message"));
        }

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(VerificationActivity.this, ProjectInfoActivity.class);
                infoIntent.putExtra("confirmation",true);
                startActivity(infoIntent);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(VerificationActivity.this, ProjectInfoActivity.class);
                infoIntent.putExtra("confirmation",fileList());
                startActivity(infoIntent);
            }
        });
    }
}
