package pl.transity.app.activities

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import pl.transity.app.R


abstract class BaseAppCompatActivity : AppCompatActivity() {

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionExit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            Log.d("BASE","android.R.id.home")
            overridePendingTransitionExit()
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    private fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    private fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}