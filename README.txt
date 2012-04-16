
The Push.IO Manager for Android abstracts the C2DM layer to provide a simple interface that allows
your application to register for and receive notifications through the Push.IO service.

You can read more about Google's C2DM at http://code.google.com/android/c2dm/


-----------------------------------------
What are the prerequisites to using C2DM?
-----------------------------------------

There are three things you need in order to establish a C2DM connection with a user:

	1) You must have a registered account with Push.IO and an API Key for use with your app.
	2) You must have a Google account of your own to use as a Sender ID.
	3) The user must be linked to a Google account on their Android phone.

The first two are done up front during development.  The last item is a requirement for each
individual user.


-------------------------------------------------------
How do I include the Push.IO Manager in my Android app?
-------------------------------------------------------

The Push.IO Manager comes packaged as a standard JAR library.  You can include it in your project
in various ways depending on what environment you're using:

* Eclipse ADT Installation
	
	1)	If you don't already have a folder with JARs in it, create one.  You can call it libs, jars, 
		or anything else you like.
		
	2)	Copy PushIOManager.jar into this folder either by drag-n-drop with Eclipse or using your OS's
		file explorer.
		
	3)	Right-click on your project and select Properties. Select Java Build Path on the left sidebar and
		then Libraries on the tab bar up top. Then click "Add JARs..."
		
	4)	Locate the PushIOManager.jar file you added to your project and hit OK.

* Ant-Based Installation:
	
	1)	If you don't already have a folder with JARs in it, create one.  You can call it libs, jars, 
		or anything else you like.
	
	2)	Copy PushIOManager.jar into this folder using your OS's file explorer.

	3)	In your build file, add PushIOManager to your classpath when compiling and/or running.
	
	
--------------------------------------------------
How do I integrate the Push.IO Manager in my code?
--------------------------------------------------
		
Integrating Push.IO Manager in your application will require you to register for, and handle, C2DM
notifications.  Here's how:

	1)	First, you'll need to add some lines to your AndroidManifest.xml.  Add these in your
		<manifest> section: (NOTE: where [APP_PACKAGE] appears, substitute your own.)
		
	    <permission android:name="[APP_PACKAGE].permission.C2D_MESSAGE" android:protectionLevel="signature" />

	    <uses-permission android:name="[APP_PACKAGE].permission.C2D_MESSAGE" />
	    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
	    <uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.READ_PHONE_STATE" />
		
	2)	In order to handle C2DM responses, you will need to create your own broadcast receiver. For it to work,
		your app needs to know about it.  In the <application> section, add:
		
		<receiver android:name="[APP_PACKAGE].C2DMBroadcastReceiver" android:permission="com.google.android.c2dm.permission.SEND">
			<intent-filter>
				<action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <category android:name="[APP_PACKAGE]" />
			</intent-filter>
			<intent-filter>
				<action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                <category android:name="[APP_PACKAGE]" />
			</intent-filter>
		</receiver>
	
	3)	With that configuration out of the way, you'll just need to add a few things to your code. First, 
		you need the actual broadcast receiver itself.  Unless you changed the XML, you should name it
		[APP_PACKAGE].C2DMBroadcastReceiver.  It should delegate functionality to the Push.IO Manager,
		but later on you can add your own logic to handle notifications, too. Here's the class:
		
		package [APP_PACKAGE];

		import android.content.BroadcastReceiver;
		import android.content.Context;
		import android.content.Intent;

		import com.pushio.manager.PushIOManager;
		import com.pushio.manager.PushIOManager.C2DMManager;


		public class C2DMBroadcastReceiver extends BroadcastReceiver {
			@Override
			public void onReceive(Context context, Intent intent) {
				PushIOManager mgr = new PushIOManager(context);
				C2DMManager c2dmmgr = mgr.new C2DMManager();

				c2dmmgr.handle(intent);
	
				if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
					// handle notifications, perhaps with a toast!
					String alert = intent.getStringExtra("alert");

					Toast t = Toast.makeText(this, alert, Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}
			}
		}		
	
	4)	Next, your code should register itself with the C2DM.  You should only need to do this once,
	 	but running it multiple times shouldn't hurt.  To register, run this code from your main
		activity's onCreate() method:
		
		mgr = new PushIOManager(this);

		if (!mgr.isRegistered()) {}
			String senderId = getString(R.string.C2DMSenderID);
			String pushIORegistrationHost = getString(R.string.PushIORegistrationHost);
			String pushIOAPIKey = getString(R.string.PushIOAPIKey);
		
			mgr.registerWithC2DM(senderId, pushIORegistrationHost, pushIOAPIKey);
		}        

	5)	You'll notice in the above code that we reference some configuration parameters.  These are
		best kept as such rather than hardcoded into your app.  if you agree, you should update
		your strings.xml with these entries (replacing the values with your own):
		
	    <string name="PushIORegistrationHost">reg.pushio.com</string>
	    <string name="PushIOAPIKey">[YOUR_PUSH_IO_API_KEY]</string>
	    <string name="C2DMSenderID">[YOUR_C2DM_SENDER_ID]</string>
		
		Note that the Sender ID is essentially a Google account.  It need
		not be unique among applications, but Google recommends one per app.

	6) 	Once you're registered with C2DM, you now just need to register some ASIDs.  That's easy:
	
		mgr = new PushIOManager(this);
	    	mgr.setPushIOManagerListener(this);

		if (mgr.isRegistered()) {
			// registering a single ASID
			mgr.registerASID("Android|Test|1");

			// registering multiple ASIDs (there is also a syntax for List)
			mgr.registerASID(new String[] { "Android|Test|1", "Android|Test|2", "Android|Test|3" });
		}

		Note here that we've also set up a listener. If you'd like to be notified of success or failure,
		implement the PushIOManagerListener interface and set it on the PushIOManager as above.


	7)	Support engagement, to find out if a user taps a notification to launch your app:

   		 Intent notificationIntent = new Intent(context, MainActivity.class);
   		 notificationIntent.putExtras(intent.getExtras()); // 'intent' is what is received

