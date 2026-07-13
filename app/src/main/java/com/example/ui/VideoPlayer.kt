package com.example.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Job

@Composable
fun FullscreenIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        val len = 6.dp.toPx()
        
        // Top Left
        drawLine(tint, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(len, 0f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(0f, len), strokeWidth)
        
        // Top Right
        drawLine(tint, androidx.compose.ui.geometry.Offset(w, 0f), androidx.compose.ui.geometry.Offset(w - len, 0f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w, 0f), androidx.compose.ui.geometry.Offset(w, len), strokeWidth)
        
        // Bottom Left
        drawLine(tint, androidx.compose.ui.geometry.Offset(0f, h), androidx.compose.ui.geometry.Offset(len, h), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(0f, h), androidx.compose.ui.geometry.Offset(0f, h - len), strokeWidth)
        
        // Bottom Right
        drawLine(tint, androidx.compose.ui.geometry.Offset(w, h), androidx.compose.ui.geometry.Offset(w - len, h), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w, h), androidx.compose.ui.geometry.Offset(w, h - len), strokeWidth)
    }
}

@Composable
fun FullscreenExitIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        val len = 6.dp.toPx()
        val offset = 4.dp.toPx()
        
        // Top Left
        drawLine(tint, androidx.compose.ui.geometry.Offset(offset, offset + len), androidx.compose.ui.geometry.Offset(offset, offset), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(offset, offset), androidx.compose.ui.geometry.Offset(offset + len, offset), strokeWidth)
        
        // Top Right
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - offset, offset + len), androidx.compose.ui.geometry.Offset(w - offset, offset), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - offset, offset), androidx.compose.ui.geometry.Offset(w - offset - len, offset), strokeWidth)
        
        // Bottom Left
        drawLine(tint, androidx.compose.ui.geometry.Offset(offset, h - offset - len), androidx.compose.ui.geometry.Offset(offset, h - offset), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(offset, h - offset), androidx.compose.ui.geometry.Offset(offset + len, h - offset), strokeWidth)
        
        // Bottom Right
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - offset, h - offset - len), androidx.compose.ui.geometry.Offset(w - offset, h - offset), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - offset, h - offset), androidx.compose.ui.geometry.Offset(w - offset - len, h - offset), strokeWidth)
    }
}

@Composable
fun PauseIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val barWidth = w * 0.3f
        val gap = w * 0.2f
        
        // Left bar
        drawRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f, 0f),
            size = androidx.compose.ui.geometry.Size(barWidth, h)
        )
        
        // Right bar
        drawRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f + barWidth + gap, 0f),
            size = androidx.compose.ui.geometry.Size(barWidth, h)
        )
    }
}

@Composable
fun VolumeIcon(isMuted: Boolean, modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        
        // Speaker body Path
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.2f, h * 0.35f)
            lineTo(w * 0.45f, h * 0.35f)
            lineTo(w * 0.7f, h * 0.15f)
            lineTo(w * 0.7f, h * 0.85f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.2f, h * 0.65f)
            close()
        }
        drawPath(path, color = tint)
        
        if (isMuted) {
            // X mark for mute
            val strokeWidth = 2.dp.toPx()
            drawLine(tint, androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.4f), androidx.compose.ui.geometry.Offset(w * 0.95f, h * 0.6f), strokeWidth)
            drawLine(tint, androidx.compose.ui.geometry.Offset(w * 0.95f, h * 0.4f), androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.6f), strokeWidth)
        } else {
            // Sound waves
            val strokeWidth = 2.dp.toPx()
            drawArc(
                color = tint,
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.4f, h * 0.25f),
                size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.5f),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

@Composable
fun RewindIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // Left triangle
        val path1 = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.45f, h * 0.25f)
            lineTo(w * 0.15f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.75f)
            close()
        }
        drawPath(path1, color = tint)
        
        // Right triangle
        val path2 = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.75f, h * 0.25f)
            lineTo(w * 0.45f, h * 0.5f)
            lineTo(w * 0.75f, h * 0.75f)
            close()
        }
        drawPath(path2, color = tint)
    }
}

