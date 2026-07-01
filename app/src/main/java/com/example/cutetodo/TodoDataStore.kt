package com.example.cutetodo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
data class TodoItem(
    val text: String,
    val isDone: Boolean = false,
    val completedAt: Long? = null,
    val isPinned: Boolean = false
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "todo_prefs")

class TodoDataStore(private val context: Context) {

    companion object {
        private val TODO_LIST_KEY = stringPreferencesKey("todo_list")
    }

    val getTodoList: Flow<List<TodoItem>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val jsonString = preferences[TODO_LIST_KEY] ?: "[]"
            try {
                Json.decodeFromString<List<TodoItem>>(jsonString)
            } catch (_: Exception) {
                emptyList()
            }
        }

    suspend fun saveTodoList(todoList: List<TodoItem>) {
        context.dataStore.edit { preferences ->
            val jsonString = Json.encodeToString(todoList)
            preferences[TODO_LIST_KEY] = jsonString
        }
    }
}
