package fragments;

import static android.app.Activity.RESULT_OK;

import static fragments.ComposeDialogFragment.CAMERA_POPUP;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.surfstop.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import models.BasePost;
import models.Group;
import models.Post;
import utils.CameraUtil;
import utils.DateConverter;
import utils.InternetUtil;
import utils.KeyGeneratorUtil;

public class ComposeDialogGroupFragment extends DialogFragment {
    public static final String TAG = ComposeDialogGroupFragment.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 21;
    public static final int MAX_POST_LENGTH = 280;

    ComposeDialogGroupFragment.ComposeDialogGroupListener composeDialogGroupListener;

    EditText etCompose;
    ImageView ivPostImage;
    Button captureButton;
    Button postButton;

    Group currentGroup;
    public static final String CURRENT_GROUP_KEY = "currentGroup";

    File photoDir;
    File photoFile;

    private ComposeDialogGroupFragment() { }

    public static ComposeDialogGroupFragment newInstance(Group currentGroup) {
        ComposeDialogGroupFragment fragment = new ComposeDialogGroupFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_GROUP_KEY, currentGroup);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void setListener(ComposeDialogGroupFragment.ComposeDialogGroupListener listener) {
        this.composeDialogGroupListener = listener;
    }

    public interface ComposeDialogGroupListener {
        void onFinishComposeDialog(BasePost post);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.currentGroup = (Group) getArguments().getSerializable(CURRENT_GROUP_KEY);
        return inflater.inflate(R.layout.fragment_compose_dialogue_group, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        etCompose = view.findViewById(R.id.etCompose);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        captureButton = view.findViewById(R.id.captureButton);
        postButton = view.findViewById(R.id.postButton);

        ivPostImage.setVisibility(View.GONE);

        // Checking for internet connection to not allow users to post photos in offline mode
        if (InternetUtil.isInternetConnected()) {
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        launchCamera();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            captureButton.setBackgroundColor(getResources().getColor(R.color.medium_gray));
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                    PopupDialogFragment popupDialogFragment = PopupDialogFragment.newInstance(CAMERA_POPUP);
                    popupDialogFragment.show(fm, "camera_fragment");
                }
            });
        }

        // Post user's input
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postContent = etCompose.getText().toString();

                if (postContent.isEmpty()) {
                    Snackbar.make(postButton, R.string.post_empty, Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                if (postContent.length() > MAX_POST_LENGTH) {
                    Snackbar.make(postButton, R.string.post_too_long, Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                savePost(ParseUser.getCurrentUser(), currentGroup, postContent, photoFile);
            }
        });
    }

    private void launchCamera() throws IOException {
        // Create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference for future access
        photoDir = getContext().getCacheDir();
        photoFile = File.createTempFile("image", ".jpg", photoDir);

        // Wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.surfstop.provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Take photo file and rotate to the appropriate orientation
                Bitmap takenImage = CameraUtil.rotateBitmapOrientation(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
                ivPostImage.setVisibility(View.VISIBLE);
            } else {
                Snackbar.make(captureButton, R.string.picture_untaken, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void savePost(ParseUser currentUser, Group currentGroup, String content, File photoFile) {
        Post post = new Post();
        post.setKeyGroup(currentGroup.getKeyGroup());
        post.setKeyContent(content);
        post.setKeyUser(currentUser);
        post.setKeyCreatedAt(DateConverter.toDate(System.currentTimeMillis()));
        if(photoFile != null) {
            post.setKeyImage(new ParseFile(photoFile));
        }

        if (InternetUtil.isInternetConnected()) {
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while trying to save post", e);
                        return;
                    }
                    Log.i(TAG, "Post successfully saved");
                    etCompose.setText("");
                    ivPostImage.setImageResource(0);

                    ComposeDialogGroupFragment.ComposeDialogGroupListener listener = composeDialogGroupListener;
                    if (listener != null) {
                        listener.onFinishComposeDialog(post);
                    }
                }
            });
        } else {
            post.setObjectId(KeyGeneratorUtil.generateRandomKey());
            etCompose.setText("");
            ivPostImage.setImageResource(0);

            ComposeDialogGroupFragment.ComposeDialogGroupListener listener = composeDialogGroupListener;
            if (listener != null) {
                listener.onFinishComposeDialog(post);
            }
        }

        dismiss();
    }
}
