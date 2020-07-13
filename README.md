<br>

Realm 이란 ?
------------

오픈 소스 데이터베이스 관리시스템(DBMS). NoSQL 데이터베이스를 지향하며, 데이터 모델 구조 자체가 객체 컨테이너로 구성되어 있다.<br><br>![screensh](./mdImg/query_compare.png) - Realm과 타 데이터베이스의 초당 쿼리수 비교

제한
----

-	클래스 이름은 최대 57자
-	필드 이름은 최대 63자
-	모델 클래스는 RealmObject 이외의 클래스 상속 불가능

Gradle
------

```kotlin
dependencies {
   classpath "io.realm:realm-gradle-plugin:6.0.2"
}
```

build.gradle(Project)

```kotlin
apply plugin: 'kotlin-kapt'
apply plugin: 'realm-android'
```

build.gradle(app)

Model Class
-----------

```kotlin
open class UserModel : RealmObject() {
    @PrimaryKey var index:Int = 0
    var name: String = ""
}
```

-	모델 클래스는 SQL의 테이블을 생각하면 될 것 같다.<br> Table : UserModel Column : index, name
-	Realm 필드 타입 지원 : short, int, long -> long타입으로 대응. Boolean, Byte, Short, Integer, Long, Double -> null 가능
-	@Required 어노테이션은 null 값 허용하지 않음 (Boolean, Byte, Short, Integer, Long, Float, Double, String, ByteArray, Date)
-	RealmList 는 암묵적으로 Required.
-	RealmObject 형의 필드는 항상 null 가능.

시작(선언)
----------

```kotlin
Realm.init(context)
```

어플리케이션이 실행되는 시점 또는 Realm을 사용하는 공통된 부분의 처음 시점에서 선언.

설정(연결) 및 해제
------------------

```kotlin
val configuration = RealmConfiguration.Builder()
    .name(fileName) // fileName ex > default.realm
    .schemaVersion(0)
    .deleteRealmIfMigrationNeeded()
    .modules(Model::class.java)
    .encryptionKey(key)
    .inMemory()
    .build()
val realm = Realm.getInstance(configuration)
```

```kotlin
realm.close()
```

-	보통 연결은 onCreate(), 해제는 onDestroy()
-	Realm Instance 는 Thread Singleton 이므로 매 Thread 마다 정적 생성자가 동일한 Instance 를 반환한다
-	name : Context.filesDir 에 위치한 test.realm 파일 ( 절대경로 : realm.path )
-	schemaVersion : 데이터 모듈이 수정되면 어떻게 변경되었는지 알려줘야하는데(Migration) schemaVersion 으로 관리. 추후에 상세하게 다룬다
-	deleteRealmIfMigrationNeeded : Migration 무시. 주로 개발할 때 사용한다.
-	modules : 사용할 모듈(데이터 모델). 꼭 필요하지는 않는듯 하다
-	encryptionKey : 64Byte 암호화 키 처음 32Byte 는 암호화에 사용되고 다음 24Byte 는 서명에 사용되고 8Byte 는 현재 사용 X. 각 4KB 데이터 블록은 암호화 블록체인(CBC) 모드와 파일 내에서 절대 재사용되지 않는 고유한 초기화 백터(IV)를 사용하여 AES-256으로 암호화 된 후 SHA 로 서명.
-	inMemory : 디스크에 저장하지 않고 메모리에 생성되고 Realm 이 닫히면 삭제된다.

READ
----

```kotlin
realm.beginTransaction()
val realmResult:RealmResults<UserModel> =
    realm.where(UserModel::class.java).findAll().sort("index", Sort.ASCENDING)
val singleUser: UserModel =
    realm.where(UserModel::class.java).equalTo("index", "index 값").findFirst()
realm.commitTransaction()
```

-	데이터를 Select할 때 where절을 이용한다.
-	전체 데이터를 찾으려면 findAll()
-	하나의 데이터만 찾으려면 findFirst()
-	sort, equalTo 첫번째 파라미터는 필드명, 두번째 파라미터는 value

INSERT
------

```kotlin
realm.beginTransaction()
val index = realm.where(UserModel::class.java).max("index")
val nextIndex = if(index == null){
  1
}else{
  index.toInt() + 1
}
val user:UserModel = realm.createObject(UserModel::class.java, nextIndex)
user.name = "이름"
realm.commitTransaction()
```

