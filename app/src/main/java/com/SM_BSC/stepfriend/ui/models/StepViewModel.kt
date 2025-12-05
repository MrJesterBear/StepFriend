/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 05/12/2025
 * @version v1,1
 */

package com.SM_BSC.stepfriend.ui.models

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.SM_BSC.stepfriend.ui.db.AppDatabase
import com.SM_BSC.stepfriend.ui.db.StepsEntity
import com.SM_BSC.stepfriend.ui.db.UpgradesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
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

    // Upgrade List
    private val _upgrades = MutableLiveData<List<UpgradesEntity>>()

    val upgradesList: LiveData<List<UpgradesEntity>> get() = _upgrades

    init {
        viewModelScope.launch(Dispatchers.IO) { // Allow thread to run queries for setup
            // https://www.baeldung.com/kotlin/current-date-time

            // Get Today
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val currentDate = LocalDate.now().format(formatter);

            val doesTodayExist: List<StepsEntity> = _dao.getToday(currentDate)
            // Check if an entry for today exists, if not, make one.
            if (doesTodayExist.isEmpty()) {
                // Get the last record for updating some things.
                val oldData: List<StepsEntity> = _dao.getLastRecord()

                // if no last record, set defaults.
                if (oldData.isEmpty()) {
                    // This assumes that the app is being opened for the very first time ever. so create everything needed.
                    insertDay(StepsEntity(currentDate, 0.00, 0, 0.0, 1.0))

                } else {
                    insertDay(StepsEntity(currentDate, oldData[0].totalSteps, 0, oldData[0].updatedSteps, oldData[0].upgradedPercent))
                }

            } else { // do nothing because  there is a record with a current date...
                // Get the last record for updating some things.
//                val oldData: List<StepsEntity> = _dao.getLastRecord()
//
//                // if no last record, set defaults.
//                if (oldData.isEmpty()) {
//                    insertDay(StepsEntity(currentDate, 0.00, 0, 0.0, 1.0))
//                } else {
//                    insertDay(StepsEntity(currentDate, oldData[0].totalSteps, 0, oldData[0].updatedSteps, oldData[0].upgradedPercent))
//                }
            }
            // finally update the list to be most recent.
            updateListDay(currentDate)
        }

    }

    // Insert a day if no day for current date is present
    fun insertDay(steps: StepsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _dao.insert(steps)
        }
    }

    // Full list Update
    fun updateList() {
        viewModelScope.launch(Dispatchers.IO) {
            _steps.postValue(_dao.getAll())
        }
     }

    // Update list for today's entry .
    fun updateListDay(currentDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _steps.postValue(_dao.getToday(currentDate))
        }
    }

    // close database.
    fun closeDB() {
        _db.close()
    }

    fun updateCurrentStepRecord(totalSteps: Double?, stepsToday: Int?, updatedSteps: Double?) {
        // get current date
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now().format(formatter);

        // run query
        viewModelScope.launch(Dispatchers.IO) {
            _dao.updateRow(totalSteps, stepsToday, updatedSteps, currentDate)
        }
    }

    fun updateUpgradeQuantity(ID: Int, amount: Int) {

        // Run Query
        viewModelScope.launch(Dispatchers.IO) {
            _dao.updateUpgrade(amount, ID)
        }
    }

    fun updateUpgradesList() {
        viewModelScope.launch(Dispatchers.IO) {
            _upgrades.postValue(_dao.getUpgrades())
        }
    }

    // when compose clears.
    override fun onCleared() {
        super.onCleared()
        closeDB()
    }
}