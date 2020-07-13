package com.example.realmtest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmModule
import io.realm.annotations.Required

/**
 * 데이터 모델
 */
open class UserModel : RealmObject() {

    @PrimaryKey var index:Int = 0
    var name: String = ""
    var migrationTest: String? = "" // 추가로 생성한 필드. Migration

}