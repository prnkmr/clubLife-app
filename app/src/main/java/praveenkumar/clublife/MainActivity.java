package praveenkumar.clublife;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import java.util.List;


public class MainActivity extends ActionBarActivity implements AsyncHttpListener{

    CallbackManager callbackManager;
    FacebookSdk facebook;
    String baseURL="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        facebook=new FacebookSdk();
        facebook.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        baseURL=getString(R.string.baseURL);
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

                        AccessToken accessToken = loginResult.getAccessToken();
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
                                            myToast("welcome " + object.getString("name"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        validateLogin(object);

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
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("MainActivity", "Login Error");
                    }
                });


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
        Log.d("Response",response);
        try {
            JSONObject json=new JSONObject(response);
            if(json.getInt("errorCode")==0){
                int id;
                if("people".equals(json.getString("userType"))) {
                    id = json.getInt("userId");
                    //startActivity(new Intent(getApplicationContext(),));
                }else{
                    id=json.getInt("userId");
                    String hotelName=json.getString("hotelName");
                    String address=json.getString("address");
                    startActivity(new Intent(getApplicationContext(),ownerEventList.class));
                }
                SharedPreferences pref = getSharedPreferences("clublife", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("userId", id);
                editor.commit();
            }else{
                myToast("Server error");
            }
        } catch (JSONException e) {

            myToast("Cannot Connect to Server");
            e.printStackTrace();
        }

    }
    void validateLogin(JSONObject FBjson){
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
        new AsyncHttp(url,json,this).execute();
    }
    void validateOwnerLogin(){
        String username=((EditText)findViewById(R.id.username)).getText().toString();
        String password=((EditText)findViewById(R.id.password)).getText().toString();

        String url=baseURL+"checkOwnerLogin.php";
        List<NameValuePair> json= new ArrayList<NameValuePair>();
        json.add(new BasicNameValuePair("username",username));
        json.add(new BasicNameValuePair("password",password));
        new AsyncHttp(url,json,this).execute();
    }
}
