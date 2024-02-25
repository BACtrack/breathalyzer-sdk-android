package com.bactrack.bactrack_mobile.android_sdk_demo;

import BreathalyzerSDK.API.BACtrackAPI;
import BreathalyzerSDK.API.BACtrackAPICallbacks;
import BreathalyzerSDK.Constants.BACTrackDeviceType;
import BreathalyzerSDK.Constants.BACtrackUnit;
import BreathalyzerSDK.Constants.Errors;
import BreathalyzerSDK.Exceptions.BluetoothLENotSupportedException;
import BreathalyzerSDK.Exceptions.BluetoothNotEnabledException;
import BreathalyzerSDK.Exceptions.LocationServicesNotEnabledException;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;

public class MainActivity extends Activity implements  BACtrackAPICallbacks {

  private static final byte PERMISSIONS_FOR_SCAN = 100;

  private static String TAG = "MainActivity";

  private TextView breathalyzerStateTextView;
  private TextView connectionTextView;
  private Button serialNumberButton;
  private Button startBlowingButton;
  private Button disconnectButton;
  private Button connectButton;
  private Button getUseCount;
  private Button getBatteryButton;
  private boolean requiresUseCount = false;

  private BACtrackAPI mAPI;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    requestPermissions();
    this.breathalyzerStateTextView = this.findViewById(R.id.breathalyzerStateTextView);
    this.connectionTextView = this.findViewById(R.id.connectionStateTextView);
    this.connectButton = findViewById(R.id.connect_nearest_button_id);
    this.startBlowingButton = findViewById(R.id.start_blow_process_button_id);
    this.getBatteryButton = findViewById(R.id.get_battery_level);
    this.serialNumberButton = findViewById(R.id.get_serial_number_button_id);
    this.disconnectButton = findViewById(R.id.disconnect_button_id);
    this.getUseCount = findViewById(R.id.get_use_count_button_id);

    connectButton.setOnClickListener(v -> {
      connectNearestClicked();
    });

    startBlowingButton.setOnClickListener(v -> {
      startBlowProcessClicked();
    });

    getBatteryButton.setOnClickListener(v -> {
      requestBatteryLevelClicked();
    });

    serialNumberButton.setOnClickListener(v -> {
      getSerialNumberClicked();
    });

    getUseCount.setOnClickListener(v -> {
      getUseCount();
    });

