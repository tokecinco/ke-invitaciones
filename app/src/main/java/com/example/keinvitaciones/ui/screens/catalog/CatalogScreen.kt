package com.example.keinvitaciones.ui.screens.catalog

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keinvitaciones.R
import com.example.keinvitaciones.data.model.PackagePlan
import com.example.keinvitaciones.data.model.TemplateModel
import com.example.keinvitaciones.data.repository.InvitationRepository
import com.example.keinvitaciones.ui.theme.DarkCharcoal
import com.example.keinvitaciones.ui.theme.GoldDark
import com.example.keinvitaciones.ui.theme.GoldPrimary
import com.example.keinvitaciones.ui.theme.SoftPink

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogScreen(
    onNavigateToDemo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { InvitationRepository.instance }
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("") }

    val categories = listOf(
        "" to "Todas",
        "quince" to "15 años",
        "boda" to "Casamientos",
        "cumple" to "Cumpleaños infantiles",
        "bautismo" to "Bautismos",
        "babyshower" to "Baby shower",
        "aniversario" to "Aniversarios",
        "especial" to "Eventos especiales"
    )

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory.isEmpty()) repository.templates
        else repository.templates.filter { it.category == selectedCategory }
    }

    fun openWhatsApp(message: String) {
        val encoded = Uri.encode(message)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/5491155554321?text=$encoded"))
        context.startActivity(intent)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("catalog_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Section
        item {
            HeroSection(
                onVerModelosClick = { selectedCategory = "" },
                onContactWhatsAppClick = {
                    openWhatsApp("¡Hola! Me gustaría consultar por las invitaciones digitales interactivas de KE Invitaciones.")
                }
            )
        }

        // How it works
        item {
            HowItWorksSection()
        }

        // Category Filter Chips
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Modelos & Diseños",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GoldDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Elegí el diseño ideal para tu gran evento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(categories) { (catId, label) ->
                        FilterChip(
                            selected = selectedCategory == catId,
                            onClick = { selectedCategory = catId },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == catId,
                                borderColor = GoldPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("chip_$catId")
                        )
                    }
                }
            }
        }

        // Template Grid/Cards
        items(filteredTemplates) { template ->
            TemplateCardItem(
                template = template,
                onVerDemo = { onNavigateToDemo("inv-valen-15") },
                onElegir = {
                    openWhatsApp("¡Hola! Me interesa la invitación digital modelo \"${template.name}\".")
                }
            )
        }

        // Packages & Pricing Section
        item {
            PricingSection(
                packages = repository.packages,
                onSelectPackage = { pkg ->
                    openWhatsApp("¡Hola! Quiero contratar el paquete ${pkg.name} para mi evento.")
                }
            )
        }

        // FAQ Section
        item {
            FaqSection()
        }

        // Footer
        item {
            FooterSection(
                onContactClick = {
                    openWhatsApp("Hola KE Invitaciones, tengo una consulta sobre las invitaciones digitales.")
                }
            )
        }
    }
}

@Composable
private fun HeroSection(
    onVerModelosClick: () -> Unit,
    onContactWhatsAppClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_invitations),
                    contentDescription = "KE Invitaciones Portada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xAA1A1A1A))
                            )
                        )
                )
                Text(
                    text = "KE INVITACIONES",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Una invitación especial para un momento inolvidable",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DarkCharcoal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Invitaciones digitales interactivas con sobre animado, música de fondo, fotos, cuenta regresiva y confirmación de asistencia en tiempo real.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onVerModelosClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_hero_ver_modelos")
                    ) {
                        Text("Ver modelos", color = Color.White)
                    }

                    Button(
                        onClick = onContactWhatsAppClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_hero_whatsapp")
                    ) {
                        Text("WhatsApp", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun HowItWorksSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Cómo funciona",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        val steps = listOf(
            Triple("1", "Elegí un modelo", "Mirá las demostraciones y elegí el diseño que más te guste."),
            Triple("2", "Pasanos los datos", "Nombre, fecha, hora, lugar, fotos y el mensaje que quieras compartir."),
            Triple("3", "Recibí tu enlace", "Creamos tu invitación interactiva personalizada y te enviamos el enlace y el código QR."),
            Triple("4", "Compartí y recibí respuestas", "Tus invitados confirman asistencia desde la invitación y vos ves todo en tiempo real.")
        )

        steps.forEach { (num, title, desc) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(16.dp),
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
                            .background(GoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkCharcoal
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateCardItem(
    template: TemplateModel,
    onVerDemo: () -> Unit,
    onElegir: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("template_card_${template.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Preview header banner with themed soft background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(android.graphics.Color.parseColor(template.softColorHex)),
                                Color(android.graphics.Color.parseColor(template.accentColorHex)).copy(alpha = 0.25f)
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(android.graphics.Color.parseColor(template.accentColorHex)),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = template.categoryLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = DarkCharcoal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onVerDemo,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_demo_${template.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkCharcoal)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver demo")
                    }

                    Button(
                        onClick = onElegir,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_elegir_${template.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text("Elegir modelo", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun PricingSection(
    packages: List<PackagePlan>,
    onSelectPackage: (PackagePlan) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Precios & Paquetes",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Precios accesibles y transparentes. Se abona una seña para reservar.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
        )

        packages.forEach { pkg ->
            val isPremium = pkg.id == "premium"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("pricing_card_${pkg.id}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremium) Color(0xFFFFF7EB) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isPremium) 2.dp else 1.dp,
                    color = if (isPremium) GoldPrimary else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (isPremium) {
                        Surface(
                            color = GoldPrimary,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "MÁS ELEGIDO",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Paquete ${pkg.name}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%,.0f", pkg.price).replace(',', '.')}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    )
                    Text(
                        text = pkg.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    pkg.features.forEach { feat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = feat,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkCharcoal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onSelectPackage(pkg) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPremium) GoldPrimary else DarkCharcoal
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Elegir ${pkg.name}",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqSection() {
    val faqs = listOf(
        "¿Cómo es una invitación digital?" to
                "Es una experiencia interactiva en tu teléfono: sobre con animación, portada con foto, cuenta regresiva en vivo, ubicación en el mapa, galería y confirmación de asistencia. Se comparte mediante enlace o código QR por WhatsApp.",
        "¿Cómo confirman asistencia mis invitados?" to
                "Desde la misma invitación: escriben su nombre, confirman si asisten y cuántas personas van (o sus cupos reservados), y pueden dejarte un mensaje. Vos ves el total y los detalles al instante desde tu cuenta.",
        "¿Se puede agregar música?" to
                "Sí. En el paquete Premium la invitación cuenta con música de fondo con botón de reproducción y pausa. Empieza cuando el invitado abre el sobre.",
        "¿Cómo se paga?" to
                "Se abona una seña inicial para comenzar el diseño y el saldo se coordina al momento de la entrega final por WhatsApp.",
        "¿Puedo cambiar datos después de publicada?" to
                "Sí. Desde tu cuenta podés editar el mensaje, el texto de bienvenida, la información adicional y el teléfono de contacto.",
        "¿Hasta cuándo está disponible mi invitación?" to
                "Permanece activa hasta semanas después del evento para que todos puedan confirmar y guardar el recuerdo."
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Preguntas frecuentes",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        faqs.forEach { (question, answer) ->
            var expanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { expanded = !expanded },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkCharcoal,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = GoldDark
                        )
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = answer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterSection(onContactClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCharcoal)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "KE INVITACIONES",
                style = MaterialTheme.typography.titleLarge.copy(
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Invitaciones digitales interactivas para momentos especiales",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onContactClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Escribinos por WhatsApp")
            }
        }
    }
}
