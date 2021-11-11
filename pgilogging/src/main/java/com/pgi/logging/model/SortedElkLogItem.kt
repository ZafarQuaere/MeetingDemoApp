package com.pgi.logging.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SortedElkLogItem: RealmObject() {
  @PrimaryKey
  var id: Long = 0
  var logItems: RealmList<ElkLogItem> = RealmList()
}