package pl.transity.app


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File




@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 21)
class ChangeTextBehaviorTest {

    private var device: UiDevice? = null

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(getInstrumentation())

        // Start from the home screen
        device!!.pressHome()

        // Wait for launcher
        val launcherPackage = launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device!!.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)

        // Launch the blueprint app
        val context = getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Wait for the app to appear
        device!!.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT)
    }

    @Test
    fun checkPreconditions() {
        assertThat(device, notNullValue())
    }

    @Test
    fun testStopMarker() {
        // Type text and then press the button.
        val marker = device!!.findObject(UiSelector().descriptionContains("700102"))
        marker.click()

        val actionBar = device!!.findObject(UiSelector().resourceId("com.scsexpert.android_app:id/toolbar"))

        val actionBarTitle = actionBar.getChild(UiSelector().index(1)).text
//        Log.d("TEST","actionBarTitle: $actionBarTitle" )
//        Log.d("TEST",actionBar.childCount.toString())
        assertThat(actionBarTitle, `is`(equalTo("EMILII PLATER 02")))
    }

    @Test
    fun testMapZoomOut() {
        val map = device!!.findObject(UiSelector().descriptionContains("Google Map"))
//        takeScreenshot("default-zoom")
        Thread.sleep(5000)
        map.pinchIn(50,100)
        Thread.sleep(5000)
//        takeScreenshot("zoomed-out")
//        mapView.
//        Log.d("TEST", "currentZoom: $currentZoom")
    }

    private fun takeScreenshot(name: String) {
        val dir = String.format("%s/%s", Environment.getExternalStorageDirectory().path, "test-screenshots")
        val theDir = File(dir)
        if (!theDir.exists()) {
            if (!theDir.mkdir()) {
                Log.d("Screenshot Test", "Oops! Failed create directory")
            }
        }
        val file = File(theDir.path + File.separator + name + ".png")
        device!!.takeScreenshot(file)
    }

    companion object {
        private val BASIC_SAMPLE_PACKAGE = "com.scsexpert.android_app"
        private val LAUNCH_TIMEOUT = 5000L
    }

    private val launcherPackageName: String
        get() {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            val pm = getApplicationContext<Context>().packageManager
            val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return resolveInfo.activityInfo.packageName
        }
}