package praveenkumar.clublife;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Praveen kumar on 16/08/2015.
 */
public class HttpParam extends ArrayList {
    public void add(String key,String value){
        add(new BasicNameValuePair(key,value));
    }
}
