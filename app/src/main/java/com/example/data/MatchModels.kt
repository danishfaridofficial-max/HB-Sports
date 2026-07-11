package com.example.data

data class TeamScore(
    val teamName: String,
    val runs: Int,
    val wickets: Int,
    val overs: Double,
    val logoUrl: String? = null
)

data class LiveMatch(
    val id: String,
    val title: String,
    val status: String, // "LIVE", "UPCOMING", "COMPLETED"
    val teamA: TeamScore,
    val teamB: TeamScore,
    val currentBattingTeam: String,
    val requiredRuns: Int? = null,
    val ballsRemaining: Int? = null,
    val target: Int? = null,
    val commentary: List<String> = emptyList()
)

data class TournamentMatch(
    val matchNumber: String,
    val teamA: String,
    val teamB: String,
    val date: String,
    val time: String,
    val venue: String,
    val status: String
)
