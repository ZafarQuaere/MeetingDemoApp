package com.pgi.convergence.listeners

/**
 * Callbacks on checking permission
 *
 *
 * 1.  Below M, runtime permission not needed. In that case onPermissionGranted() would be called.
 * If permission is already granted, onPermissionGranted() would be called.
 *
 *
 * 2.  Above M, if the permission is being asked first time onNeedPermission() would be called.
 *
 *
 * 3.  Above M, if the permission is previously asked but not granted, onPermissionPreviouslyDenied()
 * would be called.
 *
 *
 * 4.  Above M, if the permission is disabled by device policy or the user checked "Don't ask again"
 * check box on previous request permission, onPermissionDisabled() would be called.
 *
 *
 * Created by surbhidhingra on 15-11-17.
 */
interface PermissionAskListener {
	/**
	 * Callback to ask permission
	 */
	fun onNeedPermission(permission: String)

	/**
	 * Callback on permission denied
	 */
	fun onPermissionPreviouslyDenied(permission: String)

	/**
	 * Callback on permission "Never show again" checked and denied
	 */
	fun onPermissionDisabled(permission: String)

	/**
	 * Callback on permission granted
	 */
	fun onPermissionGranted(permission: String)

	/**
	 * Callbacks on allowing permissions from rationale alert
	 */
	fun onPermissionAllowedFromRationale(permission: String)

	/**
	 * Callbacks on denying permissions from rationale alert
	 */
	fun onPermissionDeniedFromRationale(permission: String)
}