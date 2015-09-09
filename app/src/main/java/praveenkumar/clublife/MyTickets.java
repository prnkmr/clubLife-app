package praveenkumar.clublife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyTickets extends ActionBarActivity implements AppData,AsyncHttpListener, AdapterView.OnItemClickListener {
    String baseURL="";
    ListView listview;
    List<String> idReference,list;
    SpinnerDialogue spinnerDialogue;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);
        pref=getSharedPreferences(AppData.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        baseURL=getString(R.string.baseURL);

        updateList();



    }

    void updateList(){
        listview = (ListView) findViewById(R.id.ticketList);
        String url=baseURL+"getMyTickets.php";
        List<NameValuePair> json=new ArrayList<>();
        String id=getSharedPreferences("clublife", Context.MODE_PRIVATE).getString("userId","");
        json.add(new BasicNameValuePair("userId",id));
        spinnerDialogue=new SpinnerDialogue(this,"Loading Tickets...");
        new AsyncHttp(url,json,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_tickets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if(id==R.id.update){
            updateList();
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
                    updateList();
                }

                @Override
                public void onCancel() {

                }
            });
            confirmReload.show(getSupportFragmentManager(), "Notice");
            return;
        }

        try {
            JSONObject json=new JSONObject(response);
            if(json.getInt("errorCode")==0){
                JSONArray events=json.getJSONArray("list");
                ArrayList<Ticket> ticketArray=new ArrayList<>();
                for (int i = 0; i < events.length(); ++i) {
                    JSONArray event=(JSONArray)events.get(i);
                    Ticket ticket=new Ticket();
                    ticket.eventId=event.getString(0);
                    ticket.eventName=event.getString(1);
                    ticket.id=event.getString(2);
                    ticketArray.add(ticket);
                }
                CustomAdapter adapter = new CustomAdapter(this,
                        android.R.layout.simple_list_item_1, ticketArray);

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
        //updateList();
    }
    class Ticket{
      public String eventName,eventId,id;
    }

    class CustomAdapter extends ArrayAdapter<Ticket>{
        public CustomAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Ticket ticket=getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.myticket_list_element, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.eventName)).setText(ticket.eventName);
            ((TextView)convertView.findViewById(R.id.eventName)).setTextColor(Color.WHITE);
            ((TextView)convertView.findViewById(R.id.ticketId)).setText(ticket.id);
            ((TextView)convertView.findViewById(R.id.ticketId)).setTextColor(Color.WHITE);
            return convertView;
        }
    }



}
