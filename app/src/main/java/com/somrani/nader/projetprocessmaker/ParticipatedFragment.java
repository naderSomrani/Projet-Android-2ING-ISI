package com.somrani.nader.projetprocessmaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.somrani.nader.projetprocessmaker.interfaces.DraftProcessItem;
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
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class ParticipatedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ParticipatedFragment.OnListFragmentInteractionListener mListener;
    String access_token;
    String refresh_token;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ParticipatedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ParticipatedFragment newInstance(int columnCount) {
        ParticipatedFragment fragment = new ParticipatedFragment();
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
        View view = inflater.inflate(R.layout.fragment_participated_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            final MyParticipatedRecyclerViewAdapter adapter = new MyParticipatedRecyclerViewAdapter(new ArrayList<DraftProcessItem>(), mListener);
            recyclerView.setAdapter(adapter);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    new LinearLayoutManager(context).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            AsyncHttpClient client = new AsyncHttpClient();
            System.out.println(access_token);
            client.addHeader("Authorization", "Bearer "+access_token);
            String api_endpoit = "/cases/participated";
            System.out.println(api_endpoit);
            client.get(getString(R.string.base_url)+api_endpoit, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // called when response HTTP status is "200 OK"
                    //System.out.println(response);
                    List<DraftProcessItem> draft_process_list = new ArrayList<DraftProcessItem>();
                    for (int i=0; i<response.length(); i++){
                        try {
                            DraftProcessItem item = new DraftProcessItem(response.getJSONObject(i).getString("app_uid"),
                                    response.getJSONObject(i).getString("tas_uid"),
                                    response.getJSONObject(i).getString("pro_uid"),
                                    response.getJSONObject(i).getString("app_pro_title"),
                                    response.getJSONObject(i).getString("app_tas_title"),
                                    response.getJSONObject(i).getString("app_create_date"),
                                    response.getJSONObject(i).getString("app_update_date"),
                                    response.getJSONObject(i).getString("usr_firstname"),
                                    response.getJSONObject(i).getString("usr_lastname"),
                                    response.getJSONObject(i).getString("usr_username"),
                                    response.getJSONObject(i).getString("del_task_due_date"));
                            draft_process_list.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(draft_process_list.get(0).app_pro_title);
                    adapter.updateItems(draft_process_list);
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
        if (context instanceof DraftProcessFragment.OnListFragmentInteractionListener) {
            mListener = (ParticipatedFragment.OnListFragmentInteractionListener) context;
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
        void onParticipatedListFragmentInteraction(DraftProcessItem item);
    }
}
