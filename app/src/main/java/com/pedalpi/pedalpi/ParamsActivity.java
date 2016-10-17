package com.pedalpi.pedalpi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pedalpi.pedalpi.communication.Message;
import com.pedalpi.pedalpi.communication.ProtocolType;
import com.pedalpi.pedalpi.communication.Server;
import com.pedalpi.pedalpi.model.Effect;
import com.pedalpi.pedalpi.model.Parameter;
import com.pedalpi.pedalpi.model.Patch;


public class ParamsActivity extends AppCompatActivity implements Server.OnMessageListener{

    private Patch patch;
    private Effect effect;
    private boolean messageReceived = false;
    public static final String PARAMETER = "PARAMETER";

    Spinner parametro1;
    ToggleButton toggleButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);

        this.patch = (Patch) getIntent().getSerializableExtra(PatchActivity.PATCH);
        int index = getIntent().getIntExtra(EffectsActivity.EFFECT_INDEX, 0);
        this.effect = patch.getEffects().get(index);

        setEffectName(index, this.effect);

        LinearLayout container = (LinearLayout) findViewById(R.id.paramsContainer);

        for (Parameter parameter : this.effect.getParameters())
            gerenateParameter(container, parameter);


        Server.getInstance().setListener(this);
    }

    private void setEffectName(int index, Effect effect) {
        TextView name = (TextView) findViewById(R.id.effectName);
        name.setText(index + " - " + effect.getName());
    }

    private void gerenateParameter(LinearLayout container, Parameter parameter) {
        LinearLayout linearLayout = new LinearLayout(new ContextThemeWrapper(container.getContext(), LinearLayout.HORIZONTAL));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        params.setMargins(0, 20, 0, 0);

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (parameter.isCombobox())
            createSpinner(linearLayout, parameter);
        else if (parameter.isToggle())
            createButton(linearLayout, parameter); //createToggle();
        else
            createSeekbar(linearLayout, parameter);

        container.addView(linearLayout);
    }

    private View createButton(LinearLayout container, Parameter parameter) {
        toggleButton1 =(ToggleButton) findViewById(R.id.toggleButton);

        toggleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (toggleButton1.isChecked()) {
                String ligado = toggleButton1.getText().toString();
                Toast.makeText(getApplicationContext(), ligado + " ligado", Toast.LENGTH_SHORT).show();
            }
            }
        });

        return toggleButton1;
    }

    private View createSpinner(LinearLayout container, Parameter parameter) {
        parametro1 = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Parâmetro_1,android.R.layout.simple_spinner_dropdown_item);
        parametro1.setAdapter(adapter);

        return parametro1;
    }

    public View createSeekbar(LinearLayout container, final Parameter parameter) {
        final TextView viewName = new TextView(getApplicationContext());
        viewName.setText(parameter.getName());
        viewName.setBackgroundColor(Color.rgb(138,43,226));
        viewName.setTextAppearance(getApplicationContext(), R.style.ParametersName);
        viewName.setGravity(Gravity.CENTER);

        final TextView viewValueCenter = new TextView(getApplicationContext());
        viewValueCenter.setBackgroundColor(Color.rgb(138,43,226));
        viewValueCenter.setTextAppearance(getApplicationContext(), R.style.ParametersPercent);
        viewValueCenter.setGravity(Gravity.CENTER);

        final float valueMin = (float) parameter.getMinimum();
        final float valueMax = (float) parameter.getMaximum();
        final float value = (float) parameter.getValue();

        SeekBar seekBar = new SeekBar(getApplicationContext());
        seekBar.setBackgroundColor(Color.rgb(138,43,226));
        seekBar.setProgress(calculePercentByRange(value, valueMin, valueMax));

        int percentDefault = calculePercentByRange(value, valueMin, valueMax);
        viewValueCenter.setText(percentDefault+"%");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            double currentValue;
            int percent;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValue = calculeSeekbarToPercent(progress, valueMax, valueMin);
                updateValues();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                updateValues();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateValues();
            }

            private void updateValues() {
                percent = calculePercentByRange(currentValue, valueMin, valueMax);
                viewValueCenter.setText(percent+"%");

                parameter.setValue(currentValue);
                updateToServer(parameter);
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        container.addView(viewName, layoutParams);
        container.addView(viewValueCenter, layoutParams);
        container.addView(seekBar, layoutParams);

        return seekBar;
    }

    private void updateToServer(Parameter parameter) {

        Log.i("effect", "" + effect.getIndex());
        Log.i("param", "" + parameter.getIndex());
        Log.i("parametro", parameter.getName() + " " + String.valueOf(parameter.getValue()));
    }

    public double calculeSeekbarToPercent(int progress, float valueMax, float valueMin){
        return (progress*valueMax + (100-progress) * valueMin)/100.0;
    }

    public int calculePercentByRange(double currentValue, double valueMin, double valueMax){
        return (int) (((currentValue - valueMin)*100/(valueMax-valueMin)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.patch = (Patch) data.getExtras().getSerializable(PatchActivity.PATCH);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PatchActivity.PATCH, this.patch);
        intent.putExtra(PatchActivity.SETTED_CURRENT_PATCH, this.messageReceived);
        //updateToServer(this.parameter);

        messageReceived = false;

        setResult(0, intent);
        finish();
    }

    @Override
    public void onMessage(Message message) {
        Log.i("MESSAGE", message.getType().toString());
        if (message.getType() == ProtocolType.PATCH) {
            this.patch = new Patch(message.getContent());
            messageReceived = true;
            onBackPressed();
        }else if(message.getType() == ProtocolType.EFFECT){

        }else if(message.getType() == ProtocolType.PARAM){

        }
    }
}
