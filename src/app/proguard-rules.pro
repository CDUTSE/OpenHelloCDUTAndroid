# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program Files\android-sdk/tools/proguard/proguard-android.txt
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

-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9后的不需要加下面这个keep
#-keep class org.xbill.DNS.** {*;}
#另外，demo中发送表情的时候使用到反射，需要keep
#SmileUtils,注意前面的包名，
#不要SmileUtils复制到自己的项目下，keep的时候还是写的demo里的包名
-keep class com.emptypointer.hellocdut.utils.SmileUtils {*;}

#2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-dontwarn org.slf4j.**

-keep class ch.imvs.** {*;}

-keep public class com.tencent.bugly.**{*;}
-dontwarn com.tencent.**

#清除log
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
    public static *** w(...);
}

#百度混淆配置
-keep class com.baidu.** { *; }
-dontwarn com.baidu.**
-keep class vi.com.gdi.bgl.android.**{*;}


-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.facebook.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**

-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}


#混淆依赖keep
-dontwarn com.loopj.android.http.**
-dontwarn org.ice4j.ice.**
-dontwarn net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.**

-keep class com.emptypointer.hellocdut.model.* {*;}


-dontwarn org.apache.**
-dontwarn com.google.android.gms.**
-keep class com.android.volley.* {*;}
-dontwarn com.android.volley.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn android.support.**
-keep class android.support.** {*;}
-keep class com.parse.* {*;}
-dontwarn com.parse.**
-keep class com.squareup.* {*;}
-dontwarn com.squareup.**
-keep class com.facebook.* {*;}
-keep class okhttp3.* {*;}
-dontwarn okhttp3.**
-keep class okio.* {*;}
-dontwarn okio.**
-dontwarn com.facebook.**
-keep class com.qiniu.* {*;}
-dontwarn com.qiniu.**
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

#}
