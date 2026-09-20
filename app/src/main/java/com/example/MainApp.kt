package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.core.design.CalmLightShapes
import com.example.ui.theme.*
import com.example.features.appointments.AppointmentsScreen
import com.example.features.appointments.AppointmentsViewModel
import com.example.features.booking.BookingScreen
import com.example.features.booking.BookingViewModel
import com.example.features.care.CareScreen
import com.example.features.care.CareViewModel
import com.example.features.consultation.ConsultationScreen
import com.example.features.consultation.ConsultationViewModel
import com.example.features.discovery.DiscoveryScreen
import com.example.features.discovery.DiscoveryViewModel
import com.example.features.home.HomeScreen
import com.example.features.home.HomeViewModel
import com.example.features.messaging.MessagingScreen
import com.example.features.messaging.MessagingViewModel
import com.example.core.model.UserRole
import com.example.core.repository.mock.AppRepositoryLocator
import com.example.features.auth.AuthSelectionScreen
import com.example.features.navigation.Screen
import com.example.features.navigation.bottomNavItems
import com.example.features.navigation.specialistBottomNavItems
import com.example.features.notifications.NotificationsScreen
import com.example.features.onboarding.OnboardingScreen
import com.example.features.payment.PaymentScreen
import com.example.features.payment.PaymentViewModel
import com.example.features.profile.ProfileScreen
import com.example.features.profile.ProfileViewModel
import com.example.features.provider.ProviderDetailScreen
import com.example.features.provider.ProviderDetailViewModel
import com.example.features.safety.SafetyScreen
import com.example.features.specialist.SpecialistAppointmentsScreen
import com.example.features.specialist.SpecialistAvailabilityScreen
import com.example.features.specialist.SpecialistConsultationScreen
import com.example.features.specialist.SpecialistEncounterNotesScreen
import com.example.features.specialist.SpecialistHomeScreen
import com.example.features.specialist.SpecialistPatientsScreen
import com.example.features.specialist.SpecialistProfileScreen

