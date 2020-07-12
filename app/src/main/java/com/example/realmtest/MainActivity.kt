package com.example.realmtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.realm.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var realm:Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRealm()
        initClickEvent()

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    // Realm 설정
    private fun initRealm(){
        Realm.init(this)
        realm = RealmManager.getInstance()!!
        Log.d(TAG," ::::: " + realm.version + " ::: " + realm.schema + " ::: " + realm.path)
    }

    // Click Event 설정
    private fun initClickEvent(){
        btn_insert.setOnClickListener{
            realmInsert()
        }
        btn_read.setOnClickListener{
            realmRead()
        }
        btn_last_data_update.setOnClickListener{
            realmLastDataUpdate()
        }
        btn_last_data_delete.setOnClickListener{
            realmLastDataDelete()
        }
        btn_all_data_delete.setOnClickListener{
            realmAllDataDelete()
        }
    }

    // 마지막 데이터의 index + 1
    private fun realmLastIndex(): Int {
        val index = realm.where(UserModule::class.java).max("index")
        val lastIndex = if(index == null){
            1
        }else{
            index.toInt() + 1
        }
        return lastIndex
    }

    // 데이터 추가
    private fun realmInsert(){
        realm.beginTransaction()
        val user:UserModule = realm.createObject(UserModule::class.java, realmLastIndex())
        user.name = "이름"
        realm.commitTransaction()
    }

    // 데이터 보기
    private fun realmRead(){
        realm.beginTransaction()
        val realmResult:RealmResults<UserModule> =
            realm.where(UserModule::class.java).findAll().sort("index", Sort.ASCENDING)
        realm.commitTransaction()
        Log.d(TAG, "realmRead : " + realmResult.asJSON())
    }

    // 마지막 데이터 수정
    private fun realmLastDataUpdate(){
        realm.beginTransaction()
        val realmResult: UserModule? =
            realm.where(UserModule::class.java).equalTo("index", realmLastIndex()-1).findFirst()
        realmResult?.name = "수정"
        realm.commitTransaction()
    }

    // 마지막 데이터 삭제
    private fun realmLastDataDelete(){
        realm.beginTransaction()
        val realmResult: UserModule? =
            realm.where(UserModule::class.java).equalTo("index", realmLastIndex()-1).findFirst()
        realmResult?.deleteFromRealm()
        realm.commitTransaction()
    }

    // 전체 데이터 삭제
    private fun realmAllDataDelete(){
        realm.beginTransaction()
        val result: RealmResults<UserModule> =
            realm.where(UserModule::class.java).findAll()
        result.deleteAllFromRealm()
        realm.commitTransaction()
    }

}