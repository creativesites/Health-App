package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.core.database.dao.*
import com.example.core.database.entity.CareGoalEntity
import com.example.core.database.entity.JournalEntryEntity
import com.example.core.database.entity.MoodCheckInEntity
import com.example.core.database.entity.PatientEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PatientEntity::class,
        CareGoalEntity::class,
        MoodCheckInEntity::class,
        JournalEntryEntity::class,
        com.example.core.database.entity.AppointmentEntity::class,
        com.example.core.database.entity.SpecialistAvailabilityRuleEntity::class,
        com.example.core.database.entity.AvailabilityExceptionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun careGoalDao(): CareGoalDao
    abstract fun moodCheckInDao(): MoodCheckInDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun specialistAvailabilityRuleDao(): SpecialistAvailabilityRuleDao
    abstract fun availabilityExceptionDao(): AvailabilityExceptionDao

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(database)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthcare_app.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                // Also ensure initial data is present in case callback already fired or empty
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(instance)
                }
                instance
            }
        }

        private suspend fun prepopulateDatabase(database: AppDatabase) {
            val patientDao = database.patientDao()
            val careGoalDao = database.careGoalDao()
            val moodCheckInDao = database.moodCheckInDao()
            val journalEntryDao = database.journalEntryDao()

            // Prepopulate Patient if not already set
            patientDao.insertOrUpdate(
                PatientEntity(
                    id = "pat_demo_me",
                    fullName = "Kondwani Tembo",
                    email = "kondwani.tembo@example.zm",
                    phoneNumber = "+260 97 5543210",
                    selectedCity = "Lusaka",
                    preferredLanguage = "English",
                    emergencyContactName = "Chileshe Tembo (Sister)",
                    emergencyContactPhone = "+260 96 1122334",
                    avatarUri = null,
                    avatarPresetId = "preset_amber"
                )
            )

            // Prepopulate initial Care Goals
            careGoalDao.insertAll(
                listOf(
                    CareGoalEntity(
                        id = "goal_1",
                        title = "Grounding Rhythm Breathing",
                        category = "Mindfulness",
                        progressPercent = 100,
                        targetDescription = "Complete 5 minutes of rhythmic diaphragmatic breathing daily",
                        isCompleted = true
                    ),
                    CareGoalEntity(
                        id = "goal_2",
                        title = "Daily Hydration & Sunlight Walk",
                        category = "Physical Vitality",
                        progressPercent = 75,
                        targetDescription = "20 min gentle walking in morning sunlight + 2L clean water",
                        isCompleted = false
                    ),
                    CareGoalEntity(
                        id = "goal_3",
                        title = "Emotional Reflection Log",
                        category = "Self-Care",
                        progressPercent = 50,
                        targetDescription = "Log feeling check-in before bedtime",
                        isCompleted = false
                    ),
                    CareGoalEntity(
                        id = "goal_4",
                        title = "Follow-up Consultation Notes",
                        category = "Clinical Care",
                        progressPercent = 0,
                        targetDescription = "Review sleep journal before next Dr. Mwansa visit",
                        isCompleted = false
                    )
                )
            )

            // Prepopulate initial Mood Check-in
            moodCheckInDao.insert(
                MoodCheckInEntity(
                    id = "mood_init_1",
                    moodValue = 4,
                    moodLabel = "Steady & Hopeful",
                    feelingsCsv = "Grateful,Calm,Focused",
                    note = "Feeling grounded after a peaceful morning walk in Lusaka.",
                    dateLabel = "Today, 08:30 AM",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Prepopulate initial Journal Entry
            journalEntryDao.insert(
                JournalEntryEntity(
                    id = "jrnl_init_1",
                    title = "Morning Gratitude & Presence",
                    reflection = "Woke up feeling steady today. Practiced 4-4-6 breathing before opening any notifications. Ready for the week ahead.",
                    moodScore = 4,
                    dateLabel = "Today, 08:45 AM",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Prepopulate initial Appointments into shared Room persistence
            val initialAppointments = com.example.core.repository.mock.MockDataContainer.initialAppointments.map {
                com.example.core.database.entity.AppointmentEntity.fromDomain(it)
            }
            database.appointmentDao().insertAll(initialAppointments)

            // Prepopulate Availability Rules for Practitioners
            val ruleDao = database.specialistAvailabilityRuleDao()
            val rules = listOf(
                // Mutale Chileshe (doc_chileshe_01)
                com.example.core.database.entity.SpecialistAvailabilityRuleEntity(
                    id = "rule_chileshe_mon_1",
                    practitionerId = "doc_chileshe_01",
                    dayOfWeek = "Monday",
                    startTime = "09:00",
                    endTime = "12:00",
                    slotDurationMinutes = 30,
                    bufferMinutes = 10,
                    enabled = true,
                    allowsOnline = true,
                    allowsInPerson = true
                ),
                com.example.core.database.entity.SpecialistAvailabilityRuleEntity(
                    id = "rule_chileshe_mon_2",
                    practitionerId = "doc_chileshe_01",
                    dayOfWeek = "Monday",
                    startTime = "14:00",
                    endTime = "17:00",
                    slotDurationMinutes = 30,
                    bufferMinutes = 10,
                    enabled = true,
                    allowsOnline = true,
                    allowsInPerson = true
                ),
                com.example.core.database.entity.SpecialistAvailabilityRuleEntity(
                    id = "rule_chileshe_wed",
                    practitionerId = "doc_chileshe_01",
                    dayOfWeek = "Wednesday",
                    startTime = "09:00",
                    endTime = "15:00",
                    slotDurationMinutes = 30,
                    bufferMinutes = 10,
                    enabled = true,
                    allowsOnline = true,
                    allowsInPerson = true
                ),
                com.example.core.database.entity.SpecialistAvailabilityRuleEntity(
                    id = "rule_chileshe_fri",
                    practitionerId = "doc_chileshe_01",
                    dayOfWeek = "Friday",
                    startTime = "09:00",
                    endTime = "16:00",
                    slotDurationMinutes = 30,
                    bufferMinutes = 10,
                    enabled = true,
                    allowsOnline = true,
                    allowsInPerson = true
                ),

                // Dr. Thandiwe Mwansa (doc_mwansa_02)
                com.example.core.database.entity.SpecialistAvailabilityRuleEntity(
                    id = "rule_mwansa_tue",
                    practitionerId = "doc_mwansa_02",
                    dayOfWeek = "Tuesday",
                    startTime = "14:00",
                    endTime = "18:00",
                    slotDurationMinutes = 45,
                    bufferMinutes = 15,
                    enabled = true,
                    allowsOnline = true,
                    allowsInPerson = true
                ),
                com.example.core.database.entity.SpecialistAvailabilityRuleEntity(
                    id = "rule_mwansa_thu",
                    practitionerId = "doc_mwansa_02",
                    dayOfWeek = "Thursday",
                    startTime = "14:00",
                    endTime = "18:00",
                    slotDurationMinutes = 45,
                    bufferMinutes = 15,
                    enabled = true,
                    allowsOnline = true,
                    allowsInPerson = true
                )
            )
            ruleDao.insertAll(rules)

            // Prepopulate some exceptions relative to today
            val exceptionDao = database.availabilityExceptionDao()
            val today = java.time.LocalDate.now()
            
            // Let's block out next Monday for Chileshe (e.g. "Clinical Seminar Blocked Time")
            val nextMonday = today.plusDays((8 - today.dayOfWeek.value).toLong() % 7)
            val blockedMonDate = if (nextMonday == today) today.plusDays(7) else nextMonday
            
            val exceptions = listOf(
                com.example.core.database.entity.AvailabilityExceptionEntity(
                    id = "exc_chileshe_blocked_1",
                    practitionerId = "doc_chileshe_01",
                    dateIso = blockedMonDate.toString(),
                    startTime = null,
                    endTime = null,
                    type = "BLOCKED",
                    reason = "Clinical Seminar Blocked Time",
                    enabled = true
                )
            )
            exceptionDao.insertAll(exceptions)
        }
    }
}
