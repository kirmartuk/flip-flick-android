package com.martyuk.flipflick.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martyuk.flipflick.BluetoothRepository
import com.martyuk.flipflick.MainActivity
import com.martyuk.flipflick.R
import com.martyuk.flipflick.entities.BluetoothDeviceWithConnectionStatus
import com.martyuk.flipflick.entities.HostDeviceWithSocket
import com.martyuk.flipflick.viewmodels.HostViewModel

class HostRecyclerViewAdapter(
    private val mAllHostDevices: ArrayList<HostDeviceWithSocket>,
    private val mSlaveDevice: BluetoothDevice? = null
) :
    RecyclerView.Adapter<HostRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_host_device, parent, false)) {
        private var mHostDeviceNameTextView: TextView? = null
        private var mHostDeviceStatusImageView: ImageView? = null


        init {
            mHostDeviceNameTextView = itemView.findViewById(R.id.tv_host_device_name)
            mHostDeviceStatusImageView = itemView.findViewById(R.id.iv_host_device_status)
        }

        fun bind(hostDeviceWithSocket: HostDeviceWithSocket) {
            mHostDeviceNameTextView?.let {
                it.text = hostDeviceWithSocket.bluetoothDevice.name
            }
            mHostDeviceStatusImageView?.visibility =
                if (hostDeviceWithSocket.bluetoothSocket != null
                    && hostDeviceWithSocket.bluetoothSocket!!.isConnected
                )
                    View.VISIBLE
                else View.GONE

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDevice = mAllHostDevices[position]
        holder.bind(currentDevice)
        when (holder.itemView.context.javaClass) {
            MainActivity::class.java ->
                println("//TODO")
            else ->
                holder.itemView.setOnClickListener {
                    currentDevice.bluetoothSocket?.let { it1 ->
                        BluetoothRepository(holder.itemView.context).reconnectBluetoothDevice(
                            mSlaveDevice!!,
                            it1
                        )
                    }
                }
        }
    }

    override fun getItemCount(): Int = mAllHostDevices.size


}