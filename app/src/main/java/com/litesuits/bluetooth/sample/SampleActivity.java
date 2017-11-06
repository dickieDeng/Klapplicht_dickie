package com.litesuits.bluetooth.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.bluetooth.LiteBleGattCallback;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.conn.BleCharactCallback;
import com.litesuits.bluetooth.conn.BleDescriptorCallback;
import com.litesuits.bluetooth.conn.BleRssiCallback;
import com.litesuits.bluetooth.conn.LiteBleConnector;
import com.litesuits.bluetooth.exception.BleException;
import com.litesuits.bluetooth.exception.hanlder.DefaultBleExceptionHandler;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import com.litesuits.bluetooth.scan.PeriodMacScanCallback;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.litesuits.bluetooth.utils.BluetoothUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.litesuits.bluetooth.sample.BleRxDataStruct.*;



public class SampleActivity extends Activity {

    private static final String TAG = SampleActivity.class.getSimpleName();

    /**
     * mac和服务uuid纯属测试，测试时请替换真实参数。
     */
    public String UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    public String UUID_CHAR_WRITE = "0000fff3-0000-1000-8000-00805f9b34fb";
    public String UUID_CHAR_READ = "0000fff4-0000-1000-8000-00805f9b34fb";

    public String UUID_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";
    public String UUID_DESCRIPTOR_WRITE = "00002902-0000-1000-8000-00805f9b34fb";
    public String UUID_DESCRIPTOR_READ = "00002902-0000-1000-8000-00805f9b34fb";

    private static String MAC = "E0:C7:9D:61:56:9E";

    private static int TIME_OUT_SCAN = 10000;
    private static int TIME_OUT_OPERATION = 5000;
    private Activity activity;
    /**
     * 蓝牙主要操作对象，建议单例。
     */
    private static LiteBluetooth liteBluetooth = null;
    /**
     * 默认异常处理器
     */
    private DefaultBleExceptionHandler bleExceptionHandler;
    /**
     * mac和服务uuid纯属测试，测试时请替换真实参数。
     */

    private final int BLE_CMD_PACKAGE_LEN = 30;
    private byte[] ble_rtn_info = new byte[BLE_CMD_PACKAGE_LEN];
    // Custom define View in layout file
//    private TextView textView = null;
    private Button button = null;
    private Button search = null;
    private Button connect = null;
    private Button notify_chara = null;
    private Button btn_motor_up = null;
    private Button btn_motor_down = null;
    private Button btn_motor_left = null;
    private Button btn_motor_right = null;
    private Button btn_exit = null;
    private Button btn_audio = null;
    private RadioGroup audio_option = null;
    private DiscreteSeekBar seekBar = null;

    //bluetooth device define
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mdevice = null;
    LiteBleConnector ble_connector = null;
    //Temporary use View define
    private Toast mToast;
    private ProgressDialog waitingDialog = null;

    /* light temperature define,1st data: temperature, 2nd data: AD value */
    private final int[][] ledTemperature = {
            {10,2632},{11,2595},{12,2556},{13,2517},{14,2478},
            {15,2438},{16,2399},{17,2359},{18,2314},{19,2281},
            {20,2241},{21,2203},{22,2163},{23,2123},{24,2085},
            {25,2048},{26,2008},{27,1969},{28,1932},{29,1894},
            {30,1856},{31,1819},{32,1783},{33,1747},{34,1711},
            {35,1675},{36,1640},{37,1607},{38,1572},{39,1539},
            {40,1506},{41,1473},{42,1441},{43,1410},{44,1377},
            {45,1346},{46,1316},{47,1288},{48,1258},{49,1228},
            {50,1202},{51,1174},{52,1146},{53,1120},{54,1094},
            {55,1067},{56,1042},{57,1017},{58,994},{59,970},
            {60,947},{61,924},{62,902},{63,882},{64,860},
            {65,839},{66,821},{67,800},{68,781},{69,762},
            {70,743},{71,727},{72,709},{73,693},{74,676},
            {75,659},{76,644},{77,630},{78,615},{79,600},
            {80,585},{81,570},{82,558},{83,546},{84,529},
            {85,517},{86,505},{87,496},{88,482},{89,470},
            {90,460},{91,451},{92,438},{93,428},{94,418},
            {95,408},{96,398},{97,392},{98,382},{99,372},
            {100,364}
    };

