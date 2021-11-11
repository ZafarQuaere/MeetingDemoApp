package com.pgi.logging.model

import io.realm.RealmObject

open class RealmMap : RealmObject()  {
    var key: String? = null
    var value: String? = null
}
