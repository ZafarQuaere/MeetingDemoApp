package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by amit1829 on 8/11/2017.
 */

public class MockPicasso extends Picasso {
    private static ArrayList<String> paths;
    private static ArrayList<ImageView> views;

    private MockPicasso(Context context) {
        super(context, new MockDispatcher(context, null, null, null, null, null), null, null, null, null, null, null, false, false);
    }

    /**
     * Initializes new {@code MockPicasso} and replaces production instance.
     */
    public static void init(Context context) {
        paths = new ArrayList<String>();
        views = new ArrayList<ImageView>();
        singleton = new MockPicasso(context);
    }

    /**
     * Returns a list of all image paths in the order requested.
     */
    public static List<String> getImagePaths() {
        return paths;
    }

    /**
     * Returns the most recent image path requested.
     */
    public static String getLastImagePath() {
        return paths.size() > 0 ? paths.get(paths.size() - 1) : null;
    }

    /**
     * Returns a list of all {@link ImageView} targets in the order requested.
     */
    public static List<ImageView> getTargetImageViews() {
        return views;
    }

    /**
     * Returns the most recent {@link ImageView} target.
     */
    public static ImageView getLastTargetImageView() {
        return views.size() > 0 ? views.get(views.size() - 1) : null;
    }

    @Override
    public RequestCreator load(String path) {
        paths.add(path);
        return new MockRequestBuilder();
    }

    static class MockRequestBuilder extends RequestCreator {
        @Override
        public void into(ImageView target) {
            views.add(target);
        }

        @Override
        public Bitmap get() throws IOException {
            return makeBitmap();
        }

        static Bitmap makeBitmap() {
            return makeBitmap(50, 50);
        }

        static Bitmap makeBitmap(int width, int height) {
            return Bitmap.createBitmap(width, height, null);
        }
    }

    static class MockDispatcher extends Dispatcher {

        MockDispatcher(Context context, ExecutorService service, Handler mainThreadHandler, Downloader downloader, Cache cache, Stats stats) {
            super(context, service, mainThreadHandler, downloader, cache, stats);
        }
    }

    static class MockStats extends Stats {
        MockStats() {
            super(Cache.NONE);
        }
    }
}