package com.pgi.logging.database

import com.pgi.logging.model.ElkLogItem
import com.pgi.logging.model.RealmMap
import com.pgi.logging.model.SortedElkLogItem
import io.realm.annotations.RealmModule

@RealmModule(library = true, classes = [(SortedElkLogItem::class),(ElkLogItem::class),
  (RealmMap::class)])
class LogRealmModule {
}