@Composable
fun HealthcareApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authRepo = AppRepositoryLocator.authRepository
    val authSession by authRepo.getActiveSession().collectAsState(initial = null)
    val isSpecialist = authSession?.role == UserRole.SPECIALIST

    val showUserBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Discover.route,
        Screen.Appointments.route,
        Screen.Care.route,
        Screen.Profile.route
    )

    val showSpecialistBottomBar = currentRoute in listOf(
        Screen.SpecialistHome.route,
        Screen.SpecialistAppointments.route,
        Screen.SpecialistPatients.route,
        Screen.SpecialistMessages.route,
        Screen.SpecialistProfile.route
    )

    val showBottomBar = if (isSpecialist) showSpecialistBottomBar else showUserBottomBar
    val activeNavItems = if (isSpecialist) specialistBottomNavItems else bottomNavItems

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CalmIvoryCanvas,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CalmLightShapes.Pill,
                        color = CalmWhite,
                        border = BorderStroke(1.dp, CalmHairline),
                        shadowElevation = 6.dp,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            activeNavItems.forEach { item ->
                                val isSelected = currentRoute == item.route

                                if (item.isAction) {
                                    // Central Prominent +Book action
                                    Surface(
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        shape = CalmLightShapes.Pill,
                                        color = CalmInkNavy,
                                        modifier = Modifier
                                            .height(44.dp)
                                            .testTag(item.testTag)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 14.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Book consultation",
                                                tint = CalmWhite,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.sp,
                                                    color = CalmWhite
                                                )
                                            )
                                        }
                                    }
                                } else if (isSelected) {
                                    // Active destination expanding into a labelled chip
                                    Surface(
                                        shape = CalmLightShapes.Pill,
                                        color = if (isSpecialist) CalmSessionsAura.copy(alpha = 0.85f) else CalmDiscoveryAura.copy(alpha = 0.75f),
                                        modifier = Modifier
                                            .height(44.dp)
                                            .testTag(item.testTag)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 14.dp)
                                        ) {
                                            Icon(
                                                imageVector = item.selectedIcon,
                                                contentDescription = item.title,
                                                tint = CalmInkNavy,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontFamily = InterFontFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.sp,
                                                    color = CalmInkNavy
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    // Inactive destination with line icon
                                    IconButton(
                                        onClick = {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .testTag(item.testTag)
                                    ) {
                                        Icon(
                                            imageVector = item.unselectedIcon,
                                            contentDescription = item.title,
                                            tint = CalmSlate,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.AuthSelection.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Selection Screen
            composable(Screen.AuthSelection.route) {
                AuthSelectionScreen(
                    onLoginSuccess = { role ->
                        if (role == UserRole.SPECIALIST) {
                            navController.navigate(Screen.SpecialistHome.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onCompleteOnboarding = { _ ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Home
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel()
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToDiscover = { specialty ->
                        navController.navigate(Screen.Discover.route)
                    },
                    onNavigateToAppointments = {
                        navController.navigate(Screen.Appointments.route)
                    },
                    onNavigateToCare = {
                        navController.navigate(Screen.Care.route)
                    },
                    onNavigateToSafety = {
                        navController.navigate(Screen.Safety.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onJoinConsultation = { appointmentId ->
                        navController.navigate(Screen.Consultation.createRoute(appointmentId))
                    },
                    onOpenMessaging = { conversationId, practitionerName ->
                        navController.navigate(Screen.Messaging.createRoute(conversationId, practitionerName))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onSwitchRole = {
                        navController.navigate(Screen.AuthSelection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Discover
            composable(Screen.Discover.route) {
                val discoveryViewModel: DiscoveryViewModel = viewModel()
                DiscoveryScreen(
                    viewModel = discoveryViewModel,
                    onSelectProvider = { providerId ->
                        navController.navigate(Screen.ProviderDetail.createRoute(providerId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Provider Profile
            composable(
                route = Screen.ProviderDetail.route,
                arguments = listOf(navArgument("providerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
                val providerViewModel = remember(providerId) { ProviderDetailViewModel(providerId) }
                ProviderDetailScreen(
                    viewModel = providerViewModel,
                    onBookAppointment = { pId ->
                        navController.navigate(Screen.Booking.createRoute(pId))
                    },
                    onMessageProvider = { pId, pName ->
                        navController.navigate(Screen.Messaging.createRoute("conv_$pId", pName))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Booking
            composable(
                route = Screen.Booking.route,
                arguments = listOf(navArgument("providerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
                val bookingViewModel = remember(providerId) { BookingViewModel(providerId) }
                BookingScreen(
                    viewModel = bookingViewModel,
                    onProceedToPayment = { appointmentId ->
                        navController.navigate(Screen.Payment.createRoute(appointmentId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Payment
            composable(
                route = Screen.Payment.route,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                val paymentViewModel = remember(appointmentId) { PaymentViewModel(appointmentId) }
                PaymentScreen(
                    viewModel = paymentViewModel,
                    onViewAppointment = {
                        navController.navigate(Screen.Appointments.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Appointments
            composable(Screen.Appointments.route) {
                val appointmentsViewModel: AppointmentsViewModel = viewModel()
                AppointmentsScreen(
                    viewModel = appointmentsViewModel,
                    onJoinConsultation = { appointmentId ->
                        navController.navigate(Screen.Consultation.createRoute(appointmentId))
                    },
                    onMessagePractitioner = { convId, name ->
                        navController.navigate(Screen.Messaging.createRoute(convId, name))
                    },
                    onNavigateToDiscovery = {
                        navController.navigate(Screen.Discover.route)
                    }
                )
            }

            // Care
            composable(Screen.Care.route) {
                val careViewModel: CareViewModel = viewModel()
                CareScreen(viewModel = careViewModel)
            }

            // Profile
            composable(Screen.Profile.route) {
                val profileViewModel: ProfileViewModel = viewModel()
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToSafety = { navController.navigate(Screen.Safety.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onRestartOnboarding = {
                        navController.navigate(Screen.AuthSelection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Consultation Room
            composable(
                route = Screen.Consultation.route,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                val consultationViewModel = remember(appointmentId) { ConsultationViewModel(appointmentId) }
                ConsultationScreen(
                    viewModel = consultationViewModel,
                    onOpenChat = { convId, name ->
                        navController.navigate(Screen.Messaging.createRoute(convId, name))
                    },
                    onLeaveConsultation = { navController.popBackStack() }
                )
            }

            // Messaging
            composable(
                route = Screen.Messaging.route,
                arguments = listOf(
                    navArgument("conversationId") { type = NavType.StringType },
                    navArgument("participantName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
                val pName = backStackEntry.arguments?.getString("participantName") ?: "Practitioner"
                val messagingViewModel = remember(convId) { MessagingViewModel(convId, pName) }
                MessagingScreen(
                    viewModel = messagingViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Safety / Crisis Help
            composable(Screen.Safety.route) {
                SafetyScreen(onBack = { navController.popBackStack() })
            }

            // Notifications
            composable(Screen.Notifications.route) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }

            // Specialist Screens
            composable(Screen.SpecialistHome.route) {
                SpecialistHomeScreen(
                    onNavigateToSchedule = { navController.navigate(Screen.SpecialistAppointments.route) },
                    onNavigateToPatients = { navController.navigate(Screen.SpecialistPatients.route) },
                    onNavigateToAvailability = { navController.navigate(Screen.SpecialistAvailability.route) },
                    onStartConsultation = { appointmentId ->
                        navController.navigate(Screen.SpecialistConsultation.createRoute(appointmentId))
                    },
                    onWriteEncounterNotes = { appointmentId ->
                        navController.navigate(Screen.SpecialistEncounterNotes.createRoute(appointmentId))
                    },
                    onOpenMessages = { patientId, patientName ->
                        navController.navigate(Screen.Messaging.createRoute("conv_$patientId", patientName))
                    }
                )
            }

            composable(Screen.SpecialistAppointments.route) {
                SpecialistAppointmentsScreen(
                    onStartConsultation = { appointmentId ->
                        navController.navigate(Screen.SpecialistConsultation.createRoute(appointmentId))
                    },
                    onWriteEncounterNotes = { appointmentId ->
                        navController.navigate(Screen.SpecialistEncounterNotes.createRoute(appointmentId))
                    },
                    onOpenAvailability = { navController.navigate(Screen.SpecialistAvailability.route) }
                )
            }

            composable(Screen.SpecialistPatients.route) {
                SpecialistPatientsScreen(
                    onOpenPatientChat = { patientId, patientName ->
                        navController.navigate(Screen.Messaging.createRoute("conv_$patientId", patientName))
                    }
                )
            }

            composable(Screen.SpecialistMessages.route) {
                // Reuse the robust messaging screen for clinical consultations
                val messagingViewModel = remember { MessagingViewModel("conv_pat_01", "Kondwani Tembo") }
                MessagingScreen(
                    viewModel = messagingViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SpecialistProfile.route) {
                SpecialistProfileScreen(
                    onNavigateToAvailability = { navController.navigate(Screen.SpecialistAvailability.route) },
                    onLogout = {
                        navController.navigate(Screen.AuthSelection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.SpecialistAvailability.route) {
                SpecialistAvailabilityScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.SpecialistConsultation.route,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                SpecialistConsultationScreen(
                    appointmentId = appointmentId,
                    onEndConsultation = { aptId ->
                        navController.navigate(Screen.SpecialistEncounterNotes.createRoute(aptId)) {
                            popUpTo(Screen.SpecialistConsultation.route) { inclusive = true }
                        }
                    },
                    onOpenEncounterNotes = { aptId ->
                        navController.navigate(Screen.SpecialistEncounterNotes.createRoute(aptId))
                    }
                )
            }

            composable(
                route = Screen.SpecialistEncounterNotes.route,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                SpecialistEncounterNotesScreen(
                    appointmentId = appointmentId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
