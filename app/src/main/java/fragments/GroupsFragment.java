package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.surfstop.R;

import java.util.ArrayList;
import java.util.List;

import adapters.GroupAdapter;
import models.BaseGroup;
import utils.QueryUtils;

public class GroupsFragment extends Fragment {

    public static final String TAG = GroupsFragment.class.getSimpleName();

    private RecyclerView rvBeaches;
    private RecyclerView rvGroups;
    protected List<BaseGroup> allGroups;
    protected List<BaseGroup> allBeachGroups;
    protected GroupAdapter groupAdapter;
    protected GroupAdapter beachAdapter;

    public GroupsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        rvGroups = view.findViewById(R.id.rvGroups);
        rvBeaches = view.findViewById(R.id.rvBeaches);

        allGroups = new ArrayList<>();
        allBeachGroups = new ArrayList<>();

        groupAdapter = new GroupAdapter(getContext(), allGroups);
        beachAdapter = new GroupAdapter(getContext(), allBeachGroups);

        rvGroups.setAdapter(groupAdapter);
        rvBeaches.setAdapter(beachAdapter);

        rvGroups.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvBeaches.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        QueryUtils.queryGroups(allGroups, groupAdapter);
        QueryUtils.queryBeaches(allBeachGroups, beachAdapter);
    }
}