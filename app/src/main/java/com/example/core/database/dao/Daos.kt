package com.example.core.database.dao

import androidx.room.*
import com.example.core.database.entity.CareGoalEntity
import com.example.core.database.entity.JournalEntryEntity
import com.example.core.database.entity.MoodCheckInEntity
import com.example.core.database.entity.PatientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    fun getPatient(id: String = "pat_demo_me"): Flow<PatientEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(patient: PatientEntity)

    @Query("""
        UPDATE patients 
        SET fullName = :name, 
            phoneNumber = :phone, 
            selectedCity = :city, 
            email = :email, 
            emergencyContactName = :emergencyName, 
            emergencyContactPhone = :emergencyPhone,
            avatarUri = :avatarUri,
            avatarPresetId = :avatarPresetId
        WHERE id = :id
    """)
    suspend fun updateProfile(
        id: String = "pat_demo_me",
        name: String,
        phone: String,
        city: String,
        email: String,
        emergencyName: String?,
        emergencyPhone: String?,
        avatarUri: String?,
        avatarPresetId: String?
    )
}

@Dao
interface CareGoalDao {
    @Query("SELECT * FROM care_goals")
    fun getAllGoals(): Flow<List<CareGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<CareGoalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: CareGoalEntity)

    @Query("UPDATE care_goals SET isCompleted = :completed, progressPercent = :progress WHERE id = :id")
    suspend fun updateGoalProgress(id: String, completed: Boolean, progress: Int)
}

@Dao
interface MoodCheckInDao {
    @Query("SELECT * FROM mood_checkins ORDER BY timestamp DESC")
    fun getMoodCheckIns(): Flow<List<MoodCheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: MoodCheckInEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(checkIns: List<MoodCheckInEntity>)
}

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<JournalEntryEntity>)
}
