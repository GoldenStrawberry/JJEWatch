# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\sdk/tools/proguard/proguard-android.txt
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
    -keep class com.iflytek.**{*;}

    -keepattributes Exceptions,InnerClasses

    -keepattributes Signature

    # RongCloud SDK
    -keep class io.rong.** {*;}
    -keep class * implements io.rong.imlib.model.MessageContent {*;}
    -dontwarn io.rong.push.**
    -dontnote com.xiaomi.**
    -dontnote com.google.android.gms.gcm.**
    -dontnote io.rong.**

    # VoIP
    -keep class io.agora.rtc.** {*;}

    #3D 地图

    -keepclass com.amap.api.mapcore.**{*;}

    -keepclass com.amap.api.maps.**{*;}

    -keepclass com.autonavi.amap.mapcore.*{*;}

    #定位

    -keepclass com.amap.api.location.**{*;}

    -keepclass com.loc.**{*;}

    -keepclass com.amap.api.fence.**{*;}

    -keepclass com.autonavi.aps.amapapi.model.**{*;}

    # 搜索
    -keep class com.amap.api.**{*;}
    -keepclass com.amap.api.services.**{*;}

    # 2D地图

    -keepclass com.amap.api.maps2d.**{*;}

    -keepclass com.amap.api.mapcore2d.**{*;}

    # 导航

    -keepclass com.amap.api.navi.**{*;}

    -keepclass com.autonavi.**{*;}

    #语音

    -keepclass com.iflytek.cloud.**{*;}

    -keepclass com.iflytek.msc.**{*;}

    -keepinterface com.iflytek.**{*;}

  #如果引用了v4或者v7包
     -dontwarn android.support.**
      -keepattributes Signature
       -keepattributes *Annotation*
       -keep class sun.misc.Unsafe { *; }

  #极光JPush
  -dontoptimize
  -dontpreverify

  -dontwarn cn.jpush.**
  -keep class cn.jpush.** { *; }

  -dontwarn cn.jiguang.**
  -keep class cn.jiguang.** { *; }

  #极光JMessage
  -dontoptimize
  -dontpreverify
  -keepattributes  EnclosingMethod,Signature
  -dontwarn cn.jpush.**
  -keep class cn.jpush.** { *; }

  -dontwarn cn.jiguang.**
  -keep class cn.jiguang.** { *; }

   -keepclassmembers class ** {
       public void onEvent*(**);
   }

  #========================gson================================
  -dontwarn com.google.**
  -keep class com.google.gson.** {*;}

  #========================protobuf================================
  -keep class com.google.protobuf.** {*;}

  -dontwarn javax.servlet.**
  -dontwarn org.joda.time.**
  -dontwarn org.w3c.dom.**
  -dontwarn jcifs.http.NetworkExplorer
 #================Bugly==============================
   -dontwarn com.tencent.bugly.**
   -keep public class com.tencent.bugly.**{*;}
   -keep class android.support.**{*;}