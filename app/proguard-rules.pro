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
-keepclassmembers class com.pgi.convergencemeetings.meetings.content.ui.ScreenShareFragment$MyJavaScriptInterface {
   public *;
}

-keepclassmembers class com.pgi.convergencemeetings.meetings.content.ui.ScreenShareFragment$MyJavaScriptInterface {
    <methods>;
}

###-----------------------Begin: Default Android--------------------------------
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose

-dontwarn javax.annotation.**

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}
###-----------------------End: Default Android--------------------------------

-keep class com.github.anrwatchdog.** { *; }
-dontwarn com.github.anrwatchdog.**

#GlobalMeet keep crashlytics in place.
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-dontwarn com.fasterxml.jackson.**
-keep class com.fasterxml.jackson.** { *; }

-keep class org.codehaus.** { *; }
-keep class com.pgi.convergencemeetings.models.** {*;}
-keep enum com.pgi.convergencemeetings.enums.** {*;}
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers class com.fasterxml.jackson.**  { *; }
-keepclassmembernames class com.fasterxml.jackson.**  { *; }
-keepclasseswithmembernames class com.fasterxml.jackson.** { *; }
-keepclasseswithmembers class com.fasterxml.jackson.**  { *; }

-dontwarn org.greenrobot.**
-keepnames class org.greenrobot.** { *; }

-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable, SourceFile, EnclosingMethod

-keep class Premiere.Softphone.SWIG.** {*;}
-dontwarn dvconference_client
-keep class dvconference_client.** { *; }
-keep interface dvconference_client.** { *; }

#keep appstate in place.
-keep class com.jenzz.appstate.** { *; }
-dontwarn com.jenzz.appstate.**

# greendao
# http://greendao-orm.com/documentation/technical-faq/
-keep class **$Properties
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}

#greendao generated code
-keep class com.pgi.convergencemeetings.models.clientInfoModel** { *; }
-keep class com.pgi.convergencemeetings.models.meetingInfoModel** { *; }

-keep class com.pgi.convergencemeetings.greendao** { *; }

-keepclassmembers class * extends org.greenrobot.greendao.AbstractDaoSession {
public static java.lang.String TABLENAME;
}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDaoMaster {
public static java.lang.String TABLENAME;
}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDaoSessionTest {
public static java.lang.String TABLENAME;
}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDaoTest {
public static java.lang.String TABLENAME;
}

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**


-keep class org.junit..** { *; }
-dontwarn org.junit.**

## Square Picasso specific rules ##
-keep class com.squareup..**{ *; }
-dontwarn com.squareup.okhttp.**

###---------------Begin: proguard configuration for ButterKnife  ----------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**

# Version 8
-keep class **_ViewBinding { *; }

-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
###---------------End: proguard configuration for ButterKnife  -----------

###---------------Begin: proguard configuration for Support design -------
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keep public class android.support.R$* { *; }

#'io.jsonwebtoken:jjwt:'
#'com.auth0.android:jwtdecode:'
-dontwarn com.auth0.android.**
-keep class com.auth0.android.** { *; }
-keep interface com.auth0.android.** { *; }
-keep public class com.auth0.android.R$* { *; }

-dontwarn net.openid.appauth.**
-keep class net.openid.appauth.** { *; }
-keep interface net.openid.appauth.** { *; }
-keep public class net.openid.appauth.R$* { *; }

###---------------End: proguard configuration for Support design -------

-keep class org.python.util.** { *; }
-keepclassmembers class org.python.util.** {*;}
-dontwarn  org.python.util.**

-keep class org.jdom.** { *; }
-keepclassmembers class org.jdom.** { *; }
-dontwarn  org.jdom.**

-keep class  freemarker.ext.jdom.** { *; }
-keepclassmembers class  freemarker.ext.jdom.** { *; }
-dontwarn freemarker.ext.jdom.**

-keep class  freemarker.ext.jdom.**$** { *; }
-keepclassmembers class  freemarker.ext.jdom.**$** { *; }
-dontwarn freemarker.ext.jdom.**$**

-keep class javax.servlet.http.** { *; }
-keepclassmembers class javax.servlet.http.** { *; }
-dontwarn javax.servlet.http.**

-keep class freemarker.ext.servlet.** { *; }
-keepclassmembers class freemarker.ext.servlet.** { *; }
-dontwarn freemarker.ext.servlet.**

-keep class javax.servlet.jsp.tagext.** { *; }
-keepclassmembers class javax.servlet.jsp.tagext.** { *; }
-dontwarn javax.servlet.jsp.tagext.**

-keep class freemarker.ext.jsp.** { *; }
-keepclassmembers class freemarker.ext.jsp.** { *; }
-dontwarn freemarker.ext.jsp.**

-keep class freemarker.ext.jython.** { *; }
-keepclassmembers class freemarker.ext.jython.** { *; }
-dontwarn freemarker.ext.jython.**

