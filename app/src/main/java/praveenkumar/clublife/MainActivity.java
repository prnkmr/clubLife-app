package praveenkumar.clublife;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MainActivity extends ActionBarActivity implements AsyncHttpListener,AppData{

    CallbackManager callbackManager;
    FacebookSdk facebook;
    String baseURL="";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SpinnerDialogue spinnerDialogue;
    JSONObject FBjson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Locator locator=new Locator(this);
        pref = getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();

        facebook=new FacebookSdk();
        facebook.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        baseURL=getString(R.string.baseURL);

        /*if(AccessToken.getCurrentAccessToken()!=null)
        {
            LoginManager.getInstance().logOut();
        }*/

        Button loginButton=(Button)findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateOwnerLogin();
            }
        });
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        myLog("Login success");
                        final AccessToken accessToken = loginResult.getAccessToken();
                        Log.d("UserId", accessToken.getUserId());
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        Log.d("user Json", object.toString());
                                        try {
                                            editor.putString(USERNAME_KEY,object.getString("name"));
                                            editor.putString(USER_ID_KEY, accessToken.getUserId());
                                            editor.putString(USER_TYPE_KEY,USER_TYPE_PEOPLE);
                                            editor.commit();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        FBjson=object;
                                        validateLogin();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,birthday,gender,location,verified");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        myToast("cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        myToast("Login Error");
                        myToast(exception.toString());
                        Log.d("MainActivity", "Login Error");
                        Log.d("MainActivity",exception.toString());
                    }
                });

        if(pref.getBoolean(LOGGED_IN_KEY,false)){
            if(pref.getString(USER_TYPE_KEY,"").equals(USER_TYPE_OWNER)){
                onLoginOwner();
            }else{
                onLoginPeople();
            }
        }
    }

    private void onLoginPeople() {
        if(spinnerDialogue!=null) {
            spinnerDialogue.cancel();
            spinnerDialogue = null;
        }
        myToast("welcome " + pref.getString(USERNAME_KEY, ""));
        startActivity(new Intent(getBaseContext(), PeopleEventList.class));
        registerGCM(USER_TYPE_PEOPLE);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    void myToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void myLog(String msg){
        Log.d("MainActivity", msg);
    }


    @Override
    public void onResponse(String response) {
        if(spinnerDialogue!=null) {
            spinnerDialogue.cancel();
            spinnerDialogue = null;
        }
        if(response==null){
            myToast("Try Again");
            return;
        }
        try {
            JSONObject json=new JSONObject(response);
            if(json.getInt("errorCode")==0){
                int id;
                id = json.getInt("userId");
                editor.putString(USER_ID_KEY, id+"");
                editor.commit();
                    editor.putString(USER_TYPE_KEY,USER_TYPE_OWNER);
                    editor.putBoolean(LOGGED_IN_KEY,true);
                    editor.commit();
                    onLoginOwner();

            }else if(json.getInt("errorCode")==5) {
                myToast("Wrong Username/Password");
            }else{
                    myToast("Server error");
                }
        } catch (JSONException e) {
            myToast("Cannot Connect to Server");
            e.printStackTrace();
        }
    }

    private void onLoginOwner() {
        startActivity(new Intent(getApplicationContext(),ownerEventList.class));
        registerGCM(USER_TYPE_OWNER);
        finish();
    }

    private void registerGCM(String userType) {
        Intent intent=new Intent(getApplicationContext(), TokenReceiver.class);
        intent.putExtra(USER_TYPE_KEY,userType);
        intent.putExtra(USER_ID_KEY,pref.getString(AppData.USER_ID_KEY,""));
        startService(intent);
    }

    void validateLogin(){
        String url=baseURL+"checkFBLogin.php";
        myLog(url);
        List<NameValuePair> json=new ArrayList<NameValuePair>();
        try {
            json.add(new BasicNameValuePair("FBId",FBjson.getString("id")));
            json.add(new BasicNameValuePair("userName",FBjson.getString("name")));
            json.add(new BasicNameValuePair("link",FBjson.getString("link")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new AsyncHttp(url, json, new AsyncHttpListener() {
            @Override
            public void onResponse(String response) {
                if(spinnerDialogue!=null) {
                    spinnerDialogue.cancel();
                    spinnerDialogue = null;
                }
                if(response==null){
                    ConfirmReload confirmReload=new ConfirmReload();
                    confirmReload.setConfirmationListener(new ConfirmationListener() {
                        @Override
                        public void onConfirm() {
                            validateLogin();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    confirmReload.show(getSupportFragmentManager(), "Notice");
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("errorCode") == 0) {
                        int id;
                        id = json.getInt("userId");
                        editor.putString(USER_ID_KEY, id + "");
                        editor.commit();
                        onLoginPeople();
                        editor.putBoolean(LOGGED_IN_KEY, true);
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        spinnerDialogue=new SpinnerDialogue(this,"Logging In...");
    }
    void validateOwnerLogin(){
        String username=((EditText)findViewById(R.id.username)).getText().toString();
        String password=((EditText)findViewById(R.id.password)).getText().toString();

        String url=baseURL+"checkOwnerLogin.php";
        List<NameValuePair> json= new ArrayList<NameValuePair>();
        json.add(new BasicNameValuePair("username",username));
        json.add(new BasicNameValuePair("password",password));

        new AsyncHttp(url,json,this);
        spinnerDialogue=new SpinnerDialogue(this,"Verifiying...");
    }

}
