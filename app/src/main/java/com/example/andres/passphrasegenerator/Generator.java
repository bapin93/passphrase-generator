package com.example.andres.passphrasegenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

    public Generator(final Context context, final int resourceId) {
        mExecutorService = ServiceUtils.getExecutorService();
        mPhraseMap = generateMap(context, resourceId);
    }

    /**
     * @return
     */
    public Map getPhraseMap() {
        return mPhraseMap;
    }

    /**
     * @return
     */
    public int getMinimumLength() {
        return mMinumumLength;
    }

    /**
     * @param minimumLength
     */
    public void setMinimumLength(final int minimumLength) {
        mMinumumLength = minimumLength;
    }

    /**
     * @return
     */
    public boolean isRequiresUppercase() {
        return mRequiresUppercase;
    }

    /**
     * @param requiresUppercase
     */
    public void setRequiresUppercase(final boolean requiresUppercase) {
        mRequiresUppercase = requiresUppercase;
    }

    /**
     * @return
     */
    public boolean isRequiresSpecialCharacter() {
        return mRequiresSpecialCharacter;
    }

    /**
     * @param requiresSpecialCharacter
     */
    public void setRequiresSpecialCharacter(final boolean requiresSpecialCharacter) {
        mRequiresSpecialCharacter = requiresSpecialCharacter;
    }

    /**
     * @return
     */
    public boolean isRequiresNumber() {
        return mRequiresNumber;
    }

    /**
     * @param requiresNumber
     */
    public void setRequiresNumber(final boolean requiresNumber) {
        mRequiresNumber = requiresNumber;
    }

    /**
     * @return
     */
    public String generatePhrase() {
        String result = "";
        do {
            result += mPhraseMap.get(generateKey()).trim();
            Log.d(getClass().getName().toString(), "Result Length: " + result.length());
            Log.d(getClass().getName().toString(), "Pre-Result: \"" + result + "\"");
        } while (result.length() <= mMinumumLength);
        if (mRequiresUppercase) {
            Log.d(getClass().getName().toString(), "Requires uppercase...");
            result = charToUppercase(result);
        }
        if (mRequiresSpecialCharacter) {
            Log.d(getClass().getName().toString(), "Requires special character...");
            result = addSpecialCharacter(result);
        }
        if (mRequiresNumber) {
            result = addNumber(result);
        }
        Log.d(getClass().getName().toString(), "Post-Result: \"" + result + "\"");
        return result;
    }

    /**
     * @param context
     * @param sharedPreferences
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
     * @param phrase
     * @return
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
     * @param phrase
     * @return
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
     * @param context
     * @param resourceId
     * @return
     */
    private Map generateMap(final Context context, final int resourceId) {
        final HashMap result = new HashMap<Integer, String>();
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
                } catch (FileNotFoundException e) {
                    Log.d(getClass().getName().toString(), e.getMessage());
                } catch (IOException e) {
                    Log.d(getClass().getName().toString(), e.getMessage());
                }
            }
        });
        return result;
    }

    /**
     * @return
     */
    private int generateKey() {
        int result = 0;
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            result += (rand.nextInt((6 - 1) + 1) + 1) * Math.pow(10, i);
        }
        Log.d(getClass().getName().toString(), "Generated Key: " + result);
        return result;
    }

    /**
     * @return
     */
    private int generateSpecialCharacterKey() {
        Log.d(getClass().getName().toString(), "Generating Special Key...");
        int result = SPECIAL_CHARACTER_KEY_PREFIX;
        int[] specialCharacterKeySuffix = {32, 33, 34, 35, 36, 41, 42, 43, 44, 45, 46, 51, 52, 53, 54, 55, 56, 61, 62, 63, 64, 64, 66};
        Random rand = new Random();
        result += specialCharacterKeySuffix[(rand.nextInt(specialCharacterKeySuffix.length))];
        Log.d(getClass().getName().toString(), "Generated Special Key: " + result);
        return result;
    }
}
