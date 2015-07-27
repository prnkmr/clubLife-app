package praveenkumar.clublife;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Abijith Krishna on 01-07-2015.
 */
public class AsyncHttp extends AsyncTask{
    HttpPost post;
    List jsonObject;
    HttpClient client;
    String resp=null;
    public AsyncHttpListener caller;
    AsyncHttp( String url, List jsonObject,AsyncHttpListener caller) {
        this.caller=caller;
        HttpParams httpParameters = new BasicHttpParams();
        this.jsonObject=jsonObject;
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


        client=new DefaultHttpClient(httpParameters);
        post=new HttpPost(url);
        System.out.println("Constructor done");

    }
    @Override
    protected Object doInBackground(Object[] params) {
        System.out.println("Process started");
        try {

            post.setEntity(new UrlEncodedFormEntity(jsonObject));
            HttpResponse response = client.execute(post);

            resp= EntityUtils.toString(response.getEntity());
          //  System.out.println(resp);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        caller.onResponse(resp);
    }
}
