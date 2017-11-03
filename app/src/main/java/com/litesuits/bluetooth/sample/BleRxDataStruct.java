package com.litesuits.bluetooth.sample;

/**
 * Created by szzh-software on 2017/10/26.
 */

public class BleRxDataStruct {
    public static final int HANDLER_MSG_ID_TF_START = 100;
    public static final int HANDLER_MSG_ID_UNDEF_MSG = 101;
    public static final int HANDLER_MSG_ID_MADE_DATE = HANDLER_MSG_ID_UNDEF_MSG + 1;
    public static final int HANDLER_MSG_ID_SERIAL_NUMBER = HANDLER_MSG_ID_UNDEF_MSG + 2;
    public static final int HANDLER_MSG_ID_DEVICE_NAME = HANDLER_MSG_ID_UNDEF_MSG + 3;
    public static final int HANDLER_MSG_ID_FIREWARE_VER = HANDLER_MSG_ID_UNDEF_MSG + 4;
    public static final int HANDLER_MSG_ID_WORK_TIME = HANDLER_MSG_ID_UNDEF_MSG + 5;
    public static final int HANDLER_MSG_ID_MOTOR_LOCATION = HANDLER_MSG_ID_UNDEF_MSG + 6;


    public static final int BLE_RX_BUF_DATA_LEN = 200;
    public static final byte BLE_CMD_START_BYTE = (byte) 170;   // AA

    public static final byte TFMI_MOTOR_DIRECTION_REQ = 0x01;
    public static final byte TFMI_MOTOR_RUN_MODE_REQ = 0x02;
    public static final byte TFMI_STEP_MOTOR_SPEED_REQ = 0x03;
    public static final byte TFMI_MOTOR_ON_OFF_REQ = 0x04;
    public static final byte TFMI_LIGHT_WORK_TIME_REQ = 0x05;
    public static final byte TFMI_EQU_WORKING_STYLE_REQ = 0x06;
    public static final byte TFMI_LED_CONTROL_REQ = 0x07;
    public static final byte TFMI_TLV_REG_READ_REQ = 0x08;
    public static final byte TFMI_TLV_REG_WRITE_REQ = 0x09;
    public static final byte TFMI_TLV_SOFTWARE_RESET_REQ = 0x0A;
    public static final byte TFMI_TLV_AUDIO_INOUT_MODE_REQ = 0x0B;

    public static final byte TFMI_READ_TEMP_CURR_AD_REQ = 0x0C;
    public static final byte TFMI_READ_AD_THRESHOLD_REQ = 0x0D;
    public static final byte TFMI_WRITE_AD_THRESHOLD_REQ = 0x0E;

    public static final byte TFMI_READ_MANUFACTURE_DATE_REQ = 0x0F;
    public static final byte TFMI_WRITE_MANUFACTURE_DATE_REQ = 0x10;		// For factory test mode

    public static final byte TFMI_READ_SERIAL_NUMBER_REQ = 0x11;
    public static final byte TFMI_WRITE_SERIAL_NUMBER_REQ = 0x12;			// for factory test mode

    public static final byte TFMI_READ_FIREWARE_VERSION_REQ = 0x13;

    public static final byte TFMI_EEPROM_READ_REQ = 0x14;
    public static final byte TFMI_EEPROM_WRITE_REQ = 0x15;

    public static final byte TFMI_SIGMA_SIGNAL_HANDLE_OPT_REQ = 0x16;
    public static final byte TFMI_SIGMA_AUDIO_IN_CH_SELECT_REQ =0x17;	/* audio input channel select:micphone/headset */
    public static final byte TFMI_SIGMA_VOLUME_ADJUST_REQ = 0x18;
    public static final byte TFMI_SIGMA_CARRY_FREQ_REQ = 0x19;

    public static final byte TFMI_ATE_CHECK_MARK_REQ = 0x20;

    public static final byte TFMI_TEST_MODE_ACK_RESP = (byte) 0x80;

  /* ADC only fopublic static final byte r LE1204 modual */
    public static final byte SERIAL_ACK_ADC_IO9_DATA = (byte) 0xA1;               // Acknowledgement of getting ADC data on I/O 9
    public static final byte SERIAL_ACK_ADC_IO10_DATA = (byte) 0xA2;               // Acknowledgement of getting ADC data on I/O 10
    public static final byte SERIAL_ACK_ADC_IO14_DATA = (byte) 0xA3;               // Acknowledgement of getting ADC data on I/O 14

    public static final byte SERIAL_ACK_ADC_IO9_INTERVAL = (byte) 0xB1;           // Acknowledgement of setting measuring	interval of ADC on I/O 9
    public static final byte SERIAL_ACK_ADC_IO10_INTERVAL = (byte) 0xB2;          // Acknowledgement of setting measuring	interval of ADC on I/O 10
    public static final byte SERIAL_ACK_ADC_IO14_INTERVAL = (byte) 0xB3;          // Acknowledgement of setting measuring	interval of ADC on I/O 14

