package praveenkumar.clublife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
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


public class PeopleEventList extends ActionBarActivity implements AppData,AsyncHttpListener, AdapterView.OnItemClickListener {

    ListView listview;
    String baseURL;
    SpinnerDialogue spinnerDialogue;
    ArrayAdapter defaultAdapter;
    EventList eventList;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_event_list);

        baseURL=getString(R.string.baseURL);
        pref=getSharedPreferences(SHARED_PREFERENCE_KEY,MODE_PRIVATE);


        updateList();



    }

    private void updateList() {
        String lat,lon;
        lat=pref.getString("lat","");
        lon=pref.getString("lon","");
        listview = (ListView) findViewById(R.id.allEvents);
        String url;
        HttpParam param=new HttpParam();

        if(lat.equals("")||lon.equals("")){
            url=baseURL+"getPeopleEvents.php";
        }else{
            url=baseURL+"getClosestEvents.php";
            param.add("lat",lat);
            param.add("lon",lon);
        }
        Log.d("URL", url);
        new AsyncHttp(url,param,this);
        spinnerDialogue=new SpinnerDialogue(this,"Loading Events...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_event_list, menu);
        MenuItem searchItem = menu.findItem(R.id.filter);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("query sub", "submit");
                if (query.equals(""))
                    listview.setAdapter(defaultAdapter);
                else {
                    EventList tEventList=eventList.filterList(query);
                    ArrayAdapter tArrayAdapter=new ArrayAdapter(PeopleEventList.this,android.R.layout.simple_list_item_1, tEventList.getListString());
                    listview.setAdapter(tArrayAdapter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Query cha", "change");
                if (newText.equals(""))
                    listview.setAdapter(defaultAdapter);
                else {
                    EventList tEventList=eventList.filterList(newText);
                    ArrayAdapter tArrayAdapter=new ArrayAdapter(PeopleEventList.this,android.R.layout.simple_list_item_1, tEventList.getListString());
                    listview.setAdapter(tArrayAdapter);
                }
                return true;
            }
        });
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
            if(AccessToken.getCurrentAccessToken()!=null)
            {

                LoginManager.getInstance().logOut();

            }

            myToast("Logging out..");
            SharedPreferences.Editor editor=pref.edit();
            editor.putBoolean(LOGGED_IN_KEY,false);
            editor.commit();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                    updateList();
                }

                @Override
                public void onCancel() {

                }
            });
            confirmReload.show(getSupportFragmentManager(), "Notice");
            return;
        }


        eventList=new EventList();
        try {
            JSONObject json=new JSONObject(response);
            if(json.getInt("errorCode")==0){
                JSONArray events=json.getJSONArray("list");
                for (int i = 0; i < events.length(); ++i) {
                    JSONArray event=(JSONArray)events.get(i);
                    Double ddist=Double.parseDouble(String.valueOf(event.get(2)));
                    eventList.add((String)event.get(0),(String)event.get(1),ddist);

                }
                defaultAdapter = new ArrayAdapter(this,
                        android.R.layout.simple_list_item_1, eventList.getListString());
                listview.setAdapter(defaultAdapter);
                listview.setOnItemClickListener(this);


                listview.setTextFilterEnabled(true);
            }
            else{
                myToast("Server Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void myToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent detailsIntent=new Intent(this,PeopleEventDetails.class);
        detailsIntent.putExtra("eventId",eventList.getId(position));
        detailsIntent.putExtra("distance",eventList.getDistance(position));
        startActivity(detailsIntent);
    }
}
