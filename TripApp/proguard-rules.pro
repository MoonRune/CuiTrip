# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
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

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**

-keepnames class * implements java.io.Serializable
-keep public class * implements java.io.Serializable {
    public *;
}

#埋点
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

-keep public class com.cuitrip.service.R$*{
    public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#埋点end

-dontwarn com.alibaba.fastjson.**
-dontwarn cn.sharesdk.**
-dontwarn com.umeng.**
-dontwarn org.android.agoo.**
-dontwarn com.makeramen.**
-dontwarn cn.smssdk.**
-dontwarn com.mob.tools.log.**
-dontwarn com.pingplusplus.**
-dontwarn butterknife.**
#butterknife start
-dontwarn butterknife.internal.**

-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}
#butterknife end

-dontwarn   com.malinskiy.superrecyclerview.**
-dontwarn   com.amap.**

-keep class com.alibaba.fastjson.**{*;}
-keep class org.agoo.**{*;}
-keep class org.android.agoo.**{*;}
-keep class org.android.du.**{*;}
-keep class org.android.spdy.**{*;}
-keep class com.umeng.**{*;}
-keep class com.ta.utdid2.**{*;}
-keep class com.ut.**{*;}
-keep class com.loopj.**{*;}
-keep class com.squareup.**{*;}
-keep class cn.smssdk.**{*;}
-keep class butterknife.**

-keep class cn.sharesdk.**{*;}
-keep class com.mob.**{*;}
-keep class com.tencent.** {*;}

-keep class com.nostra13.**{*;}

-keep class com.alipay.**{*;}
-keep class HttpUtils.**{*;}
-keep class com.tencent.**{*;}
-keep class com.pingplusplus.**{*;}

-keep class com.cuitrip.model.**{*;}
-keep class com.cuitrip.business.**{*;}

-keep class cn.pedant.SweetAlert.**{*;}
-keep class com.pnikosis.**{*;}
-keep class com.makeramen.**{*;}


