package com.jurtz.marcel.blog_viciousdino.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jurtz.marcel.blog_viciousdino.Activities.PostActivity;
import com.jurtz.marcel.blog_viciousdino.Objects.Tag;
import com.jurtz.marcel.blog_viciousdino.R;
import com.jurtz.marcel.blog_viciousdino.Settings.URLManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragTags extends Fragment {

    // GUI
    ProgressDialog progressDialog;
    ListView lvTagList;
    Button cmdNextPage;
    Button cmdPrevPage;
    TextView lblTagPageInfo;

    // VARIABLES
    String url;
    int currentBlogPage;

    // to save fetched blog data
    List<Object> list;
    Gson gson;
    List<Tag> tagList;

    String tagTitleTemp[];
    String tagTitle[];
    int tagCount;

    Map<String, Object> mapPosts;

    // to submit post id when selected
    int postID;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.frag_tags, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        currentBlogPage = 1;

        lvTagList = (ListView)getView().findViewById(R.id.lvTagList);
        lblTagPageInfo = (TextView)getView().findViewById(R.id.lblTagPageInfo);

        cmdNextPage = (Button)getView().findViewById(R.id.cmdNextPage);
        cmdPrevPage = (Button)getView().findViewById(R.id.cmdPrevPage);
        cmdNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBlogPage++;
                populateList();
            }
        });
        cmdPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentBlogPage > 1) {
                    currentBlogPage--;
                    populateList();
                }
            }
        });

        populateList();
    }

    private void populateList() {

        url = URLManager.GetPageUrlTags(currentBlogPage);
        tagList = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        // Fetch all tags and push them to listview
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                list = (List) gson.fromJson(s, List.class);
                tagTitleTemp = new String[list.size()];
                tagCount = 0;

                for(int i=0;i<list.size();++i){
                    mapPosts = (Map<String,Object>)list.get(i);

                    //Tag tag = new Tag(((Double)mapPosts.get("id")).intValue(),(String)mapPosts.get("Name"), (String)mapPosts.get("Link"));
                    Tag tag = new Tag(((Double)mapPosts.get("id")).intValue(),(String)mapPosts.get("name"), ((Double)mapPosts.get("count")).intValue());

                        tagList.add(tag);
                        tagTitleTemp[i] = (String)mapPosts.get("name");
                    if(tag.getCount() > 0) {
                        tagCount++;
                    }
                }

                tagTitle = new String[tagCount];
                int iterator = 0;
                for(int i = 0; i < tagList.size(); i++) {
                    if(tagList.get(i).getCount() > 0) {
                        tagTitle[iterator] = tagList.get(i).getTitle();
                        iterator++;
                    }
                }

                lvTagList.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, tagTitle));
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_LONG).show();
            }
        });

        if(currentBlogPage == 1) {
            cmdPrevPage.setEnabled(false);
        } else if (!cmdPrevPage.isEnabled()) {
            cmdPrevPage.setEnabled(true);
        }

        lblTagPageInfo.setText(getContext().getResources().getString(R.string.all_tags) + " - " + getContext().getResources().getString(R.string.page) + currentBlogPage);

        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
        rQueue.add(request);

        // start activity for specific post
        lvTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //mapPosts = (Map<String, Object>) list.get(position);
                //Tag tag = new Tag(((Double) mapPosts.get("id")).intValue(), (String)mapPosts.get("name"));
                Tag tag = new Tag();
                for(int i = 0; i < tagList.size(); i++) {
                    if(tagList.get(i).getTitle() == tagTitle[position]) {
                        tag = tagList.get(i);
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putInt("tagID",tag.getID());
                bundle.putString("tagName", tag.getTitle());

                Fragment fragment = new FragPostsByTags();
                fragment.setArguments(bundle);

                if (fragment != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
    }
}
