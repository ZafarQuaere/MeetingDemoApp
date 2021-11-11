package com.pgi.convergencemeetings.utils

import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.greendao.ApplicationDao
import java.util.*

object UUIDGenerator {
    var uUID: String? = generateUUID();

    private fun generateUUID(): String? {
        val applicationDao: ApplicationDao? = ApplicationDao.get(CoreApplication.appContext);
        val uidDao = com.pgi.convergencemeetings.models.UUID(UUID.randomUUID().toString())
        return if(applicationDao !== null) {
            try {
                val list = applicationDao.uuid.loadAll()
                if (list != null && !list.isEmpty()) {
                    list[0]?.value
                } else {
                    uidDao.value
                }
            } catch (e: Exception) {
                uidDao.value
            }
        } else {
            applicationDao?.uuid?.insertOrReplace(uidDao)
            uidDao.value;
        }
    }
}

