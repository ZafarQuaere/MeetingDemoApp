package com.pgi.convergence

object Versions {
	val compileSdk               = 29
	val targetSdk                = 29
	val minSdk                   = 23
	val buildTools               = "29.0.3"
	val versionName              = "1.0"
	val versionCode              = "1"
	val sourceCompat             = "1.8"
	val targetCompat             = "1.8"
	val kotlin                   = "1.3.61"
	val kotlinSerializationVer   = "0.13.0"
	val jacoco                   = "0.8.5"
	val coroutine                = "1.3.2"
	val robolectricVer           = "4.3.1"
	val appCompatVer             = "1.1.0"
	val legacySupportVer         = "1.0.0"
	val androidXBrowserVer       = "1.0.0"
	val vectorDrawableVer        = "1.1.0"
	val recyclerViewVer          = "1.1.0"
	val cardViewVer              = "1.0.0"
	val constraintLayoutVer      = "1.1.3"
	val materialVer              = "1.0.0"
	val corKtxVer                = "1.1.0"
	val navigationVer            = "2.2.0-beta01"
	val fragmentVer              = "1.2.0-beta02"
	val lifeCycleVer             = "2.2.0-beta01"
	val lifeCycleTestVer         = "2.1.0"
	val circleImageViewVer       = "3.0.0"
	val butterKnifeVer           = "10.1.0"
	val picassoVer               = "2.71828"
	val retrofitVer              = "2.5.0"
	val okHttpVer                = "3.14.1" // TODO:: Need to bump this to 4.2.1
	val rxJavaVer                = "2.2.8"
	val rxAndroid                = "2.1.0"
	val okioVer                  = "2.4.1"
	val appauthVer               = "0.7.1"
	val jwtVer                   = "0.9.1"
	val jwtdecodeVer             = "1.3.0"
	val jodaTimeVer              = "2.10.4"
	val greenDaoVer              = "3.3.0"
	val gsonVer                  = "2.8.6"
	val jacksonVer               = "2.10.0"
	val guavaVer                 = "29.0-android"
	val commonCodecVer           = "1.13"
	val rxAppStateVer            = "3.0.1"
	val sqlCipherVer             = "4.2.0"
	val fbShimmerVer             = "0.5.0"
	val koinViewModelVer         = "2.0.1"
	val anrwatchDogVer           = "1.4.0"
	val stethoVer                = "1.6.0"
	val newrelicVer              = "5.25.1"
	val devAlertVer              = "0.1.2"
	val sharedCodeVer            = "1.1.17-SNAPSHOT"
	val junitVer                 = "4.12"
	val assertJ                  = "3.13.2"
	val mockito                  = "2.1.0"
	val kluent                   = "1.41"
	val powermock                = "2.0.2"
	val mockk                    = "1.9.3" // TODO:: Bumping this version will break some unit tests. Need to work on this
	val espresso                 = "3.3.0-alpha02"
	val xJunit                   = "1.1.1"
	val xCoreTest                = "1.2.0"
	val xLiveDataTest            = "1.1.0"
	val hamcrest                 = "1.3"
	val realmVer                 = "10.4.0"
	val jfrogVer                 = "4.9.8"
	val sonarVer                 = "2.6.2"
	val androidGradleVer         = "3.6.1"
	val buildScanVer             = "2.0.2"
	val autoServiceVer           = "1.0-rc4"
	val deviceName               = "1.1.9"
	val relinkerVer  			 = "1.3.1"
	val googleServicesVer  	     = "4.3.4"
	val firebaseCrashlyticsGradleVer = "2.4.1"
	val firebaseBomVer 	         = "26.4.0"
}

