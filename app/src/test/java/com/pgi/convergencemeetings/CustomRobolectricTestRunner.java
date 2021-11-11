//package com.pgi.convergencemeetings;
//
//
//import org.junit.runners.model.InitializationError;
//import org.robolectric.RobolectricTestRunner;
//
//
//public class CustomRobolectricTestRunner extends RobolectricTestRunner {
//
//    public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
//        super(testClass);
//        String buildVariant = (BuildConfig.FLAVOR.isEmpty() ? "" : BuildConfig.FLAVOR + "/") + BuildConfig.BUILD_TYPE;
//        System.setProperty("android.package", BuildConfig.APPLICATION_ID);
//        System.setProperty("android.manifest", "build/intermediates/manifests/full/" + buildVariant + "/AndroidManifest.xml");
//        System.setProperty("android_merged_resources", "build/intermediates/res/merged/" + buildVariant);
//        //System.setProperty("android.assets", "build/intermediates/assets/" + buildVariant);
//    }
//}
//

//package com.pgi.convergencemeetings;
//
//import org.junit.runners.model.InitializationError;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//import org.robolectric.manifest.AndroidManifest;
//import org.robolectric.res.Fs;
//
//
//public class  CustomRobolectricTestRunner extends RobolectricTestRunner {
//
//    public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
//        super(testClass);
//    }
//
//    @Override
//    protected AndroidManifest getAppManifest(Config config) {
//        String buildVariant = (BuildConfig.FLAVOR.isEmpty() ? "" : BuildConfig.FLAVOR + "/") + BuildConfig.BUILD_TYPE;
//        final String manifestPath = "build/intermediates/merged_manifests/prodDebug/AndroidManifest.xml";
//        final String resPath = "build/intermediates/res/merged/prod/debug";
//        final String assetPath = "build/intermediates/merged_assets/prodDebug/out";
//
//        return new AndroidManifest(
//            Fs.fileFromPath(manifestPath),
//            Fs.fileFromPath(resPath),
//            Fs.fileFromPath(assetPath),
//            BuildConfig.APPLICATION_ID) {
//
//            @Override
//            public String getRClassName() throws Exception {
//                return "com.pgi.convergencemeetings.R";
//            }
//
//        };
//    }
//}
