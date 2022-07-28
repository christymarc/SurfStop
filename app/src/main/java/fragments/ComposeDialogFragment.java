package fragments;

import static android.app.Activity.RESULT_OK;

import static utils.ImageClassificationUtil.IMG_SIZE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;

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
import models.BeachGroup;
import models.ShortPost;
import utils.CameraUtil;
import utils.DateConverter;
import utils.FileUtil;
import utils.ImageClassificationUtil;
import utils.InternetUtil;
import utils.KeyGeneratorUtil;

public class ComposeDialogFragment extends DialogFragment{

    public static final String TAG = ComposeDialogFragment.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    public static final int MAX_POST_LENGTH = 280;
    public static final int TALLEST_WAVE_HEIGHT = 63;
    private static final int DEFAULT_WAVE_HEIGHT = 0;

    ComposeDialogListener composeDialogListener;

    EditText etCompose;
    ImageView ivPostImage;
    NumberPicker surfHeightPicker;
    String surfHeight;
    Spinner spinnerTag;
    String tag;
    Button captureButton;
    Button uploadButton;
    Button postButton;
    CheckBox checkBox;
    Boolean checked = false;
    Bitmap image;

    BeachGroup currentBeach;
    public static final String CURRENT_BEACH_KEY = "currentBeach";

    File photoDir;
    File photoFile;

    private ComposeDialogFragment() { }

    public static ComposeDialogFragment newInstance(BeachGroup currentBeach) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_BEACH_KEY, currentBeach);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void setListener(ComposeDialogListener listener) {
        this.composeDialogListener = listener;
    }

    public interface ComposeDialogListener {
        void onFinishComposeDialog(BasePost post);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.currentBeach = (BeachGroup) getArguments().getSerializable(CURRENT_BEACH_KEY);
        return inflater.inflate(R.layout.fragment_compose_dialogue, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        etCompose = view.findViewById(R.id.etCompose);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        surfHeightPicker = view.findViewById(R.id.surfHeightPicker);
        spinnerTag = view.findViewById(R.id.spinnerTag);
        captureButton = view.findViewById(R.id.captureButton);
        uploadButton = view.findViewById(R.id.uploadButton);
        postButton = view.findViewById(R.id.postButton);
        checkBox = view.findViewById(R.id.checkboxBeach);

        ivPostImage.setVisibility(View.GONE);

        surfHeightPicker.setMaxValue(TALLEST_WAVE_HEIGHT);
        surfHeightPicker.setMinValue(DEFAULT_WAVE_HEIGHT);

        this.surfHeight = Integer.toString(DEFAULT_WAVE_HEIGHT);

        surfHeightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker = surfHeightPicker.getValue();
                setSurfHeight(valuePicker);
            }
        });

        ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tags_array, android.R.layout.simple_spinner_item);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTag.setAdapter(tagAdapter);
        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                setTag(parent.getItemAtPosition(pos));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setTag(parent.getItemAtPosition(0));
            }
        });

        // Checking for internet connection to not allow users to post photos in offline mode
        if (InternetUtil.isInternetConnected()) {
            // Create a File reference for future access
            photoDir = getContext().getCacheDir();
            try {
                photoFile = File.createTempFile("temporary_image", ".jpg", photoDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchCamera();
                }
            });
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchUpload();
                }
            });
        }
        else {
            String popupMessage = getContext().getResources().getString(R.string.camera_popup);

            captureButton.setBackgroundColor(getResources().getColor(R.color.medium_gray));
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                    PopupDialogFragment popupDialogFragment = PopupDialogFragment.newInstance(popupMessage);
                    popupDialogFragment.show(fm, "camera_fragment");
                }
            });

            uploadButton.setBackgroundColor(getResources().getColor(R.color.medium_gray));
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                    PopupDialogFragment popupDialogFragment = PopupDialogFragment.newInstance(popupMessage);
                    popupDialogFragment.show(fm, "upload_fragment");
                }
            });
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                setCheckBox(isChecked);
            }
        });

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
                savePost(ParseUser.getCurrentUser(), currentBeach, postContent, photoFile, tag, surfHeight, checked);
            }
        });
    }

    private void setSurfHeight(int valuePicker) {
        this.surfHeight = Integer.toString(valuePicker);
    }

    private void setTag(Object itemAtPosition) {
        this.tag = itemAtPosition.toString();
    }

    private void setCheckBox(boolean isChecked) {
        this.checked = isChecked;
    }

    private void launchCamera() {
        // Create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.surfstop.provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void launchUpload() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.surfstop.provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Take photo file and rotate to the appropriate orientation
                this.image = CameraUtil.rotateBitmapOrientation(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(this.image);
                ivPostImage.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
            } else {
                Snackbar.make(captureButton, R.string.picture_untaken, Snackbar.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uriData = data.getData();

                this.image = null;
                try {
                    photoFile = FileUtil.from(getContext(), uriData);
                    this.image = CameraUtil.rotateBitmapOrientation(photoFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ivPostImage.setImageBitmap(this.image);
                ivPostImage.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
            } else {
                Snackbar.make(uploadButton, R.string.picture_not_uploaded, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void savePost(ParseUser currentUser, BeachGroup current_beach, String content,
                          File photoFile, String tag, String surfHeight, Boolean checked) {

        ShortPost post = new ShortPost();
        post.setKeyBeachGroup(current_beach);
        post.setKeyGroup(current_beach.getKeyGroup());
        post.setKeyContent(content);
        post.setKeyUser(currentUser);
        post.setKeyCreatedAt(DateConverter.toDate(System.currentTimeMillis()));
        if(photoFile != null) {
            post.setKeyImage(new ParseFile(photoFile));
        }
        post.setKeyTag(tag);
        post.setKeySurfHeight(surfHeight);
        post.setKeyIsImageBeach(checked);

        if (checked) {
            Bitmap scaledImage = Bitmap.createScaledBitmap(this.image, IMG_SIZE, IMG_SIZE, false);
            float beachState = ImageClassificationUtil.classifyImage(scaledImage);
            if (beachState < 0) {
                post.setKeyIsBeachClean(true);
            }
            else {
                post.setKeyIsBeachClean(false);
            }
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

                    ComposeDialogFragment.ComposeDialogListener listener = composeDialogListener;
                    if (listener != null) {
                        listener.onFinishComposeDialog(post);
                    }
                }
            });
        } else {
            post.setObjectId(KeyGeneratorUtil.generateRandomKey());
            etCompose.setText("");
            ivPostImage.setImageResource(0);

            ComposeDialogFragment.ComposeDialogListener listener = composeDialogListener;
            if (listener != null) {
                listener.onFinishComposeDialog(post);
            }
        }

        dismiss();
    }
}
