[ ![Download](https://api.bintray.com/packages/shouldit/maven/android-proxy-library/images/download.svg) ](https://bintray.com/shouldit/maven/android-proxy-library/_latestVersion)

# Android Proxy Library (APL) 
**APL** provides an abstraction layer to easily get the proxy settings from an Android device, in order to find a better and easy solution to the [Android's Issue 1273](http://www.android-proxy.com/2011/09/hello-world-issue-1273.html) for both developers and users. Since it has been clarified from [Google's spokesman](http://stackoverflow.com/questions/9446871/how-users-developers-can-set-the-androids-proxy-configuration-for-versions-2-x) that the versions 1.x-2.x won't receive an official proxy support, APL allows to abstract the device version and easily get the proxy settings on every released Android device.

# Core features
* Version agnostic support (supported all Android versions: 1.x - 2.x - 3.x - 4.x)
  * 1.x and 2.x versions support only one global proxy for every Wi-Fi AP.
  * 3.x and greater versions support Wi-Fi AP-based proxy settings.
* Proxy testing utilities (proxy reachability, web reachability)


# Try
* **[Proxy Settings](https://play.google.com/store/apps/details?id=com.lechucksoftware.proxy.proxysettings)** makes use of all the advanced features of **APL** 

# How to use it
* Getting started [here](https://github.com/shouldit/android-proxy/wiki/Getting-Started).
* See how to [make an HTTP request](https://github.com/shouldit/android-proxy/wiki/Make-a-HTTP-Request) using the proxy.
* See how to [open a WebView](https://github.com/shouldit/android-proxy/wiki/Using-WebView-with-Proxy) that support the proxy on 1.x and 2.x devices.

# Gradle Dependency (jCenter)
Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```Gradle
dependencies {
    compile 'be.shouldit:android-proxy-library:4.2.6'
}
```

# Source & Issues
If you have isolated a problem or want a new feature to be included in the **Android Proxy Library (APL)**, please [submit an issue](https://github.com/shouldit/android-proxy/issues/new). Make sure to include all the relevant information when you submit the issue such as:

* **Android Proxy Library (APL)** version
* Android device used (or emulator) with OS version
* One line of issue summary and a detailed description
* Any workarounds if you have them.

The more information you provide, the quicker the issue can be verified and prioritized. A test case (source code) that demonstrates the problem is greatly preferred.
