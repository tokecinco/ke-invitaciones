package com.example.keinvitaciones.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.keinvitaciones.ui.screens.admin.AdminDashboardScreen
import com.example.keinvitaciones.ui.screens.catalog.CatalogScreen
import com.example.keinvitaciones.ui.screens.client.ClientPortalScreen
import com.example.keinvitaciones.ui.screens.invitation.InvitationExperienceScreen
import com.example.keinvitaciones.ui.theme.DarkCharcoal
import com.example.keinvitaciones.ui.theme.GoldPrimary

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    CATALOG("Inicio", Icons.Filled.Home, Icons.Outlined.Home, "nav_catalog"),
    INVITATION("Demostración", Icons.Filled.Mail, Icons.Outlined.Mail, "nav_invitation"),
    CLIENT("Mi Cuenta", Icons.Filled.Person, Icons.Outlined.Person, "nav_client"),
    ADMIN("Gestión", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings, "nav_admin")
}

@Composable
fun MainContainerScreen() {
    var currentDestination by remember { mutableStateOf(AppDestination.CATALOG) }
    var targetInvitationId by remember { mutableStateOf("inv-valen-15") }

    // Custom backstack handling
    BackHandler(enabled = currentDestination != AppDestination.CATALOG) {
        currentDestination = AppDestination.CATALOG
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 700.dp

        if (isWideScreen) {
            // Tablet / Desktop layout with NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = DarkCharcoal,
                    contentColor = Color.White,
                    header = {
                        Text(
                            text = "KE",
                            style = MaterialTheme.typography.titleLarge,
                            color = GoldPrimary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                ) {
                    AppDestination.values().forEach { destination ->
                        NavigationRailItem(
                            selected = currentDestination == destination,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == destination) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.title
                                )
                            },
                            label = { Text(destination.title) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = DarkCharcoal,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            ),
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    AppScreenContent(
                        currentDestination = currentDestination,
                        targetInvitationId = targetInvitationId,
                        onNavigateToDemo = { invId ->
                            targetInvitationId = invId
                            currentDestination = AppDestination.INVITATION
                        }
                    )
                }
            }
        } else {
            // Handheld Mobile Layout with NavigationBar
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkCharcoal,
                        contentColor = Color.White
                    ) {
                        AppDestination.values().forEach { destination ->
                            NavigationBarItem(
                                selected = currentDestination == destination,
                                onClick = { currentDestination = destination },
                                icon = {
                                    Icon(
                                        imageVector = if (currentDestination == destination) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = destination.title
                                    )
                                },
                                label = { Text(destination.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkCharcoal,
                                    selectedTextColor = GoldPrimary,
                                    indicatorColor = GoldPrimary,
                                    unselectedIconColor = Color.LightGray,
                                    unselectedTextColor = Color.LightGray
                                ),
                                modifier = Modifier.testTag(destination.testTag)
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AppScreenContent(
                        currentDestination = currentDestination,
                        targetInvitationId = targetInvitationId,
                        onNavigateToDemo = { invId ->
                            targetInvitationId = invId
                            currentDestination = AppDestination.INVITATION
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppScreenContent(
    currentDestination: AppDestination,
    targetInvitationId: String,
    onNavigateToDemo: (String) -> Unit
) {
    Crossfade(
        targetState = currentDestination,
        label = "screen_crossfade"
    ) { destination ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 750.dp)
        ) {
            when (destination) {
                AppDestination.CATALOG -> {
                    CatalogScreen(
                        onNavigateToDemo = onNavigateToDemo
                    )
                }
                AppDestination.INVITATION -> {
                    InvitationExperienceScreen(
                        initialInvitationId = targetInvitationId
                    )
                }
                AppDestination.CLIENT -> {
                    ClientPortalScreen(
                        onOpenInvitationPreview = onNavigateToDemo
                    )
                }
                AppDestination.ADMIN -> {
                    AdminDashboardScreen(
                        onPreviewInvitation = onNavigateToDemo
                    )
                }
            }
        }
    }
}
