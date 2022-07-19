package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.surfstop.R;

import java.util.List;

import models.BaseGroup;
import models.BasePost;
import models.BeachGroup;

public class MyGroupAdapter extends GroupAdapter{
    public MyGroupAdapter(Context context, List<BaseGroup> groups) {
        super(context, groups);
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_group_item, parent, false);
        return new GroupAdapter.ViewHolder(view);
    }
}
