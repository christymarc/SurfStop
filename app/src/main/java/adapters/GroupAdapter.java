package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.surfstop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import models.BasePost;
import models.BeachGroup;
import models.FavoriteGroups;
import utils.QueryUtils;
import utils.TimeUtils;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static final String TAG = GroupAdapter.class.getSimpleName();

    private final Context context;
    private List<BeachGroup> beaches;
    //List<BeachGroup> favoriteBeaches = new ArrayList<>();

    public GroupAdapter(Context context, List<BeachGroup> beaches) {
        this.context = context;
        this.beaches = beaches;
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
        BeachGroup beach = beaches.get(position);
        holder.bind(beach);
    }

    @Override
    public int getItemCount() {
        return beaches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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
        }

        public void bind(BeachGroup beach) {
            // Bind the group data to the view elements
            tvGroupName.setText(beach.getKeyGroupName());
            ParseFile groupPhoto = beach.getKeyImage();
            if (groupPhoto != null) {
                Glide.with(context).load(groupPhoto.getUrl())
                        .override(500, 300)
                        .centerCrop()
                        .transform(new RoundedCorners(30))
                        .into(ivGroupPhoto);
                ivGroupPhoto.setVisibility(View.VISIBLE);
            } else {
                ivGroupPhoto.setVisibility(View.GONE);
            }

            ParseQuery<FavoriteGroups> groupsQuery = ParseQuery.getQuery(FavoriteGroups.class)
                    .include(FavoriteGroups.KEY_USER)
                    .whereEqualTo(FavoriteGroups.KEY_USER, ParseUser.getCurrentUser());
            ParseQuery<BeachGroup> beachQuery = ParseQuery.getQuery(BeachGroup.class)
                    .whereEqualTo(BeachGroup.KEY_GROUP, beach)
                    .whereMatchesKeyInQuery(BeachGroup.KEY_GROUP, FavoriteGroups.KEY_GROUP, groupsQuery);
            beachQuery.findInBackground(new FindCallback<BeachGroup>() {
                @Override
                public void done(List<BeachGroup> groups, ParseException e) {
                    String queryBeachName;
                    String currentBeachName = beach.getKeyGroupName();
                    if (e != null) {
                        Log.e(TAG, "Query groups error", e);
                        return;
                    }
                    for (BeachGroup group : groups) {
                        queryBeachName = group.getKeyGroupName();
                        Log.i(TAG, "Group: " + queryBeachName);
                        if(queryBeachName.equals(currentBeachName)) {
                            favoriteButton.setVisibility(View.GONE);
                            favoriteButtonPressed.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            // Group favorited
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteButton.setVisibility(View.GONE);
                    favoriteButtonPressed.setVisibility(View.VISIBLE);

                    int pos = getAbsoluteAdapterPosition();
                    BeachGroup beachGroup = beaches.get(pos);

                    FavoriteGroups favoritedGroup = new FavoriteGroups();
                    favoritedGroup.setKeyGroup(beachGroup.getKeyGroup());
                    favoritedGroup.setKeyUser(ParseUser.getCurrentUser());
                    favoritedGroup.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while trying to save favorited BeachGroup", e);
                                return;
                            }
                            Log.i(TAG, "Favorited BeachGroup successfully saved");
                        }
                    });
                }
            });

            favoriteButtonPressed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteButton.setVisibility(View.VISIBLE);
                    favoriteButtonPressed.setVisibility(View.GONE);

                    int pos = getAbsoluteAdapterPosition();
                    BeachGroup beachGroup = beaches.get(pos);

                    ParseQuery<FavoriteGroups> query = ParseQuery.getQuery(FavoriteGroups.class)
                            .whereEqualTo(FavoriteGroups.KEY_GROUP, beachGroup.getKeyGroup());
                    query.findInBackground(new FindCallback<FavoriteGroups>() {
                        @Override
                        public void done(List<FavoriteGroups> objects, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while trying to delete favorited BeachGroup", e);
                                return;
                            }
                            for (ParseObject object : objects) {
                                object.deleteInBackground();
                            }
                        }
                    });

                }
            });
        }
    }
}
