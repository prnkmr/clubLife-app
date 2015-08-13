package praveenkumar.clublife;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Praveen kumar on 01/08/2015.
 */
public class SpinnerDialogue {
    ProgressDialog loading;

    SpinnerDialogue(Context parent, String msg){
        loading=new ProgressDialog(parent);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setMessage(msg);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.show();
    }

    void show(){
        loading.show();
    }

    void cancel(){

        loading.cancel();
    }

    void setCancellable(boolean state){
        loading.setCancelable(state);
    }
}
