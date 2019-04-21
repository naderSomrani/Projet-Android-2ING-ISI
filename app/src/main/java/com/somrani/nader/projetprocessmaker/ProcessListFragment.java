package com.somrani.nader.projetprocessmaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.somrani.nader.projetprocessmaker.dummy.DummyContent;
import com.somrani.nader.projetprocessmaker.dummy.DummyContent.DummyItem;
import com.somrani.nader.projetprocessmaker.interfaces.ProcessItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ProcessListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String access_token;
    private String refresh_token;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProcessListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProcessListFragment newInstance(int columnCount) {
        ProcessListFragment fragment = new ProcessListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        access_token = pref.getString("access_token", "");
        refresh_token = pref.getString("refresh_token", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processlist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            final MyProcessListRecyclerViewAdapter adapter = new MyProcessListRecyclerViewAdapter(new ArrayList<ProcessItem>(), mListener);
            recyclerView.setAdapter(adapter);

            AsyncHttpClient client = new AsyncHttpClient();
            System.out.println(access_token);
            client.addHeader("Authorization", "Bearer "+access_token);
            String api_endpoit = "/case/start-cases";
            client.get(getString(R.string.base_url)+api_endpoit, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // called when response HTTP status is "200 OK"
                    System.out.println(response);
                    List<ProcessItem> process_list = new ArrayList<ProcessItem>();
                    for (int i=0; i<response.length(); i++){
                        try {
                            ProcessItem item = new ProcessItem(response.getJSONObject(i).getString("tas_uid"),
                                    response.getJSONObject(i).getString("pro_title"),
                                    response.getJSONObject(i).getString("pro_uid"));
                            process_list.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.updateItems(process_list);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    System.out.println("login failed");
                    AsyncHttpClient client2 = new AsyncHttpClient();

                    String body = "{\"grant_type\":\"refresh_token\" ,\n" +
                            "\"client_id\":\"SJGZDWXOPLJZLBDQGACCAGAVWSHORHJK\" ,\n" +
                            "\"client_secret\":\"6734914665b5258c7a2eb01077382585\" ,\n" +
                            "\"refresh_token\": \""+refresh_token+"\"";
                    try {
                        client2.post(getContext(), "http://process.isiforge.tn/isi/oauth2/token", new StringEntity(body), "application/json", new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                // called when response HTTP status is "200 OK"
                                System.out.println(response);
                                SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                try {
                                    editor.putString("access_token", response.getString("access_token"));
                                    editor.putString("refresh_token", response.getString("access_token"));
                                    editor.commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("refresh token valid");

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                System.out.println("refresh token invalid");
                                Toast.makeText(getContext(), "Connectez vous une autre fois", Toast.LENGTH_SHORT).show();
                                SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                editor.remove("access_token");
                                editor.remove("refresh_token");
                                editor.commit();

                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                            }

                        });
                    } catch (UnsupportedEncodingException exception) {
                        exception.printStackTrace();
                    }
                }

            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ProcessItem item);
    }
}
