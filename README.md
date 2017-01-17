# Android Gps Helpers
Packages some GPS related Android-specific Java code from around the web into ready to use classes, so you can start using GPS in your app quickly and easily. References to various sources can be found in the code if you are interested (probably a good idea to have a look).

Update your AndroidManifest.xml permissions:

```<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />```

Create one object of the GPS Wrapper class, add an observer (addObserver) and you got GPS. 

Because your app can get bombarded with locations, this package includes some  configurable code to get that under control using a couple of filtering strategies. Some of the strategies are based on Google's code, but I have also added a distance calculation (why bother reporting the same position?). In the future I will be adding a change-in-heading strategy. When the heading is changing (think of a car for example) capturing more GPS points maps much nicer. 

In the future I'll be adding some SQLite code that can be used to store and retrieve locations.

