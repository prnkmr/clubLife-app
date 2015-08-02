package praveenkumar.clublife;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ownerEventList extends ActionBarActivity implements AsyncHttpListener, AdapterView.OnItemClickListener {
    String baseURL="";
    ListView listview;
    List<String> idReference,list;
    SpinnerDialogue spinnerDialogue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_event_list);
        baseURL=getString(R.string.baseURL);
        Button newEvent=(Button)findViewById(R.id.addEvent);
        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });

        //updateList();



    }

    void updateList(){
        listview = (ListView) findViewById(R.id.events);
        String url=baseURL+"getOwnerEvents.php";
        List<NameValuePair> json=new ArrayList<>();
        String id=getSharedPreferences("clublife", Context.MODE_PRIVATE).getString("userId","");
        json.add(new BasicNameValuePair("userId",id));
        spinnerDialogue=new SpinnerDialogue(this,"Loading Events...");
        new AsyncHttp(url,json,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_owner_event_list, menu);
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

        list = new ArrayList<String>();
        idReference=new ArrayList<String>();
        try {
            JSONObject json=new JSONObject(response);
            if(json.getInt("errorCode")==0){
            JSONArray events=json.getJSONArray("list");
            for (int i = 0; i < events.length(); ++i) {
                JSONArray event=(JSONArray)events.get(i);

                idReference.add(event.getString(0));
                list.add(event.getString(1));
            }
                final ArrayAdapter adapter = new ArrayAdapter(this,
                        android.R.layout.simple_list_item_1, list);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(this);
            }
            else{
                myToast("Server Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent registeredUsersIntent = new Intent(this,PeopleRegisteredToEvent.class);
        registeredUsersIntent.putExtra("eventId",idReference.get(position));
        registeredUsersIntent.putExtra("eventName",list.get(position));
        startActivity(registeredUsersIntent);
    }

    void addEvent(){
        startActivity(new Intent(getApplicationContext(),addEvent.class));
    }

    void myToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }
}
