package com.pgi.convergence.utils;

import java.util.UUID;

/**
 * This class generate ELK transaction id
 */
public class ElkTransactionIDUtils {
    private static String mTransactionId;

    private static void  generateTransactionId(){
        mTransactionId = UUID.randomUUID().toString();
    }

    /**
     * Get transaction id string.
     *
     * @return the mTransactionId
     */
    public static String getTransactionId(){
        if(mTransactionId == null){
            generateTransactionId();
        }
        return mTransactionId;
    }

    /**
     * Reset transaction id.
     */
    public static void resetTransactionId(){
        mTransactionId = null;
    }
}
