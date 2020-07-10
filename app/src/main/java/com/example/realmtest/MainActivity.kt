package com.example.realmtest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.realm.Realm
import io.realm.RealmConfiguration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Realm 초기화
         * 어플리케이션이 실행되는 시점 또는 Realm 을 처음 사용하는 시점에 선언해준다
         */
        Realm.init(this)

        /**
         * Realm 설정
         * Realm Instance 는 Thread Singleton 이므로 매 Thread 마다 정적 생성자가 동일한 Instance 를 반환한다
         *
         * name : Context.filesDir 에 위치한 test.realm 파일 ( 절대경로 : realm.path )
         * schemaVersion : 데이터 모듈이 수정되면 어떻게 변경되었는지 알려줘야하는데(Migration) schemaVersion 으로 관리. 추후에 상세하게 다룬다
         * deleteRealmIfMigrationNeeded : Migration 무시. 주로 개발할 때 사용한다.
         * modules : 사용할 모듈(데이터 모델)
         * encryptionKey : 64Byte 암호화 키
         *  - 처음 32Byte 는 암호화에 사용되고 다음 24Byte 는 서명에 사용되고 8Byte 는 현재 사용 X.
         *  - 각 4KB 데이터 블록은 암호화 블록체인(CBC) 모드와
         *    파일 내에서 절대 재사용되지 않는 고유한 초기화 백터(IV)를 사용하여 AES-256으로 암호화 된 후 SHA 로 서명.
         */
        val key = ByteArray(64)
        val configuration = RealmConfiguration.Builder()
            .name("test.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .modules(UserModule::class.java)
            .encryptionKey(key)
            .build()
        val realm:Realm = Realm.getInstance(configuration)









    }

}