    public static final byte SET_LOCAL_DEVICE_NAME = (byte) 0xC1;                 // Set the local device name of the LE1201/LE1204 module
    public static final byte SET_ADV_DATA = (byte) 0xC2;                          // Set the advertising data
    public static final byte SEND_SERIAL_DATA = (byte) 0xC3;                      // Send serial data while connected
    public static final byte GET_LOCAL_DEVICE_NAME = (byte) 0xC4;                 // Read the local device name set
    public static final byte GET_ADV_DATA = (byte) 0xC5;                         // Read the advertising data set
    public static final byte SET_ADVERTISING = (byte) 0xC6;                       // Set the advertising state
    public static final byte GET_ADV_STATE = (byte) 0xC7;                        // Read the advertising state
    public static final byte GET_CONN_STATE = (byte) 0xC8;                        // Get connection state,only for LE1204
    public static final byte SEND_FILE = (byte) 0xCA;                           // Send the large size file
    public static final byte SET_ADV_INTERV = (byte) 0xCB;                        // Set the advertising interval
    public static final byte SET_CONN_INTERV = (byte) 0xCC;                       // Set the connection interval,only for LE1204
    public static final byte SET_SLAVE_LATENCY = (byte) 0xCD;                     // Set the slave latency,only for LE1204
    public static final byte SET_CONN_TIMEOUT = (byte) 0xCE;                      // Set the Connection timeout,only for LE1204

    public static final byte SERIAL_ACK_SET_LOCAL_DEVICE_NAME = (byte) 0xE1;      // Acknowledgement of local name set
    public static final byte SERIAL_ACK_SET_ADV_DATA = (byte) 0xE2;               // Acknowledgement of advertising data set
    public static final byte SERIAL_ACK_SEND_SERIAL_DATA = (byte) 0xE3;           // Acknowledgement of serial data sent
    public static final byte SERIAL_ACK_GET_LOCAL_DEVICE_NAME = (byte) 0xE4;      // Return the value of local device name
    public static final byte SERIAL_ACK_GET_ADV_DATA = (byte) 0xE5;              // Return the value of advertising data
    public static final byte SERIAL_ACK_SET_ADVERTISING = (byte) 0xE6;            // Acknowledgement of advertising state set
    public static final byte SERIAL_ACK_GET_ADV_STATE = (byte) 0xE7;              // Return the value of advertising state
    public static final byte SERIAL_ACK_CONNECTION = (byte) 0xE8;                 // Return the state of connection
    public static final byte SERIAL_ACK_GET_DATA = (byte) 0xE9;                   // Acknowledgement of getting data
    public static final byte SERIAL_ACK_SEND_FILE = (byte) 0xEA;                  // Acknowledgement of sending large size file
    public static final byte SERIAL_ACK_SET_ADV_INTERV = (byte) 0xEB;             // Acknowledgement of setting advertising interval
    public static final byte SERIAL_ACK_SET_CONN_INTERV = (byte) 0xEC;            // Acknowledgement of setting connection interval
    public static final byte SERIAL_ACK_SET_SLAVE_LATENCY = (byte) 0xED;          // Acknowledgement of setting slave latency
    public static final byte SERIAL_ACK_SET_CONN_TIMEOUT = (byte) 0xEE;           // Acknowledgement of setting connectiong timeout

    public static final byte SERIAL_ACK_RECEIVED_ERROR = (byte) 0xFF;             /* Error return */

    // Function state enable/disable define
    public static final byte FUNCTION_ENABLE = 1;
    public static final byte FUNCTION_DISABLE = 0;
    // MOTOR type define
    public static final byte MOTOR_TYPE_DC = 0;
    public static final byte MOTOR_TYPE_STEP = 1;

    // DC MOTOR run direction define
    public static final byte MOTOR_DIRECTION_LEFT = 1;
    public static final byte MOTOR_DIRECTION_RIGHT = 0;
    // STEP MOTOR run direction define
    public static final byte MOTOR_DIRECTION_0_DEGREE = 0;
    public static final byte MOTOR_DIRECTION_90_DEGREE = 1;

    //STEP motor location define
    public static final byte MOTOR_0_DEGREE_LOCATION = 0x50;
    public static final byte MOTOR_90_DEGREE_LOCATION = 0x51;
    public static final byte MOTOR_MIDDLE_LOCATION = 0x52;
    public static final byte MOTOR_ERROR_LOCATION = 0x53;

    /* DC Motor control command define */
    // 1.control DC motor run turn left direction
    public static final byte[] DC_MOTOR_RUN_COMMAND_LEFT = {BLE_CMD_START_BYTE,TFMI_MOTOR_DIRECTION_REQ,2,MOTOR_TYPE_DC,MOTOR_DIRECTION_LEFT,6};
    // 2.control DC motor run turn right direction
    public static final byte[] DC_MOTOR_RUN_COMMAND_RIGHT = {BLE_CMD_START_BYTE,TFMI_MOTOR_DIRECTION_REQ,2,MOTOR_TYPE_DC,MOTOR_DIRECTION_RIGHT,6};
    // 3.control DC motor stop run
    public static final byte[] DC_MOTOR_RUN_COMMAND_STOP = {BLE_CMD_START_BYTE,TFMI_MOTOR_ON_OFF_REQ,2,MOTOR_TYPE_DC,FUNCTION_DISABLE,6};

    /* STEP Motor control command define */
    // 1.control STEP motor run turn up direction
    public static final byte[] STEP_MOTOR_RUN_COMMAND_UP = {BLE_CMD_START_BYTE,TFMI_MOTOR_DIRECTION_REQ,2,MOTOR_TYPE_STEP,MOTOR_DIRECTION_90_DEGREE,6};
    // 2.control STEP motor run turn down direction
    public static final byte[] STEP_MOTOR_RUN_COMMAND_DOWN = {BLE_CMD_START_BYTE,TFMI_MOTOR_DIRECTION_REQ,2,MOTOR_TYPE_STEP,MOTOR_DIRECTION_0_DEGREE,6};
    // 3.control STEP motor stop run
    public static final byte[] STEP_MOTOR_RUN_COMMAND_STOP = {BLE_CMD_START_BYTE,TFMI_MOTOR_ON_OFF_REQ,2,MOTOR_TYPE_STEP,FUNCTION_DISABLE,6};


}
