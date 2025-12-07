/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 06/12/2025
 * @version v1.2
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
import com.SM_BSC.stepfriend.ui.db.WalkEntity
import com.SM_BSC.stepfriend.ui.db.WaypointEntity
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

    // Walk List
    private val _walk = MutableLiveData<List<WalkEntity>>()
    val walkList: LiveData<List<WalkEntity>> get() = _walk

    // Waypoint List
    private val _waypoint = MutableLiveData<List<WaypointEntity>>()
    val waypointList: LiveData<List<WaypointEntity>> get() = _waypoint

    // Waypoint and Walk list just for maps as to not interrupt location features..
    private val _mapWalk = MutableLiveData<List<WalkEntity>>()
     val mapWalkList: LiveData<List<WalkEntity>> get() = _mapWalk

    private val _mapWaypoint = MutableLiveData<List<WaypointEntity>>()
     val mapWaypointList: LiveData<List<WaypointEntity>> get() = _mapWaypoint

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

                    // Setup upgrades.
                    insertUpgrade(UpgradesEntity(1, "Shoe Insoles", "Buy some new Insoles to fill your shoes.", 50.00, 0.25, 0))
                    insertUpgrade(UpgradesEntity(2, "Sneaker Stocks", "Buy a share of stocks in a random Sneaker Company", 1000.00, 1.00, 0))
                    insertUpgrade(UpgradesEntity(3, "Protein Bar", "Buy a Nutritious Bar of processed protein!", 3000.00, 4.0, 0))
                    insertUpgrade(UpgradesEntity(4, "Super Sneakers", "Buy a pair of magical sneakers!", 10000.00, 5.0, 0))

                    // DummyData
                    insertWalk(0,57.567434, -4.037932)
                    insertWaypoint(1, 57.568807, -4.038641)
                    insertWaypoint(1, 57.570257, -4.039607)
                    finishWalk(1, 57.571983, -4.041130)

//                    // DummyData2
                    insertWalk(1,57.472663, -4.194736)
                    insertWaypoint(2, 57.473205, -4.200456)
                    insertWaypoint(2, 57.472895, -4.208405)
                    insertWaypoint(2, 57.470399, -4.212899)
                    insertWaypoint(2, 57.468421, -4.213652)
                    finishWalk(2, 57.466014, -4.210810)

                } else {
                    insertDay(StepsEntity(currentDate, oldData[0].totalSteps, 0, oldData[0].updatedSteps, oldData[0].upgradedPercent))
                }

            } else {
                // Get the last record for updating some things.
                val oldData: List<StepsEntity> = _dao.getLastRecord()

                // if no last record, set defaults.
                if (oldData.isEmpty()) {
                    insertDay(StepsEntity(currentDate, 0.00, 0, 0.0, 1.0))
                } else {
                    insertDay(StepsEntity(currentDate, oldData[0].totalSteps, oldData[0].stepsToday, oldData[0].updatedSteps, oldData[0].upgradedPercent))
                }
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

    /**
     * UPGRADE METHODS
     */
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

    fun insertUpgrade(upgrade: UpgradesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _dao.insertUpgrade(upgrade)
        }
    }

    fun updateStepUpgrade(newUpgradePercantage: Double, date: String, newTotal: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _dao.updateStepPercentage(date, newUpgradePercantage, newTotal)
        }
    }

    /**
     * Walk / Waypoint Methods
     */
    fun updateWalks() {
        viewModelScope.launch(Dispatchers.IO) {
            _walk.postValue(_dao.getWalks())
        }
    }

    fun insertWalk(oldWalkID: Int, Lat: Double, Lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            // Assumes the walkID is the old ID, so increment by 1.
            var newID: Int = oldWalkID + 1

            // get current date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val currentDate = LocalDate.now().format(formatter);

            // Post
            _dao.insertWalk(WalkEntity(newID, currentDate, Lat, Lng, null, null))
        }
    }

    fun finishWalk(walkID: Int, Lat: Double, Lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            println("FINISHING WALK $walkID WITH $Lat,$Lng")
            // Post data.
            if(_dao.finaliseWalk(Lat, Lng, walkID) != 1) {
                // do it again. just to make sure.
                println("\n" + _dao.finaliseWalk(Lat, Lng, walkID))
            }
        }
    }

    fun updateWaypoints(walkID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _waypoint.postValue(_dao.getWaypoints(walkID))
        }
    }

    fun insertWaypoint(walkID: Int, Lat: Double, Lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {

            // Get Last ID from Waypoints.
            val oldData: List<WaypointEntity> = _dao.getWaypoints(walkID)
            var waypointID: Int

            if (oldData.isEmpty()) {
                // Make the ID 1.
                waypointID = 1

            } else {
                waypointID = oldData[0].waypointID + 1 // Last ID + 1
            }

            // Post Data
            _dao.insertWaypoint(WaypointEntity(waypointID, walkID, Lat, Lng))
        }
    }

    /**
     * MAP SPECIFIC FUNCTIONS.
     */
    
    fun getWalkDetails(walkID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _mapWalk.postValue(_dao.getMapWalk(walkID))            
        }
    }
    
    fun getWaypointDetails(walkID: Int) {
        viewModelScope.launch(Dispatchers.IO){ 
            _mapWaypoint.postValue(_dao.getWaypoints(walkID))
        }
    }

    /**
     * OVERRIDDEN METHODS
     */

    // when compose clears.
    override fun onCleared() {
        super.onCleared()
        closeDB()

    }



}