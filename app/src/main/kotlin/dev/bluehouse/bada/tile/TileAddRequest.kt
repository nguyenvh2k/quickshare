/*
 * Copyright 2026 Bada contributors.
 * Licensed under the Apache License, Version 2.0.
 * Added for the custom Android receive toggle.
 */
package dev.bluehouse.bada.tile

import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.Toast
import dev.bluehouse.bada.R

internal object TileAddRequest {
    fun show(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            showManualSteps(activity)
            return
        }
        val manager = activity.getSystemService(StatusBarManager::class.java)
        if (manager == null) {
            showManualSteps(activity)
            return
        }
        try {
            manager.requestAddTileService(
                ComponentName(activity, BadaQuickShareTileService::class.java),
                activity.getString(R.string.qs_tile_label),
                // The add-tile confirmation is an app-facing surface: use the coloured
                // Quick Share mark rather than the monochrome glyph used inside the tile.
                Icon.createWithResource(activity, R.drawable.ic_quickshare_app_icon),
                activity.mainExecutor,
            ) { result ->
                if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ||
                    result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED
                ) {
                    Toast.makeText(activity, R.string.tile_added, Toast.LENGTH_SHORT).show()
                } else {
                    showManualSteps(activity)
                }
            }
        } catch (_: SecurityException) {
            showManualSteps(activity)
        } catch (_: IllegalArgumentException) {
            showManualSteps(activity)
        }
    }

    private fun showManualSteps(activity: Activity) {
        Toast.makeText(activity, R.string.tile_add_manual, Toast.LENGTH_LONG).show()
    }
}
