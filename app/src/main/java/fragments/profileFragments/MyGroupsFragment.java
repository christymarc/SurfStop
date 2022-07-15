package fragments.profileFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.surfstop.R;

import java.util.ArrayList;
import java.util.List;

import adapters.GroupAdapter;
import adapters.MyGroupAdapter;
import models.BaseGroup;
import utils.QueryUtils;

public class MyGroupsFragment extends Fragment {
    private RecyclerView rvGroups;
    protected List<BaseGroup> favGroups;
    protected GroupAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    public MyGroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        rvGroups = view.findViewById(R.id.rvFeed);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        // initialize the array that will hold posts and create a PostsAdapter
        favGroups = new ArrayList<>();
        adapter = new MyGroupAdapter(getContext(), favGroups);


        // set the adapter on the recycler view
        rvGroups.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvGroups.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Query beaches and add them to the adapter
        QueryUtils.queryFavorites(favGroups, adapter);

        // query more posts
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                QueryUtils.queryFavorites(favGroups, adapter);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
