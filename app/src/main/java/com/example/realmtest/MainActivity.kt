package com.example.realmtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.realm.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var realm:Realm

    private fun realmJoin(){
        realm.beginTransaction()
        val user:UserModel = realm.createObject(UserModel::class.java, realmLastIndex())
        val email01: EmailModel = realm.createObject(EmailModel::class.java)
        email01.address = "aaa@naver.com"
        user.emails?.add(email01)
        val email02: EmailModel = realm.createObject(EmailModel::class.java)
        email02.address = "bbb@google.com"
        user.emails?.add(email02)
        realm.commitTransaction()
        realmRead()
    }

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
        btn_join.setOnClickListener{
            realmJoin()
        }
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
        val index = realm.where(UserModel::class.java).max("index")
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
        val user:UserModel = realm.createObject(UserModel::class.java, realmLastIndex())
        user.name = "이름"
        realm.commitTransaction()
        realmRead()
    }

    // 데이터 보기
    private fun realmRead(){
        realm.beginTransaction()
        val realmResult:RealmResults<UserModel> =
            realm.where(UserModel::class.java).findAll().sort("index", Sort.ASCENDING)
        realm.commitTransaction()
        Log.d(TAG, "realmRead : " + realmResult.asJSON())
    }

    // 마지막 데이터 수정
    private fun realmLastDataUpdate(){
        realm.beginTransaction()
        val realmResult: UserModel? =
            realm.where(UserModel::class.java).equalTo("index", realmLastIndex()-1).findFirst()
        realmResult?.name = "수정"
        realm.commitTransaction()
        realmRead()
    }

    // 마지막 데이터 삭제
    private fun realmLastDataDelete(){
        realm.beginTransaction()
        val realmResult: UserModel? =
            realm.where(UserModel::class.java).equalTo("index", realmLastIndex()-1).findFirst()
        realmResult?.deleteFromRealm()
        realm.commitTransaction()
        realmRead()
    }

    // 전체 데이터 삭제
    private fun realmAllDataDelete(){
        realm.beginTransaction()
        val result: RealmResults<UserModel> =
            realm.where(UserModel::class.java).findAll()
        result.deleteAllFromRealm()
        realm.commitTransaction()
        realmRead()
    }

}