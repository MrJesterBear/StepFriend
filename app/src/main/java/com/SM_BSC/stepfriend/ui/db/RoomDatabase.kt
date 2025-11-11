/**
 * @author 21005729 / Saul Maylin / MrJesterBear
 * @since 11/11/2025
 * @version v1
 */

package com.SM_BSC.stepfriend.ui.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import java.util.Date

// Entity for storing step data per day.
@Entity
data class StepsEntity(
    @PrimaryKey val date: String, // yyyy-mm-dd (for daily tracking)
    @ColumnInfo(name = "totalSteps") val totalSteps: Double?, // Total steps constant (currency)
    @ColumnInfo(name = "stepsToday") val stepsToday: Int?, // Total steps taken in a day
    @ColumnInfo(name = "upgradedSteps") val updatedSteps: Double?, // the amount of steps from upgrades
    @ColumnInfo(name = "upgradePercent") val upgradedPercent: Double? // the percent one step gets upgraded by
)

@Dao
interface RoomDao {
    // New Days
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg steps: StepsEntity)

    @Query("SELECT * FROM StepsEntity") // Get all records
    fun getAll(): List<StepsEntity>

    @Query("SELECT * FROM StepsEntity ORDER BY date DESC LIMIT 1") // get today's record
    fun getToday(): List<StepsEntity>
}

@Database(entities = [StepsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
}