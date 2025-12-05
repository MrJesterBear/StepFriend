/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 05/12/2025
 * @version v2
 */

package com.SM_BSC.stepfriend.ui.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.google.android.gms.maps.model.LatLng

// Entity for storing step data per day.
@Entity
data class StepsEntity(
    @PrimaryKey var date: String, // yyyy-mm-dd (for daily tracking)
    @ColumnInfo(name = "totalSteps") val totalSteps: Double?, // Total steps constant (currency)
    @ColumnInfo(name = "stepsToday") val stepsToday: Int?, // Total steps taken in a day
    @ColumnInfo(name = "upgradedSteps") val updatedSteps: Double?, // the amount of steps from upgrades
    @ColumnInfo(name = "upgradePercent") val upgradedPercent: Double? // the percent one step gets upgraded by
)

// Entity for Upgrades
@Entity
data class UpgradesEntity(
    @PrimaryKey(autoGenerate = true) var upgradeID: Int, // Id of the upgrade (auto generating id)
    @ColumnInfo(name = "upgradeName") val upgradeName: String, // Name of the upgrade
    @ColumnInfo(name = "upgradeDesc") val upgradeDesc: String, // Description of the upgrade.
    @ColumnInfo(name = "basePrice") val basePrice: Double, // The base price of the upgrade (Measured in steps)
    @ColumnInfo(name = "quantityOwned") val quantityOwned: Int? // Quantity of the upgrade owned.
)

// Entity for Walking tracking
@Entity
data class WalkEntity(
    @PrimaryKey(autoGenerate = true) var walkID: Int, //ID of a walk.
    @ColumnInfo(name = "date") var date: String, // Date of walk (foreign key for step class.)
    @ColumnInfo(name = "startingLat") var startLat: Double, //Lat for starting
    @ColumnInfo(name = "StartingLng") var startLng: Double, //Lng for starting
    @ColumnInfo(name = "endingLat") var endLat: Double,
    @ColumnInfo(name = "endingLng") var endLng: Double,

    )

@Entity
data class WaypointEntity(
    @PrimaryKey(autoGenerate = true) var waypointID: Int, // ID of waypoint
    @ColumnInfo(name = "walkID") var walkID: Int, // PK/FK for the walk entity.
    @ColumnInfo(name = "waypointLat") var waypointLat: Double,
    @ColumnInfo(name = "waypointLng") var waypointLng: Double,
)


@Dao
interface RoomDao {
    /**
    STEPS ENTITY QUERIES.
     */
    // New Days
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg steps: StepsEntity)

    @Query("SELECT * FROM StepsEntity") // Get all records
    fun getAll(): List<StepsEntity>

    @Query("SELECT * FROM StepsEntity WHERE date = :date LIMIT 1") // get today's record
    fun getToday(date: String): List<StepsEntity>

    @Query("SELECT * FROM StepsEntity ORDER BY date DESC LIMIT 1")
    fun getLastRecord(): List<StepsEntity>

    @Query("UPDATE StepsEntity SET totalSteps = :newTotalSteps, stepsToday = :newStepsToday, upgradedSteps = :newUpdatedSteps WHERE date = :date")
    fun updateRow(
        newTotalSteps: Double?,
        newStepsToday: Int?,
        newUpdatedSteps: Double?,
        date: String
    )

    /**
     * UPGRADES ENTITY QUERIES.
     */

    // New Upgrades.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUpgrade(vararg upgrades: UpgradesEntity)

    // Update the upgrade with quantity.
    @Query("UPDATE UpgradesEntity SET quantityOwned = :quantity WHERE upgradeID = :ID")
    fun updateUpgrade(quantity: Int, ID: Int)

    // Get all Upgrades.
    @Query("SELECT * FROM UpgradesEntity")
    fun getUpgrades(): List<UpgradesEntity>

}

@Database(
    entities = [StepsEntity::class, UpgradesEntity::class, WalkEntity::class, WaypointEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
}