@Composable
fun ForwardIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        
        // Left triangle
        val path1 = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.25f, h * 0.25f)
            lineTo(w * 0.55f, h * 0.5f)
            lineTo(w * 0.25f, h * 0.75f)
            close()
        }
        drawPath(path1, color = tint)
        
        // Right triangle
        val path2 = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.55f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.5f)
            lineTo(w * 0.55f, h * 0.75f)
            close()
        }
        drawPath(path2, color = tint)
    }
}

@Composable
fun LockIcon(isLocked: Boolean, modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        
        // Body of padlock
        val bodyHeight = h * 0.45f
        val bodyWidth = w * 0.7f
        val bodyLeft = w * 0.15f
        val bodyTop = h * 0.5f
        
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(bodyLeft, bodyTop),
            size = androidx.compose.ui.geometry.Size(bodyWidth, bodyHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        
        // Shackle of padlock
        if (isLocked) {
            // Closed shackle: half circle on top
            drawArc(
                color = tint,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.28f, h * 0.15f),
                size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.6f),
                style = Stroke(width = strokeWidth)
            )
        } else {
            // Open shackle: shifted up
            drawArc(
                color = tint,
                startAngle = 180f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.28f, h * 0.05f),
                size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.6f),
                style = Stroke(width = strokeWidth)
            )
        }
        
        // Keyhole circle
        drawCircle(
            color = Color.Black,
            radius = 2.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.68f)
        )
    }
}

@Composable
fun AspectRatioIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        val len = 5.dp.toPx()
        
        // Draw frame border
        drawRoundRect(
            color = tint.copy(alpha = 0.3f),
            topLeft = androidx.compose.ui.geometry.Offset(2.dp.toPx(), 2.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(w - 4.dp.toPx(), h - 4.dp.toPx()),
            style = Stroke(width = 1.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        
        // Draw 4 arrows pointing outwards
        // Top-Left pointing out
        drawLine(tint, androidx.compose.ui.geometry.Offset(len, len), androidx.compose.ui.geometry.Offset(1f, 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(1f, 1f), androidx.compose.ui.geometry.Offset(len, 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(1f, 1f), androidx.compose.ui.geometry.Offset(1f, len), strokeWidth)
        
        // Top-Right pointing out
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - len, len), androidx.compose.ui.geometry.Offset(w - 1f, 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - 1f, 1f), androidx.compose.ui.geometry.Offset(w - len, 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - 1f, 1f), androidx.compose.ui.geometry.Offset(w - 1f, len), strokeWidth)
        
        // Bottom-Left pointing out
        drawLine(tint, androidx.compose.ui.geometry.Offset(len, h - len), androidx.compose.ui.geometry.Offset(1f, h - 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(1f, h - 1f), androidx.compose.ui.geometry.Offset(len, h - 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(1f, h - 1f), androidx.compose.ui.geometry.Offset(1f, h - len), strokeWidth)
        
        // Bottom-Right pointing out
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - len, h - len), androidx.compose.ui.geometry.Offset(w - 1f, h - 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - 1f, h - 1f), androidx.compose.ui.geometry.Offset(w - len, h - 1f), strokeWidth)
        drawLine(tint, androidx.compose.ui.geometry.Offset(w - 1f, h - 1f), androidx.compose.ui.geometry.Offset(w - 1f, h - len), strokeWidth)
    }
}

@Composable
fun PipIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        
        // Larger screen border
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(w, h),
            style = Stroke(width = strokeWidth),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        
        // Smaller screen inside (bottom right)
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f),
            size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.5f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx(), 1.dp.toPx())
        )
    }
}

