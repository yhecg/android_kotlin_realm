package com.example.realmtest

import android.util.Log
import com.example.realmtest.encryption.Util
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import java.security.SecureRandom

object RealmManager {

    private val TAG = RealmManager::class.java.simpleName

    private var instance : Realm? = null

    fun getInstance():Realm?{
        if(instance == null){
//            val key = ByteArray(64)
//            SecureRandom().nextBytes(key)
//            Log.d(TAG, "key : " + Util.bytesToHex(key))
            val conf : RealmConfiguration = RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(1)
                .migration(Migration())
                .deleteRealmIfMigrationNeeded() // 개발단계에서만 사용하자
//                .encryptionKey(key)
                .build()
            instance = Realm.getInstance(conf)
        }
        return instance
    }

}