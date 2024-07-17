package com.dimaster.bluetoothkotlintest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment

fun Fragment.changeButtonColor(button: ImageButton, color: Int)
{
    //take current color
    val drawable = button.drawable
    DrawableCompat.setTint(drawable, color)
    button.setImageDrawable(drawable)
}

@RequiresApi(Build.VERSION_CODES.S)
fun Fragment.checkBtPermission():Boolean {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                &&

                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

                &&

                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED

    } else {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}
