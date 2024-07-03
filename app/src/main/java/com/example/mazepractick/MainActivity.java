package com.example.mazepractick;



import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText higthET;
    EditText wigthET;
    MazeView mazeView;
    Button generateButon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mazeView = findViewById(R.id.mazeView);
        higthET=findViewById(R.id.HightMazeEditText);
        wigthET=findViewById(R.id.Wight_MazeEditText);
        generateButon = findViewById(R.id.GenerateButton);



        generateButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((higthET.getText()!=null && !higthET.getText().toString().isEmpty())  && (wigthET.getText()!=null && !wigthET.getText().toString().isEmpty())){
                    if (Integer.parseInt(higthET.getText().toString())<101 && Integer.parseInt(wigthET.getText().toString()) <101 ){
                    mazeView.setMazeSize(Integer.parseInt(higthET.getText().toString()), Integer.parseInt(wigthET.getText().toString())); // Установите размеры лабиринта
                         }
                }
            }
        });



    }
}