package com.pedalpi.pedalpi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



public class Main3Activity extends AppCompatActivity {

    Spinner parametro1;
    ToggleButton toggleButton1;
    SeekBar seekBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        // parametro 1 - plugin 1
        parametro1 = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Parâmetro_1,android.R.layout.simple_spinner_dropdown_item);
        parametro1.setAdapter(adapter);


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
        seekbar();
    }
    //parametro 3 - plugin 1
    public void seekbar(){
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textView = (TextView) findViewById(R.id.textView2);
        textView.setText(seekBar.getProgress() + "/" +seekBar.getMax());



        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener(){
                    //double progress_value;
                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //progress_value = ((double) progress);
                        progress_value = progress;
                        //textView.setText(progress/100.0 + "/" +seekBar.getMax()/100);
                        textView.setText(progress + "/" +seekBar.getMax());

                        //Toast.makeText(Main3Activity.this,"Parametro alterado!",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //textView.setText(progress_value/100.0 + "/" +seekBar.getMax()/100);
                        textView.setText(progress_value + "/" +seekBar.getMax());
                    }
                }

        );
    }
    //parametro 3 - plugin 1

}
