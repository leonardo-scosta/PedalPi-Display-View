package com.pedalpi.pedalpi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pedalpi.pedalpi.communication.Message;
import com.pedalpi.pedalpi.communication.ProtocolType;
import com.pedalpi.pedalpi.communication.Server;
import com.pedalpi.pedalpi.model.Effect;
import com.pedalpi.pedalpi.model.Patch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EffectsActivity extends AppCompatActivity implements Server.OnMessageListener {

    public static final String EFFECT_INDEX = "EFFECT_INDEX";
    private Patch patch;
    private List<Button> buttons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        this.patch = (Patch) getIntent().getSerializableExtra(PatchActivity.PATCH);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        this.buttons = createEffectsButtons(patch, container);

        Server.getInstance().setListener(this);
    }

    private List<Button> createEffectsButtons(Patch patch, LinearLayout container) {
         List<Button> buttons = new ArrayList<Button>();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            160 * 2//LinearLayout.LayoutParams.WRAP_CONTENT
        );

        for (final Effect effect : patch.getEffects()) {
            buttons.add(createEffectButton(effect, container, layoutParams));
        }
        return buttons;
    }

    private Button createEffectButton(final Effect effect, LinearLayout container, LinearLayout.LayoutParams layoutParams) {
        final Button button = new Button(this);
        button.setTextAppearance(getApplicationContext(), R.style.Effect);

        button.setBackgroundColor(effect.isActive() ? Color.rgb(60,179,113) : Color.rgb(178,34,34));
        button.setText(effect.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    toggleStatusEffect(effect, button);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openScreenParamsList(effect);
                return true;
            }
        });
        container.addView(button, layoutParams);
        return button;
    }

    private void toggleStatusEffect(final Effect effect, final Button button) throws JSONException {
        Log.i("BOTAO", "Effect selected: " + effect);
        effect.toggleStatus();
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              button.setBackgroundColor(effect.isActive() ? Color.rgb(60,179,113) : Color.rgb(178,34,34));
                              Toast.makeText(getApplicationContext(), "efeito " + effect , Toast.LENGTH_SHORT).show();
                          }
                      });
        updateEffectStatusToServer(effect);
    }


    private void updateEffectStatusToServer(Effect effect) throws JSONException {
        JSONObject indexNumber = new JSONObject();
        indexNumber.put("index",effect.getIndex());

        Server.getInstance().send(new Message(ProtocolType.EFFECT, indexNumber));
    }

    private void openScreenParamsList(Effect effect) {
        Intent intent = new Intent(getBaseContext(), ParamsActivity.class);
        intent.putExtra(PatchActivity.PATCH, this.patch);
        intent.putExtra(EffectsActivity.EFFECT_INDEX, effect.getIndex());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PatchActivity.PATCH, this.patch);

        setResult(0, intent); // onActivityResult da tela anterior
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Server.getInstance().setListener(this);

        this.patch = (Patch) data.getExtras().getSerializable(PatchActivity.PATCH);
        boolean settedCurrentPatch = data.getBooleanExtra(PatchActivity.SETTED_CURRENT_PATCH, false);

        if (settedCurrentPatch)
            onBackPressed();

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onMessage(Message message) throws JSONException {
        Log.i("MESSAGE", message.getType().toString());
        if (message.getType() == ProtocolType.PATCH) {
            this.patch = new Patch(message.getContent());
            onBackPressed();
        }else if(message.getType() == ProtocolType.EFFECT){
            Log.i("effect", "" + message.getContent().getInt("index"));
            int indexEffect = message.getContent().getInt("index");
            toggleStatusEffect(this.patch.getEffects().get(indexEffect),buttons.get(indexEffect));
        }else if(message.getType() == ProtocolType.PARAM){

        }
    }


}