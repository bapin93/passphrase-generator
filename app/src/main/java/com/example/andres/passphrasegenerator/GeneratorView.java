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

/**
 * The GeneratorView controls the activity_generator layout
 */
public class GeneratorView extends Activity implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;
    private Generator mGenerator;

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
        switch (key) {
            case "passphrase_strength":
                String strengthValue = sharedPreferences.getString("passphrase_strength",
                        strengthValues[1]);
                try {
                    if (strengthValue.equals(strengthValues[4])) {
                        strengthValue = mSharedPreferences.getString("custom_strength",
                                strengthValues[1]);
                        mGenerator.setMinimumLength(Integer.parseInt(strengthValue));
                    } else {
                        mGenerator.setMinimumLength(Integer.parseInt(strengthValue));
                    }
                } catch (NumberFormatException e) {
                    mGenerator.setMinimumLength(8);
                }
                break;
            case "custom_strength":
                strengthValue = sharedPreferences.getString("custom_strength",
                        strengthValues[1]);
                if (Integer.parseInt(strengthValue) < 6) {
                    strengthValue = "6";
                }
                mGenerator.setMinimumLength(Integer.parseInt(strengthValue));
                break;
            case "requires_uppercase":
                boolean uppercase = mSharedPreferences.getBoolean("requires_uppercase", false);
                mGenerator.setRequiresUppercase(uppercase);
                break;
            case "requires_special_character":
                boolean spacialCharacter = mSharedPreferences.getBoolean("requires_special_character",
                        false);
                mGenerator.setRequiresSpecialCharacter(spacialCharacter);
                break;
            case "requires_number":
                boolean number = mSharedPreferences.getBoolean("requires_number", false);
                mGenerator.setRequiresNumber(number);
                break;
        }
    }

    /**
     * @param view the view triggering generatePhrase
     */
    public void generatePhrase(View view) {
        TextView label = (TextView) findViewById(R.id.passphrase);
        label.setText(String.valueOf(mGenerator.generatePhrase()));
    }

    /**
     * Initializes instance variables
     */
    private void initialize() {
        mGenerator = new Generator(getApplicationContext(), R.raw.passphrase_words,
                findViewById(R.id.generate));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mGenerator.fillGeneratorWithPreferences(getApplicationContext(), mSharedPreferences);
    }
}