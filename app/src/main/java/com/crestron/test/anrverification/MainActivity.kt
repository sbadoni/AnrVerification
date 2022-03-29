package com.crestron.test.anrverification

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crestron.test.anrverification.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "AnrVerification"
    private val ACTION_GENERATE_ANR = "ANR"
    private val ACTION_GENERATE_TOMB = "TOMB"
    private val READ_ANR = "READ_ANR"
    private val READ_TOMB ="READ_TOMB"
    private val ANR_LOCATION = "/storage/emulated"
    private val TOMB_LOCATION = "/data/tombstones"
    private val ACTION_EXCEPTION_ = "com.crestron.test.EXCEPTION"
    private val ACTION = "action"
    private val LOCATION = "location"
    private lateinit var devicePolicyManager: DevicePolicyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val status = devicePolicyManager.storageEncryptionStatus
        if(status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE) {
            Log.d(TAG,"ENCRYPTION_STATUS_ACTIVE")
        }
        if(status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER) {
            Log.d(TAG,"ENCRYPTION_STATUS_ACTIVE_PER_USER")
        }
        // Example of a call to a native method
       // binding.sampleText.text = stringFromJNI()
        registerReceiver(exceptionGenReceiver, IntentFilter(ACTION_EXCEPTION_))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            &&  ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy")
        unregisterReceiver(exceptionGenReceiver)
    }

    /**
     * A native method that is implemented by the 'anrverification' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    private val exceptionGenReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                Log.d(TAG,"ACTION RECEIVED => "+intent.action)
                when (intent.action) {
                    ACTION_EXCEPTION_ -> {
                        val extras = intent.extras ?: return@let

                        Log.d(TAG, "EXTRA RECEIVED => ${extras.getString(ACTION)}")
                        extras.let {
                            val location = it.getString(LOCATION)
                            when (it.getString(ACTION)) {
                                ACTION_GENERATE_ANR -> {
                                    generateANR()
                                }
                                ACTION_GENERATE_TOMB -> {
                                    generateTOMB()
                                }
                                READ_ANR -> {
                                    readANR(location)
                                }
                                READ_TOMB -> {
                                    readTOMB(location)
                                }
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun generateANR() {
        Log.d(TAG,"generateANR")
        while(true) {}
    }

    private fun generateTOMB() {
        Log.d(TAG,"generateTOMB")
        stringFromJNI()
    }

    private fun readANR(location: String?) {
        Log.d(TAG,"readANR")
        val dir = File(location ?: ANR_LOCATION)
        Log.d(TAG,"Abs Path: "+dir.absolutePath)
        Log.d(TAG,"Is Location is File: "+dir.isFile)
        if(dir.isFile) {
            val bufferedReader: BufferedReader = dir.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            Log.d(TAG, "File Content: $inputString")
        }
        else {
            Log.d(TAG,"Total files in dir: "+dir.list()?.size)
            dir.walk().forEach { file ->
                Log.d(TAG,"File Name: "+file.name)
                Log.d(TAG,"File Permission read: "+file.canRead())
                Log.d(TAG,"File Permission write: "+file.canWrite())
                if(file.isFile) {
                    val bufferedReader: BufferedReader = file.bufferedReader()
                    val inputString = bufferedReader.use { it.readText() }
                    Log.d(TAG, "File Content: $inputString")
                }
            }
        }
    }

    private fun readTOMB(location: String?) {
        Log.d(TAG,"readTOMB")
        val dir = File(location ?: TOMB_LOCATION)
        Log.d(TAG,"Abs Path: "+dir.absolutePath)
        if(dir.isFile) {
            val bufferedReader: BufferedReader = dir.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            Log.d(TAG, "File Content: $inputString")
        }
        else {
            Log.d(TAG,"Total files in dir: "+dir.list()?.size)
            dir.walk().forEach { file ->
                Log.d(TAG,"File Name: "+file.name)
                Log.d(TAG,"File Permission read: "+file.canRead())
                Log.d(TAG,"File Permission write: "+file.canWrite())
                if(file.isFile) {
                    val bufferedReader: BufferedReader = file.bufferedReader()
                    val inputString = bufferedReader.use { it.readText() }
                    Log.d(TAG, "File Content: $inputString")
                }
            }
        }
    }

    companion object {
        init {
            System.loadLibrary("anrverification")
        }
    }
}
