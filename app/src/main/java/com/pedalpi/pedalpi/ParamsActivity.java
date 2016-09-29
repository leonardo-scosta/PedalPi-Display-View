package com.pedalpi.pedalpi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

import com.pedalpi.pedalpi.model.Effect;
import com.pedalpi.pedalpi.model.Parameter;
import com.pedalpi.pedalpi.model.Patch;


public class ParamsActivity extends AppCompatActivity {

    private Patch patch;
    private Effect effect;

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
    }

    private void setEffectName(int index, Effect effect) {
        TextView name = (TextView) findViewById(R.id.effectName);
        name.setText(index + " - " + effect.getName());
    }

    private void gerenateParameter(LinearLayout container, Parameter parameter) {
        if (parameter.isCombobox())
            createSpinner(container, parameter);
        else if (parameter.isToggle())
            createButton(container, parameter); //createToggle();
        else
            createSeekbar(container, parameter);
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
        viewName.setTextAppearance(getApplicationContext(), R.style.Parameters);
        viewName.setGravity(Gravity.CENTER);

        final TextView viewValue = new TextView(getApplicationContext());
        viewValue.setBackgroundColor(Color.rgb(138,43,226));
        viewValue.setTextAppearance(getApplicationContext(), R.style.Parameters);
        viewValue.setGravity(Gravity.CENTER);

        SeekBar seekBar = new SeekBar(getApplicationContext());
        seekBar.setBackgroundColor(Color.rgb(138,43,226));

        final float valueMin =  (float) parameter.getMinimum();
        final float valueMax =  (float) parameter.getMaximum();
        final float valueDeault = (float) parameter.getValueDefault();

        viewValue.setText(valueDeault + "/" + valueMax);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            double currentValue;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValue = functionConvert(progress,valueMax);
                viewValue.setText(currentValue + "/" +valueMax);
                Log.i("BOTAO", "currentValue selected: " + currentValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                viewValue.setText(valueDeault + "/" + valueMax);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewValue.setText(currentValue + "/" +valueMax);
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        container.addView(viewName, layoutParams);
        container.addView(seekBar, layoutParams);
        container.addView(viewValue, layoutParams);

        return seekBar;
    }

    public double functionConvert(int progress, float valueMax){
        return (double) (progress* valueMax)/100;

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PatchActivity.PATCH, this.patch);

        setResult(0, intent);
        finish();
    }
}
