package com.example.surfstop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import models.BasePost;
import models.ShortPost;
import utils.PostImage;
import utils.TimeUtils;

public class ShortPostDetailActivity extends AppCompatActivity {

    ShortPost post;

    TextView tvName;
    TextView tvUser;
    TextView tvBody;
    TextView tvTime;
    TextView tvTag;
    TextView tvSurfHeight;
    TextView tvUserTime;
    ImageView ivProfileImage;
    ImageView ivMedia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_post_detail);

        tvName = findViewById(R.id.tvName);
        tvUser = findViewById(R.id.tvUser);
        tvBody = findViewById(R.id.tvBody);
        tvTime = findViewById(R.id.tvTime);
        tvTag = findViewById(R.id.tvTag);
        tvSurfHeight = findViewById(R.id.tvSurfHeight);
        tvUserTime = findViewById(R.id.tvUserTime);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivMedia = findViewById(R.id.ivMedia);

        post = Parcels.unwrap(getIntent().getParcelableExtra(BasePost.class.getSimpleName()));

        String postCreatedAt;
        postCreatedAt = getIntent().getExtras().getString("TIMESTAMP");

        ParseUser user = post.getKeyUser();
        String userCreatedAt = TimeUtils.calculateTimeAgo(user.getCreatedAt());

        // Bind the post data to the view elements
        tvName.setText(user.getUsername());
        tvUser.setText(user.getUsername());
        tvBody.setText(post.getKeyContent());
        tvTime.setText(postCreatedAt);
        tvTag.setText(post.getKeyTag());
        tvSurfHeight.setText(post.getKeySurfHeight());
        tvUserTime.setText(userCreatedAt);
        ParseFile profilePhoto = post.getKeyUser().getParseFile("profilePhoto");
        if (profilePhoto != null) {
            PostImage.loadPfpIntoView(this, profilePhoto.getUrl(), ivProfileImage);
        }
        String imageUrl = getIntent().getExtras().getString("IMAGE_URL");
        if (imageUrl != null) {
            PostImage.loadImageIntoView(this, imageUrl, ivMedia);
        } else {
            ivMedia.setVisibility(View.GONE);
        }
    }
}