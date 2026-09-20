package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.core.database.dao.CareGoalDao
import com.example.core.database.dao.JournalEntryDao
import com.example.core.database.dao.MoodCheckInDao
import com.example.core.database.dao.PatientDao
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
        JournalEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun careGoalDao(): CareGoalDao
    abstract fun moodCheckInDao(): MoodCheckInDao
    abstract fun journalEntryDao(): JournalEntryDao

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
        }
    }
}
