package com.pgi.logging.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pgi.logging.model.ElkLogItem
import com.pgi.logging.model.ElkLogMessage
import com.pgi.logging.model.RealmMap
import io.realm.RealmList
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealmMapSerializerTest {

  private lateinit var mapSerializer: RealmMapSerializer
  private lateinit var map: RealmMap
  private lateinit var logBaseMsg: ElkLogMessage
  private lateinit var gson: Gson

  @Before
  fun `setup`() {
    mapSerializer = RealmMapSerializer()
    map = RealmMap()
    map.key = "Test Key"
    map.value = "Test value"
    val mapCOunt = RealmMap()
    mapCOunt.key = "logCount"
    mapCOunt.value = "1"
    val keyValueRealmListType = object : TypeToken<RealmList<RealmMap>>() {}.getType()
    gson = GsonBuilder().registerTypeAdapter(keyValueRealmListType,
        RealmMapSerializer()).create()
    logBaseMsg = ElkLogMessage()
    val logItem = ElkLogItem()
    logItem.customFields.add(map)
    logItem.customFields.add(mapCOunt)
    logBaseMsg.logItems.add(logItem)
  }

  @Test
  fun `test Serialize deserializer`() {
    val logJson = gson.toJson(logBaseMsg)
    val json = gson.fromJson(logJson, ElkLogMessage::class.java)
    Assert.assertEquals(json.logItems[0].customFields[0]?.value, "Test value")
    Assert.assertEquals(json.logItems[0].customFields[1]?.value, "1")
  }
}