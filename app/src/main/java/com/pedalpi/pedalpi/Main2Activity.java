package com.pedalpi.pedalpi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    Button botaoPlugin1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        botaoPlugin1 = (Button) findViewById(R.id.botao1);
        botaoPlugin1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.botao1:
                Intent intent = new Intent(this,Main3Activity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
