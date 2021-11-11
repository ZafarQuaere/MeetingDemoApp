package com.pgi.logging.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class ElkLogItem : RealmObject() {
  @PrimaryKey
  var id: String = UUID.randomUUID().toString()
  var logLevel: String = "info"
  var loggerName: String? = null
  var timestamp: String? = null
  var transactionId: String? = null
  var subTransactionId: String? = null
  var message: String? = null
  var exceptionType: String? = null
  var exceptionMessage: String? = null
  var exceptionTrace: String? = null
  var customFields: RealmList<RealmMap> = RealmList()
  var tags: RealmList<String> = RealmList()
  var metricsAnalysis: Boolean = true
  var metrics: RealmList<RealmMap> = RealmList()
}