-keep class freemarker.ext.rhino.** { *; }
-keepclassmembers class freemarker.ext.rhino.** { *; }
-dontwarn freemarker.ext.rhino.**

-keep class org.mozilla.javascript.** { *; }
-keepclassmembers class org.mozilla.javascript.** { *; }
-dontwarn org.mozilla.javascript.**

-keep class freemarker.log.Logger.** { *; }
-keepclassmembers class freemarker.log.Logger.** { *; }
-dontwarn freemarker.log.Logger.**

-keep class org.apache.log4j.** { *; }
-keepclassmembers class org.apache.log4j.** { *; }
-dontwarn org.apache.log4j.**

-keep class com.google.android.gms.ads.** { *; }
-keepclassmembers class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

-keep class io.fabric.sdk.android.** { *; }
-keepclassmembers class io.fabric.sdk.android.** { *; }
-dontwarn io.fabric.sdk.android.**

-keep class io.fabric.sdk.android.services.common.** { *; }
-keepclassmembers class io.fabric.sdk.android.services.common.** { *; }
-dontwarn io.fabric.sdk.android.services.common.**

-keep class com.google.android.gms.common.** { *; }
-keepclassmembers class com.google.android.gms.common.** { *; }
-dontwarn com.google.android.gms.common.**

-keep class com.android.org.conscrypt.** { *; }
-keepclassmembers class com.android.org.conscrypt.** { *; }
-dontwarn com.android.org.conscrypt.**

-keep class org.apache.harmony.xnet.provider.jsse.** { *; }
-keepclassmembers class org.apache.harmony.xnet.provider.jsse.** { *; }
-dontwarn org.apache.harmony.xnet.provider.jsse.**

-keep class dalvik.system.** { *; }
-keepclassmembers class dalvik.system.** { *; }
-dontwarn dalvik.system.**

-keep class sun.security.ssl.** { *; }
-keepclassmembers class sun.security.ssl.** { *; }
-dontwarn sun.security.ssl.**

-keep class org.robovm.apple.foundation.** { *; }
-keepclassmembers class org.robovm.apple.foundation.** { *; }
-dontwarn org.robovm.apple.foundation.**

-keep class okhttp3.internal.platform.** { *; }
-keepclassmembers class okhttp3.internal.platform.** { *; }
-dontwarn okhttp3.internal.platform.**

-keep class okhttp3.internal.platform.**$** { *; }
-keepclassmembers class okhttp3.internal.platform.**$** { *; }
-dontwarn okhttp3.internal.platform.**$**

-keep class retrofit2.** { *; }
-keepclassmembers class retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class freemarker.ext.jython.** { *; }
-keepclassmembers class freemarker.ext.jython.** { *; }
-dontwarn freemarker.ext.jython.**

-keep class freemarker.ext.jsp.**$** { *; }
-keepclassmembers class freemarker.ext.jsp.**$** { *; }
-dontwarn freemarker.ext.jsp.**$**

-keep class com.google.common.collect.** { *; }
-keepclassmembers class com.google.common.collect.** { *; }
-dontwarn com.google.common.collect.**

-keep class com.google.common.collect.**$** { *; }
-keepclassmembers class com.google.common.collect.**$** { *; }
-dontwarn com.google.common.collect.**$**

-keep class com.google.errorprone.annotations.** { *; }
-keepclassmembers class com.google.errorprone.annotations.** { *; }
-dontwarn com.google.errorprone.annotations.**

-keepclassmembers class com.google.common.cache.Striped64 { long base; }
-keepclassmembers class com.google.common.cache.Striped64 { int busy; }
-keepclassmembers class freemarker.cache.NullCacheStorage {
    freemarker.cache.NullCacheStorage INSTANCE;
 }

-dontwarn com.google.common.util.concurrent.**
-dontwarn com.google.common.util.concurrent.**$**
-dontwarn com.google.common.util.concurrent.**$**$**
-dontwarn freemarker.core.**
-dontwarn freemarker.debug.**
-dontwarn freemarker.debug.impl.**
-dontwarn freemarker.ext.ant.**
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.eventbus.**
-dontwarn com.google.common.hash.**
-dontwarn com.google.common.primitives.**
-dontwarn com.google.common.util.concurrent.**
-dontwarn freemarker.cache.**
-dontwarn freemarker.core.**
-dontwarn freemarker.debug.**
-dontwarn freemarker.ext.beans.**
-dontwarn freemarker.ext.dom.**
-dontwarn freemarker.ext.xml.**
-dontwarn freemarker.log.**
-dontwarn freemarker.template.utility.**
-dontwarn freemarker.template.**
-dontwarn io.jsonwebtoken.impl.**
-dontwarn okio.**


-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.pgi.convergence.**$$serializer { *; } # <-- change package
-keepclassmembers class com.pgi.convergence.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class com.pgi.convergence.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.Metadata { *; }