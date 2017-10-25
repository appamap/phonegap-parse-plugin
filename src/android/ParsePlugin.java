package org.apache.cordova.core;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ParsePlugin extends CordovaPlugin {
    public static final String TAG = "ParsePlugin";
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_GET_INSTALLATION_ID = "getInstallationId";
    public static final String ACTION_GET_INSTALLATION_OBJECT_ID = "getInstallationObjectId";
    public static final String ACTION_GET_SUBSCRIPTIONS = "getSubscriptions";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";

    public static Application myapp;
    
    public static void initializeParseWithApplication(Application app) {

//        String appId = getStringByKey(app, "parse_app_id");
//        String clientKey = getStringByKey(app, "parse_client_key");

//        String appId = "iuV3CpBCTLCmFIcaLOWdGjjtU8kgftGW1R720Ukl";
//        String clientKey = "avwkjSD8TGytJOMOK2sPyYMhEKmlIJlUclNLgj69";
//
//        Parse.enableLocalDatastore(app);
//        Log.d(TAG, "Initializing with parse_app_id: " + appId + " and parse_client_key:" + clientKey);
//        Parse.initialize(app, appId, clientKey);
        myapp=app;
    }
    
    private static String getStringByKey(Application app, String key) {
        int resourceId = app.getResources().getIdentifier(key, "string", app.getPackageName());
        return app.getString(resourceId);
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_INITIALIZE)) {
            this.initialize(callbackContext, args);
            return true;
        }
        if (action.equals(ACTION_GET_INSTALLATION_ID)) {
            this.getInstallationId(callbackContext);
            return true;
        }
        
        if (action.equals(ACTION_GET_INSTALLATION_OBJECT_ID)) {
            this.getInstallationObjectId(callbackContext);
            return true;
        }
        if (action.equals(ACTION_GET_SUBSCRIPTIONS)) {
            this.getSubscriptions(callbackContext);
            return true;
        }
        if (action.equals(ACTION_SUBSCRIBE)) {
            this.subscribe(args.getString(0), callbackContext);
            return true;
        }
        if (action.equals(ACTION_UNSUBSCRIBE)) {
            this.unsubscribe(args.getString(0), callbackContext);
            return true;
        }
        return false;
    }

    private void initialize(final CallbackContext callbackContext, final JSONArray args) throws JSONException {

        if (args.length() >= 2) {
            try{
                final String appId = args.getString(0);
                final String clientKey = args.getString(1);
                final String url = args.getString(2);

                Context mycontext=null;
//                if (this.cordova.getActivity()==null){
//                    Log.w("init","ERRORRRRRRR");
//                    return;
////                    final Context context=this.cordova.getActivity().getApplicationContext();
//                }else{
//                    mycontext=myapp;
//                }
                mycontext=myapp;
                final Context context=mycontext;


                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {

                        try {

                            //Parse.addParseNetworkInterceptor(new ParseLogInterceptor());
                            if (cordova.getActivity()==null){
                                Log.w("init","ERRORRRRRRR");
                            }else{
                                Log.w("init","INIT");
                                Parse.initialize(new Parse.Configuration.Builder(myapp.getApplicationContext())
                                        .applicationId(appId)
                                        .clientKey(clientKey)
                                        .server(url)
                                        .build()
                                );

                                final ParseInstallation parseInstallation =  ParseInstallation.getCurrentInstallation();
                                //parseInstallation.put("GCMSenderId", "817884550241");
                                parseInstallation.saveInBackground();

                                callbackContext.success();

                            }


                            //  GoogleCloudMessaging.getInstance(this.cordova.getActivity()).register(YOUR_GCMSENDERID, PARSE_DEFAULT_GCM_SENDERID);
                            // ParseInstallation.getCurrentInstallation().saveInBackground();


/*
                        InstanceID instanceID = InstanceID.getInstance(context);
                        String token = instanceID.getToken("114881002975",
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

*/



                            //ParseInstallation.getCurrentInstallation().getString("deviceToken");

/*
                     ParsePush.subscribeInBackground("", new SaveCallback() {
                     @Override
                     public void done(ParseException e) {
                     if (e == null) {
                     Log.d("com.parse.push",
                     "successfully subscribed to the broadcast channel.");
                     } else {
                     Log.e("com.parse.push", "failed to subscribe for push", e);
                     }
                     }
                     });

                     */




                        }
                        catch (Exception e) {
                            System.err.println("Caught IOException: " + e.getMessage());

                            //callbackContext.success(); //already installed on AWS
                        }
                    }
                });
            }catch(Exception ex){
                ex.printStackTrace();
            }


        }
    }
    
    private void getInstallationId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String installationId = ParseInstallation.getCurrentInstallation().getInstallationId();
                callbackContext.success(installationId);
            }
        });
    }
    
    private void getInstallationObjectId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String objectId = ParseInstallation.getCurrentInstallation().getObjectId();
                callbackContext.success(objectId);
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
    
    private void subscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try
                {
                    ParsePush.subscribeInBackground(channel);
                    callbackContext.success();
                    
                }
                catch (Exception e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                }
            }
        });
    }
    
    private void unsubscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                
                try
                {
                    ParsePush.unsubscribeInBackground(channel);
                    callbackContext.success();
                    
                }
                catch (Exception e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                }
            }
        });
    }
}

