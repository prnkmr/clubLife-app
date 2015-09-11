package praveenkumar.clublife;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PeopleDetails extends ActionBarActivity implements AppData,AsyncHttpListener {
    TextView peopleNameText,FBLinkText;
    String baseURL,userId;
    SpinnerDialogue spinnerDialogue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseURL=getString(R.string.baseURL);
        userId=getIntent().getStringExtra(USER_ID_KEY);
        setContentView(R.layout.activity_people_details);
        peopleNameText=(TextView)findViewById(R.id.userName);
        FBLinkText=(TextView)findViewById(R.id.FBLink);
        peopleNameText.setText(getIntent().getStringExtra(USERNAME_KEY));
        Log.d("userId", userId);
        loadDetails();
    }

    void loadDetails(){
        String url=baseURL+"getUserDetails.php";
        ArrayList json=new ArrayList();
        json.add(new BasicNameValuePair("userId",userId));
        new AsyncHttp(url,json,this);
        spinnerDialogue=new SpinnerDialogue(this,"Loading User Details...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_details, menu);
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
    public void onResponse(String response) {
        spinnerDialogue.cancel();
        if(response==null){
            ConfirmReload confirmReload=new ConfirmReload();
            confirmReload.setConfirmationListener(new ConfirmationListener() {
                @Override
                public void onConfirm() {
                    loadDetails();
                }

                @Override
                public void onCancel() {
                    finish();
                }
            });
            confirmReload.show(getSupportFragmentManager(), "Notice");
            return;
        }
        try {
            JSONObject respJson=new JSONObject(response);
            if(respJson.getInt("errorCode")==0){
                peopleNameText.setText(respJson.getString("userName"));
                final String FBLink=respJson.getString("FBLink");
                FBLinkText.setText(FBLink);
                //FBLinkText.setTextColor(Color.BLUE);
                FBLinkText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FBLink));
                        startActivity(browserIntent);
                    }
                });
            }else{
                myToast("Server Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void myToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    public void confirmTicket(View view){
        String url=baseURL+"confirmTicket.php";
        ArrayList param=new ArrayList();
        param.add(new BasicNameValuePair(TICKET_ID_KEY,getIntent().getStringExtra(TICKET_ID_KEY)));
        param.add(new BasicNameValuePair(EVENT_NAME_KEY,getIntent().getStringExtra(EVENT_NAME_KEY)));
        new AsyncHttp(url, param, new AsyncHttpListener() {
            @Override
            public void onResponse(String response) {
                spinnerDialogue.cancel();
                if(response==null){
                    myToast("Try Again");
                    return;
                }
                try {
                    JSONObject resp=new JSONObject(response);
                    if(resp.getInt("errorCode")==0){
                        myToast("Ticket Confirmed");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        spinnerDialogue=new SpinnerDialogue(this,"Please wait...");
    }

    public void cancelTicket(View view){
        String url=baseURL+"cancelTicket.php";
        ArrayList param=new ArrayList();
        param.add(new BasicNameValuePair(TICKET_ID_KEY,getIntent().getStringExtra(TICKET_ID_KEY)));
        param.add(new BasicNameValuePair(EVENT_NAME_KEY,getIntent().getStringExtra(EVENT_NAME_KEY)));
        new AsyncHttp(url, param, new AsyncHttpListener() {
            @Override
            public void onResponse(String response) {
                spinnerDialogue.cancel();
                if(response==null){
                    myToast("Try Again");
                    return;
                }
                try {
                    JSONObject resp=new JSONObject(response);
                    if(resp.getInt("errorCode")==0){
                        myToast("Ticket Cancelled");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        spinnerDialogue=new SpinnerDialogue(this,"Please wait...");
    }
}
