package com.example.andres.passphrasegenerator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class GeneratorView extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;
    private Generator mGenerator;
    private SharedPreferences.OnSharedPreferenceChangeListener mSettingsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean result = super.onOptionsItemSelected(item);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsView.class));
            result = true;
        }
        return result;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String[] strengthValues = getResources().getStringArray(R.array.strength_values);
        if (key.equals("passphrase_strength")) {
            String strengthValue = sharedPreferences.getString("passphrase_strength", strengthValues[1]);
            try {
                if (strengthValue.equals(strengthValues[4])) {
                    strengthValue = mSharedPreferences.getString("custom_strength", strengthValues[1]);
                    mGenerator.setMinimumLength(Integer.parseInt(strengthValue));
                } else {
                    mGenerator.setMinimumLength(Integer.parseInt(strengthValue));
                }
            } catch (NumberFormatException e) {
                mGenerator.setMinimumLength(8);
            }
        } else if (key.equals("custom_strength")) {
            String strengthValue = sharedPreferences.getString("custom_strength", strengthValues[1]);
            mGenerator.setMinimumLength(Integer.parseInt(strengthValue));
        } else if (key.equals("requires_uppercase")) {
            boolean uppercase = mSharedPreferences.getBoolean("requires_uppercase", false);
            mGenerator.setRequiresUppercase(uppercase);
        } else if (key.equals("requires_special_character")) {
            boolean spacialCharacter = mSharedPreferences.getBoolean("requires_special_character", false);
            mGenerator.setRequiresSpecialCharacter(spacialCharacter);
        } else if (key.equals("requires_number")) {
            boolean number = mSharedPreferences.getBoolean("requires_number", false);
            mGenerator.setRequiresNumber(number);
        }
    }

    /**
     *
     * @param view
     */
    public void generatePhrase(View view) {
        TextView label = (TextView) findViewById(R.id.passphrase);
        label.setText(String.valueOf(mGenerator.generatePhrase()));
    }

    /**
     *
     */
    private void initialize() {
        mGenerator = new Generator(getApplicationContext(), R.raw.passphrase_words);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mGenerator.fillGeneratorWithPreferences(getApplicationContext(), mSharedPreferences);
    }
}
