package praveenkumar.clublife;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfirmedPeople.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfirmedPeople#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmedPeople extends Fragment implements AsyncHttpListener,  AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView listView;
    String baseURL,eventId,eventName,userId;
    List<String> usersList,idReference,ticketIdReference;
    SpinnerDialogue spinnerDialogue;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmedPeople.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmedPeople newInstance(String param1, String param2) {
        ConfirmedPeople fragment = new ConfirmedPeople();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ConfirmedPeople() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        baseURL=getString(R.string.baseURL);
        userId=getActivity().getIntent().getStringExtra("userId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_confirmed_people, container, false);
        listView=(ListView)view.findViewById(R.id.registeredUsers);

        eventId=getActivity().getIntent().getExtras().getString("eventId");

        updateList();
        return view;
    }

    void updateList(){
        String url=baseURL+"getConfirmedPeople.php";
        List<NameValuePair> json=new ArrayList<>();
        json.add(new BasicNameValuePair("eventId", eventId));
        new AsyncHttp(url,json,this);
        spinnerDialogue=new SpinnerDialogue(getActivity(),"Loding User List...");
    }

    public void onResponse(String response) {
        spinnerDialogue.cancel();
        if(response==null){
            myToast("Try Again");
            return;
        }
        try {
            JSONObject respJson=new JSONObject(response);
            if(respJson.getInt("errorCode")==0){
                usersList=new ArrayList<>();
                idReference=new ArrayList<>();
                ticketIdReference=new ArrayList<>();
                JSONArray usersJson=respJson.getJSONArray("list");
                for (int i = 0; i < usersJson.length(); i++) {
                    JSONArray userJson=usersJson.getJSONArray(i);
                    usersList.add(userJson.getString(0));
                    idReference.add(userJson.getString(1));
                    ticketIdReference.add(userJson.getString(2));
                }
                ArrayAdapter adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,usersList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(this);
            }else{
                myToast("Server Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void myToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Clicked", position + "");
        Intent userDetailsIntent=new Intent(getActivity(),PeopleDetails.class);
        userDetailsIntent.putExtra("userId",idReference.get(position));
        userDetailsIntent.putExtra("ticketId",ticketIdReference.get(position));
        startActivity(userDetailsIntent);
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

}
