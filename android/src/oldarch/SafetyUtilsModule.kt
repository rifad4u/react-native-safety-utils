package com.safetyutils

import android.Manifest
import androidx.annotation.RequiresPermission
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class SafetyUtilsModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    private var delegate: SafetyUtilsImpl = SafetyUtilsImpl()

    override fun getName(): String {
        return SafetyUtilsImpl.NAME
    }

    @ReactMethod
    fun isScreenMirroring(p: Promise) {
        delegate.isScreenMirroring(reactApplicationContext, p)
    }

    @ReactMethod
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isVpnActive(p: Promise) {
        delegate.isVpnActive(reactApplicationContext, p)
    }

    @ReactMethod
    fun isProxyEnabled(p: Promise) {
        delegate.isProxyEnabled(reactApplicationContext, p)
    }

    @ReactMethod
    fun isMockedLocation(p: Promise) {
        delegate.isMockedLocation(reactApplicationContext, p)
    }

    @ReactMethod
    fun isAppTampered(signature: String, p: Promise) {
        delegate.isAppTampered(reactApplicationContext, signature, p)
    }

    @ReactMethod
    fun isDeveloperOptionsEnabled(p: Promise) {
        delegate.isDeveloperOptionsEnabled(reactApplicationContext, p)
    }

    @ReactMethod
    fun isLocationEnabled(p: Promise) {
        delegate.isLocationEnabled(reactApplicationContext, p)
    }

    @ReactMethod
    fun calculateAPKChecksum(algorithm: String, p: Promise) {
        delegate.calculateAPKChecksum(reactApplicationContext, algorithm, p)
    }

}