@Composable
fun SunIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val center = androidx.compose.ui.geometry.Offset(width / 2f, height / 2f)
        val radius = height * 0.25f
        
        // Center circle
        drawCircle(color = color, radius = radius)
        
        // Draw rays
        val strokeWidth = 2.dp.toPx()
        val rayLen = height * 0.15f
        val innerDist = radius + 3.dp.toPx()
        val outerDist = innerDist + rayLen
        
        for (i in 0 until 8) {
            val angle = i * Math.PI / 4
            val cos = Math.cos(angle).toFloat()
            val sin = Math.sin(angle).toFloat()
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(center.x + innerDist * cos, center.y + innerDist * sin),
                end = androidx.compose.ui.geometry.Offset(center.x + outerDist * cos, center.y + outerDist * sin),
                strokeWidth = strokeWidth
            )
        }
    }
}

fun enterPipMode(context: android.content.Context) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val activity = context as? Activity
        try {
            val params = android.app.PictureInPictureParams.Builder().build()
            activity?.enterPictureInPictureMode(params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun getFallbackUrls(originalUrl: String): List<String> {
    val fallbacks = mutableListOf<String>()
    
    // 1. First choice: Original URL (respected first)
    if (originalUrl.isNotBlank() && !fallbacks.contains(originalUrl)) {
        fallbacks.add(originalUrl)
    }
    
    // 2. Parse any Tamasha / Jazzauth URLs to generate direct live playlist fallbacks
    if (originalUrl.contains("tamashaweb.com") || originalUrl.contains("jazzauth")) {
        val jazzauthIndex = originalUrl.indexOf("/jazzauth/")
        if (jazzauthIndex != -1) {
            val afterJazzauth = originalUrl.substring(jazzauthIndex + "/jazzauth/".length)
            val channelName = afterJazzauth.split("/").firstOrNull()?.trim() ?: ""
            if (channelName.isNotEmpty() && channelName != "live") {
                val domains = listOf(
                    "cdn07lhr.tamashaweb.com:8087",
                    "cdn07isb.tamashaweb.com:8087",
                    "cdn23lhr.tamashaweb.com:8087"
                )
                
                for (domain in domains) {
                    val playlistUrl = "https://$domain/jazzauth/$channelName/playlist.m3u8"
                    if (playlistUrl != originalUrl && !fallbacks.contains(playlistUrl)) {
                        fallbacks.add(playlistUrl)
                    }
                }
                
                // Keep the original domain but point to standard playlist.m3u8 instead of timeshift chunk
                val domainMatcher = java.util.regex.Pattern.compile("https://([^/]+)/jazzauth/").matcher(originalUrl)
                if (domainMatcher.find()) {
                    val originalDomain = domainMatcher.group(1)
                    val originalDomainFallback = "https://$originalDomain/jazzauth/$channelName/playlist.m3u8"
                    if (originalDomainFallback != originalUrl && !fallbacks.contains(originalDomainFallback)) {
                        fallbacks.add(originalDomainFallback)
                    }
                }
            }
        }
    }
    
    // Fallback standard URLs for ARY News if URL fails and contains "ary"
    if (originalUrl.lowercase().contains("ary")) {
        val aryFallback = "https://cdn07lhr.tamashaweb.com:8087/jazzauth/vsat-arynews-abr/playlist.m3u8"
        if (!fallbacks.contains(aryFallback)) {
            fallbacks.add(aryFallback)
        }
        val aryFallback2 = "https://cdn07isb.tamashaweb.com:8087/jazzauth/vsat-arynews-abr/playlist.m3u8"
        if (!fallbacks.contains(aryFallback2)) {
            fallbacks.add(aryFallback2)
        }
    }
    
    // Fallback standard URLs for PTV Sports / Ten Sports
    if (originalUrl.lowercase().contains("ptv") || originalUrl.lowercase().contains("sports")) {
        val ptv1 = "https://tencentcdn5.tamashaweb.com/v1/0196159eeff41eb4611d121c76c781/0196159f51bb1ea5064913eb2a83ea/TMSHU1WEB_480p.m3u8"
        if (!fallbacks.contains(ptv1)) fallbacks.add(ptv1)
        val ptv2 = "https://tencentcdn5.tamashaweb.com/v1/0196159eeff41eb4611d121c76c781/0196159f51bb1ea5064913eb2a83ea/TMSHU1WEB_360p.m3u8"
        if (!fallbacks.contains(ptv2)) fallbacks.add(ptv2)
    }
    
    if (originalUrl.lowercase().contains("geo") || originalUrl.lowercase().contains("super")) {
        val geo = "https://cdn23lhr.tamashaweb.com:8087/jazzauth/189H/playlist.m3u8?"
        if (!fallbacks.contains(geo)) fallbacks.add(geo)
    }

    return fallbacks
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    url: String,
    title: String = "LIVE PLAYBACK",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    
    // Fallback Servers failover states
    var currentUrlIndex by remember(url) { mutableStateOf(0) }
    val fallbackUrls = remember(url) { getFallbackUrls(url) }
    val activePlayingUrl = remember(url, currentUrlIndex) {
        fallbackUrls.getOrNull(currentUrlIndex) ?: url
    }

    var autoRetryCount by remember(url, currentUrlIndex) { mutableStateOf(0) }
    var isBuffering by remember(url, currentUrlIndex) { mutableStateOf(true) }
    var hasError by remember(url, currentUrlIndex) { mutableStateOf(false) }
    var errorDetails by remember(url, currentUrlIndex) { mutableStateOf("") }
    var retryTrigger by remember { mutableStateOf(0) }

    var isPlaying by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(true) }
    var isMuted by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var selectedQuality by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("Auto") }
    var isFullscreen by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var controlsVisible by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(true) }
    var showQualityMenu by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var activeResolution by remember(url) { mutableStateOf("") }
    var availableQualities by remember(url) { mutableStateOf(listOf("Auto")) }

    // Premium states matching user's reference image
    var isLocked by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var resizeMode by remember { mutableStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }
    var bufferedPosition by remember { mutableStateOf(0L) }
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }

    // Initialize ExoPlayer
    val exoPlayer = remember(activePlayingUrl, retryTrigger) {
        val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(
            /* minDurationForQualityIncreaseMs = */ 15000,
            /* maxDurationForQualityDecreaseMs = */ 800,
            /* minDurationToRetainAfterDiscardMs = */ 15000,
            /* bandwidthFraction = */ 0.65f
        )
        val trackSelector = DefaultTrackSelector(appContext, adaptiveTrackSelectionFactory)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15000, // minBufferMs (15s cushion to prevent stutter on temporary signal drops)
                50000, // maxBufferMs (50s background pre-buffering)
                1500,  // bufferForPlaybackMs (Only 1.5s needed to start playing extremely fast!)
                4000   // bufferForPlaybackAfterRebufferMs (4s required before resuming after a rebuffer)
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        ExoPlayer.Builder(appContext)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build().apply {
                val isHls = activePlayingUrl.contains(".m3u8", ignoreCase = true)
                val mediaItemBuilder = MediaItem.Builder().setUri(activePlayingUrl)
                if (isHls) {
                    mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
                }
                val mediaItem = mediaItemBuilder.build()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = isPlaying
                volume = if (isMuted) 0f else 1f

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        isBuffering = playbackState == Player.STATE_BUFFERING
                        if (playbackState == Player.STATE_READY) {
                            hasError = false
                            isBuffering = false
                            autoRetryCount = 0 // Reset on successful playback
                        }
                    }

                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        val height = videoSize.height
                        val label = when {
                            height >= 1080 -> "1080p"
                            height >= 720 -> "720p"
                            height >= 480 -> "480p"
                            height >= 360 -> "360p"
                            height > 0 -> "${height}p"
                            else -> ""
                        }
                        if (label.isNotEmpty()) {
                            activeResolution = label
                        }
                    }

                    override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                        val heights = mutableSetOf<Int>()
                        for (group in tracks.groups) {
                            if (group.type == androidx.media3.common.C.TRACK_TYPE_VIDEO) {
                                for (i in 0 until group.length) {
                                    val format = group.getTrackFormat(i)
                                    if (group.isTrackSupported(i) && format.height > 0) {
                                        heights.add(format.height)
                                    }
                                }
                            }
                        }

                        val newQualities = mutableListOf("Auto")
                        heights.sortedDescending().forEach { height ->
                            val label = when (height) {
                                1080 -> "1080p"
                                720 -> "720p"
                                480 -> "480p"
                                360 -> "360p"
                                else -> "${height}p"
                            }
                            if (!newQualities.contains(label)) {
                                newQualities.add(label)
                            }
                        }

                        if (newQualities.size <= 1) {
                            availableQualities = listOf("Auto", "1080p", "720p", "480p", "360p")
                        } else {
                            availableQualities = newQualities
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        if (autoRetryCount < 2) {
                            autoRetryCount++
                            isBuffering = true
                            coroutineScope.launch {
                                delay(1500) // Wait 1.5s before retrying
                                retryTrigger++
                            }
                        } else {
                            // If auto-retry on current URL fails, try next fallback URL
                            if (currentUrlIndex < fallbackUrls.size - 1) {
                                currentUrlIndex++
                                autoRetryCount = 0
                                isBuffering = true
                            } else {
                                hasError = true
                                isBuffering = false
                                errorDetails = error.localizedMessage ?: "Unknown network error"
                            }
                        }
                    }
                })
            }
    }

    // Helper function for quality change
    fun selectQuality(quality: String) {
        selectedQuality = quality
        val builder = exoPlayer.trackSelectionParameters.buildUpon()
        if (quality == "Auto") {
            builder.clearOverrides()
            builder.setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
            builder.setMaxVideoBitrate(Int.MAX_VALUE)
        } else {
            // Parse height from string like "720p" -> 720
            val height = quality.replace("p", "").toIntOrNull() ?: Int.MAX_VALUE
            val width = when (height) {
                1080 -> 1920
                720 -> 1280
                480 -> 854
                360 -> 640
                else -> (height * 16) / 9
            }
            // Dynamic max bitrate to strictly force ExoPlayer to fall back to the selected quality
            val maxBitrate = when {
                height >= 1080 -> 5000000
                height >= 720 -> 2200000
                height >= 480 -> 900000
                height >= 360 -> 450000
                else -> 250000
            }
            builder.setMaxVideoSize(width, height)
            builder.setMaxVideoBitrate(maxBitrate)
        }
        exoPlayer.trackSelectionParameters = builder.build()

        // Force the player to discard buffer and fetch the newly selected quality immediately
        if (exoPlayer.playbackState == Player.STATE_READY) {
            val currentPos = exoPlayer.currentPosition
            exoPlayer.seekTo(currentPos)
        }
    }

    // Keep volume in sync
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    // Keep play/pause in sync
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    // Keep quality selection in sync
    LaunchedEffect(selectedQuality, exoPlayer) {
        selectQuality(selectedQuality)
    }

    // Continuously update progress position and duration
    LaunchedEffect(exoPlayer, isPlaying) {
        while (true) {
            if (isPlaying && !isDragging) {
                currentPosition = exoPlayer.currentPosition
                totalDuration = exoPlayer.duration.let { if (it < 0) 0L else it }
                bufferedPosition = exoPlayer.bufferedPosition
            }
            delay(500)
        }
    }

    // Auto-hide controls unless user is actively dragging seeker
    LaunchedEffect(controlsVisible, isDragging, isLocked) {
        if (controlsVisible && !isDragging) {
            delay(5000)
            controlsVisible = false
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Keep screen on while playing video to prevent screen timeout / screen off issues
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Screen rotation effect on fullscreen change
    DisposableEffect(isFullscreen) {
        val activity = context as? Activity
        if (isFullscreen) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            activity?.window?.let { window ->
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    window.attributes.layoutInDisplayCutoutMode = 
                        android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
                insetsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
            }
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity?.window?.let { window ->
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    window.attributes.layoutInDisplayCutoutMode = 
                        android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
                }
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                insetsController.isAppearanceLightStatusBars = false
                
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity?.window?.let { window ->
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    window.attributes.layoutInDisplayCutoutMode = 
                        android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
                }
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                insetsController.isAppearanceLightStatusBars = false
                
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    // Reset controls visibility to true when entering/exiting fullscreen to keep user controls accessible
    LaunchedEffect(isFullscreen) {
        controlsVisible = true
    }

    @Composable
    fun PlayerContent(isFullscreenMode: Boolean) {
        val context = LocalContext.current
        val isInPipMode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            (context as? Activity)?.isInPictureInPictureMode == true
        } else {
            false
        }

        val playerResizeMode = resizeMode

        // Brightness & Volume control variables
        val audioManager = remember(context) {
            context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
        }
        val maxVolume = remember(audioManager) {
            audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
        }
        var playerBrightness by remember { mutableStateOf(1.0f) }
        var startBrightness by remember { mutableStateOf(1.0f) }
        var startVolume by remember { mutableStateOf(0f) }
        var playerSize by remember { mutableStateOf(IntSize.Zero) }
        var gestureType by remember { mutableStateOf("") } // "brightness" or "volume"
        
        var showGestureOverlay by remember { mutableStateOf(false) }
        var overlayValue by remember { mutableStateOf(0) }
        var overlayIcon by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (!hasError) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false // Custom controls overlay
                            resizeMode = playerResizeMode
                            keepScreenOn = true
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { playerView ->
                        playerView.player = exoPlayer
                        playerView.resizeMode = playerResizeMode
                        playerView.keepScreenOn = true
                    }
                )

                // Dimming overlay that simulates player-only brightness (does not affect system brightness)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = (1.0f - playerBrightness).coerceIn(0f, 0.95f)))
                )
            }

            // In PiP mode, hide all custom overlays completely
            if (isInPipMode) {
                return@Box
            }

            // Transparent overlay Box that captures click events AND left/right drag-based adjustments
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { playerSize = it }
                    .pointerInput(isLocked) {
                        if (isLocked) return@pointerInput
                        detectTapGestures(
                            onTap = {
                                controlsVisible = !controlsVisible
                            }
                        )
                    }
                    .pointerInput(isLocked) {
                        if (isLocked) return@pointerInput
                        detectVerticalDragGestures(
                            onDragStart = { pointerInputChange ->
                                val startX = pointerInputChange.x
                                val isLeft = startX < (playerSize.width / 2f)
                                if (isLeft) {
                                    gestureType = "brightness"
                                    startBrightness = playerBrightness
                                } else {
                                    gestureType = "volume"
                                    val currentVol = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
                                    startVolume = currentVol.toFloat()
                                }
                                showGestureOverlay = true
                            },
                            onDragEnd = {
                                gestureType = ""
                                coroutineScope.launch {
                                    delay(1000)
                                    showGestureOverlay = false
                                }
                            },
                            onDragCancel = {
                                gestureType = ""
                                showGestureOverlay = false
                            },
                            onVerticalDrag = { change, dragAmount ->
                                val totalHeight = playerSize.height.toFloat()
                                if (totalHeight > 0f) {
                                    val deltaFraction = -dragAmount / totalHeight
                                    if (gestureType == "brightness") {
                                        val newBrightness = (startBrightness + deltaFraction * 1.5f).coerceIn(0.05f, 1.0f)
                                        startBrightness = newBrightness
                                        playerBrightness = newBrightness
                                        overlayIcon = "brightness"
                                        overlayValue = (newBrightness * 100).toInt()
                                    } else if (gestureType == "volume") {
                                        val volumeDelta = deltaFraction * maxVolume * 1.5f
                                        val newVolume = (startVolume + volumeDelta).coerceIn(0f, maxVolume.toFloat())
                                        startVolume = newVolume
                                        
                                        audioManager.setStreamVolume(
                                            android.media.AudioManager.STREAM_MUSIC,
                                            newVolume.toInt(),
                                            0
                                        )
                                        overlayIcon = "volume"
                                        overlayValue = ((newVolume / maxVolume) * 100).toInt()
                                    }
                                }
                            }
                        )
                    }
            )

            // Custom UI Controls Overlay
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    // NORMAL CONTROLS (Top, Center, Bottom)
                    
                    // 1. Top Bar (Stream Info, Back, Mute, Settings)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                IconButton(
                                    onClick = { 
                                        if (isFullscreenMode) {
                                            isFullscreen = false 
                                        } else {
                                            (context as? Activity)?.onBackPressed()
                                        }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Mute control
                                IconButton(
                                    onClick = { isMuted = !isMuted },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    VolumeIcon(isMuted = isMuted, tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))

                                // Quality/Settings selector placed at top right
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(18.dp))
                                        .clickable { showQualityMenu = true }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val currentQualityLabel = if (selectedQuality == "Auto") {
                                        if (activeResolution.isNotEmpty()) "Auto ($activeResolution)" else "Auto"
                                    } else {
                                        selectedQuality
                                    }
                                    Text(
                                        text = currentQualityLabel,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    DropdownMenu(
                                        expanded = showQualityMenu,
                                        onDismissRequest = { showQualityMenu = false },
                                        modifier = Modifier.background(Color(0xFF1E1E1E))
                                    ) {
                                        availableQualities.forEach { quality ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        if (selectedQuality == quality) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Selected",
                                                                tint = Color.Red,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                        } else {
                                                            Spacer(modifier = Modifier.width(24.dp))
                                                        }
                                                        Text(
                                                            text = if (quality == "Auto") "Auto (Adaptive)" else quality,
                                                            color = Color.White,
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectQuality(quality)
                                                    showQualityMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Center Row Controls (No PIP option, Quality moved to top bar)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Aspect Ratio Scaling Toggle (Only shown when in fullscreen mode)
                            if (isFullscreenMode) {
                                IconButton(
                                    onClick = {
                                        resizeMode = when (resizeMode) {
                                            AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                                            else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                                ) {
                                    AspectRatioIcon(tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }

                            // Rewind Button (10s)
                            IconButton(
                                onClick = {
                                    val current = exoPlayer.currentPosition
                                    exoPlayer.seekTo(maxOf(0L, current - 10000L))
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                            ) {
                                RewindIcon(tint = Color.White, modifier = Modifier.size(22.dp))
                            }

                            // Play/Pause Button
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.White, shape = CircleShape)
                            ) {
                                if (isPlaying) {
                                    PauseIcon(tint = Color.Black, modifier = Modifier.size(22.dp))
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            // Forward Button (10s)
                            IconButton(
                                onClick = {
                                    val current = exoPlayer.currentPosition
                                    val duration = exoPlayer.duration
                                    if (duration > 0) {
                                        exoPlayer.seekTo(minOf(duration, current + 10000L))
                                    } else {
                                        exoPlayer.seekTo(current + 10000L)
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                            ) {
                                ForwardIcon(tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }

                        // 3. Bottom Progress / Seeker Bar Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val sliderValue = if (isDragging) dragPosition else currentPosition.toFloat()
                            
                            // Current duration text
                            Text(
                                text = formatTime(if (isDragging) dragPosition.toLong() else currentPosition),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))

                            // Seeker Slider with HLS buffered progress rendering
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                // 1. Inactive background track
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(2.dp))
                                )
                                
                                // 2. Buffered progress track (m3u8 rendering load progress)
                                val bufferedFraction = if (totalDuration > 0) (bufferedPosition.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f) else 0f
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = bufferedFraction)
                                        .height(4.dp)
                                        .background(Color.White.copy(alpha = 0.45f), shape = RoundedCornerShape(2.dp))
                                )
                                
                                // 3. Interactive Slider on top (transparent inactive track)
                                Slider(
                                    value = sliderValue,
                                    onValueChange = { newValue ->
                                        isDragging = true
                                        dragPosition = newValue
                                    },
                                    onValueChangeFinished = {
                                        isDragging = false
                                        exoPlayer.seekTo(dragPosition.toLong())
                                    },
                                    valueRange = 0f..(if (totalDuration > 0) totalDuration.toFloat() else 1f),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = Color.Red,
                                        inactiveTrackColor = Color.Transparent, // Transparent so background tracks show
                                        activeTickColor = Color.Red,
                                        inactiveTickColor = Color.Transparent
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Total duration text
                            Text(
                                text = if (totalDuration > 0) formatTime(totalDuration) else "LIVE",
                                color = if (totalDuration > 0) Color.White else Color.Red,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Fullscreen Toggle Button restored at the bottom right
                            IconButton(
                                onClick = { isFullscreen = !isFullscreen },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                            ) {
                                if (isFullscreenMode) {
                                    FullscreenExitIcon(tint = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    FullscreenIcon(tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

            // Custom Gesture Overlay HUD
            AnimatedVisibility(
                visible = showGestureOverlay,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        if (overlayIcon == "brightness") {
                            SunIcon(
                                color = Color(0xFFFBBF24), // Bright golden yellow
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Brightness $overlayValue%",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            VolumeIcon(
                                isMuted = (overlayValue == 0),
                                tint = Color(0xFF3B82F6), // Professional sky blue
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Volume $overlayValue%",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Buffering Overlay
            if (isBuffering && !hasError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.Red,
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }

    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                decorFitsSystemWindows = false
            )
        ) {
            val dialogView = androidx.compose.ui.platform.LocalView.current
            LaunchedEffect(dialogView) {
                val windowProvider = dialogView.parent as? androidx.compose.ui.window.DialogWindowProvider
                windowProvider?.window?.let { dWindow ->
                    dWindow.setGravity(android.view.Gravity.CENTER)
                    dWindow.setLayout(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    dWindow.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.BLACK))
                    
                    dWindow.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    dWindow.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    dWindow.addFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                    
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        dWindow.attributes.layoutInDisplayCutoutMode = 
                            android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    }
                    
                    dWindow.addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    
                    androidx.core.view.WindowCompat.setDecorFitsSystemWindows(dWindow, false)
                    val insetsController = androidx.core.view.WindowCompat.getInsetsController(dWindow, dWindow.decorView)
                    insetsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                    insetsController.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    
                    @Suppress("DEPRECATION")
                    dWindow.decorView.systemUiVisibility = (
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                        or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
                }
            }

            // Capture Back Button press to exit fullscreen cleanly
            androidx.activity.compose.BackHandler {
                isFullscreen = false
            }

            PlayerContent(isFullscreenMode = true)
        }
    } else {
        Box(modifier = modifier) {
            PlayerContent(isFullscreenMode = false)
        }
    }

    // Static error overlay rendered inline if playback fails
    if (hasError) {
        Box(
            modifier = modifier
                .background(Color(0xFF121212))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Playback Connection Failed",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val currentDomain = activePlayingUrl.split("/").getOrNull(2) ?: "Default Stream"
                Text(
                    text = "Server down or offline ($currentDomain). Swipe up/switch servers to restore live feed.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = {
                            hasError = false
                            isBuffering = true
                            retryTrigger++
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry", fontSize = 11.sp)
                    }
                    
                    if (fallbackUrls.size > 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalButton(
                            onClick = {
                                currentUrlIndex = (currentUrlIndex + 1) % fallbackUrls.size
                                hasError = false
                                isBuffering = true
                                autoRetryCount = 0
                                retryTrigger++
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Switch Server",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Server (${currentUrlIndex + 1}/${fallbackUrls.size})", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
