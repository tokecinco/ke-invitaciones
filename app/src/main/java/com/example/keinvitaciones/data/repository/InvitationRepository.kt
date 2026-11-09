package com.example.keinvitaciones.data.repository

import com.example.keinvitaciones.data.model.Guest
import com.example.keinvitaciones.data.model.Invitation
import com.example.keinvitaciones.data.model.ItineraryItem
import com.example.keinvitaciones.data.model.PackagePlan
import com.example.keinvitaciones.data.model.PaymentRecord
import com.example.keinvitaciones.data.model.RsvpSubmission
import com.example.keinvitaciones.data.model.TemplateModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class InvitationRepository {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "AR"))

    val packages = listOf(
        PackagePlan(
            id = "basico",
            name = "Básico",
            price = 14500.0,
            maxPhotos = 2,
            includesMusic = false,
            includesPersonalization = false,
            description = "Ideal para eventos íntimos o celebraciones sencillas.",
            features = listOf(
                "Hasta 2 fotos en portada y galería",
                "Cuenta regresiva al evento",
                "Ubicación en Google Maps",
                "Formulario de confirmación de asistencia (RSVP)",
                "Dress code y datos de regalos / transferencias",
                "Enlace web único y código QR descargable"
            )
        ),
        PackagePlan(
            id = "premium",
            name = "Premium",
            price = 22000.0,
            maxPhotos = 6,
            includesMusic = true,
            includesPersonalization = true,
            description = "La experiencia completa con sobre interactivo, pases con QR, itinerario, Spotify e Instagram.",
            features = listOf(
                "Sobre digital interactivo con sello lacrado",
                "Pase digital de acceso con código QR para recepción",
                "Itinerario cronológico e interactivo del evento",
                "Playlist de Spotify para escuchar y sugerir canciones",
                "Hashtag de Instagram con acceso directo",
                "Música de fondo automática con control de pausa",
                "Hasta 6 fotos en alta calidad con visor pantalla completa",
                "Pases personalizados por invitado (control de cupos)",
                "Panel de invitados con enlaces únicos para WhatsApp",
                "Estadísticas en tiempo real y código QR para tarjetas"
            )
        )
    )

    val templates = listOf(
        TemplateModel(
            id = "quince-dorado",
            name = "Quince Años Gold & Rose",
            category = "quince",
            categoryLabel = "15 años",
            softColorHex = "#F6DDE2",
            accentColorHex = "#C9A24B",
            description = "Elegante diseño en oro y rosa suave con detalles brillantes y sobre interactivo."
        ),
        TemplateModel(
            id = "boda-botanica",
            name = "Casamiento Elegance Botanica",
            category = "boda",
            categoryLabel = "Casamientos",
            softColorHex = "#E8EDE4",
            accentColorHex = "#A57C26",
            description = "Estilo clásico botánico con caligrafía dorada y tipografía de ensueño."
        ),
        TemplateModel(
            id = "cumple-aventura",
            name = "Cumpleaños Safari Aventura",
            category = "cumple",
            categoryLabel = "Cumpleaños infantiles",
            softColorHex = "#FFF3D6",
            accentColorHex = "#D97706",
            description = "Divertido y colorido para los más chicos con globos e ilustraciones alegres."
        ),
        TemplateModel(
            id = "bautismo-celeste",
            name = "Bautismo Angelical",
            category = "bautismo",
            categoryLabel = "Bautismos",
            softColorHex = "#E1F0F8",
            accentColorHex = "#4A90E2",
            description = "Delicado diseño celestial en tonos pastel, palomas y cruces tenues."
        ),
        TemplateModel(
            id = "babyshower-bosque",
            name = "Baby Shower Osito Dulce",
            category = "babyshower",
            categoryLabel = "Baby shower",
            softColorHex = "#FDF0ED",
            accentColorHex = "#C07D67",
            description = "Tierna ambientación para celebrar la llegada del nuevo integrante de la familia."
        ),
        TemplateModel(
            id = "aniversario-plata",
            name = "Bodas de Plata & Oro",
            category = "aniversario",
            categoryLabel = "Aniversarios",
            softColorHex = "#F0F0F2",
            accentColorHex = "#B89745",
            description = "Distinción y sobriedad para homenajear años de amor incondicional."
        ),
        TemplateModel(
            id = "graduacion-vip",
            name = "Graduación & Gala Universitaria",
            category = "especial",
            categoryLabel = "Eventos especiales",
            softColorHex = "#EDEBF5",
            accentColorHex = "#6B46C1",
            description = "Celebración formal con estilo contemporáneo y acreditaciones personalizadas."
        )
    )

    private val initialInvitations = listOf(
        Invitation(
            id = "inv-valen-15",
            packageType = "premium",
            template = "quince",
            honoree = "Valentina",
            eventType = "Mis 15 Años",
            date = "2026-12-19",
            time = "21:30",
            venue = "Palacio Sans Souci",
            address = "Paz 705, Victoria, Buenos Aires",
            mapsUrl = "https://maps.google.com/?q=Palacio+Sans+Souci",
            welcome = "Hay momentos en la vida que son irrepetibles, pero compartirlos con la gente que uno más quiere los hace inolvidables.",
            message = "Te espero para compartir juntos una noche mágica llena de música, risas y emoción.",
            info = "Estacionamiento privado con seguridad en el predio. Recepción al aire libre a partir de las 21:30 hs puntual.",
            dressCode = "Elegante Gala (Evitar vestidos en tonos dorado y rosa palo reservado para la quinceañera)",
            gift = "Mi mejor regalo es tu compañía. Si deseás hacerme un presente: Alias: valen.mis15.banco | CBU: 0720123488000012345678",
            whatsapp = "5491155554321",
            musicUrl = "Vals de los 15 - Canción de apertura",
            photos = listOf(
                "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1520854221256-17451cc331bf?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=800&q=80"
            ),
            itinerary = listOf(
                ItineraryItem("21:30", "Recepción de Invitados", "Cocktail de bienvenida en los jardines y photocall con fotógrafo profesional", "cocktail"),
                ItineraryItem("22:30", "Ingreso de Valentina & Vals", "Comienzo formal de la noche con el vals familiar y entrada triunfal", "entrance"),
                ItineraryItem("23:15", "Cena Principal Gourmet", "Entrada de tapas gourmet, plato principal y barra de tragos libres", "dinner"),
                ItineraryItem("00:45", "Video Homenaje & Brindis", "Recorrido de momentos especiales, palabras emotivas y brindis", "toast"),
                ItineraryItem("01:15", "Apertura de Pista & Cotillón LED", "DJ en vivo, tandas de cumbia, pop y cotillón luminoso", "party"),
                ItineraryItem("03:00", "Glitter Bar & Show Sorpresa", "Estación de maquillaje glitter, gemas faciales y show de animación", "party"),
                ItineraryItem("05:00", "Fin de Fiesta & Desayuno", "Café con leche, chocolate caliente y medialunas recién horneadas", "end")
            ),
            spotifyPlaylistUrl = "https://open.spotify.com/playlist/37i9dQZF1DXa2PvU927u83",
            instagramHashtag = "#ValenMis15",
            active = true
        ),
        Invitation(
            id = "inv-boda-sofi-lucas",
            packageType = "premium",
            template = "boda",
            honoree = "Sofía & Lucas",
            eventType = "Nuestra Boda",
            date = "2027-02-14",
            time = "18:00",
            venue = "Estancia Villa María",
            address = "Ruta 205 Km 47.5, Máximo Paz",
            mapsUrl = "https://maps.google.com/?q=Estancia+Villa+Maria",
            welcome = "Decidimos dar el gran paso y queremos que seas testigo del inicio de nuestra mayor aventura.",
            message = "Ceremonia civil y religiosa en los jardines seguida de cena y fiesta hasta el amanecer.",
            info = "Servicio de transporte chárter disponible desde CABA. Confirmar lugar en el chárter antes del 20 de Enero.",
            dressCode = "Elegante (Traje oscuro / Vestido largo)",
            gift = "Si querés ayudarnos en nuestra luna de miel en Italia: Alias: sofi.lucas.boda",
            whatsapp = "5491166667890",
            musicUrl = "Canon in D - Johann Pachelbel",
            photos = listOf(
                "https://images.unsplash.com/photo-1583939003579-730e3918a45a?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?auto=format&fit=crop&w=800&q=80"
            ),
            itinerary = listOf(
                ItineraryItem("18:00", "Ceremonia al Atardecer", "Intercambio de anillos y votos en los jardines de la estancia", "entrance"),
                ItineraryItem("19:30", "Cocktail de Bodas", "Aperitivos, música instrumental en vivo y fotos", "cocktail"),
                ItineraryItem("21:00", "Cena de Gala", "Cena de 3 pasos con maridaje de vinos de autor", "dinner"),
                ItineraryItem("23:00", "Corte de Torta & Vals", "Brindis con champagne y primer baile de casados", "toast"),
                ItineraryItem("23:45", "Fiesta & Barra Libre", "DJ, show de luces, pista bailable y barra premium", "party")
            ),
            spotifyPlaylistUrl = "https://open.spotify.com/playlist/37i9dQZF1DXa2PvU927u83",
            instagramHashtag = "#BodaSofiYLucas",
            active = true
        ),
        Invitation(
            id = "inv-benja-1",
            packageType = "basico",
            template = "cumple",
            honoree = "Benjamín",
            eventType = "Mi 1er Añito",
            date = "2026-11-28",
            time = "16:00",
            venue = "Salón Mágico Kids",
            address = "Av. Santa Fe 3200, Palermo",
            mapsUrl = "https://maps.google.com/?q=Salon+Magico+Kids",
            welcome = "¡Cumplo mi primer añito y quiero festejarlo con todos mis amiguitos y familia!",
            message = "Habrá juegos, inflables, merienda rica y mucha diversión.",
            info = "Por favor traer calzado cómodo para el pelotero.",
            dressCode = "Casual / Cómodo para jugar",
            gift = "Talle de ropa: 18-24 meses. Calzado: 21.",
            whatsapp = "5491177771234",
            active = true
        )
    )

    private val initialGuests = listOf(
        Guest("g-1", "inv-valen-15", "Familia Martínez", seats = 4, attending = true, respondedAt = "Hoy 14:32", confirmedGuests = 4, message = "¡Felicitaciones Valen hermosa! Allí estaremos para bailar toda la noche.", ticketCode = "TKT-VALEN-001", checkedIn = true, checkedInAt = "21:35"),
        Guest("g-2", "inv-valen-15", "Martín & Clara Gómez", seats = 2, attending = true, respondedAt = "Ayer 19:15", confirmedGuests = 2, message = "Muchas gracias por invitarnos. ¡Va a ser una noche soñada!", ticketCode = "TKT-VALEN-002", checkedIn = false),
        Guest("g-3", "inv-valen-15", "Tía Susana y flia", seats = 3, attending = false, respondedAt = "05/11/2026", confirmedGuests = 0, message = "Lamentablemente estamos de viaje esa fecha, pero te mandamos todo nuestro amor.", ticketCode = "TKT-VALEN-003", checkedIn = false),
        Guest("g-4", "inv-valen-15", "Amigos del Colegio (Matías)", seats = 1, attending = null, ticketCode = "TKT-VALEN-004", checkedIn = false),
        Guest("g-5", "inv-valen-15", "Familia Rossi", seats = 5, attending = true, respondedAt = "06/11/2026", confirmedGuests = 5, message = "¡Ansiosos por el gran día!", ticketCode = "TKT-VALEN-005", checkedIn = true, checkedInAt = "21:42"),

        Guest("g-6", "inv-boda-sofi-lucas", "Carlos & Andrea Méndez", seats = 2, attending = true, respondedAt = "08/11/2026", confirmedGuests = 2, message = "¡Qué felicidad tan inmensa amigos queridos!", ticketCode = "TKT-BODA-001", checkedIn = false),
        Guest("g-7", "inv-boda-sofi-lucas", "Lucía Benítez", seats = 1, attending = true, respondedAt = "09/11/2026", confirmedGuests = 1, message = "Nos vemos en la pista.", ticketCode = "TKT-BODA-002", checkedIn = false)
    )

    private val initialRsvps = listOf(
        RsvpSubmission("r-1", "inv-valen-15", "g-1", "Familia Martínez", true, 4, "¡Felicitaciones Valen hermosa! Allí estaremos para bailar toda la noche.", "08/11/2026 14:32"),
        RsvpSubmission("r-2", "inv-valen-15", "g-2", "Martín & Clara Gómez", true, 2, "Muchas gracias por invitarnos. ¡Va a ser una noche soñada!", "07/11/2026 19:15"),
        RsvpSubmission("r-3", "inv-valen-15", "g-3", "Tía Susana y flia", false, 0, "Lamentablemente estamos de viaje esa fecha, pero te mandamos todo nuestro amor.", "05/11/2026 11:20"),
        RsvpSubmission("r-4", "inv-valen-15", "g-5", "Familia Rossi", true, 5, "¡Ansiosos por el gran día!", "06/11/2026 16:40")
    )

    private val initialPayments = listOf(
        PaymentRecord("pay-1", "inv-valen-15", "admin-user", 22000.0, 11000.0, "senado", "Seña 50% abonada por transferencia. Saldo contra entrega.", "02/11/2026"),
        PaymentRecord("pay-2", "inv-boda-sofi-lucas", "admin-user", 22000.0, 22000.0, "pagado", "Pago total completado.", "01/11/2026"),
        PaymentRecord("pay-3", "inv-benja-1", "admin-user", 14500.0, 0.0, "pendiente", "Reserva confirmada por WhatsApp, pendiente seña.", "08/11/2026")
    )

    private val _invitations = MutableStateFlow(initialInvitations)
    val invitations: StateFlow<List<Invitation>> = _invitations.asStateFlow()

    private val _guests = MutableStateFlow(initialGuests)
    val guests: StateFlow<List<Guest>> = _guests.asStateFlow()

    private val _rsvps = MutableStateFlow(initialRsvps)
    val rsvps: StateFlow<List<RsvpSubmission>> = _rsvps.asStateFlow()

    private val _payments = MutableStateFlow(initialPayments)
    val payments: StateFlow<List<PaymentRecord>> = _payments.asStateFlow()

    private val _selectedInvitationId = MutableStateFlow("inv-valen-15")
    val selectedInvitationId: StateFlow<String> = _selectedInvitationId.asStateFlow()

    fun setSelectedInvitation(id: String) {
        _selectedInvitationId.value = id
    }

    fun submitRsvp(
        invitationId: String,
        guestId: String?,
        name: String,
        attending: Boolean,
        guestCount: Int,
        message: String
    ): RsvpSubmission {
        val now = dateFormat.format(Date())
        val submission = RsvpSubmission(
            id = "r-" + UUID.randomUUID().toString().take(8),
            invitationId = invitationId,
            guestId = guestId,
            name = name,
            attending = attending,
            guestCount = if (attending) guestCount else 0,
            message = message,
            timestamp = now
        )
        _rsvps.value = listOf(submission) + _rsvps.value

        // If matched guest, update guest record
        if (guestId != null) {
            _guests.value = _guests.value.map { g ->
                if (g.id == guestId) {
                    g.copy(
                        attending = attending,
                        respondedAt = now,
                        confirmedGuests = if (attending) guestCount else 0,
                        message = message
                    )
                } else g
            }
        }
        return submission
    }

    fun checkInGuest(guestId: String) {
        val now = SimpleDateFormat("HH:mm", Locale("es", "AR")).format(Date())
        _guests.value = _guests.value.map { g ->
            if (g.id == guestId) {
                val newStatus = !g.checkedIn
                g.copy(
                    checkedIn = newStatus,
                    checkedInAt = if (newStatus) now else null
                )
            } else g
        }
    }

    fun addOrUpdateInvitation(invitation: Invitation) {
        val current = _invitations.value
        val index = current.indexOfFirst { it.id == invitation.id }
        if (index >= 0) {
            val updated = current.toMutableList()
            updated[index] = invitation
            _invitations.value = updated
        } else {
            _invitations.value = listOf(invitation) + current
            val defaultPrice = if (invitation.packageType == "premium") 22000.0 else 14500.0
            val payment = PaymentRecord(
                id = "pay-" + invitation.id,
                invitationId = invitation.id,
                totalAmount = defaultPrice,
                depositAmount = 0.0,
                status = "pendiente",
                note = "Nueva invitación creada",
                updatedAt = dateFormat.format(Date())
            )
            _payments.value = _payments.value + payment
        }
    }

    fun toggleInvitationActive(id: String) {
        _invitations.value = _invitations.value.map {
            if (it.id == id) it.copy(active = !it.active) else it
        }
    }

    fun deleteInvitation(id: String) {
        _invitations.value = _invitations.value.filter { it.id != id }
        _guests.value = _guests.value.filter { it.invitationId != id }
        _rsvps.value = _rsvps.value.filter { it.invitationId != id }
        _payments.value = _payments.value.filter { it.invitationId != id }
        if (_selectedInvitationId.value == id && _invitations.value.isNotEmpty()) {
            _selectedInvitationId.value = _invitations.value.first().id
        }
    }

    fun addGuest(invitationId: String, name: String, seats: Int) {
        val prefix = invitationId.replace("inv-", "").take(5).uppercase()
        val randomSuffix = UUID.randomUUID().toString().take(3).uppercase()
        val newGuest = Guest(
            id = "g-" + UUID.randomUUID().toString().take(8),
            invitationId = invitationId,
            name = name,
            seats = seats,
            ticketCode = "TKT-$prefix-$randomSuffix"
        )
        _guests.value = _guests.value + newGuest
    }

    fun addBulkGuests(invitationId: String, rawLines: String): Int {
        val lines = rawLines.lines().map { it.trim() }.filter { it.isNotEmpty() }
        var added = 0
        val prefix = invitationId.replace("inv-", "").take(5).uppercase()
        val newEntries = mutableListOf<Guest>()
        for (line in lines) {
            val parts = line.split(";")
            val name = parts.getOrNull(0)?.trim() ?: continue
            val seats = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1
            val randomSuffix = UUID.randomUUID().toString().take(4).uppercase()
            newEntries.add(
                Guest(
                    id = "g-" + UUID.randomUUID().toString().take(8),
                    invitationId = invitationId,
                    name = name,
                    seats = seats,
                    ticketCode = "TKT-$prefix-$randomSuffix"
                )
            )
            added++
        }
        if (newEntries.isNotEmpty()) {
            _guests.value = _guests.value + newEntries
        }
        return added
    }

    fun deleteGuest(guestId: String) {
        _guests.value = _guests.value.filter { it.id != guestId }
    }

    fun updatePayment(paymentId: String, status: String, deposit: Double, total: Double, note: String) {
        _payments.value = _payments.value.map { p ->
            if (p.id == paymentId) {
                p.copy(
                    status = status,
                    depositAmount = deposit,
                    totalAmount = total,
                    note = note,
                    updatedAt = dateFormat.format(Date())
                )
            } else p
        }
    }

    companion object {
        val instance = InvitationRepository()
    }
}
