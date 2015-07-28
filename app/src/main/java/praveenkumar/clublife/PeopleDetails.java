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


public class PeopleDetails extends ActionBarActivity implements AsyncHttpListener {
    TextView peopleNameText,FBLinkText;
    String baseURL,userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseURL=getString(R.string.baseURL);
        userId=getIntent().getStringExtra("userId");
        setContentView(R.layout.activity_people_details);
        peopleNameText=(TextView)findViewById(R.id.userName);
        FBLinkText=(TextView)findViewById(R.id.FBLink);
        peopleNameText.setText(getIntent().getStringExtra("userName"));
        Log.d("userId", userId);
        String url=baseURL+"getUserDetails.php";
        ArrayList json=new ArrayList();
        json.add(new BasicNameValuePair("userId",userId));
        new AsyncHttp(url,json,this);
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
        if(response==null){
            myToast("Try Again");
            return;
        }
        try {
            JSONObject respJson=new JSONObject(response);
            if(respJson.getInt("errorCode")==0){
                peopleNameText.setText(respJson.getString("userName"));
                final String FBLink=respJson.getString("FBLink");
                FBLinkText.setText(FBLink);
                FBLinkText.setTextColor(Color.BLUE);
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
}