Then you can just pass that intent that starts the activity to the PushIOManager: 

   		 Intent intent = getIntent();
        		if (intent != null) {
        			mgr.trackEngagement(intent);
       		 }

At this point, you have successfully integrated Push.IO into your Android application.  For some
additional tips, please read the following sections.	

--------------------------------------------------------
Upon receiving a notification, what data will I receive?
--------------------------------------------------------

(NOTE: this section is not complete and is still being resolved by Push.IO)

When a notification is received by the app, Push.IO will deliver data custom to each
customer.  However, in general, one standard piece of data you will receive is alert data.  This is
a text message.

Your code can read that text message by pulling it out of the extras in the intent.  Here's an example
of using a toast which displays a small message on your screen:

	if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
		// handle notifications, perhaps with a toast!
		String alert = intent.getStringExtra("alert");

		Toast t = Toast.makeText(this, alert, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

Here is an example of using a notification which will save in your status bar:

	if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
		// handle notifications, with a status bar notification!
		String message = intent.getStringExtra("alert");

		NotificationManager notifier = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);		
		Notification notification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
	
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	
		notification.setLatestEventInfo(context, "Push.IO Message", message, pendingIntent);
	
		notifier.notify(1, notification);
	}

Other data is available depending on customer needs.


-----------------------------------------------------------------
How can I be notified about C2DM registration success or failure?
-----------------------------------------------------------------

Since C2DM responses come in via the broadcast receiver, it's impossible to set up a 
standard listener like we can for the ASID registration.  However, we can still find out
from our app.

There are two ways we can go about this, each with their own pros and cons:

	1)	Add logic at the end of our broadcast receiver after C2DMManager has a chance to process
		incoming notifications.
		
	2)	Add a local broadcast receiver to an activity.
	
The first method is the easiest to implement, but it comes at a price: We have no application context.  The
problem is that the broadcast receiver has no context with the app, even when it's running.  Any attempt
to get around that limitation isn't recommended.  Therefore, while this is a good place to
handle notifications from Push.IO by firing status bar notifications or updating a database, it's not a place
for extensive logic such as updating your UI or firing off other network-based activities.

