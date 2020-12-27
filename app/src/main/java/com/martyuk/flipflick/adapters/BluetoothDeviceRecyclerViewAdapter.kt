package com.martyuk.flipflick.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.martyuk.flipflick.BluetoothDeviceDialogFragment
import com.martyuk.flipflick.MainActivity
import com.martyuk.flipflick.R
import com.martyuk.flipflick.entities.BluetoothDeviceWithConnectionStatus
import com.martyuk.flipflick.viewmodels.HostViewModel
import kotlinx.android.synthetic.main.item_bounded_device.view.*

class BluetoothDeviceRecyclerViewAdapter(
    private val mFragmentManager: FragmentManager,
    private val mAllDevices: ArrayList<BluetoothDeviceWithConnectionStatus>,
    private val mHostViewModel: HostViewModel
) :
    RecyclerView.Adapter<BluetoothDeviceRecyclerViewAdapter.ViewHolder>() {
    private var mSelectedDevices: ArrayList<BluetoothDeviceWithConnectionStatus> = arrayListOf()

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
        when (holder.itemView.context.javaClass) {
            MainActivity::class.java -> {
                holder.itemView.setOnClickListener {
                    BluetoothDeviceDialogFragment
                        .newInstance(currentDevice.bluetoothDevice.address, mHostViewModel)
                        .show(mFragmentManager, "asd")
                }
            }
            else -> {
                // selection of items when connecting hosts
                holder.itemView.apply {
                    setOnClickListener {
                        when (currentDevice in mSelectedDevices) {
                            true -> {
                                mSelectedDevices.remove(currentDevice)
                                iv_slave_device_selected.visibility = View.GONE
                            }
                            false -> {
                                mSelectedDevices.add(currentDevice)
                                iv_slave_device_selected.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }

    }


    override fun getItemCount(): Int = mAllDevices.size

    fun getSelectedDevices() = mSelectedDevices


}