-	Realm에서는 auth_increment가 없어 PrimaryKey를 증가시키려면 수동으로 해줘야 한다.
-	PrimaryKey가 없는 경우에는 nextIndex를 빼주면 된다. realm.createObject(UserModel::class.java)

UPDATE
------

```kotlin
realm.beginTransaction()
val user: UserModel =
    realm.where(UserModel::class.java).equalTo("index", "index 값").findFirst()
user.필드명 = 수정할 값 // user.name = "수정"
realm.commitTransaction()
```

DELETE
------

```kotlin
realm.beginTransaction()
// 데이터 하나 삭제
val user: UserModel =
    realm.where(UserModel::class.java).equalTo("index", "index 값").findFirst()
user.deleteFromRealm()
// 데이터 전체 삭제
val result: RealmResults<UserModel> =
    realm.where(UserModel::class.java).findAll()
result.deleteAllFromRealm()
realm.commitTransaction()
```

Migration
---------

```kotlin
open class UserModel : RealmObject() {
    @PrimaryKey var index:Int = 0
    var name: String = ""
    var migrationTest: String? = "" // 추가로 생성한 필드
}
```

```kotlin
open class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVer = oldVersion
        val schema = realm.schema
        if(oldVer == 0L){
            val tdmSchema = schema.get("UserModule")
            tdmSchema.addField("migrationTest", String::class.java)
        }
    }
}
```

```kotlin
val conf : RealmConfiguration =
            RealmConfiguration.Builder()
            .name("default.realm")
            .schemaVersion(1)
            .migration(Migration())
            .build()
```

-	데이터 모델이 수정되는 경우에는 Migration 관리를 해줘야 한다.
-	Migration 클래스를 생성하여 변경된 사항을 기록해준다.
-	Realm 설정 부분에서 schemaVersion을 수정해주고 migration을 추가해준다. 데이터 모델을 수정될때마다 schemeVersion을 한단계씩 올려야한다.
-	데이터 모델을 수정하였는데 이러한 작업을 해주지 않으면 다음 오류가 나타난다.<br><span style="color:red">io.realm.exceptions.RealmMigrationNeededException: Field count is less than expected</span><br>

두개 이상의 모델 사용하기
-------------------------

```kotlin
open class UserModel : RealmObject() {
    @PrimaryKey var index:Int = 0
    var name: String = ""
    var emails:RealmList<EmailModel>? = null // 추가
}
open class EmailModel : RealmObject(){ // 추가
    var address:String = ""
}
```

```kotlin
realm.beginTransaction()
val index = realm.where(UserModel::class.java).max("index")
val nextIndex = if(index == null){
  1
}else{
  index.toInt() + 1
}

val user:UserModel = realm.createObject(UserModel::class.java, nextIndex)

val email01: EmailModel = realm.createObject(EmailModel::class.java)
email01.address = "aaa@naver.com"
user.emails.add(email01)

val email02: EmailModel = realm.createObject(EmailModel::class.java)
email02.address = "bbb@google.com"
user.emails.add(email02)

realm.commitTransaction()

// 결과값 Ex) [{"index":1,"name":"","migrationTest":"","emails":[{"address":"aaa@naver.com"}, {"address":"bbb@google.com"}]}]
```

<span style="color:red">Encryption 암호화</span>
------------------------------------------------

<span style="color:red">아직 정확한 정보가 아니지만 일단 적어둔다</span>

```java
public class Util {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    //Original source: https://stackoverflow.com/a/9855338/1389357
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
```

```kotlin
val key = ByteArray(64)
SecureRandom().nextBytes(key)
Log.d("RealmEncryptionKey", Util.bytesToHex(key))
val conf : RealmConfiguration = RealmConfiguration.Builder()
    .name("default.realm")
    .encryptionKey(key)
    .build()
```

-	아직 암호화에 대해 정확히 알지 못하고 위 설정 및 해제의 설명이 전부이다.<br>설명 : 64Byte 암호화 키 처음 32Byte 는 암호화에 사용되고 다음 24Byte 는 서명에 사용되고 8Byte 는 현재 사용 X. 각 4KB 데이터 블록은 암호화 블록체인(CBC) 모드와 파일 내에서 절대 재사용되지 않는 고유한 초기화 백터(IV)를 사용하여 AES-256으로 암호화 된 후 SHA 로 서명.<br>
