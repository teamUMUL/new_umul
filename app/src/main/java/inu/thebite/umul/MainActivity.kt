package inu.thebite.umul

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import inu.thebite.umul.bluetooth.presentation.BluetoothConnectionCallback
import inu.thebite.umul.bluetooth.presentation.BluetoothViewModel
import inu.thebite.umul.bluetooth.presentation.ChartViewModel
import inu.thebite.umul.bluetooth.presentation.components.DeviceScreen
import inu.thebite.umul.bluetooth.service.BluetoothService
import inu.thebite.umul.ui.theme.UmulTheme

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : ComponentActivity(), BluetoothConnectionCallback {
    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val bluetoothViewModel: BluetoothViewModel by viewModels()
    private lateinit var mBluetoothAdapter : BluetoothAdapter   //블루투스 어댑터
    private lateinit var bluetoothService: BluetoothService
    private var bound: Boolean = false
    private var isConnected : Boolean = false
    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true


    //Service에서 UI접근이 힘들기 때문에 연결된 기기 리스트를 보여주는 것은 MainActivity에서 실행
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private lateinit var mListPairedDevices: List<String>

    val BT_REQUEST_ENABLE = 1
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("서비스 연결 시작", "서비스연결시작")

            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            bound = true

            //연결 될 때 isBluetoothConnected 함수를, 연결 끊을 때 isBluetoothDisconnected 함수를 사용하기 위해서 인터페이스 사용
            bluetoothService.setBluetoothConnectionCallback(this@MainActivity)

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        bluetoothPermissionChecker()
        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }


        setContent {

            val intent = Intent(this, BluetoothService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
            UmulTheme {
                val bluetoothViewModel = hiltViewModel<BluetoothViewModel>()

                val isServiceConnected by bluetoothViewModel.isServiceConnected.collectAsState()

                val chartViewModel = hiltViewModel<ChartViewModel>()

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeviceScreen(
                        chartViewModel = chartViewModel,
                        onClick = {
                                 //서비스 연결 시작
                            connectService()
                            setBLE()
                        },
                        isConnected = isServiceConnected
                    )
                }
            }
        }

    }
    fun connectService() {
        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    //블루투스 켜져있는 지 확인, 블루투스 연결X -> 페어링 리스트 보여주기, 블루투스 연결 O -> 연결 끊기
    fun setBLE(){
        if (!::bluetoothService.isInitialized) {
            Toast.makeText(applicationContext, "블루투스 서비스가 초기화되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        bluetoothOn()
        if (isConnected) {
            bluetoothService.disconnect()
        } else {
            listPairedDevices()
        }
    }

    //블루투스 켜기
    @SuppressLint("MissingPermission")
    fun bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show()
        } else {
            if (mBluetoothAdapter.isEnabled) {

            } else {
                Toast.makeText(applicationContext, "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG)
                    .show()
                val intentBluetoothEnable = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE)
            }
        }
    }

    // 블루투스 연결 하라는 알림에서 어떤 버튼을 클릭하느냐에 따른 활동
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BT_REQUEST_ENABLE) {
            if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                Toast.makeText(this, "블루투스 활성화", Toast.LENGTH_LONG).show()
            } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                Toast.makeText(this, "취소", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Service에서 UI건드는 것은 안되기에 list호출은 MainActivity 상에서 진행
    @SuppressLint("MissingPermission")
    fun listPairedDevices() {
        if (mBluetoothAdapter.isEnabled) {
            mPairedDevices = mBluetoothAdapter.bondedDevices
            if ((mPairedDevices as MutableSet<BluetoothDevice>?)!!.size > 0) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("장치 선택")
                mListPairedDevices = ArrayList()
                for (device in (mPairedDevices as MutableSet<BluetoothDevice>?)!!) {
                    (mListPairedDevices as ArrayList<String>).add(device.name)
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                val items = (mListPairedDevices as ArrayList<String>).toTypedArray<CharSequence>()
                (mListPairedDevices as ArrayList<String>).toTypedArray<CharSequence>()
                builder.setItems(
                    items
                ) { dialog: DialogInterface?, item: Int ->
                    val intent = Intent(this, BluetoothService::class.java)
                    startService(intent)
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    //선택 이후로는 Service에서 진행

                    bluetoothService.connectSelectedDevice(items[item].toString())
                }
                val alert = builder.create()
                alert.show()
            } else {
                Toast.makeText(this, "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //연결 될 때 sharedPreference 사용해서 연결 유무 저장 -> HomeFragment에서 사용하기 위해서
    override fun connecting() {
        //BluetoothService에서 연결될 때 사용
        isConnected = true
        val pref: SharedPreferences = getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("isBluetoothConnected",true)
        editor.apply()
        Log.d("Connecting", "Connecting")
        bluetoothViewModel.updateServiceConnection(true)

    }
    override fun disconnecting() {
        //BluetoothService에서 연결 끊을 때 사용
        isConnected = false
        val pref: SharedPreferences = getSharedPreferences("BluetoothConnection", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("isBluetoothConnected",false)
        editor.apply()
        bluetoothViewModel.updateServiceConnection(false)

    }

    //블루투스 권환 확인(Android 12이상인 경우 BLUETOOTH외에도 다른 권한 설정이 필요)
    fun bluetoothPermissionChecker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                BT_REQUEST_ENABLE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.BLUETOOTH
                ),
                BT_REQUEST_ENABLE
            )
        }
    }
}


