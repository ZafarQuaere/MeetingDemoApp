# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android-SDK\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more lastName, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn com.pgi.network.**
-keep class com.pgi.network.** { *; }
-keep interface com.pgi.network.** { *; }
-keep public class com.pgi.network.R$* { *; }

-dontwarn javax.annotation.**

-keep class com.squareup.retrofit2.** { *; }
-dontwarn com.squareup.retrofit2.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep class org.junit..** { *; }
-dontwarn org.junit.**
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.Metadata { *; }