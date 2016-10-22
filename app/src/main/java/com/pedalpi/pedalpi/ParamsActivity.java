package com.pedalpi.pedalpi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pedalpi.pedalpi.communication.Message;
import com.pedalpi.pedalpi.communication.MessageProcessor;
import com.pedalpi.pedalpi.communication.ProtocolType;
import com.pedalpi.pedalpi.communication.Server;
import com.pedalpi.pedalpi.component.ParamSeekbar;
import com.pedalpi.pedalpi.model.Effect;
import com.pedalpi.pedalpi.model.Parameter;
import com.pedalpi.pedalpi.model.Patch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class ParamsActivity extends AppCompatActivity implements Server.OnMessageListener, ParamSeekbar.ParamValueChangeListener {

    private Patch patch;
    private Effect effect;
    private boolean messageReceived = false;

    Spinner parametro1;
    private List<Object> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);

        this.patch = (Patch) getIntent().getSerializableExtra(PatchActivity.PATCH);
        int index = getIntent().getIntExtra(EffectsActivity.EFFECT_INDEX, 0);
        this.effect = patch.getEffects().get(index);

        setEffectName(index, this.effect);

        LinearLayout container = (LinearLayout) findViewById(R.id.paramsContainer);

        this.views = new LinkedList<>();

        for (Parameter parameter : this.effect.getParameters())
            views.add(generateParameter(container, parameter));

        Server.getInstance().setListener(this);
    }

    private void setEffectName(int index, Effect effect) {
        TextView name = (TextView) findViewById(R.id.effectName);
        name.setText(index + " - " + effect.getName());
    }

    private Object generateParameter(LinearLayout container, Parameter parameter) {
        LinearLayout linearLayout = new LinearLayout(new ContextThemeWrapper(container.getContext(), LinearLayout.HORIZONTAL));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        params.setMargins(0, 20, 0, 0);

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        Object element;

        Log.i("CMBOBOX", String.valueOf(parameter.isCombobox()));
        Log.i("TOGGLE", String.valueOf(parameter.isToggle()));
        Log.i("KNOB", String.valueOf(parameter.isKnob()));

        if (parameter.isCombobox()) {

            element = createSpinner(linearLayout, parameter, layoutParams);
        }else if (parameter.isToggle()) {

            element = createButton(linearLayout, parameter, layoutParams); //createToggle();
        }else {

            element = createSeekbar(linearLayout, parameter, layoutParams);
        }
        container.addView(linearLayout);
        return element;
    }

    private View createButton(LinearLayout container, final Parameter parameter, LinearLayout.LayoutParams layoutParams) {
        Context context = container.getContext();

        final ToggleButton toggleButton = new ToggleButton(context);
        toggleButton.setText(parameter.getName());
        toggleButton.setGravity(Gravity.CENTER);
        toggleButton.setTextAppearance(context, R.style.ButtonParamToggle);

        boolean isChecked = parameter.getValue() == 1;
        toggleButton.setBackgroundColor(isChecked ? Color.GREEN: Color.RED);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                boolean isChecked = parameter.getValue() == 1;
                boolean newChecked = !isChecked;
                int newValue = newChecked ? 1 : 0;

                parameter.setValue(newValue);

                toggleButton.setText(parameter.getName());
                toggleButton.setBackgroundColor(newChecked ? Color.GREEN: Color.RED);

                updateToServer(parameter);
            }
        });

        container.addView(toggleButton, layoutParams);
        return toggleButton;
    }

    private View createSpinner(LinearLayout container, Parameter parameter, LinearLayout.LayoutParams layoutParams) {
        parametro1 = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Parâmetro_1,android.R.layout.simple_spinner_dropdown_item);
        parametro1.setAdapter(adapter);

        return parametro1;
    }

    public ParamSeekbar createSeekbar(LinearLayout container, Parameter parameter, LinearLayout.LayoutParams layoutParams) {
        ParamSeekbar seekbar = new ParamSeekbar(container, parameter, layoutParams);
        seekbar.setListener(this);

        return seekbar;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PatchActivity.PATCH, this.patch);
        intent.putExtra(PatchActivity.SETTED_CURRENT_PATCH, this.messageReceived);

        messageReceived = false;

        setResult(0, intent);
        finish();
    }

    @Override
    public void onMessage(Message message) throws JSONException {
        Log.i("MESSAGE", message.getType().toString());
        this.patch = MessageProcessor.process(message,this.patch);

        if (message.getType() == ProtocolType.PARAM){
            JSONObject data = message.getContent();
            int effectIndex = message.getContent().getInt("effect");
            int paramIndex = data.getInt("param");

            Parameter parameter = this.patch.getEffects().get(effectIndex).getParameters().get(paramIndex);
            updateParamView(parameter, this.views.get(parameter.getIndex()));
        }

    }

    private void updateParamView(Parameter parameter, final Object object) {
        Log.i("adas", parameter.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*if (parameter.isCombobox())
                    updatesValue
                else if (parameter.isToggle())
                    parameter.setValue();
                else {*/
                ParamSeekbar seekbar = (ParamSeekbar) object;
                seekbar.refreshView();
                //}
            }
        });

    }

    @Override
    public void onChange(Parameter parameter) {
        updateToServer(parameter);
    }

    private void updateToServer(Parameter parameter) {
        Message message = MessageProcessor.generateUpdateParamValue(this.effect,parameter);
        Server.getInstance().send(message);
    }
}
