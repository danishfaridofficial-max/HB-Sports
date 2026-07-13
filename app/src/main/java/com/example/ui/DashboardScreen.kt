package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.LiveMatch
import com.example.data.StreamItem
import com.example.data.TournamentMatch
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardDark
import com.example.ui.theme.DarkBlueBackground
import com.example.ui.theme.GreenLive
import com.example.ui.theme.LightGray
import com.example.ui.theme.MutedText
import com.example.ui.theme.PitchDark
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.StadiumBlack
import com.example.ui.theme.White
import com.example.ui.theme.YellowNeon
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: StreamViewModel,
    modifier: Modifier = Modifier
) {
    val streams by viewModel.allStreams.collectAsState()
    val selectedStream by viewModel.selectedStream.collectAsState()
    val liveMatches by viewModel.liveMatchesList.collectAsState()
    val selectedLiveMatch by viewModel.selectedLiveMatch.collectAsState()
    val upcomingMatches by viewModel.upcomingMatches.collectAsState()
    val activeUsersCount by viewModel.activeUsersCount.collectAsState()

    val appUpdate by viewModel.appUpdate.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val updateStatusMessage by viewModel.updateStatusMessage.collectAsState()
    val manualUpdateMessage by viewModel.manualUpdateMessage.collectAsState()

    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(manualUpdateMessage) {
        manualUpdateMessage?.let { msg ->
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearManualUpdateMessage()
        }
    }
    androidx.compose.runtime.DisposableEffect(Unit) {
        val activity = context as? android.app.Activity
        activity?.window?.let { window ->
            val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
            insetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            insetsController.isAppearanceLightStatusBars = false
        }
        onDispose {}
    }

    var activeTab by remember { mutableIntStateOf(0) }
    var showAddStreamDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showExitDialog = true
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0F172A), // Professional Deep Slate Blue
                drawerContentColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                // Professional branding header inside side navigation drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryRed, AccentRed)
                            )
                        )
                        .padding(vertical = 32.dp, horizontal = 24.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "HB",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "HB Sports",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Premium Live Sports Streaming",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Drawer Items: Home
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            scope.launch { drawerState.close() }
                            activeTab = 0
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (activeTab == 0) AccentRed else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Home",
                        color = if (activeTab == 0) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Drawer Items: About
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            scope.launch { drawerState.close() }
                            showAboutDialog = true
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "About",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Drawer Items: Contact Us
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            scope.launch { drawerState.close() }
                            showContactDialog = true
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Contact Us",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Contact Us",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Drawer Items: Check for Updates
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            scope.launch { drawerState.close() }
                            viewModel.checkForUpdates(isManual = true)
                            android.widget.Toast.makeText(context, "Checking for updates...", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Check for Updates",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Check for Updates",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = DarkBlueBackground,
            topBar = {
                DashboardHeader(
                    viewersCount = activeUsersCount,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onShareClick = {
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(
                                android.content.Intent.EXTRA_TEXT,
                                "HB Sports Live Streaming app install karein aur live sports, cricket matches free me dekhain!\nApp Link: https://github.com/danishfaridofficial-max/HB-Sports/releases/latest/download/app-debug.apk"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Share App Via")
                        context.startActivity(shareIntent)
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Persistent Video Player section at top
                selectedStream?.let { stream ->
                    VideoPlayerSection(
                        stream = stream,
                        viewersCount = activeUsersCount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.55f)
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.55f)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading Live Stream...",
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                }

                val marqueeText by viewModel.marqueeText.collectAsState()

                // HTML Notice/Message Marquee Bar
                MarqueeBanner(text = marqueeText)

                // Navigation Tabs below the player
                SportsTabRow(
                    selectedTabIndex = activeTab,
                    onTabSelected = { activeTab = it }
                )

                // Content Area depending on Tab selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkBlueBackground)
                ) {
                    when (activeTab) {
                        0 -> {
                            val syncStatus by viewModel.syncStatus.collectAsState()
                            val isRefreshing = syncStatus is StreamViewModel.SyncStatus.Loading
                            PullToRefreshContainer(
                                isRefreshing = isRefreshing,
                                onRefresh = { viewModel.syncFromSpreadsheet(viewModel.spreadsheetId.value) }
                            ) {
                                StreamsTabContent(
                                    streams = streams,
                                    selectedStream = selectedStream,
                                    viewersCount = activeUsersCount,
                                    onStreamSelect = { viewModel.selectStream(it) },
                                    onStreamDelete = { viewModel.deleteStream(it) },
                                    onAddClick = { showAddStreamDialog = true }
                                )
                            }
                        }
                        1 -> SettingsContent(viewModel = viewModel)
                    }
                }
            }

            // Expanded Bottom Sheet Card for Custom Stream Adding
            if (showAddStreamDialog) {
                AddStreamDialog(
                    onDismiss = { showAddStreamDialog = false },
                    onAddStream = { title, url ->
                        viewModel.addCustomStream(title, url)
                        showAddStreamDialog = false
                    }
                )
            }
        }
    }

    // Modern styled, high contrast dialogs
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(text = "About HB Sports", fontWeight = FontWeight.Bold, color = White) },
            text = { Text(text = "HB Sports is your premier application for streaming high-quality sports events live. We bring the stadium experience directly to your device with real-time streaming, live match stats, and upcoming schedules.", color = LightGray) },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = AccentRed, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardDark,
            titleContentColor = White,
            textContentColor = LightGray
        )
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text(text = "Contact Us", fontWeight = FontWeight.Bold, color = White) },
            text = { Text(text = "We would love to hear from you! For support, inquiries, or feedback, feel free to reach out to us at:\n\n📧 support@hbsports.com\n📞 +1 (800) 123-4567\n\nOur team is available 24/7 to assist you.", color = LightGray) },
            confirmButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Close", color = AccentRed, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardDark,
            titleContentColor = White,
            textContentColor = LightGray
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Exit App?", fontWeight = FontWeight.Bold, color = White) },
            text = { Text(text = "Are you sure you want to exit HB Sports?", color = LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        val activity = context as? android.app.Activity
                        activity?.finishAffinity()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Yes", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("No", color = LightGray)
                }
            },
            containerColor = CardDark,
            titleContentColor = White,
            textContentColor = LightGray
        )
    }

    if (appUpdate != null) {
        AlertDialog(
            onDismissRequest = { /* Disable click-outside dismiss to keep dialog showing during critical update operations */ },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Update Available",
                        tint = AccentRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "New Update Available!",
                        fontWeight = FontWeight.Bold,
                        color = White,
                        fontSize = 20.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Version: ${appUpdate?.versionName}",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "What's New:",
                        color = White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = appUpdate?.changeLog ?: "No changelog provided.",
                        color = LightGray,
                        fontSize = 13.sp
                    )
                    
                    if (downloadProgress != null || updateStatusMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val progress = downloadProgress ?: 0f
                            val statusText = updateStatusMessage ?: "Downloading..."
                            
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AccentRed,
                                trackColor = Color.White.copy(alpha = 0.15f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$statusText (${(progress * 100).toInt()}%)",
                                color = LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (downloadProgress == null) {
                    Button(
                        onClick = {
                            appUpdate?.let {
                                viewModel.downloadAndInstallApk(context, it.downloadUrl)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                    ) {
                        Text("Update Now", color = White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (downloadProgress == null) {
                    TextButton(
                        onClick = { viewModel.dismissUpdate() }
                    ) {
                        Text("Later", color = LightGray)
                    }
                }
            },
            containerColor = CardDark,
            titleContentColor = White,
            textContentColor = LightGray
        )
    }
}

@Composable
fun DashboardHeader(viewersCount: String, onMenuClick: () -> Unit, onShareClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StadiumBlack)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp), // Taller vertical padding for a larger header
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left hamburger/menu icon
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Center HB Sports Logo Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Stylized 'HB' icon/badge
                Box(
                    modifier = Modifier
                        .size(36.dp) // Enlarged badge
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryRed, AccentRed)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "HB",
                        color = White,
                        fontSize = 18.sp, // Enlarged font
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sports",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp, // Enlarged font
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("app_title_header")
                )
            }

            // Right Share App button
            IconButton(
                onClick = onShareClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share App",
                    tint = White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EyeIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Draw the main eye outer contour using elegant cubic curves
        val path = Path().apply {
            moveTo(0.5f, height / 2f)
            cubicTo(
                width * 0.25f, -height * 0.08f,
                width * 0.75f, -height * 0.08f,
                width - 0.5f, height / 2f
            )
            cubicTo(
                width * 0.75f, height * 1.08f,
                width * 0.25f, height * 1.08f,
                0.5f, height / 2f
            )
            close()
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.75.dp.toPx())
        )
        
        // Draw the inner pupil/iris
        drawCircle(
            color = color,
            radius = height * 0.26f,
            center = center
        )
    }
}

