package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfstop.R;
import com.example.surfstop.ShortPostDetailActivity;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import models.BasePost;
import models.ShortPost;
import utils.InternetUtil;
import utils.PostImage;
import utils.TimeUtils;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private List<BasePost> posts;

    public PostAdapter(Context context, List<BasePost> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BasePost post = null;
        if (position < posts.size()) {
            post = posts.get(position);
        }
        assert (post != null && !post.getKeyContent().isEmpty());
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvBody;
        private TextView tvTime;
        private TextView tvName;
        private ImageView ivProfileImage;
        private ImageView ivMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMedia = itemView.findViewById(R.id.ivMedia);

            itemView.setOnClickListener(this);
        }

        public void bind(BasePost post) {

            String createdAt;
            if (InternetUtil.isInternetConnected()) {
                createdAt = TimeUtils.calculateTimeAgo(post.getCreatedAt());
            } else {
                createdAt = TimeUtils.calculateTimeAgo(post.getCreatedAtOffline());
            }

            // Bind the post data to the view elements
            tvBody.setText(post.getKeyContent());
            tvName.setText(post.getKeyUser().getUsername());
            tvTime.setText(createdAt);
            ParseFile profilePhoto = post.getKeyUser().getParseFile("profilePhoto");
            if (profilePhoto != null) {
                PostImage.loadPfpIntoView(context, profilePhoto.getUrl(), ivProfileImage);
            }
            ParseFile image = post.getKeyImage();
            if (image != null) {
                PostImage.loadImageIntoView(context, image.getUrl(), ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            // Check position is valid (exists in view)
            if (position != RecyclerView.NO_POSITION){
                BasePost post = posts.get(position);

                if (post.getClass().equals(ShortPost.class)) {
                    // Create intent
                    Intent intent = new Intent(context, ShortPostDetailActivity.class);
                    // Serialize the post
                    intent.putExtra(BasePost.class.getSimpleName(), Parcels.wrap(post));

                    // Make elements transition
                    Pair<View, String> p0 = Pair.create(view, "border");
                    Pair<View, String> p1 = Pair.create(ivProfileImage, "profile");
                    Pair<View, String> p2 = Pair.create(tvBody, "body");
                    Pair<View, String> p3 = Pair.create(tvTime, "time");
                    Pair<View, String> p4 = Pair.create(tvName, "username");

                    ActivityOptionsCompat options;

                    if (ivMedia.getVisibility() == View.VISIBLE) {
                        Pair<View, String> p5 = Pair.create(ivMedia, "media");
                        options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation((Activity) context, p0, p1, p2, p3, p4, p5);
                    } else {
                        options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation((Activity) context, p0, p1, p2, p3, p4);
                    }

                    context.startActivity(intent, options.toBundle());
                }
            }
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<BasePost> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}