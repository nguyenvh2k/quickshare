/*
 * Copyright 2026 Bada contributors.
 * Licensed under the Apache License, Version 2.0.
 * Added for the custom Android receive toggle.
 */
package dev.bluehouse.bada.service.receiver

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Actual foreground-service lifetime, including the default visible-on-scan mode. */
public object ReceiverRunningStateHolder {
    private val state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    public val runningFlow: StateFlow<Boolean> = state.asStateFlow()
    public val isRunning: Boolean get() = state.value

    internal fun setRunning(running: Boolean) {
        state.value = running
    }
}