@Composable
fun FacebookLiveBadge(viewersCount: String, modifier: Modifier = Modifier) {
    // Semi-transparent Eye + Viewers count Badge matching the screenshot (Red LIVE is removed)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color(0xFF1C1C1E).copy(alpha = 0.75f), shape = RoundedCornerShape(4.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        EyeIcon(
            modifier = Modifier.size(17.dp, 11.dp), // Stylish and slightly larger eye icon!
            color = Color.White
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = viewersCount,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LivePulseDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(10.dp)
            .drawBehind {
                drawCircle(
                    color = AccentRed.copy(alpha = alpha),
                    radius = size.minDimension / 2f + 4.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = AccentRed,
                    radius = size.minDimension / 2f
                )
            }
    )
}

@Composable
fun VideoPlayerSection(
    stream: StreamItem,
    viewersCount: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp) // px-4 py-2
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)), // border-white/10
        shape = RoundedCornerShape(16.dp), // rounded-2xl
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)) // bg-[#121212]
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            VideoPlayer(
                url = stream.url,
                title = stream.title,
                modifier = Modifier.fillMaxSize()
            )

            // Facebook Live-style floating badges at the top-left of the player
            FacebookLiveBadge(
                viewersCount = viewersCount,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarqueeBanner(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // mx-4 mt-2 mb-4
            .background(
                color = PrimaryRed.copy(alpha = 0.1f), // bg-red-600/10
                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp) // rounded-r-xl
            )
            .drawBehind {
                // border-l-2 border-red-600
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = PrimaryRed,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .padding(vertical = 12.dp, horizontal = 16.dp), // py-3 px-4
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notification Bell",
            tint = AccentRed, // red-500
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color(0xFFFEE2E2), // text-red-100
            fontSize = 12.sp, // text-xs
            fontWeight = FontWeight.Medium, // font-medium
            letterSpacing = 0.5.sp, // tracking-wide
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .weight(1f)
                .basicMarquee(
                    iterations = Int.MAX_VALUE
                )
        )
    }
}

