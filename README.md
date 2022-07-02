# Zulu Offshore
This app is an offline version of the open cruising guid found at [zuluwaterways.com](https://www.zuluwaterways.com).
It is available in android and IOS versions and are designed to have very little difference between the two.

To get this app up and running you will need a copy of the navionics SDK.
You can get that directly from them here: [navionics](https://developers.navionics.com/)

You will also need a mapbox API key and a ARC GIS API Key.
These keys can be added to the strings.xml file under resources.

Google-services json file is needed to activate the firebase auth logins for the app.

Feel free to contact dev@zuluwaterways.com if you get stuck.

```
dependencies {
implementation fileTree(dir: 'libs', include: ['*.jar'])
implementation(name: 'com.navionics.android.nms-release', ext: 'aar')
implementation 'com.google.firebase:firebase-analytics:21.0.0'
implementation 'com.google.firebase:firebase-crashlytics:18.2.11'
implementation 'androidx.appcompat:appcompat:1.4.1'
implementation 'com.google.android.material:material:1.6.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.preference:preference:1.2.0'
implementation 'com.google.firebase:firebase-auth:21.0.5'
implementation 'com.firebaseui:firebase-ui-auth:8.0.1'
implementation 'com.facebook.android:facebook-login:13.2.0'
implementation 'org.osmdroid:osmdroid-android:6.1.11'
implementation 'org.osmdroid:osmdroid-shape:6.1.11'
implementation 'com.firebaseui:firebase-ui-auth:8.0.1'
implementation 'com.facebook.android:facebook-android-sdk:latest.release'

}
```