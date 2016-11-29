package com.pedalpi.pedalpi.component;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pedalpi.pedalpi.R;
import com.pedalpi.pedalpi.model.Parameter;


public class ParamSeekbar {


    public interface ParamValueChangeListener {
        void onChange(Parameter parameter);
    }

    private final Parameter parameter;
    private final SeekBar seekBar;

    private final TextView viewValueCenter;

    private ParamValueChangeListener listener = new ParamValueChangeListener() {
        @Override
        public void onChange(Parameter parameter) {

        }
    };

    public ParamSeekbar(LinearLayout container, final Parameter parameter, LinearLayout.LayoutParams layoutParams) {
        this.parameter = parameter;

        Context context = container.getContext();

        final TextView viewName = new TextView(context);
        viewName.setText(parameter.getName());
        viewName.setBackgroundColor(Color.rgb(138,43,226));
        viewName.setTextAppearance(context, R.style.ParametersName);
        viewName.setGravity(Gravity.CENTER);

        this.viewValueCenter = new TextView(context);
        viewValueCenter.setBackgroundColor(Color.rgb(138,43,226));
        viewValueCenter.setTextAppearance(context, R.style.ParametersPercent);
        viewValueCenter.setGravity(Gravity.CENTER);

        this.seekBar = new SeekBar(context);
        seekBar.setBackgroundColor(Color.rgb(138,43,226));


        int valueParameter = calculatePercent(getValue());
        seekBar.setProgress(valueParameter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    parameter.setValue(calculateParamValue(progress));
                    update();
                    Log.i("PROGRESS", "");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    update();
                    Log.i("STOPP", "");

            }

            private void update() {
                refreshView();
                listener.onChange(parameter);
            }
        });

        container.addView(viewName, layoutParams);
        container.addView(viewValueCenter, layoutParams);
        container.addView(seekBar, layoutParams);

        viewValueCenter.setText(valueParameter + "%");
    }

    public float getMinimum() {
        return (float) parameter.getMinimum();
    }

    public float getMaximum() {
        return (float) parameter.getMaximum();
    }

    public float getValue() {
        return (float) parameter.getValue();
    }

    private double calculateParamValue(int progress) {
        return (progress*getMaximum() + (100-progress) * getMinimum())/100.0;
    }

    private int calculatePercent(double currentValue) {
        return (int) ((currentValue - getMinimum()) * 100 / (getMaximum()-getMinimum()));
    }

    public void refreshView() {
        int percent = calculatePercent(parameter.getValue());

        this.viewValueCenter.setText(percent + "%");
        seekBar.setProgress(percent);
    }

    public void setListener(ParamValueChangeListener listener) {
        this.listener = listener;
    }
}
