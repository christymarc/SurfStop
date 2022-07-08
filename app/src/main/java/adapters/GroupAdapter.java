package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfstop.GroupFeedActivity;
import com.example.surfstop.R;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

import models.BaseGroup;
import models.BeachGroup;
import models.FavoriteGroups;
import models.Group;
import utils.PostImage;
import utils.QueryUtils;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static final String TAG = GroupAdapter.class.getSimpleName();

    private final Context context;
    private List<BaseGroup> groups;

    public GroupAdapter(Context context, List<BaseGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaseGroup group = groups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvGroupName;
        private ImageView ivGroupPhoto;
        private Button favoriteButton;
        private Button favoriteButtonPressed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            ivGroupPhoto = itemView.findViewById(R.id.ivGroupPhoto);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            favoriteButtonPressed = itemView.findViewById(R.id.favoriteButtonPressed);

            itemView.setOnClickListener(this);
        }

        public void bind(BaseGroup group) {
            // Bind the group data to the view elements
            tvGroupName.setText(group.getKeyGroupName());
            ParseFile groupPhoto = group.getKeyImage();
            if (groupPhoto != null) {
                PostImage.loadPfpIntoView(context, groupPhoto.getUrl(), ivGroupPhoto);
            } else {
                ivGroupPhoto.setVisibility(View.GONE);
            }

            // Checks if group is a BeachGroup so we can cast the BaseGroup object to a BeachGroup without error
            if (group.getClass().equals(BeachGroup.class)) {
                BeachGroup beachGroup = (BeachGroup) group;
                // Load favorite beaches into the UI
                QueryUtils.queryBeachesforGroups(beachGroup, favoriteButton, favoriteButtonPressed);

                // BeachGroup favorited
                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFavoriteButtonState();
                        FavoriteGroups.addFavoriteGroup(group);
                    }
                });

                // BeachGroup unfavorited
                favoriteButtonPressed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFavoriteButtonState();
                        FavoriteGroups.deleteFavoriteGroup(group);
                    }
                });
            } else {
                Group otherGroup = (Group) group;
                // Load favorite beaches into the UI
                QueryUtils.queryGroupsforGroups(otherGroup, favoriteButton, favoriteButtonPressed);

                // BeachGroup favorited
                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFavoriteButtonState();
                        FavoriteGroups.addFavoriteGroup(group);
                    }
                });

                // BeachGroup unfavorited
                favoriteButtonPressed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFavoriteButtonState();
                        FavoriteGroups.deleteFavoriteGroup(group);
                    }
                });
            }
        }

        public void changeFavoriteButtonState() {
            if (favoriteButton.getVisibility() == View.VISIBLE) {
                favoriteButton.setVisibility(View.GONE);
                favoriteButtonPressed.setVisibility(View.VISIBLE);
            } else {
                favoriteButton.setVisibility(View.VISIBLE);
                favoriteButtonPressed.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            // Check position is valid (exists in view)
            if (position != RecyclerView.NO_POSITION) {
                BaseGroup group = groups.get(position);

                // Makes groups only clickable if they are not a BeachGroup
                if(group.getClass().equals(Group.class)) {
                    Group currentGroup = (Group) group;
                    // Create intent
                    Intent intent = new Intent(context, GroupFeedActivity.class);
                    // Serialize the post
                    intent.putExtra(Group.class.getSimpleName(), Parcels.wrap(currentGroup));

                    context.startActivity(intent);
                }
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        groups.clear();
        notifyDataSetChanged();
    }
    // Add a list of items -- change to type used
    public void addAll(List<BeachGroup> list) {
        groups.addAll(list);
        notifyDataSetChanged();
    }
}
