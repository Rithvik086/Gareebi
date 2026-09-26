package com.example.gareebi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// ------------------------------------------------------------
// Gareebi Color System
// ------------------------------------------------------------

private val GareebiBlack = Color(0xFF0D0D0D)
private val GareebiCard = Color(0xFF171717)
private val GareebiElevated = Color(0xFF1D1D1D)

private val GareebiWhite = Color(0xFFF5F5F5)
private val GareebiGray = Color(0xFFA3A3A3)
private val GareebiMuted = Color(0xFF666666)
private val GareebiBorder = Color(0xFF292929)

private val GareebiOrange = Color(0xFFF97316)
private val GareebiGreen = Color(0xFF4ADE80)


// ------------------------------------------------------------
// Activity
// ------------------------------------------------------------

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = GareebiPreferences(applicationContext)

        setContent {
            GareebiTheme {
                GareebiApp(preferences)
            }
        }
    }
}


// ------------------------------------------------------------
// Theme
// ------------------------------------------------------------

@Composable
private fun GareebiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = GareebiOrange,
            onPrimary = GareebiBlack,

            secondary = GareebiOrange,
            onSecondary = GareebiBlack,

            background = GareebiBlack,
            onBackground = GareebiWhite,

            surface = GareebiCard,
            onSurface = GareebiWhite,

            surfaceVariant = GareebiElevated,
            onSurfaceVariant = GareebiGray,

            outline = GareebiBorder
        ),
        content = content
    )
}


// ------------------------------------------------------------
// Main App
// ------------------------------------------------------------

@Composable
fun GareebiApp(
    preferences: GareebiPreferences
) {
    val monthlyLimit by preferences.monthlyLimit.collectAsState(
        initial = 15000.0
    )

    val scope = rememberCoroutineScope()

    var showLimitEditor by remember {
        mutableStateOf(false)
    }

    var limitText by remember {
        mutableStateOf("")
    }

    // Temporary until Room transaction database is implemented.
    val spent = 0.0

    val remaining = (monthlyLimit - spent).coerceAtLeast(0.0)

    val progress =
        if (monthlyLimit > 0) {
            (spent / monthlyLimit).toFloat().coerceIn(0f, 1f)
        } else {
            0f
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GareebiBlack)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ----------------------------------------------------
        // Header
        // ----------------------------------------------------

        item {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "GAREEBI",
                    color = GareebiWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "GUARD",
                    color = GareebiOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }


        // ----------------------------------------------------
        // Guard Status
        // ----------------------------------------------------

        item {
            GuardStatus()
        }


        // ----------------------------------------------------
        // Monthly Spending
        // ----------------------------------------------------

        item {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                SectionTitle("MONTHLY SPENDING")

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GareebiCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        GareebiBorder
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {

                            Column {

                                Text(
                                    text = formatRupees(spent),
                                    color = GareebiWhite,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(2.dp)
                                )

                                Text(
                                    text = "of ${formatRupees(monthlyLimit)}",
                                    color = GareebiGray,
                                    fontSize = 14.sp
                                )
                            }

                            StatusBadge(
                                text = if (spent < monthlyLimit) {
                                    "UNDER LIMIT"
                                } else {
                                    "LIMIT EXCEEDED"
                                },
                                positive = spent < monthlyLimit
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(
                                    RoundedCornerShape(4.dp)
                                ),
                            color = GareebiOrange,
                            trackColor = GareebiBorder
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        Text(
                            text = "${formatRupees(remaining)} remaining",
                            color = GareebiGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }


        // ----------------------------------------------------
        // Daily spending information
        // ----------------------------------------------------

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "SAFE DAILY BURN",
                    value = formatRupees(
                        if (monthlyLimit > 0) {
                            monthlyLimit / 30
                        } else {
                            0.0
                        }
                    )
                )

                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "SPENT TODAY",
                    value = formatRupees(0.0)
                )
            }
        }


        // ----------------------------------------------------
        // Actions
        // ----------------------------------------------------

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = {
                        limitText = monthlyLimit.toInt().toString()
                        showLimitEditor = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GareebiOrange,
                        contentColor = GareebiBlack
                    )
                ) {

                    Text(
                        text = "SET LIMIT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }


                OutlinedButton(
                    onClick = {
                        // Transactions screen will be implemented later.
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = GareebiCard,
                        contentColor = GareebiWhite
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        GareebiBorder
                    )
                ) {

                    Text(
                        text = "ALL TRANSACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }


        // ----------------------------------------------------
        // Recent Transactions
        // ----------------------------------------------------

        item {

            Column {

                SectionTitle("RECENT TRANSACTIONS")

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                EmptyTransactions()
            }
        }


        // ----------------------------------------------------
        // SMS Sync
        // ----------------------------------------------------

        item {
            SmsSyncStatus()
        }


        // ----------------------------------------------------
        // Bottom navigation placeholder
        // ----------------------------------------------------

        item {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            BottomNavigation()

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }


    // --------------------------------------------------------
    // Limit Editor
    // --------------------------------------------------------

    if (showLimitEditor) {

        LimitEditor(
            limitText = limitText,
            onLimitChange = {
                limitText = it
            },
            onDismiss = {
                showLimitEditor = false
            },
            onSave = {

                val newLimit = limitText.toDoubleOrNull()

                if (newLimit != null && newLimit > 0) {

                    scope.launch {
                        preferences.setMonthlyLimit(newLimit)
                    }

                    showLimitEditor = false
                }
            }
        )
    }
}


// ------------------------------------------------------------
// Guard Status
// ------------------------------------------------------------

@Composable
private fun GuardStatus() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GareebiCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GareebiBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(GareebiGreen)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column {

                Text(
                    text = "GUARD ACTIVE",
                    color = GareebiWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Monitoring your spending",
                    color = GareebiGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}


// ------------------------------------------------------------
// Status Badge
// ------------------------------------------------------------

@Composable
private fun StatusBadge(
    text: String,
    positive: Boolean
) {

    val background =
        if (positive) {
            Color(0xFF16241A)
        } else {
            Color(0xFF24170F)
        }

    val textColor =
        if (positive) {
            GareebiGreen
        } else {
            GareebiOrange
        }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .padding(
                horizontal = 9.dp,
                vertical = 6.dp
            )
    ) {

        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )
    }
}


// ------------------------------------------------------------
// Metric Card
// ------------------------------------------------------------

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GareebiCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GareebiBorder
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = label,
                color = GareebiMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = value,
                color = GareebiWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ------------------------------------------------------------
// Empty Transactions
// ------------------------------------------------------------

@Composable
private fun EmptyTransactions() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GareebiCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GareebiBorder
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = "NO TRANSACTIONS YET",
                color = GareebiWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Transactions will appear here once SMS sync is enabled.",
                color = GareebiGray,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}


// ------------------------------------------------------------
// SMS Sync
// ------------------------------------------------------------

@Composable
private fun SmsSyncStatus() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GareebiCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GareebiBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(RoundedCornerShape(50))
                    .background(GareebiGreen)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column {

                Text(
                    text = "SMS SYNC",
                    color = GareebiWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Waiting for transaction integration",
                    color = GareebiGray,
                    fontSize = 11.sp
                )
            }
        }
    }
}


