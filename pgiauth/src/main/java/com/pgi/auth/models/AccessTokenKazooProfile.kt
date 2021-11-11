package com.pgi.auth.models

data class AccessTokenKazooProfile(
    val Kazoo: List<TokenKazooProfile> = emptyList()
																	)

data class TokenKazooProfile(
		val account_id: String? = null,
		val user_id: String? = null
)