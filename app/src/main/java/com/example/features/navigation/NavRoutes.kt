package com.example.features.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object AuthSelection : Screen("auth_selection")
    object Home : Screen("home")
    object Discover : Screen("discover")
    object Appointments : Screen("appointments")
    object Care : Screen("care")
    object Profile : Screen("profile")

    // Specialist Core Screens
    object SpecialistHome : Screen("specialist_home")
    object SpecialistAppointments : Screen("specialist_appointments")
    object SpecialistPatients : Screen("specialist_patients")
    object SpecialistMessages : Screen("specialist_messages")
    object SpecialistProfile : Screen("specialist_profile")
    object SpecialistAvailability : Screen("specialist_availability")
    object SpecialistConsultation : Screen("specialist_consultation/{appointmentId}") {
        fun createRoute(appointmentId: String) = "specialist_consultation/$appointmentId"
    }
    object SpecialistEncounterNotes : Screen("specialist_encounter/{appointmentId}") {
        fun createRoute(appointmentId: String) = "specialist_encounter/$appointmentId"
    }

    // Details & Modals
    object ProviderDetail : Screen("provider/{providerId}") {
        fun createRoute(providerId: String) = "provider/$providerId"
    }

    object Booking : Screen("booking/{providerId}") {
        fun createRoute(providerId: String) = "booking/$providerId"
    }

    object Payment : Screen("payment/{appointmentId}") {
        fun createRoute(appointmentId: String) = "payment/$appointmentId"
    }

    object Consultation : Screen("consultation/{appointmentId}") {
        fun createRoute(appointmentId: String) = "consultation/$appointmentId"
    }

    object Messaging : Screen("messaging/{conversationId}/{participantName}") {
        fun createRoute(conversationId: String, participantName: String) =
            "messaging/$conversationId/$participantName"
    }

    object Safety : Screen("safety")
    object Notifications : Screen("notifications")
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
    val isAction: Boolean = false
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "tab_home"
    ),
    BottomNavItem(
        route = Screen.Discover.route,
        title = "Discover",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        testTag = "tab_discover"
    ),
    BottomNavItem(
        route = Screen.Booking.createRoute("p1"),
        title = "Book",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        testTag = "tab_book",
        isAction = true
    ),
    BottomNavItem(
        route = Screen.Care.route,
        title = "Care",
        selectedIcon = Icons.Filled.Spa,
        unselectedIcon = Icons.Outlined.Spa,
        testTag = "tab_care"
    ),
    BottomNavItem(
        route = Screen.Profile.route,
        title = "Me",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "tab_profile"
    )
)

val specialistBottomNavItems = listOf(
    BottomNavItem(
        route = Screen.SpecialistHome.route,
        title = "Practice",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        testTag = "tab_specialist_home"
    ),
    BottomNavItem(
        route = Screen.SpecialistAppointments.route,
        title = "Schedule",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        testTag = "tab_specialist_appointments"
    ),
    BottomNavItem(
        route = Screen.SpecialistPatients.route,
        title = "Patients",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People,
        testTag = "tab_specialist_patients"
    ),
    BottomNavItem(
        route = Screen.SpecialistMessages.route,
        title = "Messages",
        selectedIcon = Icons.Filled.ChatBubble,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        testTag = "tab_specialist_messages"
    ),
    BottomNavItem(
        route = Screen.SpecialistProfile.route,
        title = "Profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        testTag = "tab_specialist_profile"
    )
)
