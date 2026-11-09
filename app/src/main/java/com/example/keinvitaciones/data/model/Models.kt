package com.example.keinvitaciones.data.model

data class ItineraryItem(
    val time: String,
    val title: String,
    val description: String = "",
    val iconType: String = "general" // "cocktail", "entrance", "dinner", "toast", "party", "end"
)

data class Invitation(
    val id: String,
    val ownerId: String = "admin-user",
    val packageType: String = "premium", // "basico" or "premium"
    val template: String = "quince",     // "cumple", "quince", "boda", "bautismo", "babyshower"
    val honoree: String,                 // e.g. "Valentina", "Sofía & Lucas", "Benjamín"
    val eventType: String,               // e.g. "Mis 15 años", "Nuestra Boda", "Primer Añito"
    val date: String,                    // "YYYY-MM-DD" e.g. "2026-12-18"
    val time: String = "21:00",          // "HH:mm" e.g. "21:00"
    val venue: String = "",              // "Salón La Alborada"
    val address: String = "",            // "Av. Libertador 4500, Buenos Aires"
    val mapsUrl: String = "",            // "https://maps.google.com/?q=..."
    val welcome: String = "",            // "¡Estás invitado a compartir una noche inolvidable!"
    val message: String = "",            // Personal greeting message
    val info: String = "",               // Extra info, parking, etc.
    val dressCode: String = "",          // "Elegante sport", "Gala", etc.
    val gift: String = "",               // "Tu presencia es nuestro mejor regalo. Si querés hacernos un obsequio: Alias banco..."
    val whatsapp: String = "",           // "5491123456789"
    val musicUrl: String = "",           // Audio link or title
    val photos: List<String> = emptyList(), // Drawables or image urls
    val itinerary: List<ItineraryItem> = emptyList(),
    val spotifyPlaylistUrl: String = "",  // Spotify party playlist
    val instagramHashtag: String = "",    // e.g. "#ValenMis15"
    val active: Boolean = true,
    val expiresAt: String = "2027-01-31"
)

data class Guest(
    val id: String,
    val invitationId: String,
    val name: String,
    val seats: Int = 1,
    val attending: Boolean? = null, // null = pending, true = attending, false = declined
    val respondedAt: String? = null,
    val confirmedGuests: Int = 0,
    val message: String = "",
    val ticketCode: String = "",     // Unique digital pass ticket code for entry QR
    val checkedIn: Boolean = false,  // Checked in at reception
    val checkedInAt: String? = null
)

data class RsvpSubmission(
    val id: String,
    val invitationId: String,
    val guestId: String? = null,
    val name: String,
    val attending: Boolean,
    val guestCount: Int,
    val message: String = "",
    val timestamp: String = ""
)

data class PaymentRecord(
    val id: String,
    val invitationId: String,
    val ownerId: String = "admin-user",
    val totalAmount: Double,
    val depositAmount: Double, // seña
    val status: String = "senado", // "pendiente", "senado", "pagado"
    val note: String = "",
    val updatedAt: String = ""
) {
    val balanceRemaining: Double
        get() = (totalAmount - depositAmount).coerceAtLeast(0.0)
}

data class PackagePlan(
    val id: String,
    val name: String,
    val price: Double,
    val maxPhotos: Int,
    val includesMusic: Boolean,
    val includesPersonalization: Boolean,
    val description: String,
    val features: List<String>
)

data class TemplateModel(
    val id: String,
    val name: String,
    val category: String, // "cumple", "quince", "boda", "bautismo", "babyshower", "aniversario", "especial"
    val categoryLabel: String,
    val sampleImage: String = "",
    val softColorHex: String = "#F6DDE2",
    val accentColorHex: String = "#C9A24B",
    val description: String = ""
)
