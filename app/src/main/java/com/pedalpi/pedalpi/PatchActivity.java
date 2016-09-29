package com.pedalpi.pedalpi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pedalpi.pedalpi.model.Patch;
import com.pedalpi.pedalpi.util.JsonUtil;

import org.json.JSONObject;


public class PatchActivity extends AppCompatActivity {

    public static final String PATCH = "PATCH";
    private Patch patch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch);

        JSONObject json = readJson("json/teste.json");
        this.patch = new Patch(json);

        Button button = (Button) findViewById(R.id.button);
        button.setText(patch.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScreenEffectsList();
            }
        });
    }

    private JSONObject readJson(String fileName) {
        return JsonUtil.read(getAssets(), fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.patch = (Patch) data.getExtras().getSerializable(PatchActivity.PATCH);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openScreenEffectsList() {
        Intent intent = new Intent(getBaseContext(), EffectsActivity.class);
        intent.putExtra(PatchActivity.PATCH, this.patch);
        startActivityForResult(intent, 0);
    }
}