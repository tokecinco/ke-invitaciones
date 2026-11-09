package com.example.keinvitaciones.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keinvitaciones.data.model.Guest
import com.example.keinvitaciones.data.model.Invitation
import com.example.keinvitaciones.data.model.ItineraryItem
import com.example.keinvitaciones.data.model.PaymentRecord
import com.example.keinvitaciones.data.repository.InvitationRepository
import com.example.keinvitaciones.ui.theme.CreamBackground
import com.example.keinvitaciones.ui.theme.DarkCharcoal
import com.example.keinvitaciones.ui.theme.GoldDark
import com.example.keinvitaciones.ui.theme.GoldPrimary
import com.example.keinvitaciones.ui.theme.SoftPink
import com.example.keinvitaciones.ui.theme.StatusGreen
import com.example.keinvitaciones.ui.theme.StatusRed
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AdminDashboardScreen(
    onPreviewInvitation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { InvitationRepository.instance }
    val invitations by repository.invitations.collectAsState()
    val guests by repository.guests.collectAsState()
    val rsvps by repository.rsvps.collectAsState()
    val payments by repository.payments.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Invitaciones", "Invitados", "Recepción / QR", "Pagos", "Código QR")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Create / Edit Invitation Dialog state
    var editingInvitation by remember { mutableStateOf<Invitation?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Payment edit dialog state
    var editingPayment by remember { mutableStateOf<PaymentRecord?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("admin_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkCharcoal,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "PANEL DE CONTROL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoldPrimary
                    )
                    Text(
                        text = "KE Invitaciones Admin",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = DarkCharcoal,
                        contentColor = GoldPrimary,
                        edgePadding = 0.dp
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, color = if (selectedTabIndex == index) GoldPrimary else Color.LightGray) }
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> AdminMetricsTab(
                    invitations = invitations,
                    rsvps = rsvps,
                    payments = payments,
                    onOpenInvitation = onPreviewInvitation,
                    onCreateNew = {
                        editingInvitation = null
                        showCreateDialog = true
                    },
                    onCopySummary = {
                        val totalCreated = invitations.size
                        val activeCount = invitations.count { it.active }
                        val totalRevenue = payments.sumOf { it.depositAmount }
                        val totalPending = payments.sumOf { it.balanceRemaining }
                        val totalConfirmed = rsvps.filter { it.attending }.sumOf { it.guestCount }
                        val summaryText = """
                            KE INVITACIONES - REPORTE ADMINISTRATIVO
                            Total Invitaciones: $totalCreated ($activeCount activas)
                            Total Cobrado: $${String.format("%,.0f", totalRevenue).replace(',', '.')}
                            Saldo por Cobrar: $${String.format("%,.0f", totalPending).replace(',', '.')}
                            Total Asistentes Confirmados: $totalConfirmed personas
                        """.trimIndent()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Reporte KE Invitaciones", summaryText))
                        scope.launch { snackbarHostState.showSnackbar("¡Reporte copiado al portapapeles!") }
                    }
                )
                1 -> InvitationsManagerTab(
                    invitations = invitations,
                    onToggleActive = { repository.toggleInvitationActive(it) },
                    onEdit = {
                        editingInvitation = it
                        showCreateDialog = true
                    },
                    onDelete = { repository.deleteInvitation(it) },
                    onPreview = onPreviewInvitation,
                    onCreateNew = {
                        editingInvitation = null
                        showCreateDialog = true
                    }
                )
                2 -> GuestsManagerTab(
                    invitations = invitations,
                    guests = guests,
                    onAddGuest = { invId, name, seats -> repository.addGuest(invId, name, seats) },
                    onAddBulk = { invId, raw -> repository.addBulkGuests(invId, raw) },
                    onDeleteGuest = { repository.deleteGuest(it) },
                    onCheckInGuest = { repository.checkInGuest(it) },
                    onCopyLink = { guestLink ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Enlace Invitado", guestLink))
                        scope.launch { snackbarHostState.showSnackbar("¡Enlace copiado al portapapeles!") }
                    },
                    onExportReport = { report ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Lista de Invitados", report))
                        scope.launch { snackbarHostState.showSnackbar("¡Lista de invitados copiada!") }
                    }
                )
                3 -> ReceptionCheckInTab(
                    invitations = invitations,
                    guests = guests,
                    onToggleCheckIn = { repository.checkInGuest(it) }
                )
                4 -> PaymentsManagerTab(
                    payments = payments,
                    invitations = invitations,
                    onEditPayment = { editingPayment = it }
                )
                5 -> QrCodeManagerTab(
                    invitations = invitations,
                    onShareLink = { link ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Accedé a la invitación digital con este enlace: $link")
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir enlace QR"))
                    }
                )
            }
        }

        // Create / Edit Invitation Dialog
        if (showCreateDialog) {
            InvitationEditDialog(
                invitation = editingInvitation,
                onDismiss = {
                    showCreateDialog = false
                    editingInvitation = null
                },
                onSave = { updated ->
                    repository.addOrUpdateInvitation(updated)
                    showCreateDialog = false
                    editingInvitation = null
                    scope.launch { snackbarHostState.showSnackbar("¡Invitación guardada exitosamente!") }
                }
            )
        }

        // Edit Payment Dialog
        if (editingPayment != null) {
            PaymentEditDialog(
                payment = editingPayment!!,
                onDismiss = { editingPayment = null },
                onSave = { pId, st, dep, tot, note ->
                    repository.updatePayment(pId, st, dep, tot, note)
                    editingPayment = null
                    scope.launch { snackbarHostState.showSnackbar("¡Pago actualizado!") }
                }
            )
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
private fun AdminMetricsTab(
    invitations: List<Invitation>,
    rsvps: List<com.example.keinvitaciones.data.model.RsvpSubmission>,
    payments: List<PaymentRecord>,
    onOpenInvitation: (String) -> Unit,
    onCreateNew: () -> Unit,
    onCopySummary: () -> Unit
) {
    val totalCreated = invitations.size
    val activeCount = invitations.count { it.active }
    val salesCount = payments.count { it.status == "senado" || it.status == "pagado" }
    val pendingPaymentCount = payments.count { it.status == "pendiente" }
    val totalRevenue = payments.sumOf { it.depositAmount }
    val totalPendingBalance = payments.sumOf { it.balanceRemaining }
    val totalRsvps = rsvps.size
    val confirmedAttendees = rsvps.filter { it.attending }.sumOf { it.guestCount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumen de Rendimiento",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldDark
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onCopySummary,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exportar", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onCreateNew,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Crear", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Stat Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(title = "Invitaciones", value = totalCreated.toString(), subtitle = "$activeCount activas", color = DarkCharcoal, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Ventas", value = salesCount.toString(), subtitle = "$pendingPaymentCount pendientes", color = GoldDark, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(title = "Cobrado", value = "$${String.format("%,.0f", totalRevenue).replace(',', '.')}", subtitle = "Ingresos confirmados", color = StatusGreen, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Saldo", value = "$${String.format("%,.0f", totalPendingBalance).replace(',', '.')}", subtitle = "Por cobrar", color = Color(0xFFF57C00), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(title = "Respuestas", value = totalRsvps.toString(), subtitle = "Confirmaciones", color = DarkCharcoal, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Asistentes", value = confirmedAttendees.toString(), subtitle = "Personas totales", color = GoldDark, modifier = Modifier.weight(1f))
            }
        }

        // Upcoming Events
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Próximos Eventos",
                style = MaterialTheme.typography.headlineSmall,
                color = GoldDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(invitations.sortedBy { it.date }) { inv ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onOpenInvitation(inv.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${inv.honoree} - ${inv.eventType}",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Fecha: ${inv.date} a las ${inv.time} hs • ${inv.venue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        color = if (inv.active) StatusGreen.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (inv.active) "Activa" else "Inactiva",
                            color = if (inv.active) StatusGreen else Color.DarkGray,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = DarkCharcoal)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = color))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InvitationsManagerTab(
    invitations: List<Invitation>,
    onToggleActive: (String) -> Unit,
    onEdit: (Invitation) -> Unit,
    onDelete: (String) -> Unit,
    onPreview: (String) -> Unit,
    onCreateNew: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, invitations) {
        if (searchQuery.isBlank()) invitations
        else invitations.filter {
            it.honoree.contains(searchQuery, ignoreCase = true) ||
                    it.eventType.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gestión de Invitaciones",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldDark
                )
                Button(
                    onClick = onCreateNew,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("btn_nueva_invitacion")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nueva", color = Color.White)
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por nombre o evento") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }

        items(filtered) { inv ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = inv.honoree,
                                style = MaterialTheme.typography.titleLarge,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "${inv.eventType} • Paquete: ${inv.packageType.uppercase()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (inv.active) "Activa" else "Inactiva",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (inv.active) StatusGreen else Color.Gray,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Switch(
                                checked = inv.active,
                                onCheckedChange = { onToggleActive(inv.id) },
                                colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📅 ${inv.date} ${inv.time} hs • 📍 ${inv.venue}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { onPreview(inv.id) }) {
                            Text("Ver demo", color = GoldDark)
                        }
                        IconButton(onClick = { onEdit(inv) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = DarkCharcoal)
                        }
                        IconButton(onClick = { onDelete(inv.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusRed)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestsManagerTab(
    invitations: List<Invitation>,
    guests: List<Guest>,
    onAddGuest: (String, String, Int) -> Unit,
    onAddBulk: (String, String) -> Int,
    onDeleteGuest: (String) -> Unit,
    onCheckInGuest: (String) -> Unit,
    onCopyLink: (String) -> Unit,
    onExportReport: (String) -> Unit
) {
    var selectedInvId by remember { mutableStateOf(invitations.firstOrNull()?.id ?: "") }
    val currentGuests = remember(selectedInvId, guests) {
        guests.filter { it.invitationId == selectedInvId }
    }

    var singleName by remember { mutableStateOf("") }
    var singleSeats by remember { mutableIntStateOf(2) }
    var bulkText by remember { mutableStateOf("") }
    var showBulkDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pases & Gestión de Invitados",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldDark
                )

                OutlinedButton(
                    onClick = {
                        val report = buildString {
                            appendLine("LISTA DE INVITADOS")
                            currentGuests.forEach { g ->
                                val status = when (g.attending) {
                                    true -> "ASISTE (${g.confirmedGuests} pers.)"
                                    false -> "NO ASISTE"
                                    null -> "PENDIENTE"
                                }
                                val check = if (g.checkedIn) "[INGRESÓ ${g.checkedInAt}]" else "[NO INGRESÓ]"
                                appendLine("- ${g.name} | Pases: ${g.seats} | $status | $check | Código: ${g.ticketCode}")
                            }
                        }
                        onExportReport(report)
                    },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exportar lista", fontSize = 12.sp)
                }
            }

            // Selector of invitation
            var dropdownExpanded by remember { mutableStateOf(false) }
            val currentInv = invitations.find { it.id == selectedInvId }

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                OutlinedTextField(
                    value = currentInv?.let { "${it.honoree} (${it.eventType})" } ?: "Seleccionar evento",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Evento seleccionado") },
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

            // Quick Stats
            val totalGuests = currentGuests.size
            val totalSeats = currentGuests.sumOf { it.seats }
            val respondedCount = currentGuests.count { it.attending != null }
            val checkedInCount = currentGuests.count { it.checkedIn }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(title = "Invitados", value = totalGuests.toString(), subtitle = "Familias / Grupos", color = DarkCharcoal, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Lugares", value = totalSeats.toString(), subtitle = "Pases reservados", color = GoldDark, modifier = Modifier.weight(1f))
                AdminStatCard(title = "Ingresaron", value = checkedInCount.toString(), subtitle = "Check-in realizado", color = StatusGreen, modifier = Modifier.weight(1f))
            }

            // Add Single Guest Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Agregar Invitado Individual",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = singleName,
                        onValueChange = { singleName = it },
                        label = { Text("Nombre o Familia (ej: Familia Gómez)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lugares reservados: $singleSeats", style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { if (singleSeats > 1) singleSeats-- },
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("-") }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { singleSeats++ },
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("+") }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (singleName.isNotBlank() && selectedInvId.isNotBlank()) {
                                    onAddGuest(selectedInvId, singleName.trim(), singleSeats)
                                    singleName = ""
                                    singleSeats = 2
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Agregar", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { showBulkDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Carga masiva")
                        }
                    }
                }
            }

            Text(
                text = "Lista de Invitados & Pases QR (${currentGuests.size})",
                style = MaterialTheme.typography.titleLarge,
                color = DarkCharcoal,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(currentGuests) { guest ->
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = guest.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkCharcoal
                            )
                            if (guest.ticketCode.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = SoftPink,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = guest.ticketCode,
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                        color = DarkCharcoal,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Lugares: ${guest.seats} • " + when (guest.attending) {
                                true -> "Confirmó (${guest.confirmedGuests} pers.)"
                                false -> "No asiste"
                                null -> "Pendiente"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = when (guest.attending) {
                                true -> StatusGreen
                                false -> StatusRed
                                null -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = FontWeight.SemiBold
                        )

                        if (guest.checkedIn) {
                            Text(
                                text = "✓ Ingresó a la fiesta (${guest.checkedInAt})",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Check-in toggle button
                        IconButton(onClick = { onCheckInGuest(guest.id) }) {
                            Icon(
                                imageVector = if (guest.checkedIn) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = "Check-in recepción",
                                tint = if (guest.checkedIn) StatusGreen else Color.Gray
                            )
                        }

                        val personalizedLink = "https://ke-invitaciones.web.app/i/${guest.invitationId}?g=${guest.id}"
                        IconButton(onClick = { onCopyLink(personalizedLink) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copiar enlace personalizado", tint = GoldDark)
                        }
                        IconButton(onClick = { onDeleteGuest(guest.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusRed)
                        }
                    }
                }
            }
        }
    }

    if (showBulkDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDialog = false },
            title = { Text("Carga masiva de invitados") },
            text = {
                Column {
                    Text(
                        text = "Escribí un invitado por línea con el formato:\nNombre; cantidad\n\nEjemplo:\nFamilia Pérez; 4\nJuan Gómez; 1\nAna y Luis; 2",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = bulkText,
                        onValueChange = { bulkText = it },
                        placeholder = { Text("Pegar lista aquí...") },
                        minLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (bulkText.isNotBlank() && selectedInvId.isNotBlank()) {
                            onAddBulk(selectedInvId, bulkText)
                            bulkText = ""
                            showBulkDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Cargar lista")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReceptionCheckInTab(
    invitations: List<Invitation>,
    guests: List<Guest>,
    onToggleCheckIn: (String) -> Unit
) {
    var selectedInvId by remember { mutableStateOf(invitations.firstOrNull()?.id ?: "") }
    var searchQuery by remember { mutableStateOf("") }

    val currentGuests = remember(selectedInvId, guests) {
        guests.filter { it.invitationId == selectedInvId }
    }

    val filteredGuests = remember(searchQuery, currentGuests) {
        if (searchQuery.isBlank()) currentGuests
        else currentGuests.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.ticketCode.contains(searchQuery, ignoreCase = true)
        }
    }

    val checkedInCount = currentGuests.count { it.checkedIn }
    val totalGuests = currentGuests.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Text(
                text = "Control de Acceso & Recepción (Puerta)",
                style = MaterialTheme.typography.headlineSmall,
                color = GoldDark,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Verificá el código QR o el nombre del invitado y registrá su ingreso al salón en vivo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Progress Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCharcoal)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Ingresaron al salón", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                        Text(
                            text = "$checkedInCount de $totalGuests grupos",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                    }

                    Icon(
                        Icons.Default.QrCode,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por nombre o código de ticket (ej: TKT-...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )
        }

        items(filteredGuests) { guest ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (guest.checkedIn) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (guest.checkedIn) 1.5.dp else 1.dp,
                    color = if (guest.checkedIn) StatusGreen else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = guest.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = GoldPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = guest.ticketCode,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = DarkCharcoal,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Lugares: ${guest.seats} • ${if (guest.checkedIn) "Ingresó (${guest.checkedInAt})" else "Esperando en puerta"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (guest.checkedIn) StatusGreen else Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { onToggleCheckIn(guest.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (guest.checkedIn) StatusGreen else GoldPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (guest.checkedIn) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (guest.checkedIn) "Ingresado" else "Dar Ingreso",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentsManagerTab(
    payments: List<PaymentRecord>,
    invitations: List<Invitation>,
    onEditPayment: (PaymentRecord) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            Text(
                text = "Control de Pagos y Señas",
                style = MaterialTheme.typography.headlineSmall,
                color = GoldDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(payments) { payment ->
            val inv = invitations.find { it.id == payment.invitationId }
            val statusColor = when (payment.status) {
                "pagado" -> StatusGreen
                "senado" -> GoldDark
                else -> Color(0xFFF57C00)
            }
            val statusLabel = when (payment.status) {
                "pagado" -> "PAGADO"
                "senado" -> "SEÑADO"
                else -> "PENDIENTE"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = inv?.honoree ?: payment.invitationId,
                                style = MaterialTheme.typography.titleLarge,
                                color = DarkCharcoal
                            )
                            Text(
                                text = inv?.eventType ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total", style = MaterialTheme.typography.labelSmall)
                            Text("$${String.format("%,.0f", payment.totalAmount).replace(',', '.')}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Seña abonada", style = MaterialTheme.typography.labelSmall)
                            Text("$${String.format("%,.0f", payment.depositAmount).replace(',', '.')}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = StatusGreen)
                        }
                        Column {
                            Text("Saldo restante", style = MaterialTheme.typography.labelSmall)
                            Text("$${String.format("%,.0f", payment.balanceRemaining).replace(',', '.')}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (payment.balanceRemaining > 0) Color(0xFFF57C00) else StatusGreen)
                        }
                    }

                    if (payment.note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nota: ${payment.note}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { onEditPayment(payment) },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Actualizar pago")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrCodeManagerTab(
    invitations: List<Invitation>,
    onShareLink: (String) -> Unit
) {
    var selectedInvId by remember { mutableStateOf(invitations.firstOrNull()?.id ?: "") }
    val currentInv = invitations.find { it.id == selectedInvId } ?: invitations.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Generador de Códigos QR",
            style = MaterialTheme.typography.headlineSmall,
            color = GoldDark,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Dropdown
        var dropdownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            OutlinedTextField(
                value = currentInv?.let { "${it.honoree} (${it.eventType})" } ?: "Seleccionar",
                onValueChange = {},
                readOnly = true,
                label = { Text("Invitación para generar QR") },
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

        if (currentInv != null) {
            val fullUrl = "https://ke-invitaciones.web.app/i/${currentInv.id}"

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .border(2.dp, GoldPrimary, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "KE INVITACIONES",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                        color = GoldDark,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                            .background(Color(0xFFFAFAFA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = "Código QR",
                                tint = DarkCharcoal,
                                modifier = Modifier.size(130.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentInv.honoree,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkCharcoal
                    )
                    Text(
                        text = currentInv.eventType,
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = fullUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onShareLink(fullUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compartir enlace y QR", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvitationEditDialog(
    invitation: Invitation?,
    onDismiss: () -> Unit,
    onSave: (Invitation) -> Unit
) {
    val isNew = invitation == null

    var honoree by remember { mutableStateOf(invitation?.honoree ?: "") }
    var eventType by remember { mutableStateOf(invitation?.eventType ?: "") }
    var packageType by remember { mutableStateOf(invitation?.packageType ?: "premium") }
    var template by remember { mutableStateOf(invitation?.template ?: "quince") }
    var date by remember { mutableStateOf(invitation?.date ?: "2026-12-25") }
    var time by remember { mutableStateOf(invitation?.time ?: "21:00") }
    var venue by remember { mutableStateOf(invitation?.venue ?: "") }
    var address by remember { mutableStateOf(invitation?.address ?: "") }
    var welcome by remember { mutableStateOf(invitation?.welcome ?: "") }
    var message by remember { mutableStateOf(invitation?.message ?: "") }
    var dressCode by remember { mutableStateOf(invitation?.dressCode ?: "") }
    var gift by remember { mutableStateOf(invitation?.gift ?: "") }
    var whatsapp by remember { mutableStateOf(invitation?.whatsapp ?: "") }
    var spotifyUrl by remember { mutableStateOf(invitation?.spotifyPlaylistUrl ?: "") }
    var instagramHashtag by remember { mutableStateOf(invitation?.instagramHashtag ?: "") }

    // Itinerary items list
    var itineraryItems by remember {
        mutableStateOf(
            invitation?.itinerary?.ifEmpty {
                listOf(
                    ItineraryItem("21:30", "Recepción de Invitados", "Cocktail de bienvenida"),
                    ItineraryItem("22:30", "Ingreso & Vals", "Entrada formal y vals"),
                    ItineraryItem("23:15", "Cena Principal", "Plato gourmet"),
                    ItineraryItem("01:15", "Apertura de Pista", "DJ y cotillón")
                )
            } ?: listOf(
                ItineraryItem("21:30", "Recepción de Invitados", "Cocktail de bienvenida"),
                ItineraryItem("22:30", "Ingreso & Vals", "Entrada formal y vals"),
                ItineraryItem("23:15", "Cena Principal", "Plato gourmet"),
                ItineraryItem("01:15", "Apertura de Pista", "DJ y cotillón")
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Nueva Invitación" else "Editar Invitación") },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(
                        value = honoree,
                        onValueChange = { honoree = it },
                        label = { Text("Nombre del homenajeado *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_form_honoree")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventType,
                        onValueChange = { eventType = it },
                        label = { Text("Tipo de evento (ej: Mis 15 Años) *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { packageType = "basico" },
                            colors = if (packageType == "basico") ButtonDefaults.outlinedButtonColors(containerColor = GoldPrimary, contentColor = Color.White) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) { Text("Básico") }
                        OutlinedButton(
                            onClick = { packageType = "premium" },
                            colors = if (packageType == "premium") ButtonDefaults.outlinedButtonColors(containerColor = GoldPrimary, contentColor = Color.White) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f)
                        ) { Text("Premium") }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Fecha (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Hora (HH:mm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Salón / Lugar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Spotify & Redes Sociales:", style = MaterialTheme.typography.titleSmall, color = GoldDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = spotifyUrl,
                        onValueChange = { spotifyUrl = it },
                        label = { Text("Enlace Playlist de Spotify") },
                        placeholder = { Text("https://open.spotify.com/playlist/...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = instagramHashtag,
                        onValueChange = { instagramHashtag = it },
                        label = { Text("Hashtag de Instagram") },
                        placeholder = { Text("#ValenMis15") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = welcome,
                        onValueChange = { welcome = it },
                        label = { Text("Texto de bienvenida") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Mensaje personalizado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dressCode,
                        onValueChange = { dressCode = it },
                        label = { Text("Dress Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gift,
                        onValueChange = { gift = it },
                        label = { Text("Datos regalo / CBU / Alias") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp (549...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (honoree.isNotBlank()) {
                        val result = invitation?.copy(
                            honoree = honoree.trim(),
                            eventType = eventType.trim().ifEmpty { "Evento" },
                            packageType = packageType,
                            template = template,
                            date = date,
                            time = time,
                            venue = venue,
                            address = address,
                            welcome = welcome,
                            message = message,
                            dressCode = dressCode,
                            gift = gift,
                            whatsapp = whatsapp,
                            spotifyPlaylistUrl = spotifyUrl.trim(),
                            instagramHashtag = if (instagramHashtag.startsWith("#") || instagramHashtag.isEmpty()) instagramHashtag.trim() else "#${instagramHashtag.trim()}",
                            itinerary = itineraryItems
                        ) ?: Invitation(
                            id = "inv-" + UUID.randomUUID().toString().take(8),
                            honoree = honoree.trim(),
                            eventType = eventType.trim().ifEmpty { "Evento" },
                            packageType = packageType,
                            template = template,
                            date = date,
                            time = time,
                            venue = venue,
                            address = address,
                            welcome = welcome,
                            message = message,
                            dressCode = dressCode,
                            gift = gift,
                            whatsapp = whatsapp,
                            spotifyPlaylistUrl = spotifyUrl.trim(),
                            instagramHashtag = if (instagramHashtag.startsWith("#") || instagramHashtag.isEmpty()) instagramHashtag.trim() else "#${instagramHashtag.trim()}",
                            itinerary = itineraryItems,
                            photos = listOf("https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800&q=80")
                        )
                        onSave(result)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier.testTag("btn_guardar_invitacion")
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun PaymentEditDialog(
    payment: PaymentRecord,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Double, String) -> Unit
) {
    var status by remember { mutableStateOf(payment.status) }
    var depositText by remember { mutableStateOf(payment.depositAmount.toInt().toString()) }
    var totalText by remember { mutableStateOf(payment.totalAmount.toInt().toString()) }
    var note by remember { mutableStateOf(payment.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Pago") },
        text = {
            Column {
                OutlinedTextField(
                    value = totalText,
                    onValueChange = { totalText = it },
                    label = { Text("Precio Total ($)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = depositText,
                    onValueChange = { depositText = it },
                    label = { Text("Seña abonada ($)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Estado:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("pendiente" to "Pendiente", "senado" to "Señado", "pagado" to "Pagado").forEach { (stKey, stLabel) ->
                        OutlinedButton(
                            onClick = { status = stKey },
                            colors = if (status == stKey) ButtonDefaults.outlinedButtonColors(containerColor = GoldPrimary, contentColor = Color.White) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(stLabel, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Notas / Comprobante") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tot = totalText.toDoubleOrNull() ?: payment.totalAmount
                    val dep = depositText.toDoubleOrNull() ?: payment.depositAmount
                    onSave(payment.id, status, dep, tot, note)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