object Deps {
	val kotlinlibs = mapOf(
			"kotlinStdLib" 			      to "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}",
			"kotlinReflectionLib" 		  to "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}",
			"kotlinSerialization" 		  to "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerializationVer}",
			"coroutines" 				  to "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutine}")
	val androidXlibs = mapOf(
			"core" 				       to "androidx.core:core-ktx:${Versions.corKtxVer}",
			"appCompat"                to "androidx.appcompat:appcompat:${Versions.appCompatVer}",
			"legacySupport"            to "androidx.legacy:legacy-support-v4:${Versions.legacySupportVer}",
			"browser"                  to "androidx.browser:browser:${Versions.androidXBrowserVer}",
			"vectorDrawable"           to "androidx.vectordrawable:vectordrawable-animated:${Versions.vectorDrawableVer}",
			"constraintLayout"         to "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVer}",
			"cardView"                 to "androidx.cardview:cardview:${Versions.cardViewVer}",
			"recycleView"              to "androidx.recyclerview:recyclerview:${Versions.recyclerViewVer}",
			"navFragment"              to "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVer}",
			"navigationUI"             to "androidx.navigation:navigation-ui-ktx:${Versions.navigationVer}",
			"fragments"                to "androidx.fragment:fragment-ktx:${Versions.fragmentVer}",
			"viewModel"                to "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycleVer}",
			"liveData"                 to "androidx.lifecycle:lifecycle-livedata:${Versions.lifeCycleVer}",
			"reactiveLiveData"         to "androidx.lifecycle:lifecycle-reactivestreams:${Versions.lifeCycleVer}",
			"lifeCycleJava8"           to "androidx.lifecycle:lifecycle-common-java8:${Versions.lifeCycleVer}")
	val uilibs = mapOf(
			"material"                 to "com.google.android.material:material:${Versions.materialVer}",
			"circleImageView"          to "de.hdodenhof:circleimageview:${Versions.circleImageViewVer}",
			"picasso"                  to "com.squareup.picasso:picasso:${Versions.picassoVer}",
			"fbShimmer"                to "com.facebook.shimmer:shimmer:${Versions.fbShimmerVer}")
	val greendaolibs = mapOf(
			"greenDao"                 to "org.greenrobot:greendao:${Versions.greenDaoVer}",
			"greendaoGenerator"        to "org.greenrobot:greendao-generator:${Versions.greenDaoVer}")
	val rxlibs = mapOf(
			"rxJava"                   to "io.reactivex.rxjava2:rxjava:${Versions.rxJavaVer}",
			"rxAndroid"                to "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}")
	val networkinglibs = mapOf(
			"retrofit"                 to "com.squareup.retrofit2:retrofit:${Versions.retrofitVer}",
			"retrofitRxJavaAdatpter"   to "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofitVer}",
			"retrofitGson"             to "com.squareup.retrofit2:converter-gson:${Versions.retrofitVer}",
			"retrofitScalar"           to "com.squareup.retrofit2:converter-scalars:${Versions.retrofitVer}",
			"okHttp"                   to "com.squareup.okhttp3:okhttp:${Versions.okHttpVer}",
			"httpLoggingInterceptor"   to "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpVer}",
			"okio"                     to "com.squareup.okio:okio:${Versions.okioVer}",
			"jackson"                  to "com.fasterxml.jackson.core:jackson-databind:${Versions.jacksonVer}",
			"gson"                     to "com.google.code.gson:gson:${Versions.gsonVer}")
	val appauthlibs = mapOf(
			"appAuth"                  to "net.openid:appauth:${Versions.appauthVer}",
			"jwt"                      to "io.jsonwebtoken:jjwt:${Versions.jwtVer}",
			"jwtDecode"                to "com.auth0.android:jwtdecode:${Versions.jwtdecodeVer}")
	val devtoolslibs = mapOf(
			"devAlert"                 to "com.garena.devalert:dev-alert:${Versions.devAlertVer}",
			"anrwatchDog"              to "com.github.anrwatchdog:anrwatchdog:${Versions.anrwatchDogVer}",
			"stetho"                   to "com.facebook.stetho:stetho:${Versions.stethoVer}",
			"stethoOkHttp"             to "com.facebook.stetho:stetho-okhttp3:${Versions.stethoVer}")
	// These are the libs which we are not sure if needed
	val misclibs = mapOf(
			"guava"                    to "com.google.guava:guava:${Versions.guavaVer}",
			"commonCodec"              to "commons-codec:commons-codec:${Versions.commonCodecVer}",
			"rxAppState"               to "com.jenzz.appstate:appstate:${Versions.rxAppStateVer}",
			"sqlCipher"                to "net.zetetic:android-database-sqlcipher:${Versions.sqlCipherVer}",
			"deviceName"			   to "com.jaredrummler:android-device-names:${Versions.deviceName}")

