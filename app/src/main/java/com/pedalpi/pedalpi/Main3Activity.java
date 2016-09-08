package com.pedalpi.pedalpi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pedalpi.pedalpi.componente.RotaryKnob;


public class Main3Activity extends AppCompatActivity {

    Spinner parametro1;

    ToggleButton toggleButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        // parametro 1 - plugin 1
        parametro1 = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Parâmetro_1,android.R.layout.simple_spinner_dropdown_item);
        parametro1.setAdapter(adapter);


        RotaryKnob knob = (RotaryKnob) findViewById(R.id.knob);
        knob.setImageResource(R.drawable.botao);


        //parametro 1 - plugin 1

        //parametro 2 - plugin 1
        toggleButton1 =(ToggleButton) findViewById(R.id.toggleButton);

        toggleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButton1.isChecked()){
                    String ligado = toggleButton1.getText().toString();
                    Toast.makeText(getApplicationContext(), ligado + " ligado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //parametro 2 - plugin 1
    }
}
