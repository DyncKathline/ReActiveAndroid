package com.reactiveandroid.sample;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.internal.database.migration.Migration;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.sample.mvp.models.Folder;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.models.NoteFolderRelation;

public class App extends Application {
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            ReActiveLog.d(LogLevel.BASIC, "MIGRATION_1_2");
            database.execSQL("ALTER TABLE Notes ADD COLUMN is_read BOOLEAN DEFAULT 0");
        }
    };
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_1)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabaseConfig)
                .setLog(true)
                .build());
    }

}
