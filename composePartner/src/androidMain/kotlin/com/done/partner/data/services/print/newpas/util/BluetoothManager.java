package com.done.partner.data.services.print.newpas.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    private volatile static BluetoothManager instance; //声明成 volatile
    private final String TAG = BluetoothManager.class.getSimpleName();
    private final UUID IPOSPRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String IPosPrinter_Address = "00:AA:11:BB:22:CC";
//    private final String IPosPrinter_Address = "DC:0D:30:98:28:4A";
    //private final String IPosPrinter_Address = "11:9D:F9:09:44:83";
    //private final String IPosPrinter_Address = "11:9D:F9:A1:A8:33";
    //private final String IPosPrinter_Address = "B7:F2:C4:F7:DF:48";
    private BluetoothDevice IPosPrinter_device;
    private boolean isBluetoothOpen = false;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mBluetoothPrinterDevice = null;
    private BluetoothSocket socket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private BluetoothManager(){}

    public static BluetoothManager getInstance() {
        if (instance == null) {
            synchronized (BluetoothManager.class) {
                if (instance == null) {
                    instance = new BluetoothManager();
                }
            }
        }
        return instance;
    }

    public void setBluetoothAdapter(BluetoothAdapter adapter) {
        mBluetoothAdapter = adapter;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setBluetoothDevice(BluetoothDevice device) {
        mBluetoothPrinterDevice = device;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothPrinterDevice;
    }

    public boolean getBluetoothOpenFlag() {
        return isBluetoothOpen;
    }

    public String getBlueMac() {
        return IPosPrinter_Address;
    }

    public void setBluetoothOpenFlag(boolean flag) {
        isBluetoothOpen = flag;
    }

    public BluetoothDevice getIposPrinterDevice(){
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices){
            if(device.getAddress().equals(IPosPrinter_Address))
            {
                IPosPrinter_device =device;
                //break;
                Log.d(TAG, "connect buletooth name:" +  device.getName() + " addr:" + device.getAddress());
            }
            Log.d(TAG, "buletooth name:" +  device.getName() + " addr:" + device.getAddress());
        }
        return IPosPrinter_device;
    }

    public BluetoothSocket getSocket()
    {
        try {
            if (IPosPrinter_device == null) {
                getIposPrinterDevice();
            }
            if (socket != null && socket.isConnected()) {
                return socket;
            }
            socket = IPosPrinter_device.createRfcommSocketToServiceRecord(IPOSPRINTER_UUID);

        } catch (Exception e) {
            Log.e(TAG, "getSocket Exception:" + e);
        }
        return socket;
    }

    private void setIOStream() {
        if(socket == null || !socket.isConnected())
        {
            socket = getSocket();
            connect();
        }
        try {
            if (outputStream == null) {
                outputStream = socket.getOutputStream();
            }
            if (inputStream == null) {
                inputStream = socket.getInputStream();
            }
        } catch (Exception e) {

        }
    }

    public void connect() {
        try {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            if (socket != null && !socket.isConnected()) {
                Log.e(TAG,"~~~ connect");
                socket.connect();
            }
        } catch (Exception e) {
            Log.e(TAG,"~~~ connect error:"+e);
            try {
                Method m = mBluetoothPrinterDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                socket = (BluetoothSocket) m.invoke(mBluetoothPrinterDevice, 1);
                socket.connect();
            } catch (Exception ec) {
                Log.e("BLUE", e.toString());
                try {
                    socket.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    public void close() {
        try {
            Log.e(TAG,"~~~ close");
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (socket != null && socket.isConnected()) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            Log.e(TAG,"~~~ close error:"+e);
        }
    }

    public void writeData(byte[] writeData) {
        try {
            setIOStream();
            Log.e(TAG,"~~~ writeData1 write:"+ bytesToHexString(writeData));
            outputStream.write(writeData,0, writeData.length);
            outputStream.flush();
        } catch (Exception e) {
            Log.e(TAG,"~~~ writeData1 error:"+e);
        }
    }

    public void writeData(byte[][] writeData) {
        try {
            setIOStream();
            byte[] data = ESCUtil.byteMerger(writeData);
            Log.e(TAG,"~~~ writeData2 write:"+bytesToHexString(data));
            outputStream.write(data,0,data.length);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"~~~ writeData2 error:"+e);
        }
    }

    public byte[] readData() {
        byte[] readData = new byte[1024];
        try {
            int readsize = inputStream.read(readData);
            byte[] data = new byte[readsize];
            System.arraycopy(readData, 0, data, 0, readsize);
            return data;
        } catch (Exception e) {
            Log.e(TAG,"~~~ readData error:"+e);
        }

        return null;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (src.length > 0 && i < src.length - 1) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
