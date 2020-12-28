package com.martyuk.flipflick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.martyuk.flipflick.adapters.BluetoothDeviceRecyclerViewAdapter
import com.martyuk.flipflick.entities.HostDeviceEntity
import com.martyuk.flipflick.viewmodels.HostViewModel
import com.martyuk.flipflick.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_host_device.*

class AddHostDeviceDialogFragment() : BottomSheetDialogFragment() {
    private val mainViewModel: MainViewModel by viewModels()
    private val mHostViewModel: HostViewModel by viewModels()

    companion object {
        fun newInstance(): AddHostDeviceDialogFragment {
            return AddHostDeviceDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_host_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainViewModel.allDevices.observe(this, {
            rv_slave_devices.apply {
                layoutManager = LinearLayoutManager(context)
                (layoutManager as LinearLayoutManager).orientation =
                    LinearLayoutManager.HORIZONTAL
                adapter = BluetoothDeviceRecyclerViewAdapter(
                    requireActivity().supportFragmentManager,
                    it,
                    mHostViewModel
                )
            }
        })

        btn_save_host_device.setOnClickListener {
            val selectedDevices =
                (rv_hosts.adapter as BluetoothDeviceRecyclerViewAdapter).getSelectedDevices()
            mHostViewModel.saveHostDeviceEntities(
                ArrayList(
                    selectedDevices.map { bluetoothDeviceWithConnectionStatus ->
                        HostDeviceEntity(
                            macAddress =
                            bluetoothDeviceWithConnectionStatus.bluetoothDevice.address
                        )
                    })
            )
            dismiss()
        }
    }

}