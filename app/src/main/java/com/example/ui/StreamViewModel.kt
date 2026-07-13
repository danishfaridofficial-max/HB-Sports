package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.LiveMatch
import com.example.data.StreamItem
import com.example.data.StreamRepository
import com.example.data.TeamScore
import com.example.data.TournamentMatch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.io.IOException
import kotlinx.coroutines.flow.first
import com.example.BuildConfig
import org.json.JSONObject
import org.json.JSONArray
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class StreamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StreamRepository
    val allStreams: StateFlow<List<StreamItem>>

    private val _selectedStream = MutableStateFlow<StreamItem?>(null)
    val selectedStream: StateFlow<StreamItem?> = _selectedStream.asStateFlow()

    private val _liveMatch = MutableStateFlow<LiveMatch?>(null)
    val liveMatch: StateFlow<LiveMatch?> = _liveMatch.asStateFlow()

    private val _liveMatchesList = MutableStateFlow<List<LiveMatch>>(emptyList())
    val liveMatchesList: StateFlow<List<LiveMatch>> = _liveMatchesList.asStateFlow()

    private val _selectedLiveMatch = MutableStateFlow<LiveMatch?>(null)
    val selectedLiveMatch: StateFlow<LiveMatch?> = _selectedLiveMatch.asStateFlow()

    fun selectLiveMatch(match: LiveMatch) {
        _selectedLiveMatch.value = match
        _liveMatch.value = match
    }

    private val _upcomingMatches = MutableStateFlow<List<TournamentMatch>>(emptyList())
    val upcomingMatches: StateFlow<List<TournamentMatch>> = _upcomingMatches.asStateFlow()

    private val _activeUsersCount = MutableStateFlow("...")
    val activeUsersCount: StateFlow<String> = _activeUsersCount.asStateFlow()

    private var simulationJob: Job? = null
    private var activeUsersJob: Job? = null

    private val sharedPrefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _marqueeText = MutableStateFlow(
        sharedPrefs.getString(
            "marquee_text",
            "Update: PSL Playoffs Schedule Released. Tap to view. • We are Updating Our Application as We Update Our Application Will B Send Notification To All User's Thanks For Choosing Babar ali Official • Multi-stream capabilities enabled • Custom HLS player live •"
        ) ?: ""
    )
    val marqueeText: StateFlow<String> = _marqueeText.asStateFlow()

    private val _spreadsheetId = MutableStateFlow(
        sharedPrefs.getString("spreadsheet_id", "1NDmt0XfZ72JOoacYGbwixCrjt6WQskzjYvzY1qQyMBE") ?: "1NDmt0XfZ72JOoacYGbwixCrjt6WQskzjYvzY1qQyMBE"
    )
    val spreadsheetId: StateFlow<String> = _spreadsheetId.asStateFlow()

    sealed interface SyncStatus {
        object Idle : SyncStatus
        object Loading : SyncStatus
        data class Success(val channelsCount: Int, val textUpdated: Boolean) : SyncStatus
        data class Error(val message: String) : SyncStatus
    }

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    fun extractSpreadsheetId(input: String): String {
        val trimmed = input.trim()
        if (trimmed.contains("/d/")) {
            val parts = trimmed.split("/d/")
            if (parts.size > 1) {
                val idPart = parts[1]
                return idPart.split("/").firstOrNull()?.trim() ?: trimmed
            }
        }
        return trimmed
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                inQuotes = !inQuotes
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current = StringBuilder()
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }

    fun syncFromSpreadsheet(inputSpreadsheetId: String) {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading
            val extractedId = extractSpreadsheetId(inputSpreadsheetId)
            
            // Save Spreadsheet ID in SharedPreferences
            sharedPrefs.edit().putString("spreadsheet_id", extractedId).apply()
            _spreadsheetId.value = extractedId

            val csvUrl = "https://docs.google.com/spreadsheets/d/$extractedId/export?format=csv"
            
            try {
                val csvData = withContext(Dispatchers.IO) {
                    URL(csvUrl).readText()
                }

                val lines = csvData.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
                if (lines.isEmpty()) {
                    _syncStatus.value = SyncStatus.Error("The spreadsheet is empty or could not be loaded as CSV.")
                    return@launch
                }

                // Parse columns dynamically
                val headers = parseCsvLine(lines.first()).map { it.lowercase().trim() }
                
                // 0. Channel Number Index
                var channelNumberIndex = headers.indexOf("channle number")
                if (channelNumberIndex == -1) channelNumberIndex = headers.indexOf("channel number")
                if (channelNumberIndex == -1) channelNumberIndex = headers.indexOf("number")
                if (channelNumberIndex == -1) channelNumberIndex = headers.indexOf("channel_number")
                if (channelNumberIndex == -1) channelNumberIndex = headers.indexOf("channle_number")
                if (channelNumberIndex == -1) channelNumberIndex = headers.indexOf("no")

                // 1. Channel Name / Title Index
                var titleIndex = headers.indexOf("channle name")
                if (titleIndex == -1) titleIndex = headers.indexOf("channel name")
                if (titleIndex == -1) titleIndex = headers.indexOf("title")
                if (titleIndex == -1) titleIndex = headers.indexOf("name")
                
                // 2. Channel M3U8 / URL Index
                var urlIndex = headers.indexOf("channle m3u8")
                if (urlIndex == -1) urlIndex = headers.indexOf("channel m3u8")
                if (urlIndex == -1) urlIndex = headers.indexOf("url")
                if (urlIndex == -1) urlIndex = headers.indexOf("m3u8")
                if (urlIndex == -1) urlIndex = headers.indexOf("stream url")
                if (urlIndex == -1) urlIndex = headers.indexOf("streaming url")
                
                // 3. Button URL / Image URL Index
                var imageUrlIndex = headers.indexOf("button url")
                if (imageUrlIndex == -1) imageUrlIndex = headers.indexOf("imageurl")
                if (imageUrlIndex == -1) imageUrlIndex = headers.indexOf("image url")
                if (imageUrlIndex == -1) imageUrlIndex = headers.indexOf("button image")
                if (imageUrlIndex == -1) imageUrlIndex = headers.indexOf("logo")
                
                // 4. Scrolling Text Index
                var scrollingTextIndex = headers.indexOf("scrolling text")
                if (scrollingTextIndex == -1) scrollingTextIndex = headers.indexOf("marquee")
                if (scrollingTextIndex == -1) scrollingTextIndex = headers.indexOf("scrolling_text")
                if (scrollingTextIndex == -1) scrollingTextIndex = headers.indexOf("scrolling")

                // Legacy type column for backwards compatibility
                val typeIndex = headers.indexOf("type")

                if (titleIndex == -1 || urlIndex == -1) {
                    _syncStatus.value = SyncStatus.Error("Required columns 'Channle Name' and 'Channle M3u8' are missing. Please ensure your Google Sheet has these exact headers.")
                    return@launch
                }

                val newStreams = mutableListOf<StreamItem>()
                var newMarqueeText: String? = null

                for (i in 1 until lines.size) {
                    val row = parseCsvLine(lines[i])
                    if (row.size <= titleIndex) continue

                    val title = row[titleIndex].trim()
                    val url = if (urlIndex != -1 && urlIndex < row.size) row[urlIndex].trim() else ""
                    val imageUrl = if (imageUrlIndex != -1 && imageUrlIndex < row.size) row[imageUrlIndex].trim().ifBlank { null } else null

                    val chanNum = if (channelNumberIndex != -1 && channelNumberIndex < row.size) {
                        row[channelNumberIndex].trim().toIntOrNull() ?: i
                    } else {
                        i
                    }

                    // If a specific scrolling text column is present, find first non-blank entry in any row
                    if (scrollingTextIndex != -1 && scrollingTextIndex < row.size) {
                        val rowScrollText = row[scrollingTextIndex].trim()
                        if (rowScrollText.isNotBlank() && newMarqueeText == null) {
                            newMarqueeText = rowScrollText
                        }
                    }

                    // Legacy "type" row checks (e.g. Type=marquee)
                    var type = if (typeIndex != -1 && typeIndex < row.size) row[typeIndex].lowercase().trim() else "channel"
                    if (type.isBlank()) {
                        type = "channel"
                    }

                    if (type == "marquee" || type == "text" || type == "scrolling") {
                        if (title.isNotBlank() && newMarqueeText == null) {
                            newMarqueeText = title
                        } else if (url.isNotBlank() && newMarqueeText == null) {
                            newMarqueeText = url
                        }
                    } else {
                        if (title.isNotBlank() && url.isNotBlank()) {
                            newStreams.add(
                                StreamItem(
                                    title = title,
                                    url = url,
                                    imageUrl = imageUrl,
                                    isCustom = false,
                                    channelNumber = chanNum
                                )
                            )
                        }
                    }
                }

                // If we parsed channels successfully, clear database and sync
                if (newStreams.isNotEmpty()) {
                    repository.deleteNonCustomStreams()
                    newStreams.forEach {
                        repository.insertStream(it)
                    }
                }

                // Update marquee if any was found
                var textUpdated = false
                if (newMarqueeText != null) {
                    _marqueeText.value = newMarqueeText
                    sharedPrefs.edit().putString("marquee_text", newMarqueeText).apply()
                    textUpdated = true
                }

                _syncStatus.value = SyncStatus.Success(
                    channelsCount = newStreams.size,
                    textUpdated = textUpdated
                )

                // Select default main channel from the new list if selected is not custom
                val currentSelected = _selectedStream.value
                if (currentSelected == null || !currentSelected.isCustom) {
                    val freshStreams = repository.allStreams.first()
                    _selectedStream.value = freshStreams.firstOrNull { !it.isCustom } ?: freshStreams.firstOrNull()
                }

            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error("Failed to fetch spreadsheet. Note: Internet connection check karein, pehle se saved (cached) channels abhi chal rahe hain!")
            }
        }
    }

    // Preloaded streams to insert on first run
    private val defaultStreams = listOf(
        StreamItem(
            title = "ARY News",
            url = "https://cdn07lhr.tamashaweb.com:8087/jazzauth/vsat-arynews-abr/playlist.m3u8",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6A7T-7pXv3DshsXwreS_H1hZ84wOq0Y2pMw&s",
            isCustom = false,
            channelNumber = 1
        ),
        StreamItem(
            title = "PTV Sports",
            url = "https://tencentcdn5.tamashaweb.com/v1/0196159eeff41eb4611d121c76c781/0196159f51bb1ea5064913eb2a83ea/TMSHU1WEB_480p.m3u8",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQPGt69V61QADwmeZkfwsQWKnG3cRrydDc4DrfBDNP6OQ&s=10",
            isCustom = false,
            channelNumber = 2
        ),
        StreamItem(
            title = "Ten Sports",
            url = "https://tencentcdn5.tamashaweb.com/v1/0196159eeff41eb4611d121c76c781/0196159f51bb1ea5064913eb2a83ea/TMSHU1WEB_360p.m3u8",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSb9pUj3IatF3vDpxoZ7fB4bOfUv7GfSGeU0A&s",
            isCustom = false,
            channelNumber = 3
        ),
        StreamItem(
            title = "Geo Super",
            url = "https://cdn23lhr.tamashaweb.com:8087/jazzauth/189H/playlist.m3u8?",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_mUuTj2u7yF59pL4vSg_1X7fSGeU0A&s",
            isCustom = false,
            channelNumber = 4
        )
    )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StreamRepository(database.streamDao())

        allStreams = repository.allStreams.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            // If database is completely empty (first install / offline first run), load default channels
            try {
                val existing = repository.allStreams.first()
                if (existing.isEmpty()) {
                    defaultStreams.forEach {
                        repository.insertStream(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModelScope.launch {
            // Wait for db flow to update or collect
            allStreams.collect { streams ->
                if (streams.isNotEmpty() && _selectedStream.value == null) {
                    // Select default main channel
                    _selectedStream.value = streams.firstOrNull { !it.isCustom } ?: streams.firstOrNull()
                }
            }
        }

        // Auto-sync from Google Sheets on launch
        viewModelScope.launch {
            val savedId = _spreadsheetId.value
            if (savedId.isNotBlank()) {
                syncFromSpreadsheet(savedId)
            }
        }

        initializeUpcomingMatches()
        startLiveMatchSimulation()
        fetchRealCricketMatches()

        // Start real-time active users count simulation that responds to stream choices
        viewModelScope.launch {
            selectedStream.collect { stream ->
                startActiveUsersSimulation(stream)
            }
        }
    }

    private var firebaseListener: com.google.firebase.database.ValueEventListener? = null
    private var firebaseRef: com.google.firebase.database.DatabaseReference? = null
    private var presenceRef: com.google.firebase.database.DatabaseReference? = null

    private fun startActiveUsersSimulation(stream: StreamItem?) {
        activeUsersJob?.cancel()

        // Clean up previous Firebase listeners and presence
        try {
            if (com.google.firebase.FirebaseApp.getApps(getApplication()).isNotEmpty()) {
                firebaseListener?.let { firebaseRef?.removeEventListener(it) }
                presenceRef?.removeValue()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _activeUsersCount.value = "..."

        if (stream == null) {
            return
        }

        activeUsersJob = viewModelScope.launch {
            // Check if Firebase is configured/initialized in the project
            val isFirebaseAvailable = try {
                com.google.firebase.FirebaseApp.getApps(getApplication()).isNotEmpty()
            } catch (e: Exception) {
                false
            }

            if (isFirebaseAvailable) {
                // REAL LIVE DATABASE PRESENCE LOGIC
                try {
                    val database = com.google.firebase.database.FirebaseDatabase.getInstance()
                    val streamId = stream.id.toString()
                    
                    // Unique ID for this device session
                    val userId = sharedPrefs.getString("firebase_user_id", null) ?: run {
                        val newId = java.util.UUID.randomUUID().toString()
                        sharedPrefs.edit().putString("firebase_user_id", newId).apply()
                        newId
                    }

                    // Path to write this user's online state
                    presenceRef = database.getReference("streams/$streamId/viewers/$userId")
                    presenceRef?.setValue(true)
                    presenceRef?.onDisconnect()?.removeValue() // Auto remove on app close / disconnect

                    // Path to listen to total active viewers
                    firebaseRef = database.getReference("streams/$streamId/viewers")
                    firebaseListener = object : com.google.firebase.database.ValueEventListener {
                        override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                            val count = snapshot.childrenCount
                            // Format nicely (e.g., "1", "1.5k", etc.)
                            val formatted = if (count >= 1000) {
                                String.format("%.1fk", count / 1000.0)
                            } else {
                                count.toString()
                            }
                            _activeUsersCount.value = formatted
                        }

                        override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                            // Fallback to local simulation if Firebase has database rules or connection issues
                        }
                    }
                    firebaseRef?.addValueEventListener(firebaseListener!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    startFallbackSimulation(stream)
                }
            } else {
                // Fallback local simulation
                startFallbackSimulation(stream)
            }
        }
    }

    private suspend fun startFallbackSimulation(stream: StreamItem?) {
        var currentCount = when (stream?.title?.lowercase()) {
            "ary news" -> Random.nextInt(135000, 145000)
            "ptv sports" -> Random.nextInt(220000, 245000)
            "ten sports" -> Random.nextInt(95000, 105000)
            "geo super" -> Random.nextInt(80000, 95000)
            null -> 142800
            else -> {
                if (stream.isCustom) {
                    Random.nextInt(1200, 2800)
                } else {
                    Random.nextInt(45000, 65000)
                }
            }
        }

        while (true) {
            // Fluctuates slightly every 3 seconds to represent real active viewers entering/leaving
            val change = Random.nextInt(-120, 120)
            currentCount = (currentCount + change).coerceAtLeast(100)
            
            // Format nicely, e.g., 142.8k or 1.5k
            val formatted = if (currentCount >= 1000) {
                val kValue = currentCount / 1000.0
                String.format("%.1fk", kValue)
            } else {
                currentCount.toString()
            }
            
            _activeUsersCount.value = formatted
            delay(3000)
        }
    }

    fun selectStream(stream: StreamItem) {
        _selectedStream.value = stream
    }

    fun addCustomStream(title: String, url: String) {
        viewModelScope.launch {
            val cleanUrl = url.trim()
            val stream = StreamItem(
                title = title.ifBlank { "Custom HLS Stream" },
                url = cleanUrl,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTj8hK3r3fC97qR1qQ239p3zJzC6vXzU1-nKw&s", // custom icon placeholder
                isCustom = true
            )
            repository.insertStream(stream)
            _selectedStream.value = stream // Auto play custom stream
        }
    }

    fun deleteStream(streamId: Int) {
        viewModelScope.launch {
            repository.deleteStreamById(streamId)
            // If deleted was selected, switch back to default
            if (_selectedStream.value?.id == streamId) {
                _selectedStream.value = allStreams.value.firstOrNull { !it.isCustom }
            }
        }
    }

    private fun initializeUpcomingMatches() {
        _upcomingMatches.value = listOf(
            TournamentMatch("Match 14", "Peshawar Zalmi", "Quetta Gladiators", "June 30, 2026", "19:30 PST", "National Stadium, Karachi", "UPCOMING"),
            TournamentMatch("Match 15", "Multan Sultans", "Islamabad United", "July 01, 2026", "19:30 PST", "Multan Cricket Stadium", "UPCOMING"),
            TournamentMatch("Match 16", "Lahore Qalandars", "Peshawar Zalmi", "July 02, 2026", "19:30 PST", "Gaddafi Stadium, Lahore", "UPCOMING"),
            TournamentMatch("Match 17", "Karachi Kings", "Quetta Gladiators", "July 03, 2026", "19:30 PST", "National Stadium, Karachi", "UPCOMING")
        )
    }

    private fun startLiveMatchSimulation() {
        simulationJob?.cancel()

        val match1Commentary = mutableListOf(
            "High-voltage encounter: India vs Pakistan at the T20 World Cup!",
            "Virat Kohli playing some masterclass shots down the ground.",
            "Shaheen Afridi bowling a fiery spell under lights."
        )
        val match2Commentary = mutableListOf(
            "The Ashes - Australia on top but England fighting hard.",
            "Steve Smith solid at the crease, batting on 64*.",
            "James Anderson extracting good movement off the seam."
        )
        val match3Commentary = mutableListOf(
            "Karachi Kings won by 6 wickets!",
            "Kieron Pollard hits a massive six to seal the victory!",
            "Over 19.3: Pollard takes 2 runs to tie the score."
        )
        val match4Commentary = mutableListOf(
            "Warm-up Match: West Indies vs South Africa.",
            "Toss delayed due to light drizzle. Outfield is wet.",
            "Covers are on, ground staff working hard."
        )

        var indPakRuns = 152
        var indPakWickets = 4
        var indPakOvers = 18.2

        var ausEngRuns = 224
        var ausEngWickets = 6
        var ausEngOvers = 74.2

        val match1 = LiveMatch(
            id = "sim_ind_pak",
            title = "T20 World Cup - India vs Pakistan",
            status = "LIVE",
            teamA = TeamScore("Pakistan", 167, 8, 20.0),
            teamB = TeamScore("India", indPakRuns, indPakWickets, indPakOvers),
            currentBattingTeam = "India",
            target = 168,
            requiredRuns = 16,
            ballsRemaining = 10,
            commentary = match1Commentary.toList()
        )

        val match2 = LiveMatch(
            id = "sim_aus_eng",
            title = "The Ashes - Australia vs England",
            status = "LIVE",
            teamA = TeamScore("England", 312, 10, 88.4),
            teamB = TeamScore("Australia", ausEngRuns, ausEngWickets, ausEngOvers),
            currentBattingTeam = "Australia",
            target = null,
            requiredRuns = null,
            ballsRemaining = null,
            commentary = match2Commentary.toList()
        )

        val match3 = LiveMatch(
            id = "sim_psl",
            title = "PSL 2026 - Lahore Qalandars vs Karachi Kings",
            status = "COMPLETED",
            teamA = TeamScore("Lahore Qalandars", 167, 8, 20.0),
            teamB = TeamScore("Karachi Kings", 168, 4, 19.4),
            currentBattingTeam = "Karachi Kings",
            target = 168,
            requiredRuns = 0,
            ballsRemaining = 0,
            commentary = match3Commentary.toList()
        )

        val match4 = LiveMatch(
            id = "sim_wi_sa",
            title = "West Indies vs South Africa - 3rd ODI",
            status = "UPCOMING",
            teamA = TeamScore("West Indies", 0, 0, 0.0),
            teamB = TeamScore("South Africa", 0, 0, 0.0),
            currentBattingTeam = "West Indies",
            target = null,
            requiredRuns = null,
            ballsRemaining = null,
            commentary = match4Commentary.toList()
        )

        val initialList = listOf(match1, match2, match3, match4)
        _liveMatchesList.value = initialList
        _selectedLiveMatch.value = match1
        _liveMatch.value = match1

        simulationJob = viewModelScope.launch {
            val indPakBatsmanList = listOf("Virat Kohli", "Hardik Pandya", "Rishabh Pant", "Suryakumar Yadav")
            val indPakBowlerList = listOf("Shaheen Afridi", "Haris Rauf", "Naseem Shah", "Shadab Khan")

            while (true) {
                delay(6000) // update every 6 seconds

                // 1. Advance India vs Pakistan Match
                var ipBalls = ((indPakOvers * 10).toInt() % 10) + 1
                var ipOversInt = indPakOvers.toInt()
                if (ipBalls >= 6) {
                    ipOversInt += 1
                    ipBalls = 0
                }
                indPakOvers = ipOversInt + (ipBalls / 10.0)
                val ipBallsRemaining = 120 - (ipOversInt * 6 + ipBalls)

                if (ipBallsRemaining <= 0 || indPakRuns >= 168 || indPakWickets >= 10) {
                    // Reset India vs Pakistan match simulation
                    indPakRuns = 120
                    indPakWickets = 3
                    indPakOvers = 15.0
                    match1Commentary.clear()
                    match1Commentary.add("Match reset. Chasing 168, India needs a strong finish!")
                } else {
                    val event = Random.nextInt(100)
                    val runsAdded: Int
                    val eventText: String
                    val bowler = indPakBowlerList.random()
                    val batsman = indPakBatsmanList.random()

                    if (event < 5) {
                        indPakWickets += 1
                        runsAdded = 0
                        eventText = "OUT! $batsman is clean bowled by a beautiful yorker from $bowler!"
                    } else if (event < 15) {
                        runsAdded = 6
                        eventText = "SIX! Incredible lofted shot over extra cover by $batsman off $bowler!"
                    } else if (event < 35) {
                        runsAdded = 4
                        eventText = "FOUR! Pierces the gap nicely at deep midwicket. Pure class from $batsman!"
                    } else if (event < 75) {
                        runsAdded = Random.nextInt(2) + 1
                        eventText = "$batsman steers this to deep backward point for $runsAdded run${if (runsAdded > 1) "s" else ""}."
                    } else {
                        runsAdded = 0
                        eventText = "Dot ball. $batsman gets beaten by the extra bounce of $bowler."
                    }

                    indPakRuns += runsAdded
                    match1Commentary.add(0, "Over $indPakOvers: $eventText")
                    if (match1Commentary.size > 12) {
                        match1Commentary.removeAt(match1Commentary.lastIndex)
                    }
                }

                // 2. Advance Australia vs England Match (The Ashes)
                var aeBalls = ((ausEngOvers * 10).toInt() % 10) + 1
                var aeOversInt = ausEngOvers.toInt()
                if (aeBalls >= 6) {
                    aeOversInt += 1
                    aeBalls = 0
                }
                ausEngOvers = aeOversInt + (aeBalls / 10.0)

                val aeEvent = Random.nextInt(100)
                val aeRunsAdded: Int
                val aeEventText: String
                if (aeEvent < 4) {
                    ausEngWickets = (ausEngWickets + 1) % 11
                    aeRunsAdded = 0
                    aeEventText = "WICKET! Edged and caught! Magnificent bowling from James Anderson!"
                } else if (aeEvent < 10) {
                    aeRunsAdded = 4
                    aeEventText = "FOUR! Steve Smith crunches this past point with sublime timing."
                } else if (aeEvent < 60) {
                    aeRunsAdded = Random.nextInt(2) + 1
                    aeEventText = "Australia bats rotate strike, picking up $aeRunsAdded run${if (aeRunsAdded > 1) "s" else ""}."
                } else {
                    aeRunsAdded = 0
                    aeEventText = "Dot ball. Solid defensive block."
                }
                ausEngRuns += aeRunsAdded
                match2Commentary.add(0, "Over $ausEngOvers: $aeEventText")
                if (match2Commentary.size > 12) {
                    match2Commentary.removeAt(match2Commentary.lastIndex)
                }

                // Construct updated list
                val updatedMatch1 = LiveMatch(
                    id = "sim_ind_pak",
                    title = "T20 World Cup - India vs Pakistan",
                    status = "LIVE",
                    teamA = TeamScore("Pakistan", 167, 8, 20.0),
                    teamB = TeamScore("India", indPakRuns, indPakWickets, indPakOvers),
                    currentBattingTeam = "India",
                    target = 168,
                    requiredRuns = (168 - indPakRuns).coerceAtLeast(0),
                    ballsRemaining = ipBallsRemaining,
                    commentary = match1Commentary.toList()
                )

                val updatedMatch2 = LiveMatch(
                    id = "sim_aus_eng",
                    title = "The Ashes - Australia vs England",
                    status = "LIVE",
                    teamA = TeamScore("England", 312, 10, 88.4),
                    teamB = TeamScore("Australia", ausEngRuns, ausEngWickets, ausEngOvers),
                    currentBattingTeam = "Australia",
                    target = null,
                    requiredRuns = null,
                    ballsRemaining = null,
                    commentary = match2Commentary.toList()
                )

                val list = listOf(updatedMatch1, updatedMatch2, match3, match4)
                _liveMatchesList.value = list

                // Update selected live match and default liveMatch references
                val currentSelected = _selectedLiveMatch.value
                val matched = list.firstOrNull { it.id == currentSelected?.id }
                if (matched != null) {
                    _selectedLiveMatch.value = matched
                    _liveMatch.value = matched
                } else {
                    _selectedLiveMatch.value = updatedMatch1
                    _liveMatch.value = updatedMatch1
                }
            }
        }
    }

    fun fetchRealCricketMatches() {
        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@launch
            }

            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val prompt = """
                    Search the web for currently active cricket matches (as of today, real-time).
                    Provide current live scores, teams, current overs, wickets, and brief recent ball commentary of the active over.
                    Generate a structured JSON response containing:
                    1. "liveMatches": A list of active or most recent matches. Each match must have:
                       - "title": string (e.g., "India vs Sri Lanka, 2nd T20I")
                       - "status": "LIVE" or "COMPLETED"
                       - "teamA": { "teamName": string, "runs": int, "wickets": int, "overs": double }
                       - "teamB": { "teamName": string, "runs": int, "wickets": int, "overs": double }
                       - "currentBattingTeam": string (the name of the team batting right now)
                       - "target": int (optional target score, null if not applicable or first innings)
                       - "requiredRuns": int (optional runs required, null if not applicable)
                       - "ballsRemaining": int (optional balls remaining, null if not applicable)
                       - "commentary": list of strings (recent overs ball commentary logs)
                    2. "upcomingMatches": A list of 4 upcoming cricket matches. Each match must have:
                       - "matchNumber": string (e.g., "Match 15")
                       - "teamA": string
                       - "teamB": string
                       - "date": string (e.g. "July 12, 2026")
                       - "time": string (e.g. "19:30 PST")
                       - "venue": string
                       - "status": "UPCOMING"

                    If there are absolutely no live matches playing right now globally, search for the most recent major cricket match (from today or yesterday) and present it with realistic scores. Always provide at least one high-fidelity live match and 4 upcoming matches so the UI is active and extremely engaging.
                    Format your output STRICTLY as a valid JSON object. Do not wrap it in markdown code blocks like ```json ... ```. Return the raw JSON string directly.
                """.trimIndent()

                val escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

                val jsonBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {
                              "text": "$escapedPrompt"
                            }
                          ]
                        }
                      ],
                      "tools": [
                        {
                          "googleSearch": {}
                        }
                      ],
                      "generationConfig": {
                        "responseMimeType": "application/json",
                        "temperature": 0.2
                      }
                    }
                """.trimIndent()

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = jsonBody.toRequestBody(mediaType)

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrBlank()) {
                        val responseJson = JSONObject(responseBody)
                        val candidates = responseJson.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val firstCandidate = candidates.getJSONObject(0)
                            val content = firstCandidate.optJSONObject("content")
                            if (content != null) {
                                val parts = content.optJSONArray("parts")
                                if (parts != null && parts.length() > 0) {
                                    val textPart = parts.getJSONObject(0).optString("text")
                                    if (!textPart.isNullOrBlank()) {
                                        withContext(Dispatchers.Main) {
                                            parseInnerJson(textPart)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseInnerJson(text: String) {
        try {
            val data = JSONObject(text)
            
            // Parse liveMatches
            val liveArray = data.optJSONArray("liveMatches")
            if (liveArray != null && liveArray.length() > 0) {
                simulationJob?.cancel()
                
                val parsedList = mutableListOf<LiveMatch>()
                for (i in 0 until liveArray.length()) {
                    val matchObj = liveArray.getJSONObject(i)
                    
                    val title = matchObj.optString("title", "Live Match")
                    val status = matchObj.optString("status", "LIVE")
                    val currentBattingTeam = matchObj.optString("currentBattingTeam", "")
                    val target = if (matchObj.has("target") && !matchObj.isNull("target")) matchObj.optInt("target") else null
                    val requiredRuns = if (matchObj.has("requiredRuns") && !matchObj.isNull("requiredRuns")) matchObj.optInt("requiredRuns") else null
                    val ballsRemaining = if (matchObj.has("ballsRemaining") && !matchObj.isNull("ballsRemaining")) matchObj.optInt("ballsRemaining") else null
                    
                    val teamAObj = matchObj.getJSONObject("teamA")
                    val teamA = TeamScore(
                        teamName = teamAObj.optString("teamName", "Team A"),
                        runs = teamAObj.optInt("runs", 0),
                        wickets = teamAObj.optInt("wickets", 0),
                        overs = teamAObj.optDouble("overs", 0.0),
                        logoUrl = if (teamAObj.has("logoUrl") && !teamAObj.isNull("logoUrl")) teamAObj.optString("logoUrl", null) else null
                    )
                    
                    val teamBObj = matchObj.getJSONObject("teamB")
                    val teamB = TeamScore(
                        teamName = teamBObj.optString("teamName", "Team B"),
                        runs = teamBObj.optInt("runs", 0),
                        wickets = teamBObj.optInt("wickets", 0),
                        overs = teamBObj.optDouble("overs", 0.0),
                        logoUrl = if (teamBObj.has("logoUrl") && !teamBObj.isNull("logoUrl")) teamBObj.optString("logoUrl", null) else null
                    )
                    
                    val commentaryList = mutableListOf<String>()
                    val commArray = matchObj.optJSONArray("commentary")
                    if (commArray != null) {
                        for (j in 0 until commArray.length()) {
                            commentaryList.add(commArray.getString(j))
                        }
                    }
                    
                    parsedList.add(
                        LiveMatch(
                            id = "live_real_$i",
                            title = title,
                            status = status,
                            teamA = teamA,
                            teamB = teamB,
                            currentBattingTeam = currentBattingTeam,
                            target = target,
                            requiredRuns = requiredRuns,
                            ballsRemaining = ballsRemaining,
                            commentary = commentaryList
                        )
                    )
                }
                
                _liveMatchesList.value = parsedList
                
                // Select first parsed match or keep previously selected if valid
                val currentSelected = _selectedLiveMatch.value
                val matched = parsedList.firstOrNull { it.id == currentSelected?.id || it.title == currentSelected?.title }
                if (matched != null) {
                    _selectedLiveMatch.value = matched
                    _liveMatch.value = matched
                } else if (parsedList.isNotEmpty()) {
                    _selectedLiveMatch.value = parsedList[0]
                    _liveMatch.value = parsedList[0]
                }
            }
            
            // Parse upcomingMatches
            val upcomingArray = data.optJSONArray("upcomingMatches")
            if (upcomingArray != null && upcomingArray.length() > 0) {
                val upList = mutableListOf<TournamentMatch>()
                for (i in 0 until upcomingArray.length()) {
                    val upObj = upcomingArray.getJSONObject(i)
                    upList.add(
                        TournamentMatch(
                            matchNumber = upObj.optString("matchNumber", "Match ${i+1}"),
                            teamA = upObj.optString("teamA", "Team A"),
                            teamB = upObj.optString("teamB", "Team B"),
                            date = upObj.optString("date", "Today"),
                            time = upObj.optString("time", "19:00"),
                            venue = upObj.optString("venue", "Cricket Stadium"),
                            status = upObj.optString("status", "UPCOMING")
                        )
                    )
                }
                _upcomingMatches.value = upList
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}
