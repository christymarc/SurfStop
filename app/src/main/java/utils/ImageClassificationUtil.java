package utils;

import static com.parse.Parse.getApplicationContext;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.surfstop.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageClassificationUtil {
    public static final int IMG_SIZE = 224;
    public static final int NUM_FLOAT_BYTES = 4;
    public static final int COLOR_CHANNELS = 3;

    public static float classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize
                    (new int[]{1, IMG_SIZE, IMG_SIZE, COLOR_CHANNELS}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect
                    (NUM_FLOAT_BYTES * IMG_SIZE * IMG_SIZE * COLOR_CHANNELS);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[IMG_SIZE * IMG_SIZE];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            // Iterate over each pixel and extract R, G, and B values.
            // Add those values individually to the byte buffer.
            for (int i = 0; i < IMG_SIZE; i++) {
                for (int j = 0; j < IMG_SIZE; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] outputArray = outputFeature0.getFloatArray();
            float output = outputArray[0];

            // Releases model resources if no longer used.
            model.close();

            return output;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
