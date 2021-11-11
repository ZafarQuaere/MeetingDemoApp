package com.pgi.network.repository


/**
 * Created by Sudheer Chilumula on 9/19/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */
abstract class BaseRepository {
    protected val maxRetries = 2
    protected val deBounceTimeout: Long = 300
    protected val retryTimeout: Long = 1000
}