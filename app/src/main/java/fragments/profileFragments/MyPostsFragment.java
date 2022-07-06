package fragments.profileFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.surfstop.R;

import java.util.List;

import adapters.PostAdapter;
import models.BasePost;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { }
}
