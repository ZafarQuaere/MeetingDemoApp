package com.pgi.network.models

data class TurnServerInfo(
    val username: String,
    val password: String,
    val ttl: Long,
    val iceServers: List<IceServer>
)

data class IceServer(
    val urls: List<String>,
    val username: String,
    val credential: String
)