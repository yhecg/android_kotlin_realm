package com.example.realmtest

import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration

object RealmManager {

    private val TAG = RealmManager::class.java.simpleName

    private var instance : Realm? = null

    fun getInstance():Realm?{
        if(instance == null){
            val conf : RealmConfiguration = RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(1)
                .migration(Migration())
                .build()
            instance = Realm.getInstance(conf)
        }
        return instance
    }

}