![Push IO®](http://push.io/wp-content/uploads/2012/05/pushio_logo.png)

Copyright © 2009 - 2012 Push IO LLC. All Rights Reserved.
Push IO® is a registered trademark in the United States. All other trademarks are the property of their respective owners.

#### Software License Information
The use of any and all Push IO software regardless of release state is governed by the terms of a Software License Agreement and Terms of Use that are not included here but required by reference.

#### Privacy Information
The use of Push IO services is subject to various privacy policies and restrictions on the use of end user information. For complete information please refer to your Master Agreement, our website privacy policies, and/or other related documents.

#### Billing
For information on your account and billing, please contact sales@push.io

#### Contact Info
Push IO
1035 Pearl Street, Suite 400
Boulder, CO 80302 USA
303-335-0903
[support@push.io](mailto:support@push.io)
[www.push.io](http://www.push.io)

## Preface
Push IO is a leading provider of real-time push notification alerts and mobile data delivery.This document provides the necessary information to leverage Push IO for your mobile application. This document corresponds to the latest version of PushIOManager for Android.

## Definitions

#### UUID
A unique identifier corresponding to a specific device that is used to register with the Push IO service.

#### Category
A category defines a specific content type that your app has which users might be interested in. For example, if your app is a sports app, a category may be a specific team.

#### Audience
An audience defines a group of users based on one or more categories or platforms. For example, you might make an audience out of all of the users who have registered to a category for a team in the Western Conference who are using your app on Android.

#### Test Device
A device specially registered with Push IO to help you troubleshoot, test, and preview push notifications.

## App Integration

#### Overview
Push IO provides a lightweight PushIOManager library for each supported platform.

The library provides simple methods for registering so Push IO can send push notifications to a device via the platform gateway. The library also provides interfaces for segmenting your users into groups called categories. This allows you to send targeted push notifications only to those users who have expressed an interest in *bird watching*, and then perhaps only users who are interested in *robins*.

Push IO is the only push notification provider which provides data that allows you to understand how push notification engagement leads your users to high-value actions like in-app purchases, premium content viewing, and more. The PushIOManager library provides a simple mechanism to capture this push conversion information, which is available to you via the [Push IO Manager](https://manage.push.io) dashboard.

#### Integration Prerequisites
Before continuing, be sure you have what you need to integrate Push IO into your app.

1. Sign Up for a Push IO account at [https://manage.push.io](https://manage.push.io)
2. Download our Push IO Mobile Dashboard app to easily send your first push!
3. Obtain a Project Number and API key for Google&apos;s [GCM](http://developer.android.com/guide/google/gcm/gs.html) service
4. Setup your app and add an Android platform at [https://manage.push.io](https://manage.push.io)
5. Downloaded PushIOManager for Android and pushio_config.json from [Set Up > Android](https://manage.push.io)

![Platform download](http://pushiocdn.s3.amazonaws.com/docs_assets/platformdetail_android.png)

All set? Now you’re ready to integrate Push IO into your app!

## PushIOManager for Android

In order to add the PushIOManager JAR to your Android application, follow these simple steps:

### Locate the two files you downloaded from Set Up > Android from the Push IO Manager dashboard.

![Downloads](http://pushiocdn.s3.amazonaws.com/docs_assets/files_android.png)

### Add the PushIOManager.jar to your project&apos;s libs folder.

### Drag pushio_config.json file to your project&apos;s assets folder.

### Now you need to set up your Android Manifest. 

The following user-permissions are required to use Google Cloud Messaging (replace com.example with your application namespace).
```xml
<uses-permission android:name="com.example.permission.C2D_MESSAGE" />
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.INTERNET"/>

<permission android:name="com.example.permission.C2D_MESSAGE" android:protectionLevel="signature" />
```

In the Android Manifest under the “application” tag you need to set up the PushIOBroadcastReceiver. (Once again replace com.example for your application namespace.)

*Even though Google has moved from C2DM to CCM the permissions still use the c2dm name for backward compatibility with older devices.*

```xml
<receiver android:name="com.pushio.manager.PushIOBroadcastReceiver" android:permission="com.google.android.c2dm.permission.SEND" >
<intent-filter>
<action android:name="com.google.android.c2dm.intent.RECEIVE" />
<action android:name="com.google.android.c2dm.intent.REGISTRATION" />
<category android:name="com.example" />
</intent-filter>
</receiver>

<activity android:name="com.pushio.manager.PushIOActivityLauncher"/>
<service android:name="com.pushio.manager.PushIOGCMIntentService" />
<service android:name="com.pushio.manager.PushIOEngagementService"/>
```

### Creating the PushIOManager and PushIOListener
In any activity that is going to communicate with the PushIO servers, the best practice is to create a PushIOManager object to reuse throughout the activity. The first step, then, is to declare the variable for the class.
```java
private PushIOManager mPushIOManager;
```
When creating a new PushIOManager, the second parameter indicates the class to use to register for the callbacks. In the example below, we will use the current activity to handle the callbacks. Setting this parameter to null will ignore the callbacks and is not recommended.
```java
PushIOManager mPushIOManager =
new PushIOManager(getApplicationContext(), (PushIOListener)this, null);
```
At this point, Eclipse should offer to import the PushIOManager library and the PushIOListener library. If you are not using Eclipse or prefer to import the libraries by hand you can add
```java
import com.pushio.manager.PushIOManager;
import com.pushio.manager.tasks.PushIOListener;
```
to the import section for your activity.

Also you will update the class to implement the callbacks for the PushIOListener. To do that, we will first indicate that the class implements the PushIOListener callback methods by adding the *implements* argument to the class declaration
```java
public class MainActivity extends Activity implements PushIOListener
```

Then we add stubs for the two callback methods
```java
public void onPushIOSuccess(){
  //handle server success
  }

public void onPushIOError(String aMessage){
  //handle server error
  }
```

### Using the PushIOManager in an Activity
All of the examples below assume that the mPushIOManager variable was created using the method described in the previous section.
#### Call ensureRegistration() when the application is first starting. 
This will ensure that registration is maintained between upgrades of the application. A common location to place the call is from the onCreate method of the main activity.
```java
mPushIOManager.ensureRegistration();
```
 
 #### Register with Push IO
You can register a device with Push IO one of two ways. First, if your application gives users a way to specify a preference or favorite, you may want to register for that category you can you push to them relevant content. Note that the PushIOManager has two callbacks that can be optionally implemented that allow you to deal with success and errors. Additionally success and error is logged under the “pushio” tag.

This kind of registration would be tied to a notification UI, allowing the user to select categories.

```java
// Register for US and World Headlines
List<String> categories = new ArrayList<String>();
categories.add("US");
categories.add("World");
mPushIOManager.registerCategories( categories, false );
```
```java
// Unregister for US Headlines
List<String> categories = new ArrayList<String>();
categories.add("US");
pushIOManager.unregisterCategories( categories, false );
```
If you just want to be able to broadcast to all your users without using categories, simply call registerCategories and pass a null category.

```java
//Register for broadcast without categories
mPushIOManager.registerCategories(null,false);
```

To unregister a device call unregisterDevice.
```java
// Unregister device
mPushIOManager.unregisterDevice();
```

you can optionally unregister them from all categories depending on your UI. If they are required to reselect categories the next time they turn on notifications, then remove all of their categories
```java
// Removing all categories
mPushIOManager.unregisterAllCategories();
```
### Handle Push

In Android push notifications are broadcast using the action “com.example.PUSHIOPUSH”, if you don’t handle this broadcast the Push IO Manager will create a standard notification for you (using the badge name and sound name to to gather the proper resources from your application assets. If you don’t need anything fancy you can skip this section and move down to “Handling Push Intents”.

For custom notification you need to register a broadcast receiver in your android manifest. 

```xml
        <receiver android:name="com.example.NotificationBroadcastReceiver">
            <intent-filter>
                <action  android:name="com.example.PUSHIOPUSH"/>
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </receiver> 
```

From this broadcast receiver you can direct the push anywhere you would like, a common practice is to create an intent service that creates the notification. It is important that this broadcast receiver registers with the Push IO Manager that it is handling the notification so the Push IO Manager doesn’t create a duplicate notification. 


```java
    public void onReceive(Context context, Intent intent) {
        CustomNotificationService.runIntentInService(context, intent);
        Bundle extras = getResultExtras(true);
        extras.putInt(PushIOManager.PUSH_STATUS, PushIOManager.PUSH_HANDLED_NOTIFICATION);
        setResultExtras(extras);
    }
```

It is recommended that you use the PushIOActivityLauncher for the pending activities of your notifications. Using this class will automatically handle push engagement tracking for you. The PushIOActivityLauncher expects you to pass the notification intent (from the broadcast receiver) as extras. 

```java
            Intent newIntent = new Intent(this, PushIOActivityLauncher.class);
            newIntent.putExtras(intent);
            PendingIntent lPendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);
```


In certain activities in your application you may not want to show status bar notifications, but instead handle the push in-app. The best way to do this is to register a broadcast receiver on the onResume() of your activity, and unregister the receiver on the activitie&apos;s onPause().  The following example creates a local broadcast receiver, handles the push, and tells the Push IO Manager that the push was handled “in-app”. This will automatically track the in-app engagement and prevent the Push IO Manager from creating a custom notification. this.abortBroadcast() is used to stop the broadcast from hitting the application wide “NotificationBroadcastReceiver”. 

```java
mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String alert = intent.getStringExtra("alert");
                Toast.makeText(lContext, alert, Toast.LENGTH_LONG).show();

                Bundle extras = getResultExtras(true);
                extras.putInt(PushIOManager.PUSH_STATUS, PushIOManager.PUSH_HANDLED_IN_APP);
                setResultExtras(extras);
                this.abortBroadcast();
            }
        };
        registerReceiver( mBroadcastReceiver, new IntentFilter("com.example.PUSHIOPUSH") );
```

On the onPause() call:

```java
    @Override
    public void onPause(){
        super.onPause();

        unregisterReceiver(mBroadcastReceiver);
    }
```

###Handling Notification Pressed Intents
When a notification is pressed and is using the PushIOActivityLauncher an intent is launched for the action "com.example.NOTIFICATIONPRESSED". To handle this action you need to register an intent filter in your AndroidManaifest.xml

```xml
<activity android:name="com.example.myactivity">
            <intent-filter>
                <action android:name="com.example.NOTIFICATIONPRESSED"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>
</activity>
```

Congratulations, that&apos;s all there is to it! You should now be ready to test your Android integration.

###Advanced Engagements
"Launch" and "Active Session" engagements are handled for you. To track other engagements you need to call trackEngagement, passing one of the "PUSHIO_ENGAGEMENT_METRIC" constants found in the PushIOManager. 

```java
        pushIOManager = new PushIOManager(getApplicationContext(), this, null);
        pushIOManager.ensureRegistration();
        pushIOManager.trackEngagement(PushIOManager.PUSHIO_ENGAGEMENT_METRIC_PREMIUM_CONTENT);
```

When the session is ending it is important to clear the EngagementId. 
```java
        PushIOManager pushIOManager = new PushIOManager(getApplicationContext(), this, null);
        pushIOManager.resetEID();
```



If you need further assistance, please visit our [support site](http://pushio.zendesk.com).

