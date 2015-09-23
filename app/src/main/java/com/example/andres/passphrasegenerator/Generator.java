package com.example.andres.passphrasegenerator;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 * Created by andres on 9/22/15.
 */
public class Generator {

    private Map mPhraseMap;
    private int mMinumumLength;
    private boolean mRequiresUppercase;
    private boolean mRequiresSpecialCharacter;
    private ExecutorService mExecutorService;

    public Generator(final Context context, final int resourceId) {
        mExecutorService = ServiceUtils.getExecutorService();
        mPhraseMap = generateMap(context, resourceId);
    }

    /**
     *
     * @return
     */
    public Map getPhraseMap() {
        return mPhraseMap;
    }

    /**
     *
     * @return
     */
    public int getMinimumLength() {
        return mMinumumLength;
    }

    /**
     *
     * @param minimumLength
     */
    public void setMinimumLength(final int minimumLength) {
        mMinumumLength = minimumLength;
    }

    /**
     *
     * @return
     */
    public boolean isRequiresUppercase() {
        return mRequiresUppercase;
    }

    /**
     *
     * @param requiresUppercase
     */
    public void setRequiresUppercase(final boolean requiresUppercase) {
        mRequiresUppercase = requiresUppercase;
    }

    /**
     *
     * @return
     */
    public boolean isRequiresSpecialCharacter() {
        return mRequiresSpecialCharacter;
    }

    /**
     *
     * @param requiresSpecialCharacter
     */
    public void setRequiresSpecialCharacter(final boolean requiresSpecialCharacter) {
        mRequiresSpecialCharacter = requiresSpecialCharacter;
    }

    /**
     *
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
                    //Log.d(getClass().getName().toString(), "Trying to read file...");
                    InputStream fileInputStream = context.getResources().openRawResource(resourceId);
                    Scanner scan = new Scanner(fileInputStream);
                    while (scan.hasNext()) {
                        Integer key = scan.nextInt();
                        String value = scan.nextLine();
                        //Log.d(getClass().getName().toString(), "Key: " + key + " Value: " + value);
                        result.put(key, value);
                    }
                    //Log.d(getClass().getName().toString(), "Closing resources...");
                    scan.close();
                    fileInputStream.close();
                    //Log.d(getClass().getName().toString(), "Finished reading file! " + result.size());
                } catch (FileNotFoundException e) {
                    Log.d(getClass().getName().toString(), e.getMessage());
                } catch (IOException e) {
                    Log.d(getClass().getName().toString(), e.getMessage());
                }
            }
        });
        return result;
    }
}
