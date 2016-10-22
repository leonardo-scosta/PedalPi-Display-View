package com.pedalpi.pedalpi;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pedalpi.pedalpi.communication.Message;
import com.pedalpi.pedalpi.communication.MessageProcessor;
import com.pedalpi.pedalpi.communication.ProtocolType;
import com.pedalpi.pedalpi.communication.Server;
import com.pedalpi.pedalpi.model.Patch;
import com.pedalpi.pedalpi.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class PatchActivity extends AppCompatActivity implements Server.OnMessageListener {

    public static final String PATCH = "PATCH";
    public static final String SETTED_CURRENT_PATCH = "SETTED_CURRENT_PATCH";
    private Patch patch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Server.getInstance().setListener(this);
    }

    private JSONObject readJson(String fileName) {
        return JsonUtil.read(getAssets(), fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Server.getInstance().setListener(this);

        patch = (Patch) data.getExtras().getSerializable(PatchActivity.PATCH);
        updateScreen(patch);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openScreenEffectsList() {
        Intent intent = new Intent(getBaseContext(), EffectsActivity.class);
        intent.putExtra(PatchActivity.PATCH, this.patch);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onMessage(Message message) throws JSONException {
        Log.i("MESSAGE", message.getType().toString());

        this.patch = MessageProcessor.process(message,this.patch);
        if (message.getType() == ProtocolType.PATCH)
            updateScreen(patch);
    }

    private void updateScreen(final Patch patch) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button button = (Button) findViewById(R.id.button);
                button.setText(patch.getName());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openScreenEffectsList();
                    }
                });
            }
        });
    }
}