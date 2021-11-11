package com.pgi.convergence.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.fragment.app.Fragment;

/**
 * Created by nnennaiheke on 2/6/18.
 */

public class ResourcesUtil {

    public static int[] getResourceIdArray(Context context, int resId) {
        return getResourceIdArray(context.getResources(), resId);
    }

    public static int[] getResourceIdArray(Fragment fragment, int resId) {
        return getResourceIdArray(fragment.getActivity(), resId);
    }

    public static int[] getResourceIdArray(Resources resources, int resId) {

        TypedArray typedArr = resources.obtainTypedArray(resId);

        try {

            int[] resourceIds = new int[typedArr.length()];

            for (int i = 0; i < resourceIds.length; i++) {
                resourceIds[i] = typedArr.getResourceId(i, 0);
            }

            return resourceIds;
        }
        finally {
            typedArr.recycle();
        }
    }

    private ResourcesUtil() {
    }
}