// ------------------------------------------------------------
// Bottom Navigation
// ------------------------------------------------------------

@Composable
private fun BottomNavigation() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        NavigationItem(
            text = "HOME",
            selected = true
        )

        NavigationItem(
            text = "TRANSACTIONS",
            selected = false
        )

        NavigationItem(
            text = "SETTINGS",
            selected = false
        )
    }
}


@Composable
private fun NavigationItem(
    text: String,
    selected: Boolean
) {

    Text(
        text = text,
        color = if (selected) {
            GareebiOrange
        } else {
            GareebiMuted
        },
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.7.sp
    )
}


// ------------------------------------------------------------
// Section Title
// ------------------------------------------------------------

@Composable
private fun SectionTitle(
    text: String
) {

    Text(
        text = text,
        color = GareebiGray,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp
    )
}


// ------------------------------------------------------------
// Limit Editor
// ------------------------------------------------------------

@Composable
private fun LimitEditor(
    limitText: String,
    onLimitChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = GareebiCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                GareebiBorder
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "MONTHLY LIMIT",
                    color = GareebiWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Set the maximum amount you want to spend this month.",
                    color = GareebiGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                OutlinedTextField(
                    value = limitText,
                    onValueChange = onLimitChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    label = {
                        Text(
                            text = "Amount",
                            color = GareebiGray
                        )
                    },
                    leadingIcon = {
                        Text(
                            text = "₹",
                            color = GareebiOrange,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GareebiOrange,
                        unfocusedBorderColor = GareebiBorder,
                        focusedLabelColor = GareebiOrange,
                        unfocusedLabelColor = GareebiGray,
                        cursorColor = GareebiOrange,
                        focusedTextColor = GareebiWhite,
                        unfocusedTextColor = GareebiWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            GareebiBorder
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GareebiWhite
                        )
                    ) {

                        Text(
                            text = "CANCEL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GareebiOrange,
                            contentColor = GareebiBlack
                        )
                    ) {

                        Text(
                            text = "SAVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// ------------------------------------------------------------
// Formatting
// ------------------------------------------------------------

private fun formatRupees(
    amount: Double
): String {

    val formatter = NumberFormat.getCurrencyInstance(
        Locale("en", "IN")
    )

    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0

    return formatter.format(amount)
}