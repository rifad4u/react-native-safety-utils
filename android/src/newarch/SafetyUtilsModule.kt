package com.safetyutils

import android.Manifest
import androidx.annotation.RequiresPermission
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext

class SafetyUtilsModule(reactContext: ReactApplicationContext) :
    NativeSafetyUtilsSpec(reactContext) {

    private var delegate: SafetyUtilsImpl = SafetyUtilsImpl()

    override fun getName(): String {
        return SafetyUtilsImpl.NAME
    }

    override fun isScreenMirroring(p: Promise) {
        delegate.isScreenMirroring(reactApplicationContext, p)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isVpnActive(p: Promise) {
        delegate.isVpnActive(reactApplicationContext, p)
    }

    override fun isProxyEnabled(p: Promise) {
        delegate.isProxyEnabled(reactApplicationContext, p)
    }

    override fun isMockedLocation(p: Promise) {
        delegate.isMockedLocation(reactApplicationContext, p)
    }

    override fun isAppTampered(signature: String, p: Promise) {
        delegate.isAppTampered(reactApplicationContext, signature, p)
    }

    override fun isDeveloperOptionsEnabled(p: Promise) {
        delegate.isDeveloperOptionsEnabled(reactApplicationContext, p)
    }

    override fun isLocationEnabled(p: Promise) {
        delegate.isLocationEnabled(reactApplicationContext, p)
    }

    override fun calculateAPKChecksum(algorithm: String, p: Promise) {
        delegate.calculateAPKChecksum(reactApplicationContext, algorithm, p)
    }

}