	val annotationlibs = mapOf(
			"lifecycle" 				     to "androidx.lifecycle:lifecycle-compiler:${Versions.lifeCycleVer}",
			"butterknife" 				     to "com.jakewharton:butterknife-compiler:${Versions.butterKnifeVer}")

	val butterknife              = "com.jakewharton:butterknife:${Versions.butterKnifeVer}"
	val sharedCodeLib            = "com.pgi.convergence:PGiMobileShared:${Versions.sharedCodeVer}"
	val koin                     = "io.insert-koin:koin-androidx-viewmodel:${Versions.koinViewModelVer}"
	val jodaTime                 = "joda-time:joda-time:${Versions.jodaTimeVer}"
	val newRelic                 = "com.newrelic.agent.android:android-agent:${Versions.newrelicVer}"
	val autoService              = "com.google.auto.service:auto-service:${Versions.autoServiceVer}"
	val relinker				 = "com.getkeepsafe.relinker:relinker:${Versions.relinkerVer}"
	val firebaseCrashlytics		 = "com.google.firebase:firebase-crashlytics-ktx"
	val firebaseBom      		 = "com.google.firebase:firebase-bom:${Versions.firebaseBomVer}"
	val java9Binding = mapOf(
			"jaxbApi" 				to "javax.xml.bind:jaxb-api:2.3.1",
			"jaxbruntime" 			to "org.glassfish.jaxb:jaxb-runtime:2.3.3"
	)
}

object TestDeps {
	val testframeworks = mapOf(
			"junit"                    to "junit:junit:${Versions.junitVer}",
			"koinTest"                 to "io.insert-koin:koin-test:${Versions.koinViewModelVer}",
			"assertJ"                  to "org.assertj:assertj-core:${Versions.assertJ}",
			"robolectric"              to "org.robolectric:robolectric:${Versions.robolectricVer}",
			"mockito"                  to "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito}",
			"mockk"                    to "io.mockk:mockk:${Versions.mockk}",
			"kluent"                   to "org.amshove.kluent:kluent:${Versions.kluent}",
			"junitRetryRule"						to "com.github.kevinmost:junit-retry-rule:master-SNAPSHOT"
	)
	val powermock = mapOf(
			"junit"                    to "org.powermock:powermock-module-junit4:${Versions.powermock}",
			"rule"                     to "org.powermock:powermock-module-junit4-rule:${Versions.powermock}",
			"powerMockito"             to "org.powermock:powermock-api-mockito2:${Versions.powermock}",
			"xstream"                  to "org.powermock:powermock-classloading-xstream:${Versions.powermock}")
	val androidXtestlibs = mapOf(
			"core"                      to "androidx.test:core:${Versions.xCoreTest}",
			"coreKtx"                   to "androidx.test:core-ktx:${Versions.xCoreTest}",
			"junit"                     to "androidx.test.ext:junit:${Versions.xJunit}",
			"rules"                     to "androidx.test:rules:${Versions.xCoreTest}",
			"runner"                    to "androidx.test:runner:${Versions.xCoreTest}",
			"lifecycle"                 to "androidx.arch.core:core-testing:${Versions.lifeCycleTestVer}",
			"fragment"                  to "androidx.fragment:fragment-testing:${Versions.fragmentVer}",
			"liveData"                  to "com.jraska.livedata:testing-ktx:${Versions.xLiveDataTest}")
	val espressolibs = mapOf(
			"core"              				to "androidx.test.espresso:espresso-core:${Versions.espresso}",
			"contrib"           				to "androidx.test.espresso:espresso-contrib:${Versions.espresso}",
			"intent"            				to "androidx.test.espresso:espresso-intents:${Versions.espresso}",
			"accessibilty"      				to "androidx.test.espresso:espresso-accessibility:${Versions.espresso}",
			"web"              					to "androidx.test.espresso:espresso-web:${Versions.espresso}",
			"idlingConcurrent" 					to "androidx.test.espresso.idling:idling-concurrent:${Versions.espresso}",
			"idlingResource"   					to "androidx.test.espresso:espresso-idling-resource:${Versions.espresso}")
	val mockWebServer            = "com.squareup.okhttp3:mockwebserver:${Versions.okHttpVer}"
	val hamcrest                 = "org.hamcrest:hamcrest-all:${Versions.hamcrest}"
	val coroutineTest            = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutine}"
}