package com.pedalpi.pedalpi.communication;

import com.pedalpi.pedalpi.model.Effect;
import com.pedalpi.pedalpi.model.Parameter;
import com.pedalpi.pedalpi.model.Patch;

import org.json.JSONException;
import org.json.JSONObject;


public class MessageProcessor {

    public static Patch process(Message message, Patch patch) throws JSONException {

        if (message.getType() == ProtocolType.PATCH) {
            patch = new Patch(message.getContent());

        } else if(message.getType() == ProtocolType.EFFECT) {
            int indexEffect = message.getContent().getInt("index");
            patch.getEffects().get(indexEffect).toggleStatus();

        } else if(message.getType() == ProtocolType.PARAM) {
            JSONObject data = message.getContent();

            int effectIndex = data.getInt("effect");
            int paramIndex = data.getInt("param");
            Double value = Parameter.prepareDoubleValue(data, "value");

            Parameter param = patch.getEffects().get(effectIndex).getParameters().get(paramIndex);
            param.setValue(value);
        }

        return patch;
    }

    public static Message generateEffectStatusToggled(Effect effect) {
        try {
            JSONObject indexNumber = new JSONObject();
            indexNumber.put("index",effect.getIndex());

            return new Message(ProtocolType.EFFECT, indexNumber);
        } catch (JSONException e) {
            return new Message(ProtocolType.ERROR);
        }
    }

    public static Message generateUpdateParamValue(Effect effect, Parameter parameter){
        try {
            JSONObject data = new JSONObject();
            data.put("effect", effect.getIndex());
            data.put("param", parameter.getIndex());
            data.put("value", parameter.getValue());

            return new Message(ProtocolType.PARAM, data);
        } catch (JSONException e) {
            return new Message(ProtocolType.ERROR);
        }
    }
}
