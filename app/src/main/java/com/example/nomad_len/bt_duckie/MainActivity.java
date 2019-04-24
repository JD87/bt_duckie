package com.example.nomad_len.bt_duckie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    OutputStream mOutputStream;
    InputStream mInputStream;

    Boolean bool_stopWorker = false;

    Byte delimiter;
    String str_global_data = " ";

    EditText edtxt_test;

    TextView txtvi_hello;

    Switch sw_test;

    //Keyboard mkeyboard;


    private TextWatcher myTextWatch = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtxt_test = (EditText) findViewById(R.id.edtxt_test);
        sw_test = (Switch) findViewById(R.id.switch1);
        txtvi_hello = (TextView) findViewById(R.id.textView2);

       InputMethodManager myimm = ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE));

       myimm.showInputMethodPicker();


        myTextWatch = new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               //Log.d("hello tag change", "text changed ! " + charSequence);
               txtvi_hello.setText(edtxt_test.getText().toString());

           }

           @Override
           public void afterTextChanged(Editable editable) {

           }

       };

        txtvi_hello.addTextChangedListener(myTextWatch);

       // ---------------------------------------------------------------------

        edtxt_test.setOnKeyListener(this);

/*       edtxt_test.setOnKeyListener(new View.OnKeyListener() {
           @Override
           public boolean onKey(View view, int i, KeyEvent keyEvent) {
               if( (keyEvent.getAction() == KeyEvent.ACTION_DOWN)){
                   Log.d("hello tag", "key clicked ! " + i);

                   return true;
               }
               return false;
           }
       });*/
       sw_test.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked)
                   find_BT_device();
               else {
                   try {
                       close_BT_device();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
       });

       txtvi_hello.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               toastMessage("clic");

               try {
                   send_to_BT_device(String.valueOf("D"));
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }
       });

        //startActivityForResult( new Intent((Settings.ACTION_INPUT_METHOD_SETTINGS)),0);


    }


    @Override
    public boolean onKey(View v, int KeyCode, KeyEvent event){

        Log.d("hello tag g ", "key clicked ! " + event.getUnicodeChar());

        return false;
    }

    /*public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext= (EditText)findViewById(resid);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ) showCustomKeyboard(v); else hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType( edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        Log.d("hello tag gg ", "key clicked ! " + keyCode);

        return super.onKeyDown(keyCode,event);

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("hello", String.valueOf((event.getKeyCode())));
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d("hello2", String.valueOf((event.toString())));
        }
        return super.dispatchKeyEvent(event);
    }



    public void onKey(int primaryCode, int[] keyCodes) {

        toastMessage("onKeyDown a" + primaryCode);

        try {
            send_to_BT_device(String.valueOf(primaryCode));
        } catch (IOException e) {
            e.printStackTrace();
        }

        toastMessage("onKeyDown b" + primaryCode);

    }

    void find_BT_device(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {

            // Device doesn't support Bluetooth
            toastMessage("Device doesn't support Bluetooth :(");
            finish();
        }

        if (mBluetoothAdapter.isEnabled()){
            // Bluetooth on
        }
        else {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if( device.getName().equals("HC-05"))
                {
                    mBluetoothDevice = device;
                    toastMessage(device.getName() + " " + device.getAddress() + " found");

                    try {
                        open_BT_device(device);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

    }   //  find_BT_device

    private void open_BT_device(BluetoothDevice device) throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mBluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
        mBluetoothSocket.connect();
        mOutputStream = mBluetoothSocket.getOutputStream();
        mInputStream = mBluetoothSocket.getInputStream();

        listen_to_BT_device();

        toastMessage("Bluetooth Opened");
    }   //  open_BT_device

    void close_BT_device() throws IOException {
        bool_stopWorker = true;
        mOutputStream.close();
        mInputStream.close();
        mBluetoothSocket.close();
        toastMessage("Bluetooth Closed");
    }   //  close_BT_device

    void listen_to_BT_device() throws IOException {
        final byte delimiter_lf = 10;      // LF according to ASCII code, sort of like CR or \n... I think
        final byte delimiter_cr = 13;      // CR according to ASCII code, for raspi
        final Handler mhandler = new Handler();

        bool_stopWorker = false;
        final int[] readBufferPosition = {0};
        final byte [] readBuffer = new byte[1024];

        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                mBluetoothAdapter.cancelDiscovery();

                while(!Thread.currentThread().isInterrupted() && !bool_stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];

                                check_for_eol_pref();

                                if(b == delimiter)       //delimiter
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition[0]];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition[0] = 0;


                                    mhandler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            str_global_data = data;
                                            //show_proprietary_data();
                                            // textview here  .setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    if (b == delimiter_lf || b == delimiter_cr){
                                        //  ¯\_(ツ)_/¯
                                        //toastMessage("wrong delimiter");
                                        readBufferPosition[0] = 0;
                                    }
                                    else
                                        readBuffer[readBufferPosition[0]++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        bool_stopWorker = true;
                    }
                }
            }

        });
        //mInputStream.reset();
        workerThread.start();
    }   // listen_to_BT_device

    void send_to_BT_device(String message_to_send) throws IOException {
        message_to_send= message_to_send + "\n";
        mOutputStream.write(message_to_send.getBytes());
    }   //  send_to_BT_device

    //  -------------------------------------------------------------------------------------------

    public void check_for_eol_pref(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String str_delimiter = pref.getString("eol_pref","LF");

        if(str_delimiter.equals("LF"))
            delimiter = 10;
        else
            delimiter = 13;
    }   //  check_for_eol_pref

    //  -------------------------------------------------------------------------------------------

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }   //  toastMessage


}
