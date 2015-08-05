package praveenkumar.clublife;

import android.content.Context;
import android.content.Intent;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PeopleEventList extends ActionBarActivity implements AsyncHttpListener, AdapterView.OnItemClickListener {

    ListView listview;
    String baseURL;
    List<String> idReference;
    SpinnerDialogue spinnerDialogue;
    ArrayAdapter defaultAdapter;
    EventList eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_event_list);

        baseURL=getString(R.string.baseURL);

        updateList();



    }

    private void updateList() {
        listview = (ListView) findViewById(R.id.allEvents);
        String url=baseURL+"getPeopleEvents.php";
        Log.d("URL", url);
        List<NameValuePair> json=new ArrayList<>();
        new AsyncHttp(url,json,this);
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


        eventList=new EventList();
        try {
            JSONObject json=new JSONObject(response);
            if(json.getInt("errorCode")==0){
                JSONArray events=json.getJSONArray("list");
                for (int i = 0; i < events.length(); ++i) {
                    JSONArray event=(JSONArray)events.get(i);
                    eventList.add((String)event.get(0),(String)event.get(1));

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
        detailsIntent.putExtra("eventId",idReference.get(position));
        startActivity(detailsIntent);
    }
}
