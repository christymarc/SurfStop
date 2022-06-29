package fragments;

import static fragments.TempFeedFragment.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.surfstop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import adapters.GroupAdapter;
import models.BeachGroup;
import utils.QueryUtils;

public class GroupsFragment extends Fragment {

    public static final String TAG = GroupsFragment.class.getSimpleName();

    private RecyclerView rvBeaches;
    protected List<BeachGroup> allBeaches;
    protected GroupAdapter adapter;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        rvBeaches = view.findViewById(R.id.rvBeaches);

        // initialize the array that will hold posts and create a PostsAdapter
        allBeaches = new ArrayList<>();
        adapter = new GroupAdapter(getContext(), allBeaches);

        // set the adapter on the recycler view
        rvBeaches.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvBeaches.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ParseQuery<BeachGroup> query = ParseQuery.getQuery(BeachGroup.class)
                .include(BeachGroup.KEY_GROUP);

        query.findInBackground(new FindCallback<BeachGroup>() {
            @Override
            public void done(List<BeachGroup> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (BeachGroup group : groups) {
                    Log.i(TAG, "Group: " + group);
                }
                allBeaches.addAll(groups);
                adapter.notifyDataSetChanged();
            }
        });
    }
}