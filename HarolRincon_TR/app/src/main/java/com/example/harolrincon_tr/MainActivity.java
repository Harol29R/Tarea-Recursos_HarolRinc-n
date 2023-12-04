package com.example.harolrincon_tr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Activity activity;
    // Version
    private TextView verndroid;

    private int versdk;
    // Bateria
    private ProgressBar pbLevelBaterry;
    private TextView tvLevelBaterry;
    IntentFilter batteryFilter;
    // Conexion
    private TextView tvConexion;
    ConnectivityManager conexion;
    // Linterna
    CameraManager cameraManager;
    String cameraId;
    // Archivo
    private EditText nameFile;
    // private ClFile clFile;
    private Button onFlash;
    private Button offFlash;
    private Button btnFile;
    private Button onBotton;
    private Button offBotton;
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        this.activity = this;
        setContentView(R.layout.activity_main);
        ObjInit();
        onFlash.setOnClickListener(this::Onligth);
        offFlash.setOnClickListener(this::OffLigth);
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broReceiber, batteryFilter);
        onBotton.setOnClickListener(this::enableBluetooth);
        offBotton.setOnClickListener(this::disableBluetooth);
        btnFile.setOnClickListener(this::saveFile);
        checkBluetoothPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broReceiber);
    }

    // Bateria
    BroadcastReceiver broReceiber = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBaterry.setProgress(levelBattery);
            tvLevelBaterry.setText("Level Battery: " + levelBattery + "%");
        }
    };

    // Conexion
    private void chekConnection() {
        try {
            conexion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conexion != null) {
                NetworkInfo networkInfo = conexion.getActiveNetworkInfo();
                boolean stateNet = networkInfo != null && networkInfo.isConnectedOrConnecting();
                if (stateNet) tvConexion.setText("State On");
                else tvConexion.setText("Off");
            } else {
                tvConexion.setText("No info");
            }
        } catch (Exception e) {
            Log.i("Con", e.getMessage());
        }
    }

    // Version android
    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versdk = Build.VERSION.SDK_INT;
        verndroid.setText("Version SO:" + versionSO + "/ SDK:" + versdk);
        chekConnection();
    }

    private void Onligth(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException | RuntimeException e) {
            Log.e("Luz", "Error al encender la linterna", e);
        }
    }

    private void OffLigth(View view) {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException | RuntimeException e) {
            Log.e("Luz", "Error al apagar la linterna", e);
        }
    }

    private void checkAndToggleBluetooth(boolean enable) {
        checkBluetoothPermission();

        if (bluetoothAdapter == null) {
            return;
        }

        if (enable && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        } else if (!enable && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT}, 100);
            return;
        }

        initializeBluetoothAdapter();
    }

    private void initializeBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null) {
            bluetoothAdapter = (Build.VERSION.SDK_INT >= 31) ? bluetoothManager.getAdapter() : BluetoothAdapter.getDefaultAdapter();
        }
    }

    private void enableBluetooth(View view) {
        checkAndToggleBluetooth(true);
    }

    private void disableBluetooth(View view) {
        checkAndToggleBluetooth(false);
    }

    // Linterna
    // Linterna Off
    public void saveFile(View view) {
        String name = nameFile.getText().toString() + ".txt";
        String dateBattery = tvLevelBaterry.getText().toString();
        Clfile clfile = new Clfile(context, this);
        clfile.saveFile(name, dateBattery);
    }

    private void ObjInit() {
        this.verndroid = findViewById(R.id.tvVersionAndroid);
        this.pbLevelBaterry = findViewById(R.id.pbLevelBaterry);
        this.tvLevelBaterry = findViewById(R.id.tvLevelBaterryLB);
        this.tvConexion = findViewById(R.id.tvConexion);
        this.onFlash = findViewById(R.id.btnOn);
        this.offFlash = findViewById(R.id.btnOff2);
        this.nameFile = findViewById(R.id.etNameFile);
        this.btnFile = findViewById(R.id.btnSaveFile);
        this.onBotton = findViewById(R.id.btnBTON);
        this.offBotton = findViewById(R.id.btnBTOFF);
    }
}