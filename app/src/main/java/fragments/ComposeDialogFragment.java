package fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.surfstop.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import adapters.SpinnerAdapter;
import models.BasePost;
import models.BeachGroup;
import models.BeachWeather;
import models.ShortPost;
import okhttp3.Headers;

public class ComposeDialogFragment extends DialogFragment{

    public static final String TAG = ComposeDialogFragment.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 12;
    public static final int MAX_POST_LENGTH = 280;
    public static final int TALLEST_WAVE_HEIGHT = 63;

    ComposeDialogListener composeDialogListener;

    EditText etCompose;
    ImageView ivPostImage;
    NumberPicker surfHeightPicker;
    String surfHeight;
    Spinner spinnerTag;
    String tag;
    Button captureButton;
    Button postButton;

    BeachGroup current_beach;
    public static final String CURRENT_BEACH_KEY = "current_beach";

    private File photoFile;
    public String photoFileName = "photo.jpg";

    public ComposeDialogFragment() {
        // Required empty public constructor
    }

    public static ComposeDialogFragment newInstance(BeachGroup current_beach) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_BEACH_KEY, current_beach);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void setListener(ComposeDialogListener listener) {
        this.composeDialogListener = listener;
    }

    /**
     * Defines the compose listener interface with a method passing back data result.
     */
    public interface ComposeDialogListener {
        void onFinishComposeDialog(BasePost post);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.current_beach = (BeachGroup) getArguments().getSerializable(CURRENT_BEACH_KEY);
        // Inflate the layout for this fragment
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
        postButton = view.findViewById(R.id.postButton);

        ivPostImage.setVisibility(View.GONE);
        surfHeightPicker.setMaxValue(63);
        surfHeightPicker.setMinValue(0);

        this.surfHeight = "0";

        surfHeightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                int valuePicker = surfHeightPicker.getValue();
                setSurfHeight(valuePicker);
                Log.d("picker value", valuePicker + "");
            }
        });

        ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tags_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
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

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        // Set click listener on the button
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
                savePost(ParseUser.getCurrentUser(), current_beach, postContent, photoFile, tag, surfHeight);
            }
        });
    }

    private void setSurfHeight(int valuePicker) {
        this.surfHeight = Integer.toString(valuePicker);
    }

    private void setTag(Object itemAtPosition) {
        this.tag = itemAtPosition.toString();
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(),
                "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
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
                // by this point we have the camera photo on disk
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
                ivPostImage.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Snackbar.make(captureButton, R.string.picture_untaken, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void savePost(ParseUser currentUser, BeachGroup current_beach, String content,
                          File photoFile, String tag, String surfHeight) {
        ShortPost post = new ShortPost();
        post.setKeyBeachGroup(current_beach);
        post.setKeyContent(content);
        post.setKeyUser(currentUser);
        if(photoFile != null) {
            post.setKeyImage(new ParseFile(photoFile));
        }
        post.setKeyTag(tag);
        post.setKeySurfHeight(surfHeight);
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

                ComposeDialogListener listener = composeDialogListener; // this breaks the code
                if (listener != null) {
                    listener.onFinishComposeDialog(post);
                }

                dismiss();
            }
        });
    }
}
