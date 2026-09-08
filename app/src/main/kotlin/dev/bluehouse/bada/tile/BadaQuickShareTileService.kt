/*
 * Copyright 2026 Bada contributors.
 * Licensed under the Apache License, Version 2.0.
 * Modified: persistent receive toggle for the custom Android build.
 */
package dev.bluehouse.bada.tile

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dev.bluehouse.bada.R
import dev.bluehouse.bada.service.receiver.MdnsVisibilityOverrideHolder
import dev.bluehouse.bada.service.receiver.ReceiverForegroundService
import dev.bluehouse.bada.service.receiver.ReceiverRunningStateHolder
import dev.bluehouse.bada.service.receiver.TileVisibilityElevationHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Receive toggle. Enabling uses a visible activity for Android's FGS start rules. */
internal class BadaQuickShareTileService : TileService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var stateJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        stateJob?.cancel()
        stateJob = scope.launch {
            ReceiverRunningStateHolder.runningFlow.collect { syncTile() }
        }
        syncTile()
    }

    override fun onStopListening() {
        stateJob?.cancel()
        stateJob = null
        super.onStopListening()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onClick() {
        super.onClick()
        if (isLocked) {
            unlockAndRun { toggleReceiver() }
        } else {
            toggleReceiver()
        }
    }

    private fun toggleReceiver() {
        TileVisibilityElevationHolder.disarm()
        if (ReceiverRunningStateHolder.isRunning) {
            MdnsVisibilityOverrideHolder.setAlwaysVisible(false)
            // stopService never tries to create a background service. onDestroy
            // performs the upstream receiver cleanup, including open connections.
            stopService(Intent(this, ReceiverForegroundService::class.java))
        } else {
            openStartActivity()
        }
        syncTile()
    }

    private fun openStartActivity() {
        val intent = Intent(this, TileReceiveActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(
                PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                ),
            )
        } else {
            @Suppress("DEPRECATION", "StartActivityAndCollapseDeprecated")
            startActivityAndCollapse(intent)
        }
    }

    private fun syncTile() {
        val tile = qsTile ?: return
        val active = ReceiverRunningStateHolder.isRunning
        tile.state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.qs_tile_label)
        tile.icon = Icon.createWithResource(this, R.drawable.ic_quickshare_app_icon)
        val status = getString(if (active) R.string.qs_tile_subtitle_on else R.string.qs_tile_subtitle_off)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) tile.subtitle = status
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) tile.stateDescription = status
        tile.contentDescription = getString(
            if (active) R.string.qs_tile_content_desc_on else R.string.qs_tile_content_desc_off,
        )
        tile.updateTile()
    }
}
