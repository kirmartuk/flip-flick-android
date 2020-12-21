package com.martyuk.flipflick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.martyuk.flipflick.adapters.HostRecyclerViewAdapter
import com.martyuk.flipflick.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothRepository = BluetoothRepository(application)

        mainViewModel.slaveDevices.observe(this, { allDevices ->
            mainViewModel.connectedDevices.observe(this, { connectedDevices ->
                rv_slave_devices.apply {
                    layoutManager = LinearLayoutManager(context)
                    (layoutManager as LinearLayoutManager).orientation =
                        LinearLayoutManager.HORIZONTAL
                    adapter = HostRecyclerViewAdapter(
                        bluetoothRepository.mergeAllAndConnectedDevices(
                            allDevices,
                            connectedDevices
                        )
                    )
                }
            })
        })
        mainViewModel.loadConnectedDevices()
    }
}