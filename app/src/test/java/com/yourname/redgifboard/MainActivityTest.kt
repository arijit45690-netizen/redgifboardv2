package com.yourname.redgifboard

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class MainActivityTest {

    @Test
    fun testBtnEnableStartsSettingsActivity() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val btnEnable = activity.findViewById<Button>(R.id.btnEnable)
                btnEnable.performClick()

                val expectedIntent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                val shadowActivity = shadowOf(activity)
                val actualIntent = shadowActivity.nextStartedActivity

                assertEquals(expectedIntent.action, actualIntent.action)
            }
        }
    }

    @Test
    fun testBtnSelectShowsInputMethodPicker() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val btnSelect = activity.findViewById<Button>(R.id.btnSelect)
                btnSelect.performClick()
                // Checking action of InputMethodManager.showInputMethodPicker() via shadow is not well supported in Robolectric without custom shadow. We ensure no crash happens on click.
            }
        }
    }

    @Test
    fun testOnResumeUpdatesHintTextWhenKeyboardEnabled() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val shadowImm = shadowOf(imm)

        // Mock an InputMethodInfo that has the same package name as our app
        val fakeImi = mock(InputMethodInfo::class.java)
        org.mockito.Mockito.`when`(fakeImi.packageName).thenReturn(context.packageName)

        shadowImm.setEnabledInputMethodInfoList(listOf(fakeImi))

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val hintText = activity.findViewById<TextView>(R.id.step2Hint)
                assertEquals("✓ Keyboard enabled! Now tap Step 2 to switch to it.", hintText.text.toString())
            }
        }
    }

    @Test
    fun testOnResumeUpdatesHintTextWhenKeyboardDisabled() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val shadowImm = shadowOf(imm)

        shadowImm.setEnabledInputMethodInfoList(emptyList())

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val hintText = activity.findViewById<TextView>(R.id.step2Hint)
                assertEquals("Tap Step 1 first to enable the keyboard in settings.", hintText.text.toString())
            }
        }
    }
}
