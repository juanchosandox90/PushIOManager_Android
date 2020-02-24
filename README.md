## PushIOManager for Android 

* [Integration Guide](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/android/)

 
 
## Release Notes

### Upgrading SDK to 6.44
#### Migration to AndroidX

With this release, we have migrated from the Android Support libraries to AndroidX.

If your app uses the `android.support.xx` dependencies, please migrate them to AndroidX  to use this version of the SDK.

Learn more about migrating your App to AndroidX [here](https://developer.android.com/jetpack/androidx/migrate).


### Upgrading SDK to 6.40
#### New Permission for Displaying In-App Message and Rich Push message
Responsys SDK uses a `WebView` component to display the In-App and Rich Push messages. To improve the security of this component, we have added a new permission that is required to be declared in the `AndroidManifest` file,

```xml
<uses-permission android:name="${applicationId}.permission.RSYS_SHOW_IAM"/>

<permission
        android:name="${applicationId}.permission.RSYS_SHOW_IAM"
        android:protectionLevel="signature" />
```
This permission must be assigned to the `PushIOMessageViewActivity` as follows,

```xml
<activity
       android:name="com.pushio.manager.iam.ui.PushIOMessageViewActivity"
       android:theme="@android:style/Theme.Translucent.NoTitleBar"
       android:permission="${applicationId}.permission.RSYS_SHOW_IAM">
       <intent-filter>
          <action android:name="android.intent.action.VIEW" />
	  <category android:name="android.intent.category.BROWSABLE" />
          <category android:name="android.intent.category.DEFAULT" />
	  <data android:scheme="pio-YOUR_APP_ID" />
       </intent-filter>
</activity>
```
The permission will ensure that only your app (or apps signed with the same certificate as your app) may launch the `WebView` to display the HTML content.


#### New APIs for Geofence and Beacon Events
With the release of 6.40 SDK, we have introduced new APIs for reporting geofence and beacon related events back to Responsys.

```java
onGeoRegionEntered(PIOGeoRegion region, PIORegionCompletionListener completionListener);
onGeoRegionExited (PIOGeoRegion region, PIORegionCompletionListener completionListener);

onBeaconRegionEntered(PIOBeaconRegion region, PIORegionCompletionListener completionListener);
onBeaconRegionExited (PIOBeaconRegion region, PIORegionCompletionListener completionListener);
```
If you currently use a solution for tracking geofences/beacons, it is now possible to send the related event data to Responsys using these APIs.



#### New API for Cross-channel Conversions
For the users of our [Cross-channel Conversion Tracking](https://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/android/xchannel-conversions/)  feature, we have added a new API that allows the app to handle the deeplinks/weblinks instead of the Responsys SDK.

```java
PushIOManager.getInstance(getApplicationContext()).trackEmailConversion(getIntent(), new PIODeepLinkListener() {
   @Override
   public void onDeepLinkReceived(String deeplinkUrl, String webLinkUrl) {
   	// handle the deeplink / weblink
   }
});
```
The existing `trackEmailConversion(Intent)` API is recommended for a frictionless conversion experience for your email audience.

#### New API for Maintaining Conversion Windows
Responsys SDK supports tracking conversions (both push and cross-channel). Typically, a conversion session lasts until the app calls `resetEID()` or `resetEngagementContext()`. While this satifies most use-cases, there are scenarios where the app would want a conversion session to last a pre-defined amount of time, say, 3 days. In such cases, it is difficult to calculate the age of the conversion session.  With this release, we have added a new API for getting the timestamp of when the app was opened via a Responsys Push or Email campaign. 

```java
PushIOManager.getInstance(this).getEngagementTimestamp();
```
This API returns an ISO-8601 compatible timestamp String which allows the app developers to calculate the age of the conversion session and accordingly create custom conversion windows.


#### Deprecations
As part of the In-App message and Rich Push setup, we have been asking app developers to create/modify their `Application` class to extend our `PIOApplication` class. With the release of 6.40, this is no longer a mandatory step. The class `PIOApplication` has been deprecated. Just ensure that the SDK is instantiated with either the Application context or Activity context.

```java
PushIOManager.getInstance(getApplicationContext());

// or

PushIOManager.getInstance(MainActivity.this);
```

#### ProGuard 
If your app uses ProGuard, you may get some build-time warnings and/or runtime `ClassNotFoundException` related to the Responsys SDK. To work around this issue, add the following rules to your `proguard-rules.pro` file,

```text
# Rules for Responsys SDK
-keep class com.pushio.manager.** { *; }
-keep interface com.pushio.manager.** { *; }
-dontwarn com.pushio.manager.**
```

### Upgrading SDK to 6.39
#### New API for In-App Messaging
With the release of 6.39, we have introduced a new API to fetch In-App Messages at app launch. Once you turn-on this feature in Responsys Interact, you should use the below API to enable this feature in the SDK. 

```java
PushIOManager.getInstance(this).setInAppFetchEnabled(true);
``` 

Thereafter, the In-App messages shall be delivered to the app via a pull-based mechanism.

##### Recommendation
For any new In-App message implementation or upgrade to 6.39, we strongly recommend enabling this feature both in the SDK and for your Account at Responsys.


### Upgrading SDK to 6.38.1
#### Support for FCM
With the release of 6.38.1, the SDK is compatible with FCM (Firebase Cloud Messaging). If you wish to use FCM libraries in your app, it is recommended that you remove the GCM library dependency and vice-versa.

```
dependencies {
	// implementation 'com.google.android.gms:play-services-gcm:15.0.1'
	implementation 'com.google.firebase:firebase-messaging:17.3.4'
}
``` 

### Upgrading SDK to 6.38
#### New API for SDK Crash Reporting
With the release of 6.38, we have introduced a new feature to report all SDK-related (internal) crash/issues back to Responsys. This feature is enabled by default. The following API can be used to toggle this feature,

```java
PushIOManager.getInstance(this).setCrashLoggingEnabled(true);
```

### Upgrading PushIO SDK 6.33.1 to 6.33.2
#### Changes in APIs for Registration
With the release of 6.33.2, we have modified the following registration API methods,
```
PushIOManager.getInstance(this).registerApp();

PushIOManager.getInstance(this).registerApp( boolean useLocation );
```

As with the previous release, the API - `PushIOManager.registerApp()` brings up a modal prompt at runtime for requesting location access. 

If you would like to defer requesting the location access to a later stage, you should use the new API - `PushIOManager.registerApp( boolean useLocation )`. The `boolean` parameter value here is persisted and subsequent registration calls to Responsys backend (for ex. via `registerApp()` or `registerUserId()`) use this value to check if location access is to be requested.


### Upgrading PushIO SDK 6.33 to 6.33.1
#### New API for Registration
With the release of 6.33.1, we have added a new API for registration. 

```
PushIOManager.getInstance(this).registerApp( boolean useLocation );
```

The existing API for registration - `PushIOManager.registerApp()` brings up a modal prompt for requesting location access, which may not be suitable for all the apps. With the new API, it is now possible to do a PushIO registration without location data. This might be useful in a scenario where your app would like to defer the request for location. 

If you do a PushIO registration without location data, it is recommended to also do a PushIO registration with location data at a later stage when your app is ready to request location access.


### Upgrading PushIO SDK 6.31/6.32 to 6.33
#### Minimum Android SDK Version
PushIO SDK now requires a minimum Android SDK version of 16. Update the module-level **`build.gradle`** file as follows:

```
android {
    defaultConfig {
        minSdkVersion 16
    }
}
```
 
#### Runtime Permissions
Requesting device location shows a prompt at runtime for apps targeting Android Marshmallow (API 23) and above. See [Permissions](https://developer.android.com/preview/features/runtime-permissions.html) for more details.


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
- The internal GCM implementation has been updated to not conflict with other libraries using GCM within your app. As part of this change, you are required to declare the following services under the `<application>` tag in the AndroidManifest.xml file.
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
#### GCM Update - Troubleshooting 
If you followed the above steps and registration was successful but have not received any push notifications since upgrading the PushIO SDK, 
- Verify that your Google project has been correctly imported into Firebase. If you have not yet done that, you will need to migrate your GCM project from Google Cloud to Firebase as Firebase is the recommended platform for Cloud Messaging going forward.
- Try changing the Server Key to the new format as generated by Firebase. 
  - In the Firebase console, select your project, click the Settings menu icon (the icon that resembles a gear), then choose 'Project Settings' and under 'Cloud Messaging' tab, you should see the new format `Server Key` along with the old Server Key.
  - Copy the new Server Key and add it to the PushIO/RI dashboard as shown in section [1.2] of our Step-by-Step guides (http://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFA/android/step-by-step/, http://docs.oracle.com/cloud/latest/marketingcs_gs/OMCFB/android/step-by-step/)



## Contact
* Support: [My Oracle Support](http://support.oracle.com)

Copyright © 2019, Oracle Corporation and/or its affiliates. All rights reserved. Oracle and Java are registered trademarks of Oracle and/or its affiliates. Other names may be trademarks of their respective owners.