    @Override
    protected void onDestroy() {
        liteBluetooth = null;
        mdevice = null;
        closeBluetoothGatt();
        super.onDestroy();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        activity = this;
        if (liteBluetooth == null) {
            liteBluetooth = new LiteBluetooth(activity);
        }
        liteBluetooth.enableBluetoothIfDisabled(activity, 1);
        bleExceptionHandler = new DefaultBleExceptionHandler(this);
        ble_connector = null;
        Intent intent = getIntent();
        mdevice = (BluetoothDevice)intent.getParcelableExtra("mDevice");
        ConnectLeDevice();

        /*if (connector == null){
            connector = liteBluetooth.newBleConnector();
        }*/

       /* textView = (TextView) findViewById(R.id.textView);
        //check mobile phone support ble function
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    textView.setText("ble_not_supported");
                }
                final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                // 2.检查设备上是否支持并开启蓝牙
                if (mBluetoothAdapter == null) {
                    textView.setText("ble_not_supported1");
                    return;
                }

            }
        });*/
        //Search ble device
        /*search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //liteBluetooth = new LiteBluetooth(getBaseContext());
                liteBluetooth.startLeScan(new PeriodScanCallback(TIME_OUT_SCAN) {
                    @Override
                    public void onScanTimeout() {
                        dialogShow(TIME_OUT_SCAN + " Millis Scan Timeout! ");
                    }

                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        mdevice = device;
                        if ((device.getName() != null) && device.getName().equals("LE1201")) {
                            Log.i("dickie", "device: " + device.getName() + "  mac: " + device.getAddress()
                                    + "  rssi: " + rssi + "  scanRecord: " + Arrays.toString(scanRecord));
                            Log.i("dickie","UUIDS : " + Arrays.toString(device.getUuids()));
                            textView.setText(device.getName() + ",MAC:" + device.getAddress() + ",RSSI:" + rssi);
                        }
                    }

                });
            }
        });*/
        //ready connect to device
        /*connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectLeDevice();
                *//*liteBluetooth.connect(mdevice, false, new LiteBleGattCallback() {
                    @Override
                    public void onConnectSuccess(BluetoothGatt bluetoothGatt, int i) {
                        bluetoothGatt.discoverServices();
                        Log.i("dickie","connect success");
                        sendHandlerMsgReady(HANDLER_MSG_ID_UNDEF_MSG,"connect success");
                        //连接成功后，还需要发现服务成功后才能进行相关操作
                    }
                    @Override
                    public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
                        BluetoothUtil.printServices(bluetoothGatt);//把服务打印出来
                        //服务发现成功后，我们就可以进行数据相关的操作了，比如写入数据、开启notify等等
                        Log.i("dickie","service find success");
                        sendHandlerMsgReady(HANDLER_MSG_ID_UNDEF_MSG,"service find success");
                    }
                    @Override
                    public void onConnectFailure(BleException e) {
                        bleExceptionHandler.handleException(e);
                    }
                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        //开启notify之后，我们就可以在这里接收数据了。
                        //处理数据也是需要注意的，在我们项目中需要进行类似分包的操作，感兴趣的我以后分享
                        Log.i("dickie", "onCharacteristicChanged: "+ characteristic.getValue());
                        super.onCharacteristicChanged(gatt, characteristic);
                    }
                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        //当我们对ble设备写入相关数据成功后，这里也会被调用
                        Log.i("dickie", "onCharacteristicWrite: "+ characteristic.getValue());
                        super.onCharacteristicWrite(gatt, characteristic, status);
                    }
                });*//*

            }
        });*/
        //seekbar for Light lux adjust
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                BleWriteCommand2Device(new byte[]{BLE_CMD_START_BYTE, TFMI_LED_CONTROL_REQ, 1, (byte) seekBar.getProgress(), 5});
                /*LiteBleConnector connector = liteBluetooth.newBleConnector();
                connector.withUUIDString(UUID_SERVICE, UUID_CHAR_WRITE, null);
                byte lux_value = (byte) seekBar.getProgress();
                connector.writeCharacteristic(new byte[]{(byte)0xAA, 7, 1, lux_value, 5}, new BleCharactCallback() {
                    @Override
                    public void onSuccess(BluetoothGattCharacteristic characteristic) {
                        Log.i("dickie", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                    }
                    @Override
                    public void onFailure(BleException exception) {
                        Log.i("dickie", "Write failure: " + exception);
                        bleExceptionHandler.handleException(exception);
                    }
                });*/
            }
        });

        //switch
        audio_option = (RadioGroup) findViewById(R.id.radio_group);
        audio_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                byte path;
                if (checkedId == R.id.option_micphone){
                    Log.i("dickie","micphone");
                    path = 0;
                }
                else{
                    Log.i("dickie","headset");
                    path = 1;
                }
                BleWriteCommand2Device(new byte[]{BLE_CMD_START_BYTE, TFMI_SIGMA_AUDIO_IN_CH_SELECT_REQ, 1, path, 5});
            }
        });
        // step motor up-direction control
        btn_motor_up = (Button) findViewById(R.id.btn_motor_up);
        btn_motor_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    BleWriteCommand2Device(STEP_MOTOR_RUN_COMMAND_UP);
                    Log.i("dickie","STEP motor run turn Up");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP){
                    BleWriteCommand2Device(STEP_MOTOR_RUN_COMMAND_STOP);
                    Log.i("dickie","STEP motor stop run");
                }
                return false;
            }
        });
        // step motor down-direction control
        btn_motor_down = (Button) findViewById(R.id.btn_motor_down);
        btn_motor_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    BleWriteCommand2Device(STEP_MOTOR_RUN_COMMAND_DOWN);
                    Log.i("dickie","STEP motor run turn Down");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP){
                    BleWriteCommand2Device(STEP_MOTOR_RUN_COMMAND_STOP);
                    Log.i("dickie","STEP motor stop run");
                }
                return false;
            }
        });
        // dc motor left-direction control
        btn_motor_left = (Button) findViewById(R.id.btn_motor_left);
        btn_motor_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    BleWriteCommand2Device(DC_MOTOR_RUN_COMMAND_LEFT);
                    Log.i("dickie","DC motor run turn Left");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP){
                    BleWriteCommand2Device(DC_MOTOR_RUN_COMMAND_STOP);
                    Log.i("dickie","DC motor stop run");
                }
                return false;
            }
        });
        // dc motor right-direction control
        btn_motor_right = (Button) findViewById(R.id.btn_motor_right);
        btn_motor_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    BleWriteCommand2Device(DC_MOTOR_RUN_COMMAND_RIGHT);
                    Log.i("dickie","DC motor run turn Right");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP){
                    BleWriteCommand2Device(DC_MOTOR_RUN_COMMAND_STOP);
                    Log.i("dickie","STEP motor stop run");
                }
                return false;
            }
        });
        // exit button
        btn_exit = (Button) findViewById(R.id.button_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Audio processing button
        btn_audio = (Button) findViewById(R.id.btn_audio);
        btn_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioProcessDialog();
            }
        });
        //write desc
       /* write_desc = (Button) findViewById(R.id.write_desc);
        write_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleWriteCommand2Device(new byte[]{(byte)0xAA, 7, 1, 100, 5});
            }
        });*/
        //read desc
        /*read_desc = (Button) findViewById(R.id.read_desc);
        read_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiteBleConnector connector = liteBluetooth.newBleConnector();
                connector.withUUIDString(UUID_SERVICE, UUID_CHAR_READ, UUID_DESCRIPTOR_READ)
                        .readDescriptor(new BleDescriptorCallback() {
                            @Override
                            public void onSuccess(BluetoothGattDescriptor descriptor) {
                                Log.i("dickie", "Read Success, DATA: " + Arrays.toString(descriptor.getValue()));
                            }

                            @Override
                            public void onFailure(BleException exception) {
                                Log.i("dickie", "Read failure : " + exception);
                                bleExceptionHandler.handleException(exception);
                            }
                        });
            }
        });*/

        //enable charat notify
        /*notify_chara = (Button) findViewById(R.id.notify_chara);
        notify_chara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableNotificationOfCharacteristic();

                *//*LiteBleConnector connector = liteBluetooth.newBleConnector();
                connector.withUUIDString(UUID_SERVICE, UUID_CHAR_READ, null)
                        .enableCharacteristicNotification(new BleCharactCallback() {
                            @Override
                            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                Log.i("dickie", "Notification characteristic Success, DATA: " + Arrays
                                        .toString(characteristic.getValue()));
                                 dealWithBleResponse(characteristic.getValue(),characteristic.getValue().length);
                            }

                            @Override
                            public void onFailure(BleException exception) {
                                Log.i("dickie", "Notification characteristic failure: " + exception);
                                bleExceptionHandler.handleException(exception);
                            }
                        });*//*
            }
        });*/
        //enable descriptor notify
        /*notify_desc = (Button) findViewById(R.id.notify_desc);
        notify_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiteBleConnector connector = liteBluetooth.newBleConnector();
                connector.withUUIDString(UUID_SERVICE, UUID_CHAR_READ, UUID_DESCRIPTOR_READ)
                        .enableDescriptorNotification(new BleDescriptorCallback() {
                            @Override
                            public void onSuccess(BluetoothGattDescriptor descriptor) {
                                Log.i("dickie",
                                        "Notification descriptor Success, DATA: " + Arrays.toString(descriptor.getValue()));
                            }

                            @Override
                            public void onFailure(BleException exception) {
                                Log.i("dickie", "Notification descriptor failure : " + exception);
                                bleExceptionHandler.handleException(exception);
                            }
                        });
            }
        });*/
    }

    /**
     * scan devices for a while
     */
    private void scanDevicesPeriod() {
        liteBluetooth.startLeScan(new PeriodScanCallback(TIME_OUT_SCAN) {
            @Override
            public void onScanTimeout() {
                dialogShow(TIME_OUT_SCAN + " Millis Scan Timeout! ");
            }

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.i("dickie", "device: " + device.getName() + "  mac: " + device.getAddress()
                              + "  rssi: " + rssi + "  scanRecord: " + Arrays.toString(scanRecord));
            }
        });
    }

    /**
     * scan a specified device for a while
     */
    private void scanSpecifiedDevicePeriod() {
        liteBluetooth.startLeScan(new PeriodMacScanCallback(MAC, TIME_OUT_SCAN) {

            @Override
            public void onScanTimeout() {
                dialogShow(TIME_OUT_SCAN + " Millis Scan Timeout!  Device Not Found! ");
            }

            @Override
            public void onDeviceFound(BluetoothDevice device, int rssi, byte[] scanRecord) {
                dialogShow(" Device Found " + device.getName() + " MAC: " + device.getAddress()
                           + " \n RSSI: " + rssi + " records:" + Arrays.toString(scanRecord));
            }
        });
    }

    /**
     * scan and connect to device
     */
    private void scanAndConnect() {
        liteBluetooth.scanAndConnect(MAC, false, new LiteBleGattCallback() {

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                // discover services !
                gatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                BluetoothUtil.printServices(gatt);
                dialogShow(MAC + " Services Discovered SUCCESS !");
            }

            @Override
            public void onConnectFailure(BleException exception) {
                bleExceptionHandler.handleException(exception);
                dialogShow(MAC + " Services Discovered FAILURE !");
            }
        });
    }

    /**
     * scan first, then connect
     */
    private void scanThenConnect() {
        liteBluetooth.startLeScan(new PeriodMacScanCallback(MAC, TIME_OUT_SCAN) {

            @Override
            public void onScanTimeout() {
                dialogShow(TIME_OUT_SCAN + "毫秒扫描结束，未发现设备");
            }

            @Override
            public void onDeviceFound(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                Toast.makeText(activity, "发现 " + device.getAddress() + " 正在连接...", Toast.LENGTH_LONG).show();
                liteBluetooth.connect(device, false, new LiteBleGattCallback() {

                    @Override
                    public void onConnectSuccess(BluetoothGatt gatt, int status) {
                        gatt.discoverServices();
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        BluetoothUtil.printServices(gatt);
                    }

                    @Override
                    public void onConnectFailure(BleException exception) {
                        bleExceptionHandler.handleException(exception);
                        dialogShow(device.getAddress() + " 设备连接失败");
                    }
                });

            }
        });
    }

    /**
     * get state
     */
    private void getBluetoothState() {
        Log.i("dickie", "liteBluetooth.getConnectionState: " + liteBluetooth.getConnectionState());
        Log.i("dickie", "liteBluetooth isInScanning: " + liteBluetooth.isInScanning());
        Log.i("dickie", "liteBluetooth isConnected: " + liteBluetooth.isConnected());
        Log.i("dickie", "liteBluetooth isServiceDiscoered: " + liteBluetooth.isServiceDiscoered());
        if (liteBluetooth.getConnectionState() >= LiteBluetooth.STATE_CONNECTING) {
            Log.i("dickie", "lite bluetooth is in connecting or connected");
        }
        if (liteBluetooth.getConnectionState() == LiteBluetooth.STATE_SERVICES_DISCOVERED) {
            Log.i("dickie", "lite bluetooth is in connected, services have been found");
        }
    }

    /**
     * add(remove) new callback to an existing connection.
     * One Device, One {@link LiteBluetooth}.
     * But one device( {@link LiteBluetooth}) can add many callback {@link BluetoothGattCallback}
     *
     * {@link LiteBleGattCallback} is a extension of {@link BluetoothGattCallback}
     */
    private void addNewCallbackToOneConnection() {
        BluetoothGattCallback liteCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {}

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic, int status) {
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {}
        };

        if (liteBluetooth.isConnectingOrConnected()) {
            liteBluetooth.addGattCallback(liteCallback);
            liteBluetooth.removeGattCallback(liteCallback);
        }
    }

    /**
     * refresh bluetooth device cache
     */
    private void refreshDeviceCache() {
        liteBluetooth.refreshDeviceCache();
    }


    /**
     * close connection
     */
    private void closeBluetoothGatt() {
        if (liteBluetooth.isConnectingOrConnected()) {
            liteBluetooth.closeBluetoothGatt();
        }
    }

    /**
     * write data to characteristic
     */
    private void writeDataToCharacteristic() {
        /*LiteBleConnector connector = liteBluetooth.newBleConnector();
        connector.withUUIDString(UUID_SERVICE, UUID_CHAR_WRITE, null)
                 .writeCharacteristic(new byte[]{(byte)170, 0x05, 0x00, 0x04}, new BleCharactCallback() {
                     @Override
                     public void onSuccess(BluetoothGattCharacteristic characteristic) {
                         Log.i("dickie", "Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
                     }

                     @Override
                     public void onFailure(BleException exception) {
                         Log.i("dickie", "Write failure: " + exception);
                         bleExceptionHandler.handleException(exception);
                     }
                 });*/
    }

    /**
     * write data to descriptor
     */
    private void writeDataToDescriptor() {
        LiteBleConnector connector = liteBluetooth.newBleConnector();
        connector.withUUIDString(UUID_SERVICE, UUID_CHAR_WRITE, UUID_DESCRIPTOR_WRITE)
                 .writeDescriptor(new byte[]{1, 2, 3}, new BleDescriptorCallback() {
                     @Override
                     public void onSuccess(BluetoothGattDescriptor descriptor) {
                         Log.i("dickie", "Write Success, DATA: " + Arrays.toString(descriptor.getValue()));
                     }

                     @Override
                     public void onFailure(BleException exception) {
                         Log.i("dickie", "Write failure: " + exception);
                         bleExceptionHandler.handleException(exception);
                     }
                 });
    }

    /**
     * read data from characteristic
     */
    private void readDataFromCharacteristic() {
        LiteBleConnector connector = liteBluetooth.newBleConnector();
        connector.withUUIDString(UUID_SERVICE, UUID_CHAR_READ, null)
                 .readCharacteristic(new BleCharactCallback() {
                     @Override
                     public void onSuccess(BluetoothGattCharacteristic characteristic) {
                         Log.i("dickie", "Read Success, DATA: " + Arrays.toString(characteristic.getValue()));
                     }

                     @Override
                     public void onFailure(BleException exception) {
                         Log.i("dickie", "Read failure: " + exception);
                         bleExceptionHandler.handleException(exception);
                     }
                 });
    }

    /**
     * read data from descriptor
     */
    private void readDataFromDescriptor() {
        LiteBleConnector connector = liteBluetooth.newBleConnector();
        connector.withUUIDString(UUID_SERVICE, UUID_CHAR_READ, UUID_DESCRIPTOR_READ)
                 .readDescriptor(new BleDescriptorCallback() {
                     @Override
                     public void onSuccess(BluetoothGattDescriptor descriptor) {
                         Log.i("dickie", "Read Success, DATA: " + Arrays.toString(descriptor.getValue()));
                     }

                     @Override
                     public void onFailure(BleException exception) {
                         Log.i("dickie", "Read failure : " + exception);
                         bleExceptionHandler.handleException(exception);
                     }
                 });
    }

    /**
     * enble notification of characteristic
     */
    private void enableNotificationOfCharacteristic() {
        LiteBleConnector connector = liteBluetooth.newBleConnector();
        connector.withUUIDString(UUID_SERVICE, UUID_CHAR_READ, null)
                 .enableCharacteristicNotification(new BleCharactCallback() {
                     @Override
                     public void onSuccess(BluetoothGattCharacteristic characteristic) {
                         Log.i("dickie", "Notification characteristic Success, DATA: " + Arrays.toString(characteristic.getValue()));
                         dealWithBleResponse(characteristic.getValue(),characteristic.getValue().length);
                     }

                     @Override
                     public void onFailure(BleException exception) {
                         Log.i("dickie", "Notification characteristic failure: " + exception);
                         bleExceptionHandler.handleException(exception);
                     }
                 });
    }

    /**
     * enable notification of descriptor
     */
    private void enableNotificationOfDescriptor() {
        LiteBleConnector connector = liteBluetooth.newBleConnector();
        connector.withUUIDString(UUID_SERVICE, UUID_CHAR_READ, UUID_DESCRIPTOR_READ)
                 .enableDescriptorNotification(new BleDescriptorCallback() {
                     @Override
                     public void onSuccess(BluetoothGattDescriptor descriptor) {
                         Log.i("dickie","Notification descriptor Success, DATA: " + Arrays.toString(descriptor.getValue()));
                     }

                     @Override
                     public void onFailure(BleException exception) {
                         Log.i("dickie", "Notification descriptor failure : " + exception);
                         bleExceptionHandler.handleException(exception);
                     }
                 });
    }


    /**
     * read RSSI of device
     */
    public void readRssiOfDevice() {
        liteBluetooth.newBleConnector().readRemoteRssi(new BleRssiCallback() {
            @Override
            public void onSuccess(int rssi) {
                Log.i("dickie", "Read Success, rssi: " + rssi);
            }

            @Override
            public void onFailure(BleException exception) {
                Log.i("dickie", "Read failure : " + exception);
                bleExceptionHandler.handleException(exception);
            }
        });
    }

    public void dialogShow(String msg) {
        return;
        /*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Lite BLE");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", null);
        builder.show();*/
    }

    private void showProductInfoDialog(String title,String msg)
    {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(SampleActivity.this);
        //    normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle(title);
        normalDialog.setMessage(msg);

        AlertDialog.Builder sure = normalDialog.setPositiveButton("sure", new DialogInterface.OnClickListener() {
        @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //   normalDialog.show();
        AlertDialog dialog = normalDialog.show();
    }

    private void sendHandlerMsgReady(int msgId,String msg){
        Message tempMsg = mHandler.obtainMessage();
        tempMsg.what = msgId;
        tempMsg.obj = msg;
        mHandler.sendMessage(tempMsg);
    }

    private void dealWithBleResponse(byte[] data,int length){
        // store data[] to ble_rtn_info[]
        System.arraycopy(data,0,ble_rtn_info,0,length);
        /*for (int i = 0; i < length; i++) {
            Log.i("dickie","data" + i +":" + data[i]);
        }*/
        byte cmdHeader = data[0];
        byte cmdCode = data[1];
        byte cmdLength = data[2];

        if (cmdHeader == BLE_CMD_START_BYTE) {
            String tempString = "";
            for (int i = 0; i < cmdLength; i++) {
                tempString = tempString + (char)data[3 + i];
            }
            Message tempMsg = mHandler.obtainMessage();
            tempMsg.what = HANDLER_MSG_ID_TF_START;
            switch (cmdCode) {
                case TFMI_MOTOR_DIRECTION_REQ:
                    if ((data[3] == MOTOR_0_DEGREE_LOCATION) && (data[4] == MOTOR_0_DEGREE_LOCATION)){
                        tempString = "0-degree maximum angle reached!";
                        tempMsg.what = HANDLER_MSG_ID_MOTOR_LOCATION;
                    }
                    else if ((data[3] == MOTOR_90_DEGREE_LOCATION) && (data[4] == MOTOR_90_DEGREE_LOCATION)){
                        tempString = "90-degree maximum angle reached!";
                        tempMsg.what = HANDLER_MSG_ID_MOTOR_LOCATION;
                    }
                    else if ((data[3] == MOTOR_ERROR_LOCATION) && (data[4] == MOTOR_ERROR_LOCATION)){
                        tempString = "motor location error!";
                        tempMsg.what = HANDLER_MSG_ID_MOTOR_LOCATION;
                    }
                    break;
                case TFMI_MOTOR_RUN_MODE_REQ:
                    break;
                case TFMI_LED_CONTROL_REQ:
                    break;
                case TFMI_READ_TEMP_CURR_AD_REQ:
                    /*
                    * data[3]=STEP_H; data[4]=STEP_L;
                    * data[5]=DC_H;   data[6]=DC_L;
                    * data[7]=LED_H;  data[8]=LED_L;
                    */
                    if (cmdLength == 0x06){
                        int tempAD;
                        /* Calc STEP motor sensor voltage */
                        tempAD = data[3] * 256 + data[4];
                        float voltage = (tempAD * 3300) / 4096;
                        tempString = "Step motor's voltage: " + String.valueOf(voltage) + "mV\n";
                        /* Calc DC motor sensor voltage */
                        tempAD = data[5] * 256 + data[6];
                        voltage = (tempAD * 3300) / 4096;
                        tempString = tempString + "DC motor's voltage: " + String.valueOf(voltage) + "mV\n";
                        /* Calc light sensor temperature */
                        int i = 0;
                        tempAD = data[7] * 256 + data[8];
                        for (i = 0; i < ledTemperature.length; i++) {
                            if (tempAD >= ledTemperature[i][1]){
                                if(i == 0){
                                    tempString = tempString + "LED Temperature < " + String.valueOf(ledTemperature[i][0]) + "°C";
                                }else {
                                    tempString = tempString + "LED Temperature: " + String.valueOf(ledTemperature[i][0]) + "°C";
                                }
                                break;
                            }
                        }
                        if (i == ledTemperature.length){
                            tempString = tempString + "LED Temperature > " + String.valueOf(ledTemperature[i-1][0]) + "°C";
                        }
                        tempMsg.what = HANDLER_MSG_ID_MOTOR_LED_AD;
                    }
                    break;
                case TFMI_READ_AD_THRESHOLD_REQ:
                    break;
                case TFMI_LIGHT_WORK_TIME_REQ:
                    tempMsg.what = HANDLER_MSG_ID_WORK_TIME;
                    int tempData = data[3] * 256 + data[4]; //((int)data[3]) << 8 + ((int)data[4]);

                    tempString = String.valueOf(tempData) + "hours ";
                    tempData = (int)data[5];
                    tempString = tempString + String.valueOf(tempData) + "minutes";
                    break;
                case TFMI_READ_MANUFACTURE_DATE_REQ:
                    tempMsg.what = HANDLER_MSG_ID_MADE_DATE;
                    break;
                case TFMI_READ_SERIAL_NUMBER_REQ:
                    tempMsg.what = HANDLER_MSG_ID_SERIAL_NUMBER;
                    break;
                case GET_LOCAL_DEVICE_NAME:
                    tempMsg.what = HANDLER_MSG_ID_DEVICE_NAME;
                    break;
                case TFMI_READ_FIREWARE_VERSION_REQ:
                    tempMsg.what = HANDLER_MSG_ID_FIREWARE_VER;
                    break;
                case TFMI_EEPROM_READ_REQ:
                    break;
                case TFMI_TLV_REG_READ_REQ:
                    break;
                case TFMI_ATE_CHECK_MARK_REQ:
                    break;
                case TFMI_TEST_MODE_ACK_RESP:
                    break;
            }
            if (tempMsg.what != HANDLER_MSG_ID_TF_START){
                tempMsg.obj = tempString;
                mHandler.sendMessage(tempMsg);
            }
        }

    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case HANDLER_MSG_ID_MADE_DATE:
                    showProductInfoDialog("Manufacture date",msg.obj.toString());
                    break;
                case HANDLER_MSG_ID_SERIAL_NUMBER:
                    showProductInfoDialog("Serial number",msg.obj.toString());
                    break;
                case HANDLER_MSG_ID_DEVICE_NAME:
                    showProductInfoDialog("Device name",msg.obj.toString());
                    break;
                case HANDLER_MSG_ID_FIREWARE_VER:
                    showProductInfoDialog("Fireware version",msg.obj.toString());
                    break;
                case HANDLER_MSG_ID_WORK_TIME:
                    showProductInfoDialog("Light work time",msg.obj.toString());
                    break;
                case HANDLER_MSG_ID_MOTOR_LED_AD:
                    showProductInfoDialog("Motor and Light information",msg.obj.toString());
                    break;
                case HANDLER_MSG_ID_MOTOR_LOCATION:
                case HANDLER_MSG_ID_UNDEF_MSG:
                    showProductInfoDialog("System information",msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };
    //write control command to ble device
    private void BleWriteCommand2Device(byte[] byteBuff){
        if (ble_connector == null) {
            ble_connector = liteBluetooth.newBleConnector();
        }
        ble_connector.withUUIDString(UUID_SERVICE, UUID_CHAR_WRITE, null)
                       .writeCharacteristic(byteBuff, new BleCharactCallback() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.i("dickie", "TF Write Success, DATA: " + Arrays.toString(characteristic.getValue()));
            }
            @Override
            public void onFailure(BleException exception) {
                Log.i("dickie", "TF Write failure: " + exception);
                bleExceptionHandler.handleException(exception);
            }
        });
    }
    // ready to connect to ble device
    private void ConnectLeDevice() {
        showWaitingDialog();
        liteBluetooth.connect(mdevice, false, new LiteBleGattCallback() {
            @Override
            public void onConnectSuccess(BluetoothGatt bluetoothGatt, int i) {
                bluetoothGatt.discoverServices();
                Log.i("dickie","connect success");
//                sendHandlerMsgReady(HANDLER_MSG_ID_UNDEF_MSG,"connect success");
                //连接成功后，还需要发现服务成功后才能进行相关操作
            }
            @Override
            public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
                BluetoothUtil.printServices(bluetoothGatt);//把服务打印出来
                //服务发现成功后，我们就可以进行数据相关的操作了，比如写入数据、开启notify等等
                Log.i("dickie","service find success");
//                sendHandlerMsgReady(HANDLER_MSG_ID_UNDEF_MSG,"service find success");
                waitingDialog.dismiss();
            }
            @Override
            public void onConnectFailure(BleException e) {
                bleExceptionHandler.handleException(e);
            }
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                //开启notify之后，我们就可以在这里接收数据了。
                //处理数据也是需要注意的，在我们项目中需要进行类似分包的操作，感兴趣的我以后分享
                Log.i("dickie", "onCharacteristicChanged: "+ Arrays.toString(characteristic.getValue()));
                super.onCharacteristicChanged(gatt, characteristic);
            }
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                //当我们对ble设备写入相关数据成功后，这里也会被调用
//                Log.i("dickie", "onCharacteristicWrite: "+ Arrays.toString(characteristic.getValue()));
                super.onCharacteristicWrite(gatt, characteristic, status);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionitem,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.set_device_name:
                setDeviceNameInfo();
                break;
            case R.id.temperature_monitor:
                BleWriteCommand2Device(new byte[]{BLE_CMD_START_BYTE, TFMI_READ_TEMP_CURR_AD_REQ, 0,4});
                break;
            case R.id.led_work_time:
                BleWriteCommand2Device(new byte[]{BLE_CMD_START_BYTE, TFMI_LIGHT_WORK_TIME_REQ, 0,4});
                break;
            case R.id.made_date:
                BleWriteCommand2Device(new byte[]{BLE_CMD_START_BYTE, TFMI_READ_MANUFACTURE_DATE_REQ, 0, 4});
                break;
            case R.id.serial_number:
                BleWriteCommand2Device(new byte[]{BLE_CMD_START_BYTE, TFMI_READ_SERIAL_NUMBER_REQ, 0, 4});
                break;
            case R.id.fireware_version:
                BleWriteCommand2Device(new byte[]{BLE_CMD_START_BYTE, TFMI_READ_FIREWARE_VERSION_REQ, 0,4});
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* set ble device name information */
    private void setDeviceNameInfo(){
        final Dialog mDialog = new AlertDialog.Builder(this).setView(new EditText(this)).create();
        mDialog.show();
        final Window window = mDialog.getWindow();
        window.setContentView(R.layout.alert_set_device_name);

        final EditText etAlias;
        etAlias = (EditText) window.findViewById(R.id.etAlias);

        LinearLayout llNo,llSure;
        llNo = (LinearLayout) window.findViewById(R.id.llNo);
        llSure = (LinearLayout) window.findViewById(R.id.llSure);

        //display old device name info if it is not empty
        if (!TextUtils.isEmpty(mdevice.getName())) {
            etAlias.setText(mdevice.getName());
        }
        // set listener for Cancel key
        llNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        // set listener for OK key
        llSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etAlias.getText().toString()))
                {
                    myToast("Please input alias for device!");
                    return;
                }
                // Convert string to byte[]
                byte[] nameByte = etAlias.getText().toString().getBytes();
                // init
                ArrayList tempList = new ArrayList();
                tempList.add(BLE_CMD_START_BYTE);
                tempList.add(SET_LOCAL_DEVICE_NAME);
                tempList.add((byte)nameByte.length);
                for (int i = 0; i < nameByte.length; i++) {
                    tempList.add(nameByte[i]);
                }
                tempList.add((byte)(nameByte.length + 4));      // write CRC byte value
                byte[] dataToWrite = new byte[tempList.size()];
                for (int i = 0; i < tempList.size(); i++) {
                    dataToWrite[i] = (byte)tempList.get(i);
                }
                BleWriteCommand2Device(dataToWrite);
                mDialog.dismiss();
            }
        });
    }
    /* convert string with hex format to byte[]
     * for example : "AA07016405" -> AA 07 01 64 05,
     */
    public byte[] parseHexStringToBytes(final String hex) {
//		String tmp = hex.substring(2).replaceAll("[^[0-9][a-f]]", "");
        String tmp = hex.replaceAll("[^[0-9][a-f][A-F]]", "");
        byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally

        String part = "";

        for(int i = 0; i < bytes.length; ++i) {
            part = "0x" + tmp.substring(i*2, i*2+2);
            bytes[i] = Long.decode(part).byteValue();
        }

        return bytes;
    }

    public void myToast(String string) {
        if (mToast != null) {
            mToast.setText(string);
        } else {
            mToast = Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG);
        }
        /* 设置Toast显示在窗口中心位置 */
        mToast.setGravity(Gravity.CENTER,0,0);
        /* 显示Toast */
        mToast.show();
    }
    // display audio processing dialog for dealwith audio signal
    public void showAudioProcessDialog(){
        final AlertDialog.Builder audioProcessDialog = new AlertDialog.Builder(SampleActivity.this);
        final View dialogView = LayoutInflater.from(SampleActivity.this).inflate(R.layout.audio_process,null);
        audioProcessDialog.setTitle("Audio process");
        audioProcessDialog.setView(dialogView);
        audioProcessDialog.setPositiveButton("Exit",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取EditView中的输入内容
//                EditText edit_text = (EditText) dialogView.findViewById(R.id.edit_text);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = audioProcessDialog.show();
        // Enable/Disable left channel
        CheckBox checkBoxLeftChannel = (CheckBox) dialogView.findViewById(R.id.cb_left_channel);
        checkBoxLeftChannel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    Log.i("dickie","Left channel enable");
                    BleWriteCommand2Device(BleRxDataStruct.SIGMA_SIGNAL_LEFT_CH_ENABLE_CMD);
                }
                else{
                    Log.i("dickie","Left channel disable");
                    BleWriteCommand2Device(BleRxDataStruct.SIGMA_SIGNAL_LEFT_CH_DISABLE_CMD);
                }

            }
        });
        // Enable / Disable right channel
        CheckBox checkBoxRightChannel = (CheckBox) dialogView.findViewById(R.id.cb_right_channel);
        checkBoxRightChannel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    Log.i("dickie","Right channel enable");
                    BleWriteCommand2Device(BleRxDataStruct.SIGMA_SIGNAL_RIGHT_CH_ENABLE_CMD);
                }
                else{
                    Log.i("dickie","Right channel enable");
                    BleWriteCommand2Device(BleRxDataStruct.SIGMA_SIGNAL_RIGHT_CH_DISABLE_CMD);
                }
            }
        });
        // Select dsp program mode
        RadioGroup radioGroupDsp = (RadioGroup)dialogView.findViewById(R.id.radio_group_audio);
        radioGroupDsp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.high_end_dsp){
                    Log.i("dickie","high end dsp program");
                    BleWriteCommand2Device(BleRxDataStruct.SIGMA_SIGNAL_DSP_PROG_HIGH_END_CMD);
                }
                else /* if (checkedId == R.id.mainstream_dsp) */{
                    Log.i("dickie","mainstream dsp program");
                    BleWriteCommand2Device(BleRxDataStruct.SIGMA_SIGNAL_DSP_PROG_MAIN_STREAM_CMD);
                }
            }
        });
    }

    // display wait dialog for connecting to ble device
    private void showWaitingDialog() {
    /*
     * @setCancelable:false-> disable screen click event，
     * dismiss dialog after connect success
     */
        waitingDialog = new ProgressDialog(SampleActivity.this);
        waitingDialog.setTitle("Connecting to device");
        waitingDialog.setMessage("please wait...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();

        waitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i("dickie","waiting dialog dismiss!");
                enableNotificationOfCharacteristic();
            }
        });
    }
}
