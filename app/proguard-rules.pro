# Add project specific ProGuard rules here.
  # By default, the flags in this file are appended to flags specified
  # in /Users/yy/Library/Android/sdk/tools/proguard/proguard-android.txt
  # You can edit the include path and order by changing the proguardFiles
  # directive in build.gradle.
  #
  # For more details, see
  #   http://developer.android.com/guide/developing/tools/proguard.html

  # Add any project specific keep options here:

  # If your project uses WebView with JS, uncomment the following
  # and specify the fully qualified class name to the JavaScript interface
  # class:
  #-keepclassmembers class fqcn.of.javascript.interface.for.webview {
  #   public *;
  #}


#---------------------------------1.实体类---------------------------------
-keep class com.linsh.lshapp.model.bean.** { *; }
-keep class com.linsh.lshapp.view.** {*;}

#---------------------------------2.第三方包-------------------------------
#gson
-keep class com.google.gson.** {*;}
-keep class com.google.gson.examples.android.model.** { *; }

#Glide
-keep class com.bumptech.glide.** {*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#okHttp
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }
-keep class okhttp3.** {*;}
-keep class okio.** {*;}
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
-dontwarn com.squareup.**
-dontwarn okio.**

# Realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Retrolambda
-dontwarn java.lang.invoke.*

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.google.** { *;}
-keep class org.apache.** { *;}
-keep class com.squareup.** {*;}
-keep class com.tencent.** {*;}
-keep class com.github.** {*;}

-keep class java.** {*;}
-dontwarn java.**
#-dontwarn java.lang.management.**
-keep class org.** {*;}
-dontwarn org.**
-keep class sun.** {*;}
-dontwarn sun.**



-keepattributes *Annotation*
-keepattributes EnclosingMethod

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keep public class * extends android.os.IInterface

-keepclasseswithmembernames class * { # 保持 native 方法不被混淆
 native <methods>;
}

-keepclassmembers enum * { # 保持枚举 enum 类不被混淆
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * { # 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
 public void *(android.view.View);
}

-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
 public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}