@Composable
fun SportsTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabTitles = listOf("STREAMS", "SETTINGS")
    val tabIcons = listOf(Icons.Default.Home, Icons.Default.Settings)

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color(0xFF121212), // bg-[#121212]
        contentColor = White,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = PrimaryRed, // red-600
                height = 3.dp
            )
        },
        divider = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.05f)) // border-white/5
            )
        }
    ) {
        tabTitles.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 32.dp) // w-12 h-8
                                .background(
                                    color = if (isSelected) PrimaryRed.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tabIcons[index],
                                contentDescription = title,
                                tint = if (isSelected) PrimaryRed else MutedText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) White else MutedText
                        )
                    }
                },
                modifier = Modifier.testTag("tab_$index")
            )
        }
    }
}

@Composable
fun StreamsTabContent(
    streams: List<StreamItem>,
    selectedStream: StreamItem?,
    viewersCount: String,
    onStreamSelect: (StreamItem) -> Unit,
    onStreamDelete: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main channel streams section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECT CHANNEL",
                    color = MutedText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "See All",
                    color = PrimaryRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Circular Server Buttons Grid, matches HTML style
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                streams.filter { !it.isCustom }.forEach { stream ->
                    val isActive = selectedStream?.id == stream.id

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onStreamSelect(stream) }
                            .testTag("stream_button_${stream.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp) // w-16 h-16
                                .background(CardDark, shape = CircleShape)
                                .border(
                                    width = if (isActive) 2.dp else 1.dp,
                                    color = if (isActive) PrimaryRed else Color.White.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(stream.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stream.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stream.title,
                            color = if (isActive) PrimaryRed else MutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(76.dp)
                        )
                    }
                }
            }
        }




    }
}

