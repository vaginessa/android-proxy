# Android Proxy Library (APL) 
**APL** provides an abstraction layer to easily get the proxy settings from an Android device, in order to find a better and easy solution to the [Android's Issue 1273](http://www.android-proxy.com/2011/09/hello-world-issue-1273.html) for both developers and users. Since it has been clarified from [Google's spokesman](http://stackoverflow.com/questions/9446871/how-users-developers-can-set-the-androids-proxy-configuration-for-versions-2-x) that the versions 1.x-2.x won't receive an official proxy support, APL allows to abstract the device version and easily get the proxy settings on every released Android device.

# Core features
* Version agnostic support (supported all Android versions: 1.x - 2.x - 3.x - 4.x)
  * 1.x and 2.x versions support only one global proxy for every Wi-Fi AP.
  * 3.x and greater versions support Wi-Fi AP-based proxy settings.
* Proxy testing utilities (proxy reachability, web reachability)