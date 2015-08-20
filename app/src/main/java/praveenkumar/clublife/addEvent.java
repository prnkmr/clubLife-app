package praveenkumar.clublife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class addEvent extends ActionBarActivity implements AsyncHttpListener, DatePickListener, TimePickListener {

    EditText eventNameText,ticketCountText;
    String date,time;
    String baseURL;
    SharedPreferences sharedPreference;


    private boolean dateSet=false,timeSet=false;
    private SpinnerDialogue spinnerDialogue;
    private int PLACE_PICKER_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        baseURL=getString(R.string.baseURL);
        eventNameText=(EditText)findViewById(R.id.eventName);
        ticketCountText=(EditText)findViewById(R.id.ticketCount);
        sharedPreference=getSharedPreferences("clublife", MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
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

    public void showDatePickerDialog(View v) {
        DatePickerFragment dateFragment = new DatePickerFragment();
        dateFragment.setDatePickListener(this);

        dateFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setTimePickListener(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void onDateSet(String date){
        TextView dateText=(TextView)findViewById(R.id.date);
        dateText.setText(date);
        this.date=date;
        dateSet=true;
    }

    public void onTimeSet(String time){
        TextView timeText=(TextView)findViewById(R.id.time);
        timeText.setText(time);
        this.time=time;
        timeSet=true;
    }
    String eventName,ticketCount,userId;
    public void addNewEvent(View v){
        eventName=eventNameText.getText().toString();
        ticketCount=ticketCountText.getText().toString();
        userId=sharedPreference.getString("userId", "");
        if("".equals(eventName)){
            myToast("Give an Event Name");
            return;
        }
        if(!dateSet){
            myToast("Choose Date");
            return;
        }
        if(!timeSet){
            myToast("Choose Time");
            return;
        }
        if("".equals(ticketCount)){
            myToast("Mention the Ticket Count");
            return;
        }

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Context context = getApplicationContext();
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }







    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng=place.getLatLng();
                spinnerDialogue=new SpinnerDialogue(this,"Creating Event...");
                String url=baseURL+"addEvent.php";
                HttpParam json=new HttpParam();
                json.add("userId",userId);
                json.add("eventName",eventName);
                json.add("dateTime",date+" "+time);
                json.add("ticketCount",ticketCount);
                json.add("lat",String.valueOf(latLng.latitude));
                json.add("lon",String.valueOf(latLng.longitude));


                new AsyncHttp(url,json,addEvent.this);
            }
        }
    }

    void myToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
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
            if(respJson.getInt("errorCode")==0){
                finish();
            }else{
                myToast("Server Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
