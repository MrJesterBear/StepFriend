/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 11/11/2025
 * @version v1
 */

package com.SM_BSC.stepfriend.ui.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
import com.SM_BSC.stepfriend.ui.db.AppDatabase
import com.SM_BSC.stepfriend.ui.db.StepsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application) {
    // Build Database
    private val _db: AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "StepDB"
    ).build()

    private val _dao = _db.roomDao() // Access the Dao Methods for the database
    private val _steps = MutableLiveData<List<StepsEntity>>()
    val stepsList: LiveData<List<StepsEntity>> get() = _steps // create the list

    init {

    }

    // Insert a day if no day for current date is present
    fun insertDay(steps: StepsEntity) {

    }

    // Full list Update
    fun updateList() {

    }

    // Update list for today's entry .
    fun updateListDay() {

    }

    // close database.
    fun closeDB() {
        _db.close()
    }
    
    // when compose clears.
    override fun onCleared() {
        super.onCleared()
        closeDB()
    }
}