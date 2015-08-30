package praveenkumar.clublife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerEventDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerEventDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerEventDetails extends Fragment implements AppData,AsyncHttpListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String eventId;
    private EditText eventTitleText;
    private TextView dateText;
    private TextView timeText;
    private EditText ticketCountText;
    private String baseURL;
    private SpinnerDialogue spinnerDialogue;
    private Button deleteButton;
    private Button editButton;
    private String dateString;
    private String timeString;
    private Button downloadButton;
    boolean loaded=false;
    String oldResp;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OwnerEventDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerEventDetails newInstance(String param1, String param2) {
        OwnerEventDetails fragment = new OwnerEventDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OwnerEventDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_owner_event_details, container, false);

        eventId=getActivity().getIntent().getExtras().getString(EVENT_ID_KEY);
        eventTitleText=(EditText)view.findViewById(R.id.eventTitle);
        dateText=(TextView)view.findViewById(R.id.date);
        timeText=(TextView)view.findViewById(R.id.time);
        ticketCountText=(EditText)view.findViewById(R.id.ticketCount);

        eventTitleText.setFocusable(false);
        ticketCountText.setFocusable(false);
        baseURL=getString(R.string.baseURL);

        deleteButton=(Button)view.findViewById(R.id.deleteEvent);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialogue newFragment = new ConfirmDialogue();
                newFragment.setId(eventId,getActivity());
                newFragment.show(getActivity().getSupportFragmentManager(), "Notice");



            }
        });

        editButton=(Button)view.findViewById(R.id.editDetails);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent(v);
            }
        });

        downloadButton=(Button)view.findViewById(R.id.download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseURL+"downloadExcel.php?eventId="+eventId));
                startActivity(browserIntent);
            }
        });
        if(loaded) onResponse(oldResp); else updateData();

        return view;
    }

    private void updateData() {
        String url=baseURL+"getEventDetailsPeople.php";
        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("eventId", eventId));
        new AsyncHttp(url,json,this);
        spinnerDialogue=new SpinnerDialogue(getActivity(),"Loading Event Details...");

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void editEvent(View v) {

        if(editButton.getText().toString().equals("Edit")) {
            editButton.setText("Update");
            eventTitleText.setFocusable(true);
            eventTitleText.setFocusableInTouchMode(true);
            ticketCountText.setFocusable(true);
            ticketCountText.setFocusableInTouchMode(true);
            dateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerFragment dateFragment = new DatePickerFragment();
                    dateFragment.setDatePickListener(new DatePickListener() {
                        @Override
                        public void onDateSet(String dateString) {
                            OwnerEventDetails.this.dateString = dateString;
                            dateText.setText(dateString);
                        }
                    });

                    dateFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                }
            });
            timeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerFragment timeFragment=new TimePickerFragment();
                    timeFragment.setTimePickListener(new TimePickListener() {
                        @Override
                        public void onTimeSet(String timeString) {
                            OwnerEventDetails.this.timeString=timeString;
                            timeText.setText(timeString);
                        }
                    });
                    timeFragment.show(getActivity().getSupportFragmentManager(),"timePicker");
                }
            });
        }else{
            updateDetails();
            editButton.setText("Edit");
            eventTitleText.setFocusable(false);
            ticketCountText.setFocusable(false);
            dateText.setOnClickListener(null);
            timeText.setOnClickListener(null);

        }

    }

    private void updateDetails() {
        String url=baseURL+"updateEventDetails.php";
        HttpParam param=new HttpParam();
        param.add(EVENT_ID_KEY,eventId);
        param.add(EVENT_NAME_KEY,eventTitleText.getText().toString());
        param.add("dateTime",dateString+" "+timeString);
        param.add("ticketCount",ticketCountText.getText().toString());
        final SpinnerDialogue dialogue=new SpinnerDialogue(getActivity(),"Updating...");
        new AsyncHttp(url, param, new AsyncHttpListener() {
            @Override
            public void onResponse(String response) {
                dialogue.cancel();
                if(response==null){
                    myToast("TryAgain");
                    return;
                }
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getInt(ERROR_CODE_KEY)==0){
                        myToast("Success");
                    }else{
                        myToast("Update Fail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResponse(String response) {
        spinnerDialogue.cancel();
        if(response==null){
            ConfirmReload confirmReload=new ConfirmReload();
            confirmReload.setConfirmationListener(new ConfirmationListener() {
                @Override
                public void onConfirm() {
                    updateData();
                }

                @Override
                public void onCancel() {
                    getActivity().finish();
                }


            });
            confirmReload.show(getActivity().getSupportFragmentManager(), "Notice");
            return;
        }
        try {
            JSONObject respJson=new JSONObject(response);
            if(respJson.getInt("errorCode")==0) {
                eventTitleText.setText(respJson.getString("eventName"));
                String dateTime=respJson.getString("eventDateTime");

                SimpleDateFormat formater= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    Date date = formater.parse(dateTime);
                    dateText.setText(new SimpleDateFormat("EEE, dd-MMM-yy").format(date));
                    dateString=new SimpleDateFormat("yyyy-MM-dd").format(date);
                    Time time=new Time(date.getTime());
                    timeText.setText(new SimpleDateFormat("h:mm a").format(date));
                    timeString=new SimpleDateFormat("HH:mm:ss").format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ticketCountText.setText(respJson.getString("ticketCount"));
            }else{
                myToast("Server Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        oldResp=response;
        loaded=true;
    }

    private void myToast(String s) {
        Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }



}
