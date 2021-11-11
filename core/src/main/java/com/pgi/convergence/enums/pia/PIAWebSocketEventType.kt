package com.pgi.convergence.enums.pia

/**
 * Created by surbhidhingra on 25-10-17.
 */
enum class PIAWebSocketEventType(val value: String) {
	PARTINFO("partinfo"),
	CONFINFO("confinfo"),
	CONFSTATE("confstate"),
	TALKER("talker"),
	WATCHLOST("watchlost"),
	CONFLINK("conflink"),
	CONFUNLINK("confunlink");
}