@Composable
fun MatchCenterContent(
    liveMatches: List<LiveMatch>,
    selectedMatch: LiveMatch?,
    onMatchSelect: (LiveMatch) -> Unit,
    onRefreshClick: () -> Unit
) {
    if (liveMatches.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = PrimaryRed)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Loading Live Scores...", color = LightGray, fontSize = 14.sp)
            }
        }
        return
    }

    val activeMatch = selectedMatch ?: liveMatches.first()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Google Style Header Search Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = YellowNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Google Cricket Live Score",
                            color = White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onRefreshClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = GreenLive,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Horizontal Carousel of matches (Google Search Cricket Header Style)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "MATCHES",
                    color = LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(liveMatches) { match ->
                        val isSelected = match.id == activeMatch.id
                        val statusColor = when (match.status) {
                            "LIVE" -> GreenLive
                            "UPCOMING" -> YellowNeon
                            else -> LightGray
                        }

                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .height(115.dp)
                                .clickable { onMatchSelect(match) }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) YellowNeon else Color.White.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CardDark.copy(alpha = 0.95f) else CardDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (match.title.length > 20) match.title.take(18) + ".." else match.title,
                                        color = White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(statusColor)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = match.status,
                                            color = statusColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Team A row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = match.teamA.teamName,
                                            color = LightGray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (match.status == "UPCOMING") "TBD" else "${match.teamA.runs}/${match.teamA.wickets}",
                                            color = White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Team B row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = match.teamB.teamName,
                                            color = LightGray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (match.status == "UPCOMING") "TBD" else "${match.teamB.runs}/${match.teamB.wickets}",
                                            color = if (match.status == "LIVE") AccentRed else White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Selected Match Details Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeMatch.title,
                            color = YellowNeon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (activeMatch.status == "LIVE") GreenLive else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeMatch.status,
                                color = if (activeMatch.status == "LIVE") GreenLive else White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Match scores side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Team A
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = activeMatch.teamA.teamName,
                                color = White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (activeMatch.status == "UPCOMING") "-" else "${activeMatch.teamA.runs}/${activeMatch.teamA.wickets}",
                                color = LightGray,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                            if (activeMatch.status != "UPCOMING") {
                                Text(
                                    text = "(${activeMatch.teamA.overs} Ov)",
                                    color = MutedText,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // VS Badge
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CardDark)
                                .size(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VS",
                                color = PrimaryRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }

                        // Team B
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = activeMatch.teamB.teamName,
                                color = White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (activeMatch.status == "UPCOMING") "-" else "${activeMatch.teamB.runs}/${activeMatch.teamB.wickets}",
                                color = AccentRed,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            )
                            if (activeMatch.status != "UPCOMING") {
                                Text(
                                    text = "(${activeMatch.teamB.overs} Ov)",
                                    color = LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Target & Calculation Panel
                    activeMatch.target?.let { target ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardDark)
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "TARGET: $target",
                                    color = YellowNeon,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (activeMatch.status == "LIVE") {
                                        "${activeMatch.teamB.teamName} require ${activeMatch.requiredRuns ?: 0} runs in ${activeMatch.ballsRemaining ?: 0} balls to win."
                                    } else {
                                        "Match Completed."
                                    },
                                    color = White,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Commentary Feed Section
        item {
            Text(
                text = "BALL-BY-BALL COMMENTARY",
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
        }

        if (activeMatch.commentary.isEmpty()) {
            item {
                Text(
                    text = if (activeMatch.status == "UPCOMING") "Stay tuned! Live match commentary will start once play begins." else "Commentary starting shortly as soon as overs resume...",
                    color = MutedText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        } else {
            items(activeMatch.commentary) { log ->
                val isWicket = log.contains("OUT!") || log.contains("WICKET!")
                val isSix = log.contains("SIX!")
                val isFour = log.contains("FOUR!")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isWicket -> AccentRed.copy(alpha = 0.15f)
                            isSix -> GreenLive.copy(alpha = 0.15f)
                            isFour -> YellowNeon.copy(alpha = 0.15f)
                            else -> CardDark
                        }
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Event tag indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        isWicket -> AccentRed
                                        isSix -> GreenLive
                                        isFour -> YellowNeon
                                        else -> MutedText
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when {
                                    isWicket -> "WKT"
                                    isSix -> "6"
                                    isFour -> "4"
                                    else -> "RUN"
                                },
                                color = if (isFour) StadiumBlack else White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = log,
                            color = White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SchedulesContent(matches: List<TournamentMatch>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "UPCOMING MATCH SCHEDULES",
                color = White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(matches) { match ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.matchNumber,
                            color = YellowNeon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardDark)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = match.status,
                                color = LightGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Teams encounter row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = match.teamA,
                            color = White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .clip(CircleShape)
                                .background(CardDark)
                                .size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VS",
                                color = PrimaryRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = match.teamB,
                            color = White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date & Stadium details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DATE",
                                color = MutedText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = match.date,
                                color = White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "VENUE",
                                color = MutedText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = match.venue,
                                color = LightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddStreamDialog(
    onDismiss: () -> Unit,
    onAddStream: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {} // Prevent click-through closing
                .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = PitchDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Add Custom Live Stream Link",
                    color = White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Paste any valid HLS .m3u8 or MP4 stream address to watch it securely.",
                    color = MutedText,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Stream Title") },
                    placeholder = { Text("e.g. My Custom HLS Channel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_stream_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = CardDark,
                        focusedLabelColor = PrimaryRed,
                        unfocusedLabelColor = MutedText
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // URL Input
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        hasError = false
                    },
                    label = { Text("HLS Streaming M3U8 URL") },
                    placeholder = { Text("https://example.com/playlist.m3u8") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_stream_url"),
                    isError = hasError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = CardDark,
                        focusedLabelColor = PrimaryRed,
                        unfocusedLabelColor = MutedText
                    )
                )

                if (hasError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please enter a valid URL beginning with http:// or https://",
                        color = AccentRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val cleanUrl = url.trim()
                            if (cleanUrl.isBlank() || (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://"))) {
                                    hasError = true
                            } else {
                                onAddStream(title, cleanUrl)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("save_custom_stream_btn")
                    ) {
                        Text("Save & Play", color = White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    viewModel: StreamViewModel,
    modifier: Modifier = Modifier
) {
    val spreadsheetId by viewModel.spreadsheetId.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val marqueeText by viewModel.marqueeText.collectAsState()

    var inputUrlOrId by remember { mutableStateOf(spreadsheetId) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Title / Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GOOGLE SPREADSHEET CONTROL PANEL",
                        color = YellowNeon,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "App channels, banner scrolling notifications, image button URLs ko apni Google Sheet se control karein.",
                        color = LightGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Integration and Sync Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Spreadsheet ID ya Sharing Link darj karein:",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputUrlOrId,
                        onValueChange = { inputUrlOrId = it },
                        placeholder = { Text("ID ya full link paste karein", color = MutedText) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("spreadsheet_id_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = PrimaryRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedLabelColor = PrimaryRed,
                            unfocusedLabelColor = MutedText
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sync Status Indicator Section
                    when (val status = syncStatus) {
                        is StreamViewModel.SyncStatus.Loading -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PrimaryRed,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Google Sheets se data sync ho raha hai...",
                                    color = LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        is StreamViewModel.SyncStatus.Success -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1B5E20).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Taseeq Kaamyab! Sync Hogaya.\nChannels loaded: ${status.channelsCount} • Ticker updated: ${if (status.textUpdated) "Yes" else "No"}",
                                    color = Color(0xFFC8E6C9),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        is StreamViewModel.SyncStatus.Error -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFB71C1C).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFEF5350).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = Color(0xFFEF5350),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = status.message,
                                    color = Color(0xFFFFCDD2),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        else -> {
                            // Idle
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.syncFromSpreadsheet(inputUrlOrId)
                            },
                            enabled = syncStatus != StreamViewModel.SyncStatus.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sync_spreadsheet_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Now", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                // Clear custom settings and restore defaults
                                viewModel.syncFromSpreadsheet("1NDmt0XfZ72JOoacYGbwixCrjt6WQskzjYvzY1qQyMBE")
                                inputUrlOrId = "1NDmt0XfZ72JOoacYGbwixCrjt6WQskzjYvzY1qQyMBE"
                            },
                            enabled = syncStatus != StreamViewModel.SyncStatus.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset Default", color = LightGray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Setup Instructions Card (Visual step by step)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Instructions",
                            tint = YellowNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HOW TO SETUP / KAISE SETUP KAREIN?",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val steps = listOf(
                        "1. Google Sheet Banayein\nApne Google Drive par jaakar ek nayi Spreadsheet banayein.",
                        "2. Headers Darj Karein\nPehele row (Row 1) me ye charo columns bilkul aise hi likhein:\nChannle Name | Channle M3u8 | Button Url | Scrolling Text",
                        "3. Channels Aur Marquee Likhein\n• Channle Name: Channel ka naam (e.g. ARY).\n• Channle M3u8: Streaming link (.m3u8).\n• Button Url: Channel image/logo ka link.\n• Scrolling Text: App ke neeche chalne wala marquee text.",
                        "4. Web Par Publish Karein\nGoogle Sheet me File > Share > Publish to Web par click karein aur Publish dabayein.",
                        "5. Link Copy Karke Sync Karein\nSpreadsheet ka address/URL copy karke upar box me paste karein aur 'Sync Now' par click karein!"
                    )

                    steps.forEachIndexed { index, step ->
                        Text(
                            text = step,
                            color = LightGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        if (index < steps.size - 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.White.copy(alpha = 0.05f))
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Live Active Config Details Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LIVE CONFIG STATUS",
                        color = MutedText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Spreadsheet ID in Use:",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = spreadsheetId.ifBlank { "None (Using offline defaults)" },
                        color = YellowNeon,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Active Ticker Notice:",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = marqueeText,
                        color = LightGray,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }
    val threshold = 180f // trigger refresh threshold in px
    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isRefreshing) 100f else offsetY
    )

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // If user is scrolling up and we have pulled down offset, consume it first
                return if (available.y < 0f && offsetY > 0f) {
                    val prevOffset = offsetY
                    offsetY = (offsetY + available.y).coerceAtLeast(0f)
                    Offset(0f, offsetY - prevOffset)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // If child has left over pull-down scrolling (meaning we are at the top)
                return if (available.y > 0f) {
                    val prevOffset = offsetY
                    offsetY = (offsetY + available.y * 0.5f) // resistance
                    Offset(0f, offsetY - prevOffset)
                } else {
                    Offset.Zero
                }
            }

            override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                if (offsetY >= threshold && !isRefreshing) {
                    onRefresh()
                }
                offsetY = 0f
                return super.onPreFling(available)
            }
            
            override suspend fun onPostFling(
                consumed: androidx.compose.ui.unit.Velocity,
                available: androidx.compose.ui.unit.Velocity
            ): androidx.compose.ui.unit.Velocity {
                offsetY = 0f
                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { androidx.compose.ui.unit.IntOffset(0, animatedOffsetY.roundToInt()) }
        ) {
            content()
        }

        if (offsetY > 15f || isRefreshing) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .background(Color(0xFF1E293B), shape = CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = PrimaryRed,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Pull down to refresh",
                        tint = PrimaryRed,
                        modifier = Modifier
                            .size(22.dp)
                    )
                }
            }
        }
    }
}
