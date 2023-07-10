package com.example.vin_app2

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.vin_app2.camera.CameraHandler
import com.example.vin_app2.databinding.ActivityMainBinding
import com.example.vin_app2.utilits.hideKeyboard
import com.example.vin_app2.utilits.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraHandler: CameraHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraHandler = CameraHandler(this, binding)

        if (allPermissionsGranted()) {
            cameraHandler.startCamera()
        } else {
            requestPermissions()
        }
        binding.buttonCapture.setOnClickListener { cameraHandler.takePhoto() }


        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
        binding.editTextPhone.setOnClickListener {
            true
        }
        binding.editTextVin.setOnClickListener {
            true
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                android.Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHandler.shutdown()
    }

    val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast(baseContext, "Запрос на разрешение отклонен")
            } else {
                cameraHandler.startCamera()
            }
        }
}