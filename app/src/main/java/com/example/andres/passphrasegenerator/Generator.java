package com.example.andres.passphrasegenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.regex.PatternSyntaxException;

/**
 * The Generator class creates a model for generating a passphrase
 *
 * Created by andres on 9/22/15.
 */
public class Generator {

    private final int SPECIAL_CHARACTER_KEY_PREFIX = 66600;

    private Map<Integer, String> mPhraseMap;
    private int mMinumumLength;
    private boolean mRequiresUppercase;
    private boolean mRequiresSpecialCharacter;
    private boolean mRequiresNumber;
    private ExecutorService mExecutorService;

    /**
     * @param context the context from which our resources can be accessed
     * @param resourceId the resource Id of the txt file
     * @param button the generate button to be enabled when the hash map has been generated
     */
    public Generator(final Context context, final int resourceId, final View button) {
        mExecutorService = ServiceUtils.getExecutorService();
        mPhraseMap = generateMap(context, resourceId, button);
    }

    /**
     * @return the minimum length setting
     */
    @SuppressWarnings("unused")
    public int getMinimumLength() {
        return mMinumumLength;
    }

    /**
     * @param minimumLength the minimum length to set
     */
    public void setMinimumLength(final int minimumLength) {
        mMinumumLength = minimumLength;
    }

    /**
     * @return true if the passphrase requires uppercase, otherwise false
     */
    @SuppressWarnings("unused")
    public boolean isRequiresUppercase() {
        return mRequiresUppercase;
    }

    /**
     * @param requiresUppercase a boolean specifying if the passphrase requires uppercase or not
     */
    public void setRequiresUppercase(final boolean requiresUppercase) {
        mRequiresUppercase = requiresUppercase;
    }

    /**
     * @return true if the passphrase requires a special character, otherwise false
     */
    @SuppressWarnings("unused")
    public boolean isRequiresSpecialCharacter() {
        return mRequiresSpecialCharacter;
    }

    /**
     * @param requiresSpecialCharacter a boolean specifying if the passphrase requires
     * a special character or not
     */
    public void setRequiresSpecialCharacter(final boolean requiresSpecialCharacter) {
        mRequiresSpecialCharacter = requiresSpecialCharacter;
    }

    /**
     * @return true if the passphrase requires a number, otherwise false
     */
    @SuppressWarnings("unused")
    public boolean isRequiresNumber() {
        return mRequiresNumber;
    }

    /**
     * @param requiresNumber a boolean specifying if the passphrase requires a number or not
     */
    public void setRequiresNumber(final boolean requiresNumber) {
        mRequiresNumber = requiresNumber;
    }

    /**
     * @return the generated passphrase
     */
    public String generatePhrase() {
        String result = "";
        do {
            result += mPhraseMap.get(generateKey()).trim();
            Log.d(getClass().getName(), "Result Length: " + result.length());
            Log.d(getClass().getName(), "Pre-Result: \"" + result + "\"");
        } while (result.length() <= mMinumumLength);
        if (mRequiresUppercase) {
            Log.d(getClass().getName(), "Requires uppercase...");
            result = charToUppercase(result);
        }
        if (mRequiresSpecialCharacter) {
            Log.d(getClass().getName(), "Requires special character...");
            result = addSpecialCharacter(result);
        }
        if (mRequiresNumber) {
            result = addNumber(result);
        }
        Log.d(getClass().getName(), "Post-Result: \"" + result + "\"");
        return result;
    }

