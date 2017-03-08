## PushIOManager for Android 

* [Integration Guide](http://docs.push.io)

## Release Notes
### Upgrading PushIO SDK 6.29.1 to 6.31
#### Minimum Android SDK Version 
- PushIO SDK now requires minimum SDK version of 14 (Ice Cream Sandwich). So remember to update your modules' `build.gradle` file,
```
    android {
      defaultConfig {
         minSdkVersion 14
      }
    }
```
#### GCM Update
- The internal GCM implementation has been updated to not conflict with other libraries using GCM within your app. As part of this change, you are required to declare the following services under `<application>` tag in the AndroidManifest.xml file.
```xml
   <application>
    <service android:name="com.pushio.manager.PIOGCMRegistrationIntentService"
        android:exported="false"/>
    <service
        android:name="com.pushio.manager.PIOInstanceIDListenerService"
        android:exported="false">
      <intent-filter>
        <action android:name="com.google.android.gms.iid.InstanceID" />
      </intent-filter>
    </service>
   </application>
```
- Also, as part of this update, the following libraries are now required in the `dependencies` section of your modules' `build.gradle` file. 
```
dependencies {
  compile 'com.google.android.gms:play-services-location:10.2.0'
  compile 'com.google.android.gms:play-services-gcm:10.2.0'
}
```
## Other Resources
* [Downloads + Documenation] (http://docs.push.io)
* [Sign In / Sign Up] (https://manage.push.io)

## Contact
* Support: [My Oracle Support] (http://support.oracle.com)

Copyright © 2016, Oracle Corporation and/or its affiliates. All rights reserved. Oracle and Java are registered trademarks of Oracle and/or its affiliates. Other names may be trademarks of their respective owners.
