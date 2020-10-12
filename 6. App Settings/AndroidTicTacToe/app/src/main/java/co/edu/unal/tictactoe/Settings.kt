package co.edu.unal.tictactoe

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android.preference.PreferenceManager


class Settings : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val difficultyLevelPref = findPreference("difficulty_level") as ListPreference
        var difficulty = prefs.getString(
            "difficulty_level",
            resources.getString(R.string.difficulty_expert)
        )
        difficultyLevelPref.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                difficultyLevelPref.summary = newValue as CharSequence

                // Since we are handling the pref, we must save it
                val ed = prefs.edit()
                ed.putString("difficulty_level", newValue.toString())
                ed.commit()
                true
            }

        val victoryMessagePref =
            findPreference("victory_message") as EditTextPreference
        val victoryMessage = prefs.getString(
            "victory_message",
            resources.getString(R.string.result_human_wins)
        )
        victoryMessagePref.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, newValue ->
                victoryMessagePref.summary = newValue as CharSequence

                // Since we are handling the pref, we must save it
                val ed = prefs.edit()
                ed.putString("victory_message", newValue.toString())
                ed.commit()
                true
            }
    }


}
