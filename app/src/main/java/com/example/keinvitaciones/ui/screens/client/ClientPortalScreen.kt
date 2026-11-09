package com.example.keinvitaciones.ui.screens.client

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keinvitaciones.data.model.Invitation
import com.example.keinvitaciones.data.model.RsvpSubmission
import com.example.keinvitaciones.data.repository.InvitationRepository
import com.example.keinvitaciones.ui.theme.DarkCharcoal
import com.example.keinvitaciones.ui.theme.GoldDark
import com.example.keinvitaciones.ui.theme.GoldPrimary
import com.example.keinvitaciones.ui.theme.SoftPink
import com.example.keinvitaciones.ui.theme.StatusGreen
import com.example.keinvitaciones.ui.theme.StatusRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPortalScreen(
    onOpenInvitationPreview: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { InvitationRepository.instance }
    val invitations by repository.invitations.collectAsState()
    val rsvps by repository.rsvps.collectAsState()
    val payments by repository.payments.collectAsState()

    var selectedInvId by remember { mutableStateOf(invitations.firstOrNull()?.id ?: "") }
    val currentInvitation = invitations.find { it.id == selectedInvId } ?: invitations.firstOrNull()
    val currentPayment = payments.find { it.invitationId == selectedInvId }

    val currentRsvps = remember(selectedInvId, rsvps) {
        rsvps.filter { it.invitationId == selectedInvId }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Form fields for editing
    var welcomeText by remember(currentInvitation) { mutableStateOf(currentInvitation?.welcome ?: "") }
    var messageText by remember(currentInvitation) { mutableStateOf(currentInvitation?.message ?: "") }
    var infoText by remember(currentInvitation) { mutableStateOf(currentInvitation?.info ?: "") }
    var dressCodeText by remember(currentInvitation) { mutableStateOf(currentInvitation?.dressCode ?: "") }
    var giftText by remember(currentInvitation) { mutableStateOf(currentInvitation?.gift ?: "") }
    var whatsappText by remember(currentInvitation) { mutableStateOf(currentInvitation?.whatsapp ?: "") }
    var spotifyText by remember(currentInvitation) { mutableStateOf(currentInvitation?.spotifyPlaylistUrl ?: "") }
    var instagramText by remember(currentInvitation) { mutableStateOf(currentInvitation?.instagramHashtag ?: "") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("client_portal_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
        ) {
            // Header / User Session Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCharcoal)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mi Cuenta",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "cliente@keinvitaciones.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

            // Invitation Selector
            if (invitations.isNotEmpty()) {
                item {
                    var dropdownExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = currentInvitation?.let { "${it.honoree} (${it.eventType})" } ?: "Seleccionar evento",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Mi Invitación Activa") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            invitations.forEach { inv ->
                                DropdownMenuItem(
                                    text = { Text("${inv.honoree} - ${inv.eventType}") },
                                    onClick = {
                                        selectedInvId = inv.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (currentInvitation != null) {
                // Payment & Status Pill
                item {
                    val statusLabel = when (currentPayment?.status) {
                        "pagado" -> "Pagado completo"
                        "senado" -> "Señado (50% abonado)"
                        else -> "Pendiente de seña"
                    }
                    val statusColor = when (currentPayment?.status) {
                        "pagado" -> StatusGreen
                        "senado" -> GoldDark
                        else -> Color(0xFFF57C00)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Estado de tu invitación:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (currentInvitation.active) "PUBLICADA Y ACTIVA" else "EN EDICIÓN",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (currentInvitation.active) StatusGreen else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = statusLabel,
                                    color = statusColor,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // RSVP Metrics Grid
                item {
                    val confirmedCount = currentRsvps.count { it.attending }
                    val totalGuestsConfirmed = currentRsvps.filter { it.attending }.sumOf { it.guestCount }
                    val declinedCount = currentRsvps.count { !it.attending }
                    val messagesCount = currentRsvps.count { it.message.isNotBlank() }

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Respuestas en Tiempo Real",
                            style = MaterialTheme.typography.headlineSmall,
                            color = GoldDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(
                                value = confirmedCount.toString(),
                                label = "Confirmaron",
                                color = StatusGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = totalGuestsConfirmed.toString(),
                                label = "Personas totales",
                                color = GoldDark,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(
                                value = declinedCount.toString(),
                                label = "No asisten",
                                color = StatusRed,
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                value = messagesCount.toString(),
                                label = "Mensajes",
                                color = DarkCharcoal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Quick Action Buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenInvitationPreview(currentInvitation.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ver mi tarjeta", color = Color.White)
                        }

                        Button(
                            onClick = {
                                val shareUrl = "https://ke-invitaciones.web.app/i/${currentInvitation.id}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "¡Te invito a mi evento! Mirá los detalles aquí: $shareUrl")
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartir enlace"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compartir", color = Color.White)
                        }
                    }
                }

                // Responses list
                item {
                    Text(
                        text = "Detalle de Confirmaciones",
                        style = MaterialTheme.typography.headlineSmall,
                        color = GoldDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (currentRsvps.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "Aún no has recibido confirmaciones para este evento. ¡Comparte el enlace con tus invitados!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(currentRsvps) { rsvp ->
                        RsvpItemCard(rsvp = rsvp)
                    }
                }

                // Edit Details Section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("edit_event_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Editar textos de tu invitación",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DarkCharcoal
                                )
                            }
                            Text(
                                text = "Podés actualizar estos datos cuantas veces quieras y se reflejarán de inmediato.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = welcomeText,
                                onValueChange = { welcomeText = it },
                                label = { Text("Texto de bienvenida") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                label = { Text("Mensaje personalizado") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = dressCodeText,
                                onValueChange = { dressCodeText = it },
                                label = { Text("Dress code (código de vestimenta)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = giftText,
                                onValueChange = { giftText = it },
                                label = { Text("Regalo / Datos bancarios (Alias/CBU)") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = spotifyText,
                                onValueChange = { spotifyText = it },
                                label = { Text("Enlace Playlist Spotify") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = instagramText,
                                onValueChange = { instagramText = it },
                                label = { Text("Hashtag de Instagram (#...)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = infoText,
                                onValueChange = { infoText = it },
                                label = { Text("Información adicional") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = whatsappText,
                                onValueChange = { whatsappText = it },
                                label = { Text("WhatsApp de contacto") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val updated = currentInvitation.copy(
                                        welcome = welcomeText,
                                        message = messageText,
                                        info = infoText,
                                        dressCode = dressCodeText,
                                        gift = giftText,
                                        whatsapp = whatsappText,
                                        spotifyPlaylistUrl = spotifyText.trim(),
                                        instagramHashtag = if (instagramText.startsWith("#") || instagramText.isEmpty()) instagramText.trim() else "#${instagramText.trim()}"
                                    )
                                    repository.addOrUpdateInvitation(updated)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Cambios guardados con éxito!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar cambios", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun MetricBox(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SoftPink
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = DarkCharcoal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RsvpItemCard(rsvp: RsvpSubmission) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (rsvp.attending) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (rsvp.attending) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (rsvp.attending) StatusGreen else StatusRed
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rsvp.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkCharcoal
                )
                Text(
                    text = if (rsvp.attending) "Asiste (${rsvp.guestCount} personas) • ${rsvp.timestamp}" else "No asiste • ${rsvp.timestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (rsvp.attending) StatusGreen else StatusRed,
                    fontWeight = FontWeight.SemiBold
                )
                if (rsvp.message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${rsvp.message}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