    disconnectButton.setOnClickListener(v -> {
      disconnectClicked();
    });
    startSDK();
  }

  private void startSDK() {
    String apiKey = "Your API KEY goes here";
    try {
      mAPI = new BACtrackAPI(this, this, apiKey);
    } catch (BluetoothLENotSupportedException e) {
      e.printStackTrace();
      this.setStatus(getString(R.string.TEXT_ERR_BLE_NOT_SUPPORTED));
    } catch (BluetoothNotEnabledException e) {
      e.printStackTrace();
      this.setStatus(getString(R.string.TEXT_ERR_BT_NOT_ENABLED));
    } catch (LocationServicesNotEnabledException e) {
      e.printStackTrace();
      this.setStatus(getString(R.string.TEXT_ERR_LOCATIONS_NOT_ENABLED));
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode,
      String permissions[], int[] grantResults) {
      if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        /**
         * Only start scan if permissions granted.
         */
        setStatus("Tap on Connect Breathalyzer");
        startSDK();
      }
  }
  @Override
  public void BACtrackAPIKeyDeclined(String errorMessage) {
    setStatus("API Key Declined");
    connectionTextView.setText("");
    Log.d(TAG, "BACtrackAPIKeyDeclined");
  }

  @Override
  public void BACtrackAPIKeyAuthorized() {
    Log.d(TAG, "BACtrackAPIKeyAuthorized");
  }

  @Override
  public void BACtrackConnected(BACTrackDeviceType bacTrackDeviceType) {
    runOnUiThread(() -> {
      String name = bacTrackDeviceType.getDisplayName();
      connectionTextView.setText("Connected to device:\n" +name);
      setStatus(getString(R.string.TEXT_CONNECTED));
    });
  }

  @Override
  public void BACtrackDidConnect(String s) {
    Log.d("DEBUG_TAG", "BACtrackDidConnect: s" + s);
    setStatus(getString(R.string.TEXT_DISCOVERING_SERVICES));
  }

  @Override
  public void BACtrackDisconnected() {
    setStatus(getString(R.string.TEXT_DISCONNECTED));
    connectionTextView.setText("");
  }
  @Override
  public void BACtrackConnectionTimeout() {
    Log.d(TAG, "BACtrackConnectionTimeout");
  }

  @SuppressLint("MissingPermission") @Override
  public void BACtrackFoundBreathalyzer(BACtrackAPI.BACtrackDevice breathalyzer) {
    Log.d(TAG, "Found breathalyzer");
    this.connectionTextView.setText("Device found nearby");
    this.breathalyzerStateTextView.setText("Connecting..");
  }

  @Override
  public void BACtrackCountdown(int currentCountdownCount) {
    setStatus(getString(R.string.TEXT_COUNTDOWN) + " " + currentCountdownCount);
  }

  @Override
  public void BACtrackStart() {
    setStatus(getString((R.string.TEXT_BLOW_NOW)));
  }

  @Override public void BACtrackBlow(final float v) {
    final String text = "Keep blowing " + v;
    breathalyzerStateTextView.setText(text);
  }

  @Override
  public void BACtrackAnalyzing() {
    setStatus(getString(R.string.TEXT_ANALYZING));
  }

  @Override
  public void BACtrackResults(float measuredBac) {
    String text = getString(R.string.TEXT_FINISHED) + " " + measuredBac;
    if(requiresUseCount){
      text = text + "\n\n You can now obtain Use Count";
    }
    setStatus(text);
  }

  @Override
  public void BACtrackFirmwareVersion(String version) {
    setStatus(getString(R.string.TEXT_FIRMWARE_VERSION) + " " + version);
  }

  @Override
  public void BACtrackSerial(String serialHex) {
    setStatus(getString(R.string.TEXT_SERIAL_NUMBER) + " " + serialHex);
  }

  @Override
  public void BACtrackUseCount(int useCount) {
    Log.d(TAG, "UseCount: " + useCount);
    // C6/C8 bug in hardware does not allow getting use count
    if (useCount == 4096) {
      setStatus("Cannot retrieve use count for C6/C8 devices");
    } else if (useCount == -1) {
      requiresUseCount = true;
      setStatus("You must take a test before obtaining use count\n Tap on Start Test Countdown");
    } else {
      setStatus(getString(R.string.TEXT_USE_COUNT) + " " + useCount);
    }
  }

  @Override
  public void BACtrackBatteryVoltage(float voltage) {
    Log.d(TAG, "BACtrackBatteryVoltage: " + voltage);
  }

  @Override
  public void BACtrackBatteryLevel(int level) {
    runOnUiThread(() -> {
      String message = getString(R.string.TEXT_BATTERY_LEVEL) + " " + level;
      Log.d(TAG, "setBatteryStatus: " + message);
      breathalyzerStateTextView.setText(String.format("\n%s", message));
    });
  }

  @Override
  public void BACtrackError(int errorCode) {
    if (errorCode == Errors.ERROR_BLOW_ERROR) {
      setStatus(getString(R.string.TEXT_ERR_BLOW_ERROR));
    }
  }

  @Override public void BACtrackUnits(final BACtrackUnit baCtrackUnit) {
    Log.d(TAG, "BACtrackUnits: " + baCtrackUnit);
  }

  private void requestPermissions() {
    ArrayList<String> list = new ArrayList<>();
    list.add(Manifest.permission.BLUETOOTH_SCAN);
    list.add(Manifest.permission.BLUETOOTH_CONNECT);
    list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
    list.add(Manifest.permission.ACCESS_FINE_LOCATION);
    String[] array = list.toArray(new String[0]);
    ActivityCompat.requestPermissions(MainActivity.this, array, PERMISSIONS_FOR_SCAN);
  }

  public void connectNearestClicked() {
    if (mAPI != null) {
      setStatus(getString(R.string.TEXT_CONNECTING));
      mAPI.connectToNearestBreathalyzer();
    }
  }

  public void disconnectClicked() {
    if (mAPI != null) {
      mAPI.disconnect();
    }
  }

  public void getSerialNumberClicked() {
    if (mAPI != null) {
      mAPI.getSerialNumber();
    }
  }

  public void getUseCount() {
    if (mAPI != null) {
      boolean count = mAPI.getUseCount();
      Log.d(TAG, "getUseCount: count " + count);
    }
  }

  public void requestBatteryLevelClicked() {
    if (mAPI != null) {
      mAPI.getBreathalyzerBatteryVoltage();
    }
  }

  public void startBlowProcessClicked() {
    if (mAPI != null) {
      mAPI.startCountdown();
    }
  }


  private void setStatus(final String message) {
      Log.d(TAG, "Status: " + message);
      breathalyzerStateTextView.setText(message);
  }

}