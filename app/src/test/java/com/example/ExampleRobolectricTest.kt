package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.database.AppDatabase
import com.example.core.database.RoomCareRepository
import com.example.core.database.RoomPatientRepository
import com.example.core.database.entity.PatientEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var patientRepo: RoomPatientRepository
    private lateinit var careRepo: RoomCareRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        patientRepo = RoomPatientRepository(database.patientDao())
        careRepo = RoomCareRepository(database.careGoalDao(), database.moodCheckInDao(), database.journalEntryDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Zambia Health", appName)
    }

    @Test
    fun `patient profile persistence and updates in Room database`() = runBlocking {
        // Pre-insert patient
        database.patientDao().insertOrUpdate(
            PatientEntity(
                id = "patient_1",
                fullName = "Kondwani Tembo",
                email = "kondwani@example.zm",
                phoneNumber = "+260 97 5543210",
                selectedCity = "Lusaka",
                preferredLanguage = "English",
                avatarPresetId = "preset_amber"
            )
        )

        // Read back
        val initialPatient = patientRepo.getCurrentPatient().first()
        assertEquals("Kondwani Tembo", initialPatient?.fullName)

        // Update profile (name, phone, avatar)
        patientRepo.updatePatientProfile(
            name = "Dr. Kondwani M. Tembo",
            phone = "+260 97 9988776",
            city = "Kitwe",
            email = "ktembo@health.zm",
            emergencyName = "Bwalya Tembo",
            emergencyPhone = "+260 96 1122334",
            avatarUri = "content://media/photos/123",
            avatarPresetId = null
        )

        val updatedPatient = patientRepo.getCurrentPatient().first()
        assertEquals("Dr. Kondwani M. Tembo", updatedPatient?.fullName)
        assertEquals("+260 97 9988776", updatedPatient?.phoneNumber)
        assertEquals("Kitwe", updatedPatient?.selectedCity)
        assertEquals("content://media/photos/123", updatedPatient?.avatarUri)
    }

    @Test
    fun `care goals and mood check-in persistence in Room database`() = runBlocking {
        careRepo.addCareGoal("Morning mindful hydration", "Hydration", "Drink 500ml water upon waking")
        val goals = careRepo.getCareGoals().first()
        assertTrue(goals.any { it.title == "Morning mindful hydration" })

        careRepo.recordMoodCheckIn(5, "Grounded & Thriving", listOf("Grateful", "Calm"), "Feeling centered today")
        val moods = careRepo.getMoodCheckIns().first()
        assertTrue(moods.any { it.moodLabel == "Grounded & Thriving" && it.moodValue == 5 })
    }
}
