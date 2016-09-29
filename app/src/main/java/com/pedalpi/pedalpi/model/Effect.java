package com.pedalpi.pedalpi.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Effect implements Serializable {
    private final int index;
    private final String name;
    private final List<Parameter> parameters;

    private boolean status;

    public Effect(int index, JSONObject data) {
        this.index = index;
        this.name = prepareName(data);
        this.status = prepareStatus(data);
        this.parameters = prepareParameters(data);
    }

    private List<Parameter> prepareParameters(JSONObject data) {
        List<Parameter> parameters = new ArrayList<>();
        try {
            JSONArray paramsJson = data.getJSONObject("ports").getJSONObject("control").getJSONArray("input");

            for (int i = 0; i < paramsJson.length(); i++)
                parameters.add(new Parameter(i, paramsJson.getJSONObject(i)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parameters;
    }

    private String prepareName(JSONObject data) {
        try {
            return data.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "Nome não localizado";
    }

    private boolean prepareStatus(JSONObject data) {
        try {
            return data.getBoolean("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return status;
    }

    public void toggleStatus() {
        this.status = !this.status;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return this.index + " - " + this.getName() + " " + (isActive() ? "Actived" : "Disable");
    }

}
