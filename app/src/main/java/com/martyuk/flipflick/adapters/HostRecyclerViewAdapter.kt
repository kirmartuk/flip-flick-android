package com.martyuk.flipflick.adapters

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.martyuk.flipflick.BluetoothDeviceActivity
import com.martyuk.flipflick.R
import com.martyuk.flipflick.entities.BluetoothDeviceWithConnectionStatus

class HostRecyclerViewAdapter(
    private val mAllDevices: ArrayList<BluetoothDeviceWithConnectionStatus>,
) :
    RecyclerView.Adapter<HostRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_bounded_device, parent, false)) {
        private var mDeviceNameTextView: TextView? = null
        private var mDeviceStatusImageView: ImageView? = null


        init {
            mDeviceNameTextView = itemView.findViewById(R.id.tv_slave_device_name)
            mDeviceStatusImageView = itemView.findViewById(R.id.iv_slave_device_status)
        }

        fun bind(bluetoothDeviceWithConnectionStatus: BluetoothDeviceWithConnectionStatus) {
            mDeviceNameTextView?.let {
                it.text = bluetoothDeviceWithConnectionStatus.bluetoothDevice.name
            }
            mDeviceStatusImageView?.visibility =
                if (bluetoothDeviceWithConnectionStatus.status) View.VISIBLE else View.GONE

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDevice = mAllDevices[position]
        holder.bind(currentDevice)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, BluetoothDeviceActivity::class.java)
            intent.putExtra("macAddress", currentDevice.bluetoothDevice.address)
            holder.itemView.context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = mAllDevices.size


}