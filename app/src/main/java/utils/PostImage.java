package utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class PostImage {
    public static void loadPfpIntoView(Context context, String imageUrl, ImageView ivProfileImage){
        Glide.with(context).load(imageUrl)
                .transform(new CircleCrop())
                .into(ivProfileImage);
    }
    public static void loadImageIntoView(Context context, String imageUrl, ImageView ivMedia) {
        Glide.with(context).load(imageUrl)
                .override(470, 300)
                .centerCrop()
                .transform(new RoundedCorners(30))
                .into(ivMedia);
        ivMedia.setVisibility(View.VISIBLE);
    }
}
