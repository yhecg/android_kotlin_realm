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
         * inMemory : 디스크에 저장하지 않고 메모리에 생성되고 Realm 이 닫히면 삭제된다. ( 변수에 담는 것과 무슨 차이일까 ? )
         */
        val key = ByteArray(64)
        val configuration = RealmConfiguration.Builder()
            .name("test.realm")
//            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .modules(UserModule::class.java)
//            .encryptionKey(key)
//            .inMemory()
            .build()
        val realm:Realm = Realm.getInstance(configuration)

        /**
         * Realm 실행 및 커밋
         * DB 작업을 시작할 때 beginTransaction
         * DB 작업을 종료할 때 commitTransaction
         */
        realm.beginTransaction()
        realm.commitTransaction()

        /**
         * 데이터 저장
         * Realm 에서는 auto_increment 가 없어 PrimaryKey 를 증가시키려면 수동으로 해줘야 한다.
         * Realm.where(모델 클래스) 는 Select 의 개념이고 현재 사용한 max 는 최대값을 구하는 것.
         * 현재 index(PrimaryKey) 최대값을 구한 후 데이터가 없으면 1부터, 있으면 최대값을 + 1을 해주어 Insert 를 한다.
         * Realm.createObject 는 RealmObject 를 생성하여 데이터를 추가 해줄수 있다.
         */
        realm.beginTransaction()
        val index = realm.where(UserModule::class.java).max("index")
        val nextIndex = if(index == null){
            1
        }else{
            index.toInt() + 1
        }
        val user:UserModule = realm.createObject(UserModule::class.java, nextIndex)
//        val user:UserModule = realm.createObject(UserModule::class.java)
        user.name = "이름"
        realm.commitTransaction()

        /**
         * 데이터 보기
         */





















    }

}