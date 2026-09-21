package com.example.core.database.dao

import androidx.room.*
import com.example.core.database.entity.CareGoalEntity
import com.example.core.database.entity.JournalEntryEntity
import com.example.core.database.entity.MoodCheckInEntity
import com.example.core.database.entity.PatientEntity
import com.example.core.database.entity.AppointmentEntity
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

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY createdAtEpoch DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE practitionerId = :practitionerId ORDER BY createdAtEpoch DESC")
    fun getAppointmentsForPractitioner(practitionerId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY createdAtEpoch DESC")
    fun getAppointmentsForPatient(patientId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getAppointmentById(id: String): AppointmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(appointment: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appointments: List<AppointmentEntity>)

    @Query("UPDATE appointments SET status = :status, paymentStatus = :paymentStatus WHERE id = :id")
    suspend fun updateStatusAndPayment(id: String, status: String, paymentStatus: String): Int

    @Query("UPDATE appointments SET dateIso = :newDateIso, timeSlotLabel = :newTimeSlot, status = :status WHERE id = :id")
    suspend fun rescheduleAppointment(id: String, newDateIso: String, newTimeSlot: String, status: String = "RESCHEDULED"): Int

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: String): Int
}

@Dao
interface SpecialistAvailabilityRuleDao {
    @Query("SELECT * FROM specialist_availability_rules WHERE practitionerId = :practitionerId")
    fun getRulesForPractitioner(practitionerId: String): Flow<List<com.example.core.database.entity.SpecialistAvailabilityRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(rule: com.example.core.database.entity.SpecialistAvailabilityRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<com.example.core.database.entity.SpecialistAvailabilityRuleEntity>)

    @Query("DELETE FROM specialist_availability_rules WHERE id = :id")
    suspend fun deleteById(id: String): Int
}

@Dao
interface AvailabilityExceptionDao {
    @Query("SELECT * FROM availability_exceptions WHERE practitionerId = :practitionerId")
    fun getExceptionsForPractitioner(practitionerId: String): Flow<List<com.example.core.database.entity.AvailabilityExceptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(exception: com.example.core.database.entity.AvailabilityExceptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exceptions: List<com.example.core.database.entity.AvailabilityExceptionEntity>)

    @Query("DELETE FROM availability_exceptions WHERE id = :id")
    suspend fun deleteById(id: String): Int
}

