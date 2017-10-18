# DISCONTINUED
Project has been discontinued since Android Marshmallow (API 23) release who introduced the [following restriction](https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-network):

> Your apps can now change the state of WifiConfiguration objects only if you created these objects. You are not permitted to modify or delete WifiConfiguration objects created by the user or by other apps.

An issue has been opened to ask Google to change their mind about it. More information here:

https://issuetracker.google.com/issues/37068375

Based on the direction taken by Google, in my opinion it doesn't make sense to go on with [Android Proxy Library (APL)](https://github.com/shouldit/android-proxy/tree/master/android-proxy-library) and [Proxy Settings app](https://github.com/shouldit/android-proxy/tree/master/proxy-settings) development anymore.

## Android Proxy project

The main purpose of this project is to try to fix one of the issues of Android that Google engineers decide do never address or do it just partially: [the issue 1273](https://code.google.com/p/android/issues/detail?id=1273)

What does it mean? In few words, with Android 1.x and 2.x you can't setup an HTTP proxy for Wifi networks. Google partially solved this problem starting from the 3.1 version but still hasn't the support for authenticated proxy.
 
When the project started, more than 98% of devices lacks of native support for HTTP proxy for Wifi networks.

The Android-Proxy project is composed by:

[Android Proxy Library (APL)](https://github.com/shouldit/android-proxy/tree/master/android-proxy-library)  

[![Download](https://api.bintray.com/packages/shouldit/maven/android-proxy-library/images/download.svg)  ](https://bintray.com/shouldit/maven/android-proxy-library/_latestVersion)

[Proxy Settings app](https://github.com/shouldit/android-proxy/tree/master/proxy-settings)

[![Download](https://developer.android.com/images/brand/en_generic_rgb_wo_45.png)  ](https://play.google.com/store/apps/details?id=com.lechucksoftware.proxy.proxysettings)

