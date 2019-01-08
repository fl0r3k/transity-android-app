package pl.transity.app.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import pl.transity.app.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}