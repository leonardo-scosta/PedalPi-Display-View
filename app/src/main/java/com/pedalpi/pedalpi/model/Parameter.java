package com.pedalpi.pedalpi.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parameter implements Serializable {
    private final int index;
    private final String name;
    private final List<String> properties;

    private double value;

    private final double minimum;
    private final double maximum;
    private final double valueDefault;

    public Parameter(int index, JSONObject data) {
        this.index = index;
        this.name = prepareName(data);

        this.minimum = prepareMinimum(data);
        this.maximum = prepareMaximum(data);
        this.valueDefault = prepareValueDefault(data);

        Double value = prepareDoubleValue(data, "value");
        this.value = value != null ? value : this.valueDefault;

        //this.units = prepareUnits(data);
        //this.symbol = prepareSymbol(data);
        //this.scalePoints = prepareScalePoints(data);
        this.properties = prepareProperties(data);
    }

    private Double prepareDoubleValue(JSONObject data, String key) {
        try {
            Object value = data.get(key);
            if (value instanceof Integer)
                return new Double((Integer) value);
            else if (value instanceof Long)
                return new Double((Long) value);
            else if (value instanceof String)
                return Double.parseDouble((String) value);
            else
                return (Double) value;
        } catch (JSONException e) {
            return null;
        }
    }

    private double prepareMinimum(JSONObject data) {
        return prepareRangesData(data, "minimum");
    }

    private double prepareMaximum(JSONObject data) {
        return prepareRangesData(data, "maximum");
    }

    private double prepareValueDefault(JSONObject data) {
        return prepareRangesData(data, "default");
    }

    private double prepareRangesData(JSONObject data, String key) {
        try {
            JSONObject ranges = data.getJSONObject("ranges");
            return prepareDoubleValue(ranges, key);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String prepareName(JSONObject data) {
        try {
            return data.getString("name");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> prepareProperties(JSONObject data) {
        List<String> properties = new ArrayList<>();
        try {
            JSONArray propertiesJson = data.getJSONArray("properties");
            for (int i=0; i<propertiesJson.length(); i++)
                properties.add(propertiesJson.getString(i));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    public boolean isCombobox() {
        return properties.contains("enumeration");
    }

    public boolean isKnob() {
        return !properties.contains("enumeration")
            && !properties.contains("toggle");
    }

    public boolean isToggle() {
        return properties.contains("toggled");
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMaximum() {
        return maximum;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getValueDefault(){return valueDefault;}

}
