package net.stuple.worseflash;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast; // Added for showing error messages

import androidx.appcompat.app.AppCompatActivity;

import net.stuple.worseflash.databinding.ActivityMainBinding;

// We no longer need these if we are not using the navigation component
// import androidx.navigation.NavController;
// import androidx.navigation.Navigation;
// import androidx.navigation.ui.AppBarConfiguration;
// import androidx.navigation.ui.NavigationUI;
// import android.view.Menu;
// import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Get the CameraManager service
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // 2. Find the ID of a camera with a flashlight
        try {
            // Get the list of all camera IDs
            String[] cameraIds = cameraManager.getCameraIdList();
            for (String id : cameraIds) {
                // Assuming the first camera ID is suitable (usually the back camera)
                cameraId = id;
                break;
            }
        } catch (CameraAccessException e) {
            Log.e("CAMERA", "Cannot access camera list", e);
            Toast.makeText(this, "Camera error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Handle error, e.g., disable the button
            binding.button.setEnabled(false);
            return;
        }

        binding.button.setOnClickListener(v -> {
            Log.d("BUTTON", "Toggle flash clicked");
            toggleFlashlight();
        });
    }

    /**
     * Toggles the flashlight ON/OFF using the CameraManager.
     */
    private void toggleFlashlight() {
        if (cameraId == null) {
            Log.e("CAMERA", "Camera ID is null, cannot toggle flash.");
            Toast.makeText(this, "Error: Camera not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // The correct method is cameraManager.setTorchMode(cameraId, enabled)
            isFlashlightOn = !isFlashlightOn;
            cameraManager.setTorchMode(cameraId, isFlashlightOn);

            // Update button text or show a message
            String state = isFlashlightOn ? "ON" : "OFF";
            Log.d("CAMERA", "Flashlight turned " + state);
            binding.button.setText("");

        } catch (CameraAccessException e) {
            Log.e("CAMERA", "Error toggling flash", e);
            Toast.makeText(this, "Cannot toggle flash: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // A cleanup method is recommended when the app is paused or destroyed
    @Override
    protected void onStop() {
        super.onStop();
        // Turn off the flashlight when the app is no longer in the foreground
        if (isFlashlightOn) {
            try {
                cameraManager.setTorchMode(cameraId, false);
                isFlashlightOn = false;
                // Optional: Update UI if needed
            } catch (CameraAccessException e) {
                Log.e("CAMERA", "Error turning off flash in onStop", e);
            }
        }
    }
}