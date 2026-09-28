package com.example

import com.example.ui.update.InAppUpdateManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InAppUpdateManagerTest {

    @Test
    fun testDismissDialog() {
        InAppUpdateManager.dismissUpdateDialog()
        assertFalse(InAppUpdateManager.showUpdatePromptDialog)
    }

    @Test
    fun testRequestCodeConstant() {
        assertEquals(8901, InAppUpdateManager.IN_APP_UPDATE_REQUEST_CODE)
    }
}
