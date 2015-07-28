package praveenkumar.clublife;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class PeopleEventDetails extends ActionBarActivity implements AsyncHttpListener {
    TextView eventTitleText,dateText,timeText,locationText;
    String baseURL,eventId;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences=getSharedPreferences("clublife",MODE_PRIVATE);
        eventId=getIntent().getExtras().getString("eventId");
        setContentView(R.layout.activity_people_event_details);
        baseURL=getString(R.string.baseURL);
        eventTitleText=(TextView)findViewById(R.id.eventTitle);
        dateText=(TextView)findViewById(R.id.date);
        timeText=(TextView)findViewById(R.id.time);
        locationText=(TextView)findViewById(R.id.location);
        updateData();
    }

    private void updateData() {
        String url=baseURL+"getEventDetailsPeople.php";
        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("eventId",eventId));
        new AsyncHttp(url,json,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_event_details, menu);
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
            if(response==null){
                myToast("Try Again");
                return;
            }
        try {
            JSONObject respJson=new JSONObject(response);
            if(respJson.getInt("errorCode")==0) {
                eventTitleText.setText(respJson.getString("eventName"));
                String dateTime=respJson.getString("eventDateTime");

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
        String userId=preferences.getString("userId","");
        String userName=preferences.getString("userName","");

        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("userId",userId));
        json.add(new BasicNameValuePair("userName",userName));
        json.add(new BasicNameValuePair("eventId",eventId));

        new AsyncHttp(url, json, new AsyncHttpListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respJson=new JSONObject(response);
                    if(respJson.getInt("errorCode")==0) {
                        myToast("Registration Success");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
