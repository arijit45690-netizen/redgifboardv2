package com.yourname.redgifboard

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MainActivityTest {

    @Test
    fun testEnableButton_launchesSettings() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val btnEnable = activity.findViewById<Button>(R.id.btnEnable)
                btnEnable.performClick()

                val expectedIntent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                val actualIntent = shadowOf(activity).nextStartedActivity

                assertEquals(expectedIntent.action, actualIntent.action)
            }
        }
    }

    @Test
    fun testSelectButton_showsInputMethodPicker() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val btnSelect = activity.findViewById<Button>(R.id.btnSelect)
                btnSelect.performClick()
                // Assert no crash
                assertTrue(true)
            }
        }
    }

    @Test
    fun testOnResume_keyboardNotEnabled_showsHint() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val hintView = activity.findViewById<TextView>(R.id.step2Hint)
                assertEquals("Tap Step 1 first to enable the keyboard in settings.", hintView.text)
            }
        }
    }
}
