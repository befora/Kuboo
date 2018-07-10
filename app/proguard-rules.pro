# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#kotlin fix issue https://youtrack.jetbrains.com/issue/KT-21628
-keep class kotlin.internal.annotations.AvoidUninitializedObjectCopyingCheck { *; }
-dontwarn kotlin.internal.annotations.AvoidUninitializedObjectCopyingCheck

#kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#dagger
-dontwarn com.google.errorprone.annotations.*

#kodein
-keepattributes Signature

# glide
-keep class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.AppGlideModule
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.**

#retrofit
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

#jsoup
-keep class org.jsoup.**

#folioreader
-dontwarn javax.annotation.**
-dontwarn nl.siegmann.epublib.**

#baserecyclerviewadapter
-keepattributes InnerClasses
-keep class com.chad.library.adapter.** { *; }
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder { *; }

#epubparser
-keep public class com.github.mertakdut.**{ *; }
-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

#apache
-dontpreverify
-keep class org.apache.** { *; }
-dontwarn org.apache.**

#supportlibrary
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.contentView.menu.** { *; }

-keep public class * extends android.support.v4.contentView.ActionProvider {
    public <init>(android.content.Context);
}

-keepclassmembers class android.support.design.internal.BottomNavigationMenuView {
    boolean mShiftingMode;
}

#
-dontwarn sun.misc.Unsafe

#mupdf
-keep class com.artifex.mupdf.** {*;}