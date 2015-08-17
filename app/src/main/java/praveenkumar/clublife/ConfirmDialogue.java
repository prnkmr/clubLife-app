package praveenkumar.clublife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Praveen kumar on 15/08/2015.
 */
public class ConfirmDialogue extends DialogFragment implements AppData {

    String eventId;
    Activity from;

    void setId(String id,Activity from){
        eventId=id;
        this.from=from;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you Sure to Delete this Event?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = getString(R.string.baseURL) + "deleteEvent.php";
                        HttpParam param = new HttpParam();
                        param.add(EVENT_ID_KEY, eventId);

                        final SpinnerDialogue dialogue = new SpinnerDialogue(getActivity(), "Deleteing...");
                        new AsyncHttp(url, param, new AsyncHttpListener() {
                            @Override
                            public void onResponse(String response) {
                                dialogue.cancel();
                                if (response == null) {
                                    myToast("TryAgain");
                                    return;
                                }
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt(ERROR_CODE_KEY) == 0) {
                                        try {
                                            from.finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        myToast("Delete Fail");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void myToast(String s) {
        Toast.makeText(from.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}