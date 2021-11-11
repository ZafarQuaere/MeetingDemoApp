package com.pgi.auth

import android.content.Context
import android.content.Intent


/**
 * Created by Sudheer Chilumula on 2018-12-05.
 * PGi
 * sudheer.chilumula@pgi.com
 */
interface PGiAuthService {
  fun isAuthorized(): Boolean
  fun doAuth(context: Context, locale: String, completionClass: String, cancelClass: String)
  fun onAuthResponse(intent: Intent)
  fun authState(): Any
  fun signOut()
  fun destroy()
  fun refreshAccessToken()
}