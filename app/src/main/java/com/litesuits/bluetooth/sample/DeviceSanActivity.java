package com.litesuits.bluetooth.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.litesuits.bluetooth.LiteBleGattCallback;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.exception.BleException;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.litesuits.bluetooth.utils.BluetoothUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.litesuits.bluetooth.sample.BleRxDataStruct.HANDLER_MSG_ID_UNDEF_MSG;
import static com.litesuits.bluetooth.sample.R.id.textView;

public class DeviceSanActivity extends Activity {
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private boolean mScanning = false;

    /**
     * 蓝牙主要操作对象，建议单例。
     */
    private static LiteBluetooth liteBluetooth = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private Activity activity;
    private SimpleAdapter simpleAdapter;
    private ListView bleDeviceList = null;
    private List<HashMap<String, Object>> listData;
    private ArrayList<BluetoothDevice> mLeDeviceList;
    private BluetoothDevice mDevice = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_san);
        activity = this;

        liteBluetooth = new LiteBluetooth(activity);
        liteBluetooth.enableBluetoothIfDisabled(activity, 1);
        mLeDeviceList = new ArrayList<BluetoothDevice>();

        boolean ble_enable = true;
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ble_enable = false;
            Toast.makeText(DeviceSanActivity.this,"BLE not supported!",Toast.LENGTH_LONG).show();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 2.检查设备上是否支持并开启蓝牙
        if (mBluetoothAdapter == null) {
            ble_enable = false;
            Toast.makeText(DeviceSanActivity.this,"ble_not_supported1",Toast.LENGTH_LONG).show();
            return;
        }
        // Initializes list view adapter
        bleDeviceList = (ListView) findViewById(R.id.ble_listview);
        listData = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(DeviceSanActivity.this,listData,R.layout.listitem_device,
                new String[]{"name","mac"},
                new int[]{R.id.device_name,R.id.device_address});
        bleDeviceList.setAdapter(simpleAdapter);
        bleDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDevice = mLeDeviceList.get(position);
//                Toast.makeText(DeviceSanActivity.this,mDevice.getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeviceSanActivity.this, SampleActivity.class);
                intent.putExtra("mDevice",mDevice);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ble_scan, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
//                mLeDeviceListAdapter.clear();
                bleDeviceList = null;
                scanLeDevice(true);
                StartScanBleDevice();
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
//        return super.onOptionsItemSelected(item);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
//            liteBluetooth.stopScan();
        }
        invalidateOptionsMenu();
    }

    private void StartScanBleDevice(){
        liteBluetooth.startLeScan(new PeriodScanCallback(SCAN_PERIOD) {
            @Override
            public void onScanTimeout() {
                Toast.makeText(DeviceSanActivity.this,SCAN_PERIOD + " Millis Scan Timeout! ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if ((device.getName() != null) && (device.getAddress() != null) && !mLeDeviceList.contains(device)) {
                    mLeDeviceList.add(device);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", device.getName());
                    map.put("mac", device.getAddress());
                    listData.add(map);
                    simpleAdapter.notifyDataSetChanged();
                    /*Message tempMsg = mHandler.obtainMessage();
                    mHandler.sendEmptyMessage(101);*/
                }
                /*if ((device.getName() != null) && device.getName().equals("LE1201")) {
                    Log.i("dickie", "device: " + device.getName() + "  mac: " + device.getAddress()
                            + "  rssi: " + rssi + "  scanRecord: " + Arrays.toString(scanRecord));
                    Log.i("dickie","UUIDS : " + Arrays.toString(device.getUuids()));
                    textView.setText(device.getName() + ",MAC:" + device.getAddress() + ",RSSI:" + rssi);
                }*/
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            if (msg.what == 101){
                simpleAdapter.notifyDataSetChanged();
            }
        }
    };
}
