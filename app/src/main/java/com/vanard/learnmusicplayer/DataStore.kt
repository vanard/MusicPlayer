package com.vanard.learnmusicplayer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.vanard.learnmusicplayer.util.Constants.Companion.PREFERENCES_NAME
import com.vanard.learnmusicplayer.util.Constants.Companion.PREFERENCES_SORT_BY
import com.vanard.learnmusicplayer.util.Constants.Companion.PREFERENCES_SORT_BY_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepo @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val selectedSortBy = preferencesKey<String>(PREFERENCES_SORT_BY)
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCES_NAME
    )

    suspend fun saveSelectedSortBy(sortBy: String) {
        dataStore.edit {
            it[PreferenceKeys.selectedSortBy] = sortBy
        }
    }

    val readSelectedSortBy: Flow<String> = dataStore.data.catch { exp ->
        if (exp is IOException) {
            emit(emptyPreferences())
        } else {
            throw exp
        }
    }.map { pref ->
        val selectedSortBy = pref[PreferenceKeys.selectedSortBy] ?: PREFERENCES_SORT_BY_NAME
        selectedSortBy
    }

}