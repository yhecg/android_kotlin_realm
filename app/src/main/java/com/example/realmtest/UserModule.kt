package com.example.realmtest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmModule
import io.realm.annotations.Required

/**
 * 데이터 모델
 * 코틀린은 자바와 다르게 기본이 final 이라 open 을 추가해줘야 한다
 */
open class UserModule : RealmObject() {

    @PrimaryKey var index:Int = 0
    var name: String = ""
    var migrationTest: String? = "" // 추가로 생성한 필드

}