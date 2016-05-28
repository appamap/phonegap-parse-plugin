Plugin Adjustments
==================

We changed this plugin for our own AWS Parse Server, so we could handshake with service with custom URL. 

    Parse.initialize(new Parse.Configuration.Builder(cordova.getActivity())
                                        .applicationId(appId)
                                        .clientKey(clientKey)
                                        .server("http://elasticbeanstalklink")
                                        .build()
                        );

                        //ParseInstallation.getCurrentInstallation().saveInBackground();


                        final ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                        parseInstallation.put("GCMSenderId","89844585235");
                        parseInstallation.saveInBackground();



We replaced PushService objects with ParsePush objects e.g.:

 private void getSubscriptions(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                 Set<String> subscriptions = PushService.getSubscriptions(cordova.getActivity());
                 callbackContext.success(subscriptions.toString());
            }
        });
    }
    
    

private void getSubscriptions(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                List<String> list = ParseInstallation.getCurrentInstallation().getList("channels");

                try
                {
                    callbackContext.success(list.toString()); //try to pull list from aws
                    
                }
                catch (Exception e) {
                    System.err.println("Caught IOException: " + e.getMessage());

                    callbackContext.success("");
                }
            }
        });
    }
    
    
    Compiles/ Maven
    ===============
    
      compile 'com.parse:parse-android:1.10.2'
    debugCompile 'com.parse:parseinterceptors:0.0.1'
    
    
    
    


Phonegap Parse.com Plugin
=========================

Phonegap 3.0.0 plugin for Parse.com push service

Using [Parse.com's](http://parse.com) REST API for push requires the installation id, which isn't available in JS

This plugin exposes the four native Android API push services to JS:
* <a href="https://www.parse.com/docs/android/api/com/parse/ParseInstallation.html#getInstallationId()">getInstallationId</a>
* <a href="https://www.parse.com/docs/android/api/com/parse/PushService.html#getSubscriptions(android.content.Context)">getSubscriptions</a>
* <a href="https://www.parse.com/docs/android/api/com/parse/PushService.html#subscribe(android.content.Context, java.lang.String, java.lang.Class, int)">subscribe</a>
* <a href="https://www.parse.com/docs/android/api/com/parse/PushService.html#unsubscribe(android.content.Context, java.lang.String)">unsubscribe</a>

Installation
------------

Pick one of these two commands:

```
phonegap local plugin add https://github.com/benjie/phonegap-parse-plugin --variable APP_ID=PARSE_APP_ID --variable CLIENT_KEY=PARSE_CLIENT_KEY
cordova plugin add https://github.com/benjie/phonegap-parse-plugin --variable APP_ID=PARSE_APP_ID --variable CLIENT_KEY=PARSE_CLIENT_KEY
```

Initial Setup
-------------

Once the device is ready, call ```parsePlugin.initialize()```. This will register the device with Parse, you should see this reflected in your Parse control panel. After this runs you probably want to save the installationID somewhere, and perhaps subscribe the user to a few channels. Here is a contrived example.

(Note: When using Windows Phone, clientKey must be your .NET client key from Parse. So you will need to set this based on platform i.e. if( window.device.platform == "Win32NT"))

```
parsePlugin.initialize(appId, clientKey, function() {

	parsePlugin.subscribe('SampleChannel', function() {
		
		parsePlugin.getInstallationId(function(id) {
		
			/**
			 * Now you can construct an object and save it to your own services, or Parse, and corrilate users to parse installations
			 * 
			 var install_data = {
			  	installation_id: id,
			  	channels: ['SampleChannel']
			 }
			 *
			 */

		}, function(e) {
			alert('error');
		});

	}, function(e) {
		alert('error');
	});
	
}, function(e) {
	alert('error');
});

```


Usage
-----
```
<script type="text/javascript">
	parsePlugin.initialize(appId, clientKey, function() {
		alert('success');
	}, function(e) {
		alert('error');
	});
  
	parsePlugin.getInstallationId(function(id) {
		alert(id);
	}, function(e) {
		alert('error');
	});
	
	parsePlugin.getSubscriptions(function(subscriptions) {
		alert(subscriptions);
	}, function(e) {
		alert('error');
	});
	
	parsePlugin.subscribe('SampleChannel', function() {
		alert('OK');
	}, function(e) {
		alert('error');
	});
	
	parsePlugin.unsubscribe('SampleChannel', function(msg) {
		alert('OK');
	}, function(e) {
		alert('error');
	});
</script>
```

Quirks
------

### Android

Parse needs to be initialized once in the `onCreate` method of your application class using the `initializeParseWithApplication` method.

If you don’t have an application class (which is most likely the case for a Cordova app), you can create one using this template:

```java
package my.package.namespace;

import android.app.Application;
import org.apache.cordova.core.ParsePlugin;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParsePlugin.initializeParseWithApplication(this);
    }

}
```

And add your application name to `AndroidManifest.xml`:

```xml
<application android:name="my.package.namespace.App" ... >...</application>
```


Compatibility
-------------
Phonegap > 3.0.0
