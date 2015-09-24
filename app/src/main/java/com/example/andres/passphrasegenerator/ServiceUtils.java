package com.example.andres.passphrasegenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andres on 9/22/15.
 */
public class ServiceUtils {

    private static ExecutorService mExecutorService;

    /**
     *
     * @return
     */
    public static ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
        return  mExecutorService;
    }
}