    /**
     * @param context the context from which our resources can be accessed
     * @param sharedPreferences the application shared preferences
     */
    public void fillGeneratorWithPreferences(final Context context, final SharedPreferences sharedPreferences) {
        String[] strengthValues = context.getResources().getStringArray(R.array.strength_values);
        String strengthValue = sharedPreferences.getString("passphrase_strength", strengthValues[1]);
        try {
            if (strengthValue.equals(strengthValues[4])) {
                strengthValue = sharedPreferences.getString("custom_strength", strengthValues[1]);
                setMinimumLength(Integer.parseInt(strengthValue));
            } else {
                setMinimumLength(Integer.parseInt(strengthValue));
            }
        } catch (NumberFormatException e) {
            setMinimumLength(8);
        }
        boolean uppercase = sharedPreferences.getBoolean("requires_uppercase", false);
        boolean spacialCharacter = sharedPreferences.getBoolean("requires_special_character", false);
        boolean number = sharedPreferences.getBoolean("requires_number", false);
        setRequiresUppercase(uppercase);
        setRequiresSpecialCharacter(spacialCharacter);
        setRequiresNumber(number);
    }

    /**
     * @param phrase the phrase to add capital letters to
     * @return the phrase with randomly capitalized letters
     */
    private String charToUppercase(final String phrase) {
        String result = "";
        Random rand = new Random();
        for (char c : phrase.toCharArray()) {
            if (Character.isLetter(c)) {
                result += (rand.nextBoolean() ? Character.toLowerCase(c) : Character.toUpperCase(c));
            } else {
                result += c;
            }
        }
        return result;
    }

    /**
     * @param phrase the phrase to add a special character to to
     * @return the phrase with one character replaced by a special character
     */
    private String addSpecialCharacter(final String phrase) {
        String result = phrase;
        if (phrase.matches("\\w*")) {
            Random rand = new Random();
            int index = rand.nextInt(phrase.length() - 1);
            result = result.replaceFirst(String.valueOf(result.charAt(index)), mPhraseMap.get(generateSpecialCharacterKey()).trim());
        }
        return result;
    }

    /**
     * @param phrase the phrase to add a number to
     * @return the proase with one character replaced by a random number
     */
    private String addNumber(final String phrase) {
        String result = phrase;
        Random rand = new Random();
        int index = rand.nextInt(phrase.length() - 1);
        try {
            while(!Character.isLetter(result.charAt(index))) {
                index = rand.nextInt(phrase.length() - 1);
            }
            result = result.replaceFirst(String.valueOf(result.charAt(index)), String.valueOf(index));

        } catch (PatternSyntaxException e) {
            addNumber(phrase);
        }
        return result;
    }

    /**
     * @param context the context from which our resources can be accessed
     * @param resourceId the resource Id of the txt file
     * @param button the generate button to be enabled when the hash map has been generated
     * @return a HashMap containing key, value pairs from the txt file
     */
    private Map generateMap(final Context context, final int resourceId, final View button) {
        final HashMap result = new HashMap<>();
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream fileInputStream = context.getResources().openRawResource(resourceId);
                    Scanner scan = new Scanner(fileInputStream);
                    while (scan.hasNext()) {
                        Integer key = scan.nextInt();
                        String value = scan.nextLine();
                        result.put(key, value);
                    }
                    scan.close();
                    fileInputStream.close();
                    button.setEnabled(true);

                } catch (FileNotFoundException e) {
                    Log.d(getClass().getName(), e.getMessage());
                } catch (IOException e) {
                    Log.d(getClass().getName(), e.getMessage());
                }
            }
        });
        return result;
    }

    /**
     * @return a random key associated with any value
     */
    private int generateKey() {
        int result = 0;
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            result += (rand.nextInt((6 - 1) + 1) + 1) * Math.pow(10, i);
        }
        Log.d(getClass().getName(), "Generated Key: " + result);
        return result;
    }

    /**
     * @return a random key associated with a special character
     */
    private int generateSpecialCharacterKey() {
        Log.d(getClass().getName(), "Generating Special Key...");
        int result = SPECIAL_CHARACTER_KEY_PREFIX;
        int[] specialCharacterKeySuffix = {32, 33, 34, 35, 36, 41, 42, 43, 44, 45, 46, 51, 52, 53, 54, 55, 56, 61, 62, 63, 64, 64, 66};
        Random rand = new Random();
        result += specialCharacterKeySuffix[(rand.nextInt(specialCharacterKeySuffix.length))];
        Log.d(getClass().getName(), "Generated Special Key: " + result);
        return result;
    }
}
