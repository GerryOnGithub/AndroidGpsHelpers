# AndroidGpsHelpers
Packages some GPS related Android-specific Java code from around the web into ready to use classes,
so you can start using GPS in your app quickly and easily. References to various sources can
be found in the code if you are interested (probably a good idea to have a look).

Update your AndroidManifest.xml permissions:

```<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />```

Initialize the GPS Wrapper class, add an observer and you got GPS. 

Because your app can get bombarded with locations, this package includes some built-in and 
configurable code to get that under control using a couple filtering strategies.

In the future I'll be adding some SQLite code that can be used to store and retrieve locations.

