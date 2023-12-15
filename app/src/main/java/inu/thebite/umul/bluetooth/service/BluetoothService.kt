package inu.thebite.umul.bluetooth.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.io.IOException
import java.util.*
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import inu.thebite.umul.bluetooth.presentation.BluetoothConnectionCallback
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets


@Suppress("DEPRECATION")
class BluetoothService : Service() {
    private val binder: IBinder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private lateinit var connectedThread: ConnectedBluetoothThread

    private lateinit var pairedDevices: Set<BluetoothDevice>
    private var callback: BluetoothConnectionCallback? = null
    private lateinit var bluetoothHandler: Handler
    val BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    companion object {
        const val BT_MESSAGE_READ = 2
        const val BT_CONNECTING_STATUS = 3
        const val ACTION_DATA_RECEIVED = "com.example.bluetooth.DATA_RECEIVED"
        const val EXTRA_DATA = "data"
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothService {
            return this@BluetoothService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            if(intent.hasCategory("1")){
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == BT_MESSAGE_READ) {  //what =
                    var readMessage: String? = null
                    try {
                        readMessage = String((msg.obj as ByteArray), StandardCharsets.UTF_8)
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    Log.d("handleMessage", readMessage!!)
                    val intent = Intent(ACTION_DATA_RECEIVED)
                    intent.putExtra(EXTRA_DATA, readMessage)
                    sendBroadcast(intent)

                }
            }
        }
    }
    fun setBluetoothConnectionCallback(callback: BluetoothConnectionCallback){
        this.callback = callback
    }

    fun connectToDevice(device: BluetoothDevice) {

        bluetoothDevice = device
        ConnectThread().start()
    }

    fun disconnect() {
        Log.d("disconnect", "disconnect")
        connectedThread?.cancel()
        callback?.disconnecting()
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }






    @SuppressLint("MissingPermission")
    fun connectSelectedDevice(selectedDeviceName: String) {

        pairedDevices = bluetoothAdapter!!.bondedDevices
        for (tempDevice in pairedDevices) {
            if (selectedDeviceName == tempDevice.name) {
                bluetoothDevice = tempDevice
                connectToDevice(bluetoothDevice!!)
                break
            }
        }
    }

    inner class ConnectThread : Thread() {
        override fun run() {
            try {
                try {
                    bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(BT_UUID)

                    bluetoothSocket!!.connect()

                    connectedThread = ConnectedBluetoothThread(bluetoothSocket!!)
                    connectedThread.start()
                    bluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1)
                        .sendToTarget()

                    callback?.connecting()

                } catch (e: IOException) {
                    callback?.disconnecting()
                    Log.e("Error Reason", e.toString())
                }
            }catch (e : SecurityException){

            }

        }
    }

    inner class ConnectedBluetoothThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        // 스레드 생성자
        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {

                Toast.makeText(applicationContext, "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        // run() 메서드
        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int
            while (true) {
                try {
                    bytes = mmInStream!!.available() //현재 읽을 수 있는 바이트 수를 리턴
                    if (bytes != 0) {
                        SystemClock.sleep(100)
                        bytes = mmInStream.available()
                        bytes = mmInStream.read(
                            buffer,
                            0,
                            bytes
                        ) //read(byte[]b, int off, int len) -> len만큼 읽어서 byte[]b의 off위치에 저장하고 읽은 바이트 수를 리턴
                        Log.d("run", "run")
                        bluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget()
                    }
                } catch (e: IOException) {
                    break
                }
            }
        }

        fun write(str: String) {
            val bytes = str.toByteArray()
            try {
                mmOutStream!!.write(bytes)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}