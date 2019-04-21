package com.somrani.nader.projetprocessmaker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewProcessFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewProcessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewProcessFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "formJsonString";
    private static final String ARG_PARAM2 = "pro_uid";
    private static final String ARG_PARAM3 = "tas_uid";
    private static final String ARG_PARAM4 = "token";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String pro_uid;
    private String tas_uid;
    private String token;
    private JSONArray dynaformObject;
    private JSONObject apiPostFormObject;
    private OnFragmentInteractionListener mListener;

    public NewProcessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NewProcessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewProcessFragment newInstance(String param1, String param2) {
        NewProcessFragment fragment = new NewProcessFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            pro_uid = getArguments().getString(ARG_PARAM2);
            tas_uid = getArguments().getString(ARG_PARAM3);
            token = getArguments().getString(ARG_PARAM4);
            try {
                JSONObject paramObject = new JSONObject(mParam1);
                JSONObject dyn_content = new JSONObject(paramObject.getString("dyn_content"));
                dynaformObject = dyn_content.getJSONArray("items").getJSONObject(0).getJSONArray("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final LinearLayout myLayout = new LinearLayout(getActivity());
        myLayout.setOrientation(LinearLayout.VERTICAL);
        myLayout.setPadding(48, 32, 48, 0);
        apiPostFormObject = new JSONObject();
        for(int i=0; i<dynaformObject.length(); i++){
            try {
                JSONArray item = dynaformObject.getJSONArray(i);
                for(int j=0; j<item.length(); j++){
                    JSONObject formItem = item.getJSONObject(j);
                    try {
                        String type = formItem.getString("type");
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 32, 0,0);
                        switch (type) {
                            case "title":
                                TextView title = new TextView(getActivity());
                                title.setText(formItem.getString("label"));
                                title.setTextAppearance(getActivity(), R.style.TextAppearance_MaterialComponents_Headline5);
                                title.setPadding(0, 32, 0, 32);
                                myLayout.addView(title);
                                break;
                            case "text":
                                TextInputLayout inputLayout = new TextInputLayout(getActivity(), null, R.attr.myInputStyle);
                                inputLayout.setLayoutParams(params);
                                TextInputEditText editText = new TextInputEditText(getActivity());
                                editText.setHint(formItem.getString("label"));
                                editText.setTag(formItem.getString("variable"));
                                apiPostFormObject.put(formItem.getString("variable"), "");
                                inputLayout.addView(editText);
                                myLayout.addView(inputLayout);
                                break;
                            case "submit":
                                params.setMargins(0, 32, 0,0);
                                MaterialButton button = new MaterialButton(getActivity());
                                button.setText(formItem.getString("label"));
                                button.setLayoutParams(params);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Iterator<String> temp = apiPostFormObject.keys();
                                        while (temp.hasNext()) {
                                            String key = temp.next();
                                            try {
                                                TextInputEditText edit = (TextInputEditText) myLayout.findViewWithTag(key);
                                                apiPostFormObject.put(key, edit.getText().toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.addHeader("Authorization", "Bearer "+token);
                                        String api_endpoit = "/cases";
                                        JSONArray variables = new JSONArray();
                                        variables.put(apiPostFormObject);
                                        String body = "{\"pro_uid\":\""+pro_uid+"\",\n" +
                                                        "\"tas_uid\":\""+tas_uid+"\",\n"+
                                                        "\"variables\":"+variables.toString()+"\n"+
                                                        "}";

                                        Log.d("body", body);

                                        try {
                                            client.post(getContext(), getString(R.string.base_url)+api_endpoit, new StringEntity(body), "application/json", new JsonHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                    Toast.makeText(getContext(), "New cases added", Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                                                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                                    System.out.println("failed");
                                                }
                                            });
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                myLayout.addView(button);
                                break;
                        }
                    }catch(JSONException e){
                        System.out.println("No type field");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ScrollView scrollView = new ScrollView(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myLayout.setLayoutParams(layoutParams);
        scrollView.addView(myLayout);
        return scrollView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
