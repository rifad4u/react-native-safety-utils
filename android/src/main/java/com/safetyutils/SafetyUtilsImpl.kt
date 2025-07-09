package com.safetyutils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.hardware.display.DisplayManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.view.Display
import androidx.annotation.RequiresPermission
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

class SafetyUtilsImpl {

    companion object {
        const val NAME = "SafetyUtils"
    }

    fun isDeveloperOptionsEnabled(reactContext: ReactApplicationContext, p: Promise) {
        try {
            val isEnabled = Settings.Global.getInt(
                reactContext.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) == 1
            p.resolve(isEnabled)
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    fun isLocationEnabled(reactContext: ReactApplicationContext, p: Promise) {
        try {
            val locationManager = reactContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val isEnabled =
                locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
            p.resolve(isEnabled)
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    fun isMockedLocation(reactContext: ReactApplicationContext, p: Promise) {
        try {
            val locationManager = reactContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (locationManager != null) {
                @SuppressLint("MissingPermission")
                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastKnownLocation != null) {
                    val isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        lastKnownLocation.isMock
                    } else {
                        lastKnownLocation.isFromMockProvider
                    }
                    p.resolve(isMock)
                    return
                }
            }
            p.resolve(false)
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isVpnActive(reactContext: ReactApplicationContext, p: Promise) {
        try {
            val cm = reactContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (cm == null) {
                p.resolve(false)
                return
            }

            val activeNetwork = cm.activeNetwork
            if (activeNetwork == null) {
                p.resolve(false)
                return
            }

            val caps = cm.getNetworkCapabilities(activeNetwork)
            p.resolve(caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true)
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    fun isProxyEnabled(reactContext: ReactApplicationContext, p: Promise) {
        try {
            val cm = reactContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (cm != null) {
                val proxyInfo = cm.defaultProxy
                p.resolve(proxyInfo?.host != null)
                return
            }
            p.resolve(false)
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    fun isScreenMirroring(reactContext: ReactApplicationContext, p: Promise) {
        try {
            val displayManager = reactContext.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            if (displayManager != null) {
                for (display in displayManager.displays) {
                    if (display.displayId != Display.DEFAULT_DISPLAY) {
                        p.resolve(true)
                        return
                    }
                }
                p.resolve(false)
                return
            }
            p.resolve(false)
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    private fun validateSignature(originalSignature: String, signatures: Array<Signature>, p: Promise) {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(signatures[0].toByteArray())
            val digest = md.digest()

            // Convert to hex string
            val hexString = digest.joinToString(":") { byte -> "%02X".format(byte) }
            val currentSignature = hexString.uppercase()
            p.resolve(!currentSignature.equals(originalSignature, ignoreCase = true))
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    fun isAppTampered(reactContext: ReactApplicationContext, originalSignature: String, p: Promise) {
        try {
            val pm = reactContext.packageManager
            val packageName = reactContext.packageName

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signatures = packageInfo.signingInfo!!.apkContentsSigners;
                validateSignature(originalSignature, signatures, p);
            } else {
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                val signatures = packageInfo.signatures
                validateSignature(originalSignature, signatures!!, p);
            }
        } catch (e: Exception) {
            p.resolve(false)
        }
    }

    private fun getAPKFilePath(reactContext: ReactApplicationContext): String? {
        return try {
            val pm = reactContext.packageManager
            val packageInfo = pm.getPackageInfo(reactContext.packageName, 0)
            packageInfo.applicationInfo!!.sourceDir
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun calculateAPKChecksum(reactContext: ReactApplicationContext, algorithm: String, p: Promise) {
        try {
            val apkPath = getAPKFilePath(reactContext)
            if (apkPath == null) {
                p.resolve(null)
                return
            }
            val digest = MessageDigest.getInstance(algorithm)
            val file = File(apkPath)
            val inputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
            inputStream.close()
            val hexString = digest.digest().joinToString("") { "%02x".format(it) }
            p.resolve(hexString)
        } catch (e: Exception) {
            p.resolve(null)
        }
    }

}
