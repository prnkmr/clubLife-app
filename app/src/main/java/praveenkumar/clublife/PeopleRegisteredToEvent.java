package praveenkumar.clublife;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class PeopleRegisteredToEvent extends ActionBarActivity implements AsyncHttpListener,  AdapterView.OnItemClickListener {
    ListView listView;
    TextView titleText;
    String baseURL,eventId,eventName,userId;
    List<String> usersList,idReference,ticketIdReference;
    SpinnerDialogue spinnerDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseURL=getString(R.string.baseURL);
        userId=getIntent().getStringExtra("userId");
        setContentView(R.layout.activity_people_registered_to_event);
        listView=(ListView)findViewById(R.id.registeredUsers);
        titleText=(TextView)findViewById(R.id.eventTitle);
        eventId=getIntent().getExtras().getString("eventId");
        eventName=getIntent().getExtras().getString("eventName");
        titleText.setText(eventName);
        updateList();
    }

    void updateList(){
        String url=baseURL+"getRegisteredUsers.php";
        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("eventId",eventId));
        new AsyncHttp(url,json,this);
        spinnerDialogue=new SpinnerDialogue(this,"Loding User List...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_registered_to_event, menu);
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
            if(respJson.getInt("errorCode")==0){
                usersList=new ArrayList<>();
                idReference=new ArrayList<>();
                ticketIdReference=new ArrayList<>();
                JSONArray usersJson=respJson.getJSONArray("list");
                for (int i = 0; i < usersJson.length(); i++) {
                    JSONArray userJson=usersJson.getJSONArray(i);
                    usersList.add(userJson.getString(0));
                    idReference.add(userJson.getString(1));
                    ticketIdReference.add(userJson.getString(2));
                }
                ArrayAdapter adapter=new ArrayAdapter(this,R.layout.default_list_element_dark,usersList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        ((TextView)convertView.findViewById(R.id.eventName)).setText(usersList.get(position));
                        return convertView;
                    }
                };
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Clicked", position + "");
        Intent userDetailsIntent=new Intent(this,PeopleDetails.class);
        userDetailsIntent.putExtra("userId",idReference.get(position));
        userDetailsIntent.putExtra("ticketId",ticketIdReference.get(position));
        startActivity(userDetailsIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
