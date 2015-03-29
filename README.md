Android Proxy project
=============

The main purpose of this project is to try to fix one of the issues of Android that Google engineers decide do never address or do it just partially: [the issue 1273](https://code.google.com/p/android/issues/detail?id=1273)

What does it mean? In few words, with Android 1.x and 2.x you can't setup an HTTP proxy for Wifi networks. Google partially solved this problem starting from the 3.1 version but still hasn't the support for authenticated proxy.
 
When the project started, more than 98% of devices lacks of native support for HTTP proxy for Wifi networks.

The Android-Proxy project is composed by:
* [Android Proxy Library (APL)](https://github.com/shouldit/android-proxy/tree/master/android-proxy-library)
* [Proxy Settings app](https://github.com/shouldit/android-proxy/tree/master/proxy-settings)
