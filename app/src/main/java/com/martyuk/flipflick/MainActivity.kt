package com.martyuk.flipflick

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.martyuk.flipflick.adapters.BluetoothDeviceRecyclerViewAdapter
import com.martyuk.flipflick.adapters.HostRecyclerViewAdapter
import com.martyuk.flipflick.viewmodels.HostViewModel
import com.martyuk.flipflick.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val hostViewModel: HostViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothRepository = BluetoothRepository(applicationContext)
        val filter = IntentFilter()
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            var device: BluetoothDevice? = null
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                val action = intent.action
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                when {
                    BluetoothDevice.ACTION_ACL_CONNECTED == action -> {
                        // use post delay, because method
                        // loadConnectedDevices() can't get all connected devices in time
                        Handler().postDelayed({
                            mainViewModel.loadSlaveDevices()
                            mainViewModel.loadConnectedDevices()
                        }, 3500)
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED == action -> {
                        hostViewModel.updateHostDevice(device!!)
                        mainViewModel.loadSlaveDevices()
                        mainViewModel.loadConnectedDevices()
                    }
                    BluetoothRepository.UPDATE_HOSTS == action -> {
                        hostViewModel.getHostDeviceEntitiesWithStatus()
                    }
                }
            }
        }


        mainViewModel.slaveDevices.observe(this, { allDevices ->
            mainViewModel.connectedDevices.observe(this, { connectedDevices ->
                rv_slave_devices.apply {
                    layoutManager = LinearLayoutManager(context)
                    (layoutManager as LinearLayoutManager).orientation =
                        LinearLayoutManager.HORIZONTAL
                    adapter = BluetoothDeviceRecyclerViewAdapter(
                        supportFragmentManager,
                        bluetoothRepository.mergeAllAndConnectedDevices(
                            allDevices,
                            connectedDevices
                        ),
                        hostViewModel
                    )
                }
            })
        })

        hostViewModel.hostDevices.observe(this, {
            rv_host_devices.apply {
                layoutManager = LinearLayoutManager(context)
                it?.let {
                    adapter =
                        HostRecyclerViewAdapter(it)
                }
            }
        })

        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        filter.addAction(BluetoothRepository.UPDATE_HOSTS)
        registerReceiver(broadcastReceiver, filter)

        startService(Intent(this, HostObservingService::class.java))

        btn_add_host_device.setOnClickListener {
            AddHostDeviceDialogFragment.newInstance().show(supportFragmentManager, "asd")
        }
    }

    /*@Throws(IOException::class)
    fun receiveData(socket: BluetoothSocket) {
        GlobalScope.launch(Dispatchers.Default) {
            hostViewModel.loadConnectedDevices()
            val socketInputStream: InputStream = socket.inputStream
            val buffer = ByteArray(256)
            var bytes: Int

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = socketInputStream.read(buffer) //read bytes from input buffer
                    val readMessage = String(buffer, 0, bytes)
                    // Send the obtained bytes to the UI Activity via handler
                    Log.i("logging", readMessage + "")
                } catch (e: IOException) {
                    break
                }
            }
        }
    }*/

}