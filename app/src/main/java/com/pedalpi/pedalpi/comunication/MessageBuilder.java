package com.pedalpi.pedalpi.comunication;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageBuilder {
    public static Message generate(String message) {
        String strings[] = message.split(" ", 2);

        ProtocolType type = searchType(strings[0]);

        try {
            return generateMessage(strings, type);
        } catch (JSONException e) {
            return new Message(ProtocolType.ERROR);
        }
    }

    private static ProtocolType searchType(String word) {
        for (ProtocolType type : ProtocolType.values())
            if (type.toString().equals(word))
                return type;

        return ProtocolType.ERROR;
    }

    @NonNull
    private static Message generateMessage(String[] strings, ProtocolType type) throws JSONException {
        if (strings.length == 2)
            return new Message(type, new JSONObject(strings[1]));
        else
            return new Message(type);
    }
}