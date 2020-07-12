package com.example.realmtest

import android.util.Log
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

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