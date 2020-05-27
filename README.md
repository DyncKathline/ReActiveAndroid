![logo][logo]

<p align="center">
<a href="https://travis-ci.org/ImangazalievM/ReActiveAndroid"><img alt="Build Status" src="https://api.travis-ci.org/ImangazalievM/ReActiveAndroid.svg?branch=master"/></a>
<a href="https://bintray.com/imangazaliev/maven/reactiveandroid/_latestVersion"><img alt="Download" src="https://api.bintray.com/packages/imangazaliev/maven/reactiveandroid/images/download.svg"/></a>
<img alt="minSdkVersion 14" src="https://img.shields.io/badge/minSdkVersion-14-blue.svg"/>
<a href="https://opensource.org/licenses/MIT"><img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
</p>

<p align="center">
  <strong>Full Documentation: <a href="https://imangazalievm.gitbooks.io/reactiveandroid">ReActiveAndroid</a></strong>
</p>

ReActiveAndroid is Android ORM based on popular library [ActiveAndroid](https://github.com/pardom/ActiveAndroid). Unfortunately, the author of the library stopped maintaining it, so I decided to continue maintain the library instead of him.

New features in ReActiveAndroid in comparison with ActiveAndroid:

- multiple databases support
- more convenient migration mechanism
- a new and improved syntax for creating SQL queries
- One-to-Many relation
- inherited models
- table/model change notifications
- RxJava 2 support
- fixed bugs and more

In the plans:

- Annotation Processing instead of Java Reflection
- improved compatibility with Kotlin
- composite primary key support
- SQL Cipher support
- AutoValue support


## Installation

Add this to your app **build.gradle**:

```gradle
implementation 'com.reactiveandroid:reactiveandroid:1.4.3'
```

## Initial setup

First, we need to create a database class:

```java
@Database(name = "MyDatabase", version = 1)
public class AppDatabase {

}
```

In the `@Database` annotation  we specified the name of the database without an extension and the version of the schema.

Next, we need to initialize the library in the `onCreate` method of the Application class:

```java
DatabaseConfig appDatabase = new DatabaseConfig.Builder(AppDatabase.class)
        .build();

ReActiveAndroid.init(new ReActiveConfig.Builder(this)
        .addDatabaseConfigs(appDatabase)
        .build());
```

## Creating tables

To create a table, we need to create a model class that inherits from the `Model` class and annotate it with `@Table`:

```java
@Table(name = "Notes", database = AppDatabase.class)
public class Note extends Model {

    @PrimaryKey
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "text")
    private String text;

    public Note(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
```

In the `@Table` annotation we specify table name and the database class in which the table belongs. Also you can create a model class without inheritance from the `Model`, but then you will not have access to `save()` and `delete()` methods.

Now we can save, update and delete records:

```java
Note note = new Note("Title", "Text");
note.save();

note.setText("New text");
note.save();

note.delete();
```

## Query building

Building a query in ReActiveAndroid is like building a normal SQL statement:

```java
//inserting record
Insert.into(Note.class).columns("title", "text").values("Title", "Text").execute();

//getting all table records
List<Note> notes = Select.from(Note.class).fetch();

//getting a specific record
Note note = Select.from(Note.class).where("id = ?", 1).fetchSingle();

//getting a selection of records
List<Note> notes = Select.from(Note.class).where("title = ?", "title").fetch();

//updating record
Update.table(Note.class).set("title = ?", "New title").where("id = ?", 1).execute();

//deleting record
Delete.from(Note.class).where("id = ?", 1).execute();
```
数据库版本升级
需要增加version
```java
@Database(name = "MyDatabase", version = 2)
public class AppDatabase {

}
```
增加一个is_read字段
```
@Table(name = "Notes", database = AppDatabase.class)
public class Note extends Model {
    ...
    @Column(name = "is_read")
    private boolean isRead;
    ...
}
```
然后在配置中配置addMigrations(MIGRATION_1_2)
```
public class App extends Application {
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            ReActiveLog.d(LogLevel.BASIC, "MIGRATION_1_2");
            database.execSQL("ALTER TABLE Notes ADD COLUMN is_read BOOLEAN DEFAULT 0");
        }
    };

    @Override
        public void onCreate() {
            super.onCreate();

            DatabaseConfig appDatabaseConfig = new DatabaseConfig.Builder(AppDatabase.class)
                    .addModelClasses(Note.class, Folder.class, NoteFolderRelation.class)
                    .disableMigrationsChecking()
                    .addMigrations(MIGRATION_1_2)
                    .build();

            ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                    .addDatabaseConfigs(appDatabaseConfig)
                    .setLog(true)
                    .build());
        }
}
```
数据库版本降级
需要降低version
```java
@Database(name = "MyDatabase", version = 1)
public class AppDatabase {

}
```
删除一个is_read字段
```
@Table(name = "Notes", database = AppDatabase.class)
public class Note extends Model {
    ...
//    @Column(name = "is_read")
    private boolean isRead;
    ...
}
```
然后在配置中配置addMigrations(MIGRATION_2_1)
```
public class App extends Application {
    static final Migration MIGRATION_2_1 = new Migration(2, 1) {
        @Override
        public void migrate(SQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            ReActiveLog.d(LogLevel.BASIC, "MIGRATION_2_1");
            //老的表重命名
            String CREATE_TEMP_NOTES = "alter table Notes rename to temp_Notes";
            //创建新的表，表名跟原来一样，并保留原来的字段和增加新的字段
            String CREATE_NOTES  = "create table Notes(id integer primary key autoincrement,title text,text text, color integer, created_at integer, updated_at integer)";
            //将重命名后的老表中的数据导入新的表中
            String INSERT_DATA = "insert into Notes select id, title, text, color, created_at, updated_at from temp_Notes";
            //删除老表
            String DROP_NOTES = "drop table temp_Notes";
            database.execSQL(CREATE_TEMP_NOTES);
            database.execSQL(CREATE_NOTES);
            database.execSQL(INSERT_DATA);
            database.execSQL(DROP_NOTES);
        }
    };

    @Override
        public void onCreate() {
            super.onCreate();

            DatabaseConfig appDatabaseConfig = new DatabaseConfig.Builder(AppDatabase.class)
                    .addModelClasses(Note.class, Folder.class, NoteFolderRelation.class)
                    .disableMigrationsChecking()
                    .addMigrations(MIGRATION_2_1)
                    .build();

            ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                    .addDatabaseConfigs(appDatabaseConfig)
                    .setLog(true)
                    .build());
        }
}
```
如果你想使用 com.google.code.gson 来解析对象的话，需要使用如下配置才能解析成功
```
    ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {

        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return clazz == Field.class || clazz == Method.class;
        }
    };

    Gson gson = new GsonBuilder()
            .addSerializationExclusionStrategy(exclusionStrategy)
            .addDeserializationExclusionStrategy(exclusionStrategy)
            .create();

    // Student is a simple class extends com.reactiveandroid.Model
    Student student = gson.fromJson(json, Student.class);
    student.save();
```
## License

```
The MIT License

Copyright (c) 2017-2018 Mahach Imangazaliev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

[logo]: https://raw.githubusercontent.com/ImangazalievM/ReActiveAndroid/master/art/logo.png
