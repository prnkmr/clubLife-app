package praveenkumar.clublife;

import android.content.SharedPreferences;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class addEvent extends ActionBarActivity implements AsyncHttpListener {

    EditText eventNameText,ticketCountText;
    String date,time;
    String baseURL;
    SharedPreferences sharedPreference;


    private boolean dateSet=false,timeSet=false;
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
        dateFragment.setCaller(this);

        dateFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setCaller(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    void onDateSet(String date){
        TextView dateText=(TextView)findViewById(R.id.date);
        dateText.setText(date);
        this.date=date;
        dateSet=true;
    }

    void onTimeSet(String time){
        TextView timeText=(TextView)findViewById(R.id.time);
        timeText.setText(time);
        this.time=time;
        timeSet=true;
    }

    public void addNewEvent(View v){
        String eventName=eventNameText.getText().toString();
        String ticketCount=ticketCountText.getText().toString();
        String userId=sharedPreference.getString("userId","");
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

        String url=baseURL+"addEvent.php";
        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("userId",userId));
        json.add(new BasicNameValuePair("eventName",eventName));
        json.add(new BasicNameValuePair("dateTime",date+" "+time));
        json.add(new BasicNameValuePair("ticketCount",ticketCount));
        new AsyncHttp(url,json,this);



    }

    void myToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResponse(String response) {
        if(response==null){
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
