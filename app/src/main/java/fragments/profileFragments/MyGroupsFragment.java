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

    public MyGroupsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        rvGroups = view.findViewById(R.id.rvFeed);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        favGroups = new ArrayList<>();
        adapter = new MyGroupAdapter(getContext(), favGroups);

        rvGroups.setAdapter(adapter);
        rvGroups.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        QueryUtils.queryFavorites(getContext(), favGroups, adapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                QueryUtils.queryFavorites(getContext(), favGroups, adapter);
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
