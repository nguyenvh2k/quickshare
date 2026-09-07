/*
 * Copyright 2026 Bada contributors.
 * Licensed under the Apache License, Version 2.0.
 * Added for the custom Android receive toggle.
 */
package dev.bluehouse.bada.tile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import dev.bluehouse.bada.MainActivity
import dev.bluehouse.bada.R
import dev.bluehouse.bada.onboarding.PermissionRequirements
import dev.bluehouse.bada.service.receiver.MdnsVisibilityOverrideHolder
import dev.bluehouse.bada.service.receiver.ReceiverForegroundService
import dev.bluehouse.bada.service.receiver.TileVisibilityElevationHolder

/** Brief visible trampoline: starts the receiver only after the activity resumes. */
internal class TileReceiveActivity : Activity() {
    private var handled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply { setText(R.string.tile_starting) })
    }

    override fun onPostResume() {
        super.onPostResume()
        if (handled) return
        handled = true
        if (!PermissionRequirements.allGranted(this) && !PermissionRequirements.onlyOptionalMissing(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        TileVisibilityElevationHolder.disarm()
        MdnsVisibilityOverrideHolder.setAlwaysVisible(true)
        try {
            // No root, wireless debugging, or radio-helper is required.
            // The user enables Wi-Fi and Bluetooth using the system controls.
            ReceiverForegroundService.start(this)
        } catch (_: IllegalStateException) {
            showStartFailure()
        } catch (_: SecurityException) {
            showStartFailure()
        }
        finish()
    }

    private fun showStartFailure() {
        MdnsVisibilityOverrideHolder.setAlwaysVisible(false)
        Toast.makeText(this, R.string.tile_start_failed, Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