To do this, the second method is recommended.  It's easy, too.  Here's what you do:

	1)	First, update your main activity (or whatever activity you want to do this in) to create and remove
		the broadcast receiver on pause and resume:
		
	    @Override
	    public void onPause() {
			super.onPause();
	    	unregisterReceiver(c2dmRegistrationReceiver);
	    }

	    @Override
	    public void onResume() {
			super.onResume();
			
	    	IntentFilter filter = new IntentFilter();
	    	filter.setPriority(-Integer.MIN_VALUE);
	        filter.addAction("com.google.android.c2dm.intent.REGISTRATION");    	
	        filter.addCategory("[APP_PACKAGE]");

	        registerReceiver(c2dmRegistrationReceiver, filter, "com.google.android.c2dm.permission.SEND", null);    
	    }
	
		It's important we prioritize this very low to make sure our other broadcast receiver runs first.
		We make sure it runs last by giving it the minimum priority allowed.

	2) 	Now, we need a broadcast receiver.  We create an anonymous object as a private member of our activity:
	
		private BroadcastReceiver c2dmRegistrationReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String regId = intent.getStringExtra("registration_id");
				String errorStr = intent.getStringExtra("error");
				boolean isRegistered = (regId != null) && (errorStr == null);

				// register with Push.IO
				if (isRegistered) {
					// ...do whatever in-activity logic we need...
				} else {
					//...handle the error, checking errorStr as appropriate...
				}
			}
		};	 
		
There may be other ways to accomplish this, but this is one of them.		


--------------------------------------------------------------
What happens if the user doesn't have a Google account set up?
--------------------------------------------------------------

Unlike the iOS Push Notification system, there is a requirement for the user
to register a Google account with their phone before they are able to use the C2DM system.

In the event the user doesn't have an account registered (it's not usually required), 
registration with C2DM will fail with the error string "ACCOUNT_MISSING".  You can read about
this error condition in the official C2DM documentation: http://code.google.com/android/c2dm/

It's appropriate to handle this specific case (if not any of the others) as it will likely
be a common case among early adopters. You can use the broadcast handling code in the section
above to look out for it.


-----------------------------------------------------------
What other useful things can I do with the Push.IO Manager?
-----------------------------------------------------------

Aside from the code above, there's a few other things you can do that may be handy
in your application.

First, creating a new Push.IO Manager can be done as many times as you like as it's
stateless.  The only thing you need to think about is the listener, which you would
have to set again if you create multiple instances.  The only parameter is your app context:

	mgr = new PushIOManager(this);

You can find out if your app is currently register with the C2DM:

	boolean isRegistered = mgr.isRegistered();
	
You can get the C2DM Registration ID as well as the last one you had:

	String currentRegistrationID = mgr.getRegistrationId();
	String previousRegistrationID = mgr.getOldRegistrationId();

The Push.IO Manager keeps track of all the ASIDs you register as you go along.  Sometimes, these
calls may fail, possibly due to a lack of network connectivity for the user.  Handling
success or failure can be cumbersome because notifying a user when an ASID registration fails
may be something they simply don't care about or is too technical to describe.  Therefore,
it can be handy to re-register all known ASIDs every time the user restarts the app.

To do that, you simply get the list of ASIDs stored by the Push.IO manager and then re-register them
as follows:

	mgr.reregisterASIDs();

If you merely want to know what ASIDs the app has registered, this will let you know:

	mgr.getASIDs();
	

-----------------------------------------------------------
Unregister specific ASID support addition
-----------------------------------------------------------

It is now possible to unregister specific ASIDs.  This is as easy as registering them:
	
	mgr = new PushIOManager(this);
	mgr.setPushIOManagerListener(this);

	if (mgr.isRegistered()) {
		// unregistering a single ASID
		mgr.unregisterASID("Android|Test|1");

		// unregistering multiple ASIDs (there is also a syntax for List)
		mgr.unregisterASID(new String[] { "Android|Test|1", "Android|Test|2", "Android|Test|3" });
	}

The PushIOManagerListener interface has changed to support this new functionality.  The following 
methods should be overridden:

	void registrationWithASIDSucceeded(...);
	void registrationWithASIDFailedWithError(…);
	// New methods
	void unregistrationWithASIDSucceeded(...);
	void unregistrationWithASIDFailedWithError(…);

