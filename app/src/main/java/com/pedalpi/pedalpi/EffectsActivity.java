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

import com.pedalpi.pedalpi.model.Effect;
import com.pedalpi.pedalpi.model.Patch;

public class EffectsActivity extends AppCompatActivity {

    public static final String EFFECT_INDEX = "EFFECT_INDEX";
    private Patch patch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        this.patch = (Patch) getIntent().getSerializableExtra(PatchActivity.PATCH);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        createEffectsButtons(patch, container);
    }

    private void createEffectsButtons(Patch patch, LinearLayout container) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            160 * 2//LinearLayout.LayoutParams.WRAP_CONTENT
        );

        for (final Effect effect : patch.getEffects())
            createEffectButton(effect, container, layoutParams);
    }

    private void createEffectButton(final Effect effect, LinearLayout container, LinearLayout.LayoutParams layoutParams) {
        final Button button = new Button(this);
        button.setTextAppearance(getApplicationContext(), R.style.Effect);

        button.setBackgroundColor(effect.isActive() ? Color.rgb(60,179,113) : Color.rgb(178,34,34));
        button.setText(effect.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStatusEffect(effect, button);
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
    }

    private void toggleStatusEffect(Effect effect, Button button) {
        Log.i("BOTAO", "Effect selected: " + effect);
        effect.toggleStatus();
        button.setBackgroundColor(effect.isActive() ? Color.rgb(60,179,113) : Color.rgb(178,34,34));
        Toast.makeText(getApplicationContext(), "efeito " + effect + " :", Toast.LENGTH_SHORT).show();
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
        this.patch = (Patch) data.getExtras().getSerializable(PatchActivity.PATCH);
        super.onActivityResult(requestCode, resultCode, data);
    }
}