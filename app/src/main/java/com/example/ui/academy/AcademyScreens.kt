package com.example.ui.academy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.CareOsisApp
import com.example.core.components.*
import com.example.data.local.entity.AssessmentQuestionEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.TrainingProgressEntity
import com.example.ui.navigation.Destinations
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyDashboardScreen(
    onNavigate: (String) -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val profile by repository.getProfile().collectAsStateWithLifecycle(initialValue = null)
    val products by repository.getAllProducts().collectAsStateWithLifecycle(initialValue = emptyList())
    val progressList by repository.getAllTrainingProgress().collectAsStateWithLifecycle(initialValue = emptyList())

    val progressMap = remember(progressList) {
        progressList.associateBy { it.productId }
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "CareOsis Academy",
                subtitle = "Product Mastery & Certification",
                actions = {
                    IconButton(onClick = { onNavigate(Destinations.LEADERBOARD) }) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard", tint = ClinicalWhite)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Certification Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "MR Certification Level",
                                    style = MaterialTheme.typography.bodySmall.copy(color = GoldLight)
                                )
                                Text(
                                    text = profile?.level ?: "Expert MR",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ClinicalWhite
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldMetallic
                            ) {
                                Text(
                                    text = "${profile?.trainingProgressPercent ?: 78}% Complete",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        CareOsisProgressBar(
                            progressPercent = profile?.trainingProgressPercent ?: 78,
                            progressColor = GoldMetallic,
                            trackColor = ClinicalWhite.copy(alpha = 0.25f),
                            barHeight = 10.dp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Complete all 19 pharmaceutical product masterclasses to unlock Master MR badge and +15% incentive boost.",
                            style = MaterialTheme.typography.bodySmall.copy(color = ClinicalWhite.copy(alpha = 0.9f))
                        )
                    }
                }
            }

            // Section: All 19 Products
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "19 Pharmaceutical Masterclasses",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Catalog",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        ),
                        modifier = Modifier.clickable { onNavigate(Destinations.PRODUCT_LIST) }
                    )
                }
            }

            items(products, key = { it.id }) { product ->
                val progress = progressMap[product.id]
                val percent = progress?.completionPercentage ?: 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("product_detail/${product.id}") }
                        .testTag("academy_item_${product.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (percent >= 80) EmeraldPrimary else GoldDark
                                    )
                                )
                            }
                            Text(
                                text = "${product.category} • MRP ₹${product.mrp.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            CareOsisProgressBar(
                                progressPercent = percent,
                                barHeight = 6.dp,
                                progressColor = if (percent >= 80) EmeraldPrimary else GoldDark
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val products by repository.getAllProducts().collectAsStateWithLifecycle(initialValue = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct()
    }

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { prod ->
            val matchesQuery = prod.name.contains(searchQuery, ignoreCase = true) ||
                    prod.composition.contains(searchQuery, ignoreCase = true) ||
                    prod.indications.contains(searchQuery, ignoreCase = true)
            val matchesCategory = if (selectedCategory == "All") true else prod.category == selectedCategory
            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Product Portfolio",
                subtitle = "19 CareOsis Formulations",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("product_search_bar"),
                placeholder = { Text("Search composition, indications, brand...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = ClinicalWhite
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 24.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate("product_detail/${product.id}") }
                            .testTag("product_card_${product.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = product.category,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                                CareOsisStatusChip(
                                    text = "MRP ₹${product.mrp.toInt()}",
                                    containerColor = GoldContainer,
                                    contentColor = OnGoldContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.composition,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Pack: ${product.packaging} • Retailer Rate: ₹${product.retailerRate.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeutralTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val product by repository.getProductById(productId).collectAsStateWithLifecycle(initialValue = null)
    val progress by repository.getProgressForProduct(productId).collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = product?.name ?: "Product Masterclass",
                subtitle = product?.category,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (product == null) {
            CareOsisLoadingState(message = "Loading product masterclass...")
        } else {
            val prod = product!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ClinicalBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Product Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prod.name,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ClinicalWhite
                                        )
                                    )
                                    Text(
                                        text = prod.category,
                                        style = MaterialTheme.typography.titleSmall.copy(color = GoldLight)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(ClinicalWhite.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Vaccines,
                                        contentDescription = null,
                                        tint = GoldMetallic,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Pack Size: ${prod.packaging} • Available for Sampling",
                                style = MaterialTheme.typography.bodySmall.copy(color = ClinicalWhite.copy(alpha = 0.9f))
                            )
                        }
                    }
                }

                // Interactive Tools Grid (MoA, Competitor Battle, Assessment)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigate("moa_visualizer/${prod.id}") },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, EmeraldPrimary)
                        ) {
                            Text("MoA Visualizer", color = EmeraldPrimary, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        Button(
                            onClick = { onNavigate("competitor_battle/${prod.id}") },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, GoldDark)
                        ) {
                            Text("Battlecard", color = GoldDark, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                // Scientific Composition
                item {
                    DetailSectionCard(title = "Active Composition & Formulation") {
                        Text(
                            text = prod.composition,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                // Key Benefits
                item {
                    DetailSectionCard(title = "Key USPs & Clinical Highlights") {
                        Text(
                            text = prod.keyBenefits,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }

                // Clinical Indications
                item {
                    DetailSectionCard(title = "Indications & Target Patient Cohorts") {
                        Text(
                            text = prod.indications,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }

                // Dosage & Administration
                item {
                    DetailSectionCard(title = "Dosage & Administration Guidelines") {
                        Text(
                            text = prod.dosage,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }

                // Commercial Pricing Structure
                item {
                    DetailSectionCard(title = "Commercial Economics") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PricePill(label = "MRP", price = prod.mrp)
                            PricePill(label = "Rate to Retailer", price = prod.retailerRate)
                            PricePill(label = "Estimated Margin", price = prod.mrp - prod.retailerRate)
                        }
                    }
                }

                // Detailing Pitch Script
                item {
                    DetailSectionCard(title = "Field Detailing Pitch Script") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ClinicalBackground,
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "\"${prod.mrPitch}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = EmeraldDark
                                    )
                                )
                            }
                        }
                    }
                }

                // Assessment & Certification Launch
                item {
                    CareOsisPrimaryButton(
                        text = "Take 3-Min Certification Assessment",
                        onClick = { onNavigate("assessment/${prod.id}") },
                        icon = Icons.Default.Quiz,
                        testTag = "start_assessment_button"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun PricePill(label: String, price: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Text(
            text = "₹${price.toInt()}",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoaVisualizerScreen(
    productId: String,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val product by repository.getProductById(productId).collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Mechanism of Action",
                subtitle = product?.name ?: "MoA Visualizer",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Biochemical Pathway Overview",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product?.mechanismOfAction ?: "Loading mechanism...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }

            // 4-Step Visual Pathway Diagram
            item {
                Text(
                    text = "4-Phase Cellular Action Flow",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            val steps = listOf(
                Pair("1. Rapid Solubilization & Ingestion", "Pre-dissolved active micro-crystals enter mucosal contact within 3 minutes of oral delivery."),
                Pair("2. Enhanced Ion Membrane Transport", "Direct receptor-mediated endocytosis bypasses first-pass hepatic degradation."),
                Pair("3. Mitochondrial Bio-Activation", "Active compounds stimulate intracellular ATP release and targeted enzyme co-factors."),
                Pair("4. Sustained 24h Clinical Response", "Elimination half-life calibrated to maintain steady-state serum concentration without accumulation.")
            )

            items(steps) { (title, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = ClinicalWhite, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall.copy(color = NeutralTextSecondary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitorBattleScreen(
    productId: String,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val product by repository.getProductById(productId).collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Competitor Battlecard",
                subtitle = product?.name ?: "Clinical Comparison",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Win-Themes for Prescribers",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ClinicalWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Why doctors should switch to ${product?.name ?: "CareOsis"} from conventional brand alternatives.",
                            style = MaterialTheme.typography.bodySmall.copy(color = GoldLight)
                        )
                    }
                }
            }

            val battleMetrics = listOf(
                Triple("Bioavailability Index", "98% (Effervescent Solution)", "42% (Traditional Compressed Tablets)"),
                Triple("Onset of Action", "< 15 Minutes", "45 - 90 Minutes"),
                Triple("GI Side-Effects / Gastric Burning", "0% Reported in Clinical Trials", "28% Patients report constipation & acidity"),
                Triple("Patient Compliance Rate", "96% (Pleasant Taste / Single Dose)", "64% (Multiple large pills)"),
                Triple("Monthly Therapy Cost", "Optimized 1x Daily (₹${product?.mrp?.toInt() ?: 450})", "Expensive 3x Daily (₹750 - ₹900)")
            )

            items(battleMetrics) { (metric, careOsisAdvantage, competitorFlaw) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = metric,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // CareOsis advantage
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CareOsis: $careOsisAdvantage",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = OnEmeraldContainer)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Competitor flaw
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StatusErrorContainer.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, tint = StatusError, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Competitor: $competitorFlaw",
                                    style = MaterialTheme.typography.bodySmall.copy(color = StatusError)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    productId: String,
    onAssessmentCompleted: () -> Unit,
    onBackClick: () -> Unit
) {
    val repository = remember { CareOsisApp.instance.repository }
    val scope = rememberCoroutineScope()
    val product by repository.getProductById(productId).collectAsStateWithLifecycle(initialValue = null)
    val questions by repository.getQuestionsForProduct(productId).collectAsStateWithLifecycle(initialValue = emptyList())

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CareOsisTopBar(
                title = "Certification Assessment",
                subtitle = product?.name ?: "Product Quiz",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (questions.isEmpty()) {
            CareOsisEmptyState(
                title = "No Questions Available",
                description = "Masterclass questions for this product are being synchronized.",
                modifier = Modifier.padding(innerPadding)
            )
        } else if (isSubmitted) {
            // Results screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ClinicalBackground)
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(if (score >= 2) EmeraldPrimary else GoldDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (score >= 2) Icons.Default.WorkspacePremium else Icons.Default.Refresh,
                        contentDescription = null,
                        tint = ClinicalWhite,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (score >= 2) "Congratulations! Certified" else "Good Try! Review & Retake",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You scored $score out of ${questions.size} questions correct (${(score * 100) / questions.size}%).",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(24.dp))
                CareOsisPrimaryButton(
                    text = "Done & Save Progress",
                    onClick = onAssessmentCompleted,
                    testTag = "assessment_done_button"
                )
            }
        } else {
            val q = questions[currentQuestionIndex]
            val qOptions = listOf(q.optionA, q.optionB, q.optionC, q.optionD)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ClinicalBackground)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Quiz progress tracker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                        Text(
                            text = "${((currentQuestionIndex + 1) * 100) / questions.size}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    CareOsisProgressBar(
                        progressPercent = ((currentQuestionIndex + 1) * 100) / questions.size,
                        barHeight = 6.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Question Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = q.questionText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            qOptions.forEachIndexed { index, option ->
                                val isSelected = userAnswers[currentQuestionIndex] == index
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable { userAnswers[currentQuestionIndex] = index },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) EmeraldContainer else ClinicalBackground,
                                    border = BorderStroke(1.dp, if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { userAnswers[currentQuestionIndex] = index },
                                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) OnEmeraldContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Next or Submit Button
                CareOsisPrimaryButton(
                    text = if (currentQuestionIndex < questions.size - 1) "Next Question" else "Submit Assessment",
                    onClick = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            // Calculate Score
                            var correctCount = 0
                            questions.forEachIndexed { idx, question ->
                                if (userAnswers[idx] == question.correctOptionIndex) {
                                    correctCount++
                                }
                            }
                            score = correctCount
                            isSubmitted = true

                            // Save to DB
                            scope.launch {
                                val completion = if (questions.isNotEmpty()) (correctCount * 100) / questions.size else 100
                                repository.saveTrainingProgress(
                                    TrainingProgressEntity(
                                        productId = productId,
                                        productName = product?.name ?: "Product",
                                        category = product?.category ?: "Core",
                                        dossierRead = true,
                                        videoWatched = true,
                                        completionPercentage = completion,
                                        quizScore = score,
                                        isCompleted = score >= 2,
                                        lastAccessedAt = System.currentTimeMillis()
                                    )
                                )
                            }
                        }
                    },
                    testTag = "assessment_next_button"
                )
            }
        }
    }
}
