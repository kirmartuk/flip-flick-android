package com.martyuk.flipflick

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep


class HostObservingService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runObservingService()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun runObservingService() {
        GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                sleep(15000)
                val intent = Intent()
                intent.action = BluetoothRepository.UPDATE_HOSTS
                sendBroadcast(intent)
            }
        }
    }
}