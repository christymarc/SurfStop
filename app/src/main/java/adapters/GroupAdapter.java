package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfstop.R;
import com.parse.ParseFile;

import java.util.List;

import models.BasePost;
import models.BeachGroup;
import utils.PostImage;
import utils.QueryUtils;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static final String TAG = GroupAdapter.class.getSimpleName();

    private final Context context;
    private List<BeachGroup> beaches;

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
                PostImage.loadPfpIntoView(context, groupPhoto.getUrl(), ivGroupPhoto);
            } else {
                ivGroupPhoto.setVisibility(View.GONE);
            }

            // Load favorite beaches into the UI
            QueryUtils.queryBeachesforGroups(beach, favoriteButton, favoriteButtonPressed);

            // Group favorited
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteButton.setVisibility(View.GONE);
                    favoriteButtonPressed.setVisibility(View.VISIBLE);

                    int pos = getAbsoluteAdapterPosition();
                    BeachGroup beachGroup = beaches.get(pos);

                    BeachGroup.addBeachGroup(beachGroup);
                }
            });

            // group unfavorited
            favoriteButtonPressed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteButton.setVisibility(View.VISIBLE);
                    favoriteButtonPressed.setVisibility(View.GONE);

                    int pos = getAbsoluteAdapterPosition();
                    BeachGroup beachGroup = beaches.get(pos);

                    BeachGroup.deleteBeachGroup(beachGroup);
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        beaches.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<BeachGroup> list) {
        beaches.addAll(list);
        notifyDataSetChanged();
    }
}
