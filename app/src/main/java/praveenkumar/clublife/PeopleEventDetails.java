package praveenkumar.clublife;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PeopleEventDetails extends ActionBarActivity implements AppData, AsyncHttpListener {
    TextView eventTitleText,dateText,timeText,locationText,addressText,ticketCountText,distanceText;
    String baseURL,eventId;
    SharedPreferences preferences;
    SpinnerDialogue spinnerDialogue;
    Button shareButton;
    String lat=null,lon=null;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences=getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        eventId=getIntent().getExtras().getString(EVENT_ID_KEY);
        setContentView(R.layout.activity_people_event_details);
        baseURL=getString(R.string.baseURL);
        eventTitleText=(TextView)findViewById(R.id.eventTitle);
        dateText=(TextView)findViewById(R.id.date);
        timeText=(TextView)findViewById(R.id.time);
        locationText=(TextView)findViewById(R.id.location);
        addressText=(TextView)findViewById(R.id.address);
        ticketCountText=(TextView)findViewById(R.id.ticketCount);
        distanceText=(TextView)findViewById(R.id.distance);
        double kms=getIntent().getDoubleExtra("distance",0.0);
        distanceText.setText(String.valueOf(kms));
        ((Button)findViewById(R.id.direct)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat!=null&&lon!=null){
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?f=d&daddr="+lat+","+lon));
                    intent.setComponent(new ComponentName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity"));
                    startActivity(intent);
                }
            }
        });
        updateData();


    }

    private void updateData() {
        String url=baseURL+"getEventDetailsPeople.php";
        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("eventId",eventId));
        new AsyncHttp(url,json,this);
        spinnerDialogue=new SpinnerDialogue(this,"Loading Event Details...");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_event_details, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

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
                myToast("Try Again");
                return;
            }
        try {
            JSONObject respJson=new JSONObject(response);
            if(respJson.getInt("errorCode")==0) {
                eventTitleText.setText(respJson.getString("eventName"));
                String dateTime=respJson.getString("eventDateTime");
                lat=respJson.getString("lat");
                lon=respJson.getString("lon");

                SimpleDateFormat formater= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    Date date = formater.parse(dateTime);
                    dateText.setText(new SimpleDateFormat("EEE, dd-MMM-yy").format(date));
                    Time time=new Time(date.getTime());
                    timeText.setText(new SimpleDateFormat("h:mm a").format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                locationText.setText(respJson.getString("hotelName"));
                addressText.setText(respJson.getString("address"));
                ticketCountText.setText(respJson.getString("ticketCount"));
            }else{
                myToast("Server Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void myToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    public  void registerEvent(View view){
        String url=baseURL+"registerToEvent.php";
        String userId=preferences.getString(USER_ID_KEY,"");
        String userName=preferences.getString(USERNAME_KEY,"");

        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("userId",userId));
        json.add(new BasicNameValuePair("userName",userName));
        json.add(new BasicNameValuePair("eventId",eventId));
        spinnerDialogue=new SpinnerDialogue(this,"Registering...");
        new AsyncHttp(url, json, new AsyncHttpListener() {
            @Override
            public void onResponse(String response) {
                spinnerDialogue.cancel();
                if(response==null){
                    myToast("Try Again");
                    return;
                }
                try {
                    JSONObject respJson=new JSONObject(response);
                    if(respJson.getInt("errorCode")==0) {
                        myToast("Registration Success");
                        finish();
                    }else if(respJson.getInt("errorCode")==6){
                        myToast("Ticket not Available");
                        updateData();
                    }else if(respJson.getInt("errorCode")==101){
                        myToast("Already Registered");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
