package com.pgi.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.pgi.convergence.utils.KotlinSingletonHolder
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import org.json.JSONException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by Sudheer Chilumula on 2018-12-05.
 * PGi
 * sudheer.chilumula@pgi.com
 */
class PGiAuthStateManager private constructor(context: Context) {
  private val TAG = "PGiAuthStateManager"
  private val STORE_NAME = "AuthState"
  private val KEY_STATE = "state"

  private var mPrefs: SharedPreferences? = null
  private var mPrefsLock: ReentrantLock = ReentrantLock()
  private var mCurrentAuthState: AtomicReference<AuthState> = AtomicReference()

  init {
    mPrefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
  }

  companion object: KotlinSingletonHolder<PGiAuthStateManager, Context>({
    val instance: AtomicReference<WeakReference<PGiAuthStateManager>> = AtomicReference(WeakReference(PGiAuthStateManager(it)))
    instance.get().get()!!
  })

  fun getCurrent(): AuthState {
    if (mCurrentAuthState.get() != null) {
      return mCurrentAuthState.get() as AuthState
    }

    val state = readState()
    return if (mCurrentAuthState.compareAndSet(null, state)) {
      state
    } else {
      mCurrentAuthState.get() as AuthState
    }
  }

  fun replace(state: AuthState): AuthState {
    writeState(state)
    mCurrentAuthState.set(state)
    return state
  }

  fun updateAfterAuthorization(
      response: AuthorizationResponse?,
      ex: AuthorizationException?): AuthState {
    val current = getCurrent()
    current.update(response, ex)
    return replace(current)
  }

  fun updateAfterTokenResponse(
      response: TokenResponse?,
      ex: AuthorizationException?): AuthState {
    val current = getCurrent()
    current.update(response, ex)
    return replace(current)
  }

  fun readState(): AuthState {
    mPrefsLock.lock()
    try {
      val currentState = mPrefs?.getString(KEY_STATE, null) ?: return AuthState()

      try {
        return AuthState.jsonDeserialize(currentState)
      } catch (ex: JSONException) {
        Log.w(TAG, "Failed to deserialize stored auth state - discarding")
        return AuthState()
      }

    } finally {
      mPrefsLock.unlock()
    }
  }

  fun writeState(state: AuthState?) {
    mPrefsLock.lock()
    try {
      val editor = mPrefs?.edit()
      if (state == null) {
        editor?.remove(KEY_STATE)
      } else {
        editor?.putString(KEY_STATE, state.jsonSerializeString())
      }

      if (editor?.commit() == false) {
        throw IllegalStateException("Failed to write state to shared prefs")
      }
    } finally {
      mPrefsLock.unlock()
    }
  }
}