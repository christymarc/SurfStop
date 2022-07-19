package fragments.profileFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.surfstop.R;

import java.util.ArrayList;
import java.util.List;

import adapters.PostAdapter;
import fragments.DescriptionBoxFragment;
import models.BasePost;
import utils.QueryUtils;

public class MyPostsFragment extends Fragment {

    // Feed variables
    private RecyclerView rvPersonalFeed;
    public List<BasePost> allPosts;
    public PostAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvPersonalFeed = view.findViewById(R.id.rvFeed);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setClipToOutline(true);

        // set the adapter on the recycler view
        rvPersonalFeed.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPersonalFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        QueryUtils.queryPersonalPosts(allPosts, adapter);

        // query more posts
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                QueryUtils.queryPersonalPosts(allPosts, adapter);
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
