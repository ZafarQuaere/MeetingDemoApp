package com.pgi.logging.database

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.pgi.logging.model.RealmMap
import io.realm.RealmList
import java.lang.reflect.Type


class RealmMapSerializer: JsonSerializer<RealmList<RealmMap>>, JsonDeserializer<RealmList<RealmMap>> {
  override fun serialize(src: RealmList<RealmMap>?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
    val keyValueJson = JsonObject()
    if (src != null) {
      for (realmMap in src) {
          keyValueJson.addProperty(realmMap.key, realmMap.value)
      }
    }
    return keyValueJson
  }

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RealmList<RealmMap> {
    val type = object : TypeToken<Map<String, String>>() {}.getType()
    val data = Gson().fromJson<Map<String, String>>(json, type)
    val realmMap = RealmList<RealmMap>()
    for (entry in data.entries) {
      val map = RealmMap()
      map.key = entry.key
      map.value = entry.value

      realmMap.add(map)
    }
    return realmMap
  }
}