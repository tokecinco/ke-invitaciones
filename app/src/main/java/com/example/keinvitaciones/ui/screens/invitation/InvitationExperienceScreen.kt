package com.example.keinvitaciones.ui.screens.invitation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.keinvitaciones.data.model.Guest
import com.example.keinvitaciones.data.model.Invitation
import com.example.keinvitaciones.data.model.ItineraryItem
import com.example.keinvitaciones.data.repository.InvitationRepository
import com.example.keinvitaciones.ui.theme.CreamBackground
import com.example.keinvitaciones.ui.theme.DarkCharcoal
import com.example.keinvitaciones.ui.theme.GoldDark
import com.example.keinvitaciones.ui.theme.GoldPrimary
import com.example.keinvitaciones.ui.theme.SoftPink
import com.example.keinvitaciones.ui.theme.StatusGreen
import com.example.keinvitaciones.ui.theme.StatusRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationExperienceScreen(
    initialInvitationId: String = "inv-valen-15",
    modifier: Modifier = Modifier
) {
    val repository = remember { InvitationRepository.instance }
    val invitations by repository.invitations.collectAsState()
    val guests by repository.guests.collectAsState()

    var selectedInvId by remember(initialInvitationId) { mutableStateOf(initialInvitationId) }
    val currentInvitation = invitations.find { it.id == selectedInvId } ?: invitations.firstOrNull()

    // Guest personalization selector (allows previewing personalized envelope vs general)
    val invitationGuests = remember(selectedInvId, guests) {
        guests.filter { it.invitationId == selectedInvId }
    }
    var selectedGuestId by remember(selectedInvId) {
        mutableStateOf<String?>(invitationGuests.firstOrNull()?.id)
    }
    val currentGuest = remember(selectedGuestId, guests) {
        guests.find { it.id == selectedGuestId }
    }

    var isEnvelopeOpen by remember(selectedInvId, selectedGuestId) { mutableStateOf(false) }
    var isMusicPlaying by remember(selectedInvId) { mutableStateOf(true) }

    // Lightbox for photos
    var lightboxIndex by remember { mutableStateOf<Int?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .testTag("invitation_screen")
    ) {
        if (currentInvitation == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay invitaciones disponibles.")
            }
            return
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Preview Toolbar: Selector of which invitation & guest mode
            PreviewControlToolbar(
                invitations = invitations,
                selectedInvId = selectedInvId,
                onSelectInvitation = {
                    selectedInvId = it
                    isEnvelopeOpen = false
                },
                invitationGuests = invitationGuests,
                selectedGuestId = selectedGuestId,
                onSelectGuest = {
                    selectedGuestId = it
                    isEnvelopeOpen = false
                }
            )

            // Main Content: Either the Envelope or the Unfolded Invitation
            if (!isEnvelopeOpen) {
                EnvelopeSection(
                    invitation = currentInvitation,
                    guest = currentGuest,
                    onOpenEnvelope = {
                        isEnvelopeOpen = true
                        isMusicPlaying = currentInvitation.packageType == "premium"
                    }
                )
            } else {
                InvitationContentSection(
                    invitation = currentInvitation,
                    guest = currentGuest,
                    isMusicPlaying = isMusicPlaying,
                    onToggleMusic = { isMusicPlaying = !isMusicPlaying },
                    onPhotoClick = { idx -> lightboxIndex = idx },
                    onCloseEnvelope = { isEnvelopeOpen = false },
                    onRsvpSubmitted = { msg ->
                        scope.launch {
                            snackbarHostState.showSnackbar(msg)
                        }
                    },
                    onCopyText = { label, text ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
                        scope.launch {
                            snackbarHostState.showSnackbar("¡$label copiado al portapapeles!")
                        }
                    },
                    onOpenMap = {
                        val url = currentInvitation.mapsUrl.ifEmpty { "https://maps.google.com/?q=${Uri.encode(currentInvitation.venue + " " + currentInvitation.address)}" }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    onOpenSpotify = {
                        val url = currentInvitation.spotifyPlaylistUrl.ifEmpty { "https://open.spotify.com" }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    onOpenInstagram = { tag ->
                        val cleanTag = tag.replace("#", "").trim()
                        val url = "https://www.instagram.com/explore/tags/$cleanTag/"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }
        }

        // Fullscreen Lightbox Dialog
        if (lightboxIndex != null && currentInvitation.photos.isNotEmpty()) {
            val photos = currentInvitation.photos
            val idx = lightboxIndex!!.coerceIn(0, photos.size - 1)
            LightboxViewerDialog(
                photos = photos,
                currentIndex = idx,
                onIndexChange = { lightboxIndex = it },
                onDismiss = { lightboxIndex = null }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewControlToolbar(
    invitations: List<Invitation>,
    selectedInvId: String,
    onSelectInvitation: (String) -> Unit,
    invitationGuests: List<Guest>,
    selectedGuestId: String?,
    onSelectGuest: (String?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkCharcoal,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VISTA PREVIA INTERACTIVA",
                    color = GoldPrimary,
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Invitation picker
                var invExpanded by remember { mutableStateOf(false) }
                val currentInv = invitations.find { it.id == selectedInvId }

                ExposedDropdownMenuBox(
                    expanded = invExpanded,
                    onExpandedChange = { invExpanded = !invExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = currentInv?.honoree ?: "Seleccionar",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Evento", color = Color.LightGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = invExpanded) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF262626),
                            unfocusedContainerColor = Color(0xFF262626)
                        ),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = invExpanded,
                        onDismissRequest = { invExpanded = false }
                    ) {
                        invitations.forEach { inv ->
                            DropdownMenuItem(
                                text = { Text("${inv.honoree} (${inv.eventType})") },
                                onClick = {
                                    onSelectInvitation(inv.id)
                                    invExpanded = false
                                }
                            )
                        }
                    }
                }

                // Guest personalization picker
                var guestExpanded by remember { mutableStateOf(false) }
                val currentGuest = invitationGuests.find { it.id == selectedGuestId }

                ExposedDropdownMenuBox(
                    expanded = guestExpanded,
                    onExpandedChange = { guestExpanded = !guestExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = currentGuest?.name ?: "General (Sin pase)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Invitado", color = Color.LightGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = guestExpanded) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF262626),
                            unfocusedContainerColor = Color(0xFF262626)
                        ),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = guestExpanded,
                        onDismissRequest = { guestExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Invitado General (sin cupos fijos)") },
                            onClick = {
                                onSelectGuest(null)
                                guestExpanded = false
                            }
                        )
                        invitationGuests.forEach { g ->
                            DropdownMenuItem(
                                text = { Text("${g.name} (${g.seats} lugares)") },
                                onClick = {
                                    onSelectGuest(g.id)
                                    guestExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnvelopeSection(
    invitation: Invitation,
    guest: Guest?,
    onOpenEnvelope: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, shape = RoundedCornerShape(24.dp))
                .border(2.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .testTag("envelope_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFBF7))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KE INVITACIONES",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldDark
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Realistic Wax Seal graphic
                WaxSealMonogram(
                    onClick = onOpenEnvelope
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = invitation.honoree,
                    style = MaterialTheme.typography.headlineLarge,
                    color = DarkCharcoal,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = invitation.eventType,
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (guest != null) {
                    Surface(
                        color = SoftPink,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Especialmente para:",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkCharcoal
                            )
                            Text(
                                text = guest.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "Lugares reservados: ${guest.seats}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onOpenEnvelope,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .testTag("btn_abrir_sobre")
                ) {
                    Icon(Icons.Default.Mail, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Abrir invitación",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun WaxSealMonogram(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFE5C06E), GoldPrimary, GoldDark)
                )
            )
            .border(3.dp, Color(0xFF8A6A1F), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "KE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFF4A380E)
            )
        }
    }
}

@Composable
private fun InvitationContentSection(
    invitation: Invitation,
    guest: Guest?,
    isMusicPlaying: Boolean,
    onToggleMusic: () -> Unit,
    onPhotoClick: (Int) -> Unit,
    onCloseEnvelope: () -> Unit,
    onRsvpSubmitted: (String) -> Unit,
    onCopyText: (String, String) -> Unit,
    onOpenMap: () -> Unit,
    onOpenSpotify: () -> Unit,
    onOpenInstagram: (String) -> Unit
) {
    val isPremium = invitation.packageType == "premium"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("invitation_scroll"),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // Music Player Floating/Top Bar
        if (isPremium) {
            item {
                MusicPlayerBar(
                    musicTitle = invitation.musicUrl.ifEmpty { "Música de fondo del evento" },
                    isPlaying = isMusicPlaying,
                    onToggle = onToggleMusic
                )
            }
        }

        // Portada / Header with Photo or Luxury Typography
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (invitation.photos.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            AsyncImage(
                                model = invitation.photos.first(),
                                contentDescription = "Foto de portada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onPhotoClick(0) }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color(0x99000000))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = invitation.honoree,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = invitation.eventType,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GoldPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = invitation.honoree,
                                style = MaterialTheme.typography.headlineLarge,
                                color = DarkCharcoal,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = invitation.eventType,
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Guest personalized pill
                    if (guest != null) {
                        Surface(
                            color = SoftPink,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Invitación especial para:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = guest.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = "Pases reservados: ${guest.seats}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Welcome Message
                    if (invitation.welcome.isNotEmpty()) {
                        Text(
                            text = "\"${invitation.welcome}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkCharcoal,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }

                    if (invitation.message.isNotEmpty()) {
                        Text(
                            text = invitation.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // PREMIUM FEATURE: Digital Access Pass with QR Code
        if (isPremium) {
            item {
                DigitalAccessPassCard(
                    invitation = invitation,
                    guest = guest
                )
            }
        }

        // Initial Countdown Clock
        item {
            CountdownTimerCard(dateString = invitation.date, timeString = invitation.time)
        }

        // Event Date, Time & Venue
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "¿Cuándo y Dónde?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = GoldDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = formatDisplayDate(invitation.date),
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "A partir de las ${invitation.time} hs",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = invitation.venue.ifEmpty { "Salón del evento" },
                                style = MaterialTheme.typography.titleMedium,
                                color = DarkCharcoal
                            )
                            if (invitation.address.isNotEmpty()) {
                                Text(
                                    text = invitation.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onOpenMap,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkCharcoal),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver ubicación en Google Maps")
                    }
                }
            }
        }

        // NEW FEATURE: Event Itinerary / Timeline
        if (invitation.itinerary.isNotEmpty()) {
            item {
                EventItineraryCard(itinerary = invitation.itinerary)
            }
        }

        // NEW FEATURE: Spotify Playlist
        if (invitation.spotifyPlaylistUrl.isNotEmpty()) {
            item {
                SpotifyPlaylistCard(
                    playlistUrl = invitation.spotifyPlaylistUrl,
                    onOpenSpotify = onOpenSpotify
                )
            }
        }

        // NEW FEATURE: Instagram Hashtag
        if (invitation.instagramHashtag.isNotEmpty()) {
            item {
                InstagramHashtagCard(
                    hashtag = invitation.instagramHashtag,
                    onCopy = { onCopyText("Hashtag de Instagram", invitation.instagramHashtag) },
                    onOpenInstagram = { onOpenInstagram(invitation.instagramHashtag) }
                )
            }
        }

        // Dress Code & Gift Sections
        if (invitation.dressCode.isNotEmpty()) {
            item {
                InfoHighlightCard(
                    title = "Dress Code",
                    content = invitation.dressCode,
                    icon = Icons.Default.Info
                )
            }
        }

        if (invitation.gift.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Regalo",
                                style = MaterialTheme.typography.titleLarge,
                                color = GoldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = invitation.gift,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkCharcoal
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onCopyText("Datos de regalo", invitation.gift) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copiar datos", color = Color.White)
                        }
                    }
                }
            }
        }

        if (invitation.info.isNotEmpty()) {
            item {
                InfoHighlightCard(
                    title = "Información adicional",
                    content = invitation.info,
                    icon = Icons.Default.Info
                )
            }
        }

        // Photo Gallery
        if (invitation.photos.size > 1) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Galería de Fotos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = GoldDark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(invitation.photos) { idx, url ->
                            Card(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clickable { onPhotoClick(idx) },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Foto $idx",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        // RSVP Confirmation Form
        item {
            RsvpFormCard(
                invitation = invitation,
                guest = guest,
                onSubmitted = onRsvpSubmitted
            )
        }

        // END OF INVITATION: Replaces previous WhatsApp share button with "Nos vemos en:" + Countdown
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Phrase requested by user
                Text(
                    text = "¡Nos vemos en!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Countdown timer repeated at the end as requested
                CountdownTimerCard(
                    dateString = invitation.date,
                    timeString = invitation.time
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onCloseEnvelope,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkCharcoal),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cerrar sobre / Volver a empezar")
                }
            }
        }
    }
}

@Composable
private fun DigitalAccessPassCard(
    invitation: Invitation,
    guest: Guest?
) {
    val ticketCode = guest?.ticketCode?.ifEmpty { "TKT-${invitation.id.takeLast(4).uppercase()}-GEN" } ?: "TKT-${invitation.id.takeLast(4).uppercase()}-GEN"
    val guestName = guest?.name ?: "Pase General de Acceso"
    val seatCount = guest?.seats ?: 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("digital_access_pass_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
        border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = GoldPrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "PASE DIGITAL VIP",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = ticketCode,
                    color = GoldPrimary,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = invitation.honoree,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Text(
                text = invitation.eventType,
                style = MaterialTheme.typography.bodySmall,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Realistic QR Code Box for entry scanning
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(170.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode,
                            contentDescription = "Código QR de Acceso",
                            tint = DarkCharcoal,
                            modifier = Modifier.size(130.dp)
                        )
                        Text(
                            text = ticketCode,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = DarkCharcoal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = guestName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Pases habilitados: $seatCount ${if (seatCount > 1) "personas" else "persona"}",
                style = MaterialTheme.typography.bodyMedium,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (guest?.checkedIn == true) {
                Surface(
                    color = StatusGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ingresado al salón (${guest.checkedInAt ?: "Recepción"})",
                            color = StatusGreen,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "Mostrá este código QR en la recepción para acceder al evento.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EventItineraryCard(itinerary: List<ItineraryItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("event_itinerary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Itinerario del Evento",
                style = MaterialTheme.typography.headlineSmall,
                color = GoldDark
            )
            Text(
                text = "Cronograma y momentos especiales para que no te pierdas nada.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            itinerary.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Time pill
                    Surface(
                        color = SoftPink,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.width(72.dp)
                    ) {
                        Text(
                            text = "${item.time} hs",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                        if (item.description.isNotEmpty()) {
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (index < itinerary.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(start = 86.dp, top = 4.dp, bottom = 4.dp),
                        color = Color(0xFFF0EBE1)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotifyPlaylistCard(
    playlistUrl: String,
    onOpenSpotify: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("spotify_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1DB954)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = "Spotify",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Playlist de la Fiesta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Escuchá y sugerí tus canciones favoritas en Spotify",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenSpotify,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir en Spotify", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun InstagramHashtagCard(
    hashtag: String,
    onCopy: () -> Unit,
    onOpenInstagram: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("instagram_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE1306C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("#", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "¡Compartí tus fotos en Instagram!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = SoftPink,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = hashtag,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC13584)
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Etiquetá tus historias y fotos para que todos podamos revivir cada momento.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar tag")
                }

                Button(
                    onClick = onOpenInstagram,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Instagram", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun MusicPlayerBar(
    musicTitle: String,
    isPlaying: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoftPink)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                    contentDescription = null,
                    tint = GoldDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isPlaying) "Reproduciendo música de fondo" else "Música pausada",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = musicTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkCharcoal
                    )
                }
            }

            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GoldPrimary)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Pausar o reproducir música",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CountdownTimerCard(dateString: String, timeString: String) {
    var diffSeconds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(dateString, timeString) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val targetDate = try {
            format.parse("$dateString $timeString") ?: Date()
        } catch (e: Exception) {
            Date()
        }
        while (true) {
            val now = Date().time
            val diff = (targetDate.time - now) / 1000
            diffSeconds = diff.coerceAtLeast(0L)
            delay(1000)
        }
    }

    val days = diffSeconds / 86400
    val hours = (diffSeconds % 86400) / 3600
    val minutes = (diffSeconds % 3600) / 60
    val seconds = diffSeconds % 60

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5EE)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = GoldDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cuenta Regresiva",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CountdownUnit(value = days.toString(), label = "Días")
                CountdownUnit(value = String.format(Locale.getDefault(), "%02d", hours), label = "Horas")
                CountdownUnit(value = String.format(Locale.getDefault(), "%02d", minutes), label = "Minutos")
                CountdownUnit(value = String.format(Locale.getDefault(), "%02d", seconds), label = "Segundos")
            }
        }
    }
}

@Composable
private fun CountdownUnit(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GoldDark
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = DarkCharcoal
        )
    }
}

@Composable
private fun InfoHighlightCard(title: String, content: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = GoldPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldDark
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkCharcoal
            )
        }
    }
}

@Composable
private fun RsvpFormCard(
    invitation: Invitation,
    guest: Guest?,
    onSubmitted: (String) -> Unit
) {
    val repository = remember { InvitationRepository.instance }
    val maxAllowedGuests = guest?.seats ?: 20

    var name by remember(guest) { mutableStateOf(guest?.name ?: "") }
    var attending by remember { mutableStateOf(true) }
    var guestCount by remember(guest) { mutableIntStateOf(guest?.seats ?: 1) }
    var message by remember { mutableStateOf("") }
    var submittedState by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("rsvp_form_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Confirmación de Asistencia",
                style = MaterialTheme.typography.headlineSmall,
                color = GoldDark
            )
            Text(
                text = "Por favor confirma tu presencia para que podamos organizar los detalles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            if (submittedState) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Muchas gracias por confirmar!",
                            style = MaterialTheme.typography.titleMedium,
                            color = StatusGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (attending) "Tu asistencia ($guestCount personas) quedó registrada." else "Lamentamos que no puedas venir. Tu respuesta quedó registrada.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkCharcoal,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val waText = if (attending) {
                            "¡Hola! Soy $name. Confirmo mi asistencia al evento de ${invitation.honoree} ($guestCount personas)."
                        } else {
                            "¡Hola! Soy $name. Lamentablemente no voy a poder asistir al evento de ${invitation.honoree}."
                        }
                        Button(
                            onClick = {
                                val cleanNum = invitation.whatsapp.filter { it.isDigit() }
                                val url = "https://wa.me/$cleanNum?text=${Uri.encode(waText)}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Avisar también por WhatsApp", color = Color.White)
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre y Apellido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_rsvp_nombre")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¿Asistirás?",
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkCharcoal
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { attending = true }
                            .padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = attending,
                            onClick = { attending = true },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                        )
                        Text("Sí, confirmo asistencia", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { attending = false }
                    ) {
                        RadioButton(
                            selected = !attending,
                            onClick = { attending = false },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                        )
                        Text("No podré asistir", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (attending) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (guest != null) "Cantidad de personas (máximo ${guest.seats} reservados):" else "Cantidad de personas:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (guestCount > 1) guestCount-- },
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "$guestCount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        OutlinedButton(
                            onClick = { if (guestCount < maxAllowedGuests) guestCount++ },
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mensaje o dedicatoria (opcional)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            onSubmitted("Por favor ingresa tu nombre")
                            return@Button
                        }
                        repository.submitRsvp(
                            invitationId = invitation.id,
                            guestId = guest?.id,
                            name = name.trim(),
                            attending = attending,
                            guestCount = if (attending) guestCount else 0,
                            message = message.trim()
                        )
                        submittedState = true
                        onSubmitted("¡Respuesta enviada con éxito!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_enviar_rsvp")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar confirmación", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LightboxViewerDialog(
    photos: List<String>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
        ) {
            AsyncImage(
                model = photos[currentIndex],
                contentDescription = "Foto ampliada",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 60.dp)
            )

            // Top close & counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentIndex + 1} / ${photos.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }

            // Left / Right navigation arrows
            if (photos.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            val next = if (currentIndex > 0) currentIndex - 1 else photos.size - 1
                            onIndexChange(next)
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Anterior", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            val next = (currentIndex + 1) % photos.size
                            onIndexChange(next)
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Siguiente", tint = Color.White)
                    }
                }
            }
        }
    }
}

private fun formatDisplayDate(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateStr) ?: return dateStr
        val formatter = SimpleDateFormat("EEEE d 'de' MMMM, yyyy", Locale.forLanguageTag("es-AR"))
        val formatted = formatter.format(date)
        formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.forLanguageTag("es-AR")) else it.toString() }
    } catch (e: Exception) {
        dateStr
    }
}
