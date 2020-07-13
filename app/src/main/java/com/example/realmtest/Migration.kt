package com.example.realmtest

import android.util.Log
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

/**
 * 데이터 모델이 변경될때마다 업데이트를 해줘야 한다.
 */
open class Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion
        val schema = realm.schema
        if(oldVersion == 0L){
            val tdmSchema = schema.get("UserModule")
            tdmSchema!!.addField("migrationTest", String::class.java)
            oldVersion++
        }
    }

}