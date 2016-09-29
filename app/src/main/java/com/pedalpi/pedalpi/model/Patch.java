package com.pedalpi.pedalpi.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Patch implements Serializable {
    private final String name;
    private final List<Effect> effects;

    public Patch(JSONObject data) {
        this.name = prepareName(data);
        this.effects = prepareEffects(data);
    }

    private String prepareName(JSONObject data) {
        try {
            return data.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "Nome não localizado";
    }

    private List<Effect> prepareEffects(JSONObject data) {
        try {
            List<Effect> effects = new LinkedList<>();
            JSONArray effectsJson = data.getJSONArray("effects");

            for (int index = 0; index < effectsJson.length(); index++) {
                JSONObject effectJson = effectsJson.getJSONObject(index);
                effects.add(new Effect(index, effectJson));
            }

            return effects;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
