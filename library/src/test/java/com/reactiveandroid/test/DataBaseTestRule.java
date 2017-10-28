package com.reactiveandroid.test;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.database.DatabaseConfig;
import com.reactiveandroid.test.databases.TestDatabase;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class DataBaseTestRule implements TestRule {

    public static DataBaseTestRule create() {
        return new DataBaseTestRule();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                DatabaseConfig testDatabaseConfig = new DatabaseConfig.Builder(TestDatabase.class)
                        .setDatabaseName("TestDatabase.db")
                        .setDatabaseVersion(1)
                        .build();

                ReActiveAndroid.init(new ReActiveConfig.Builder(TestUtils.getApplication())
                        .addDatabaseConfig(testDatabaseConfig)
                        .build());
                try {
                    base.evaluate();
                } finally {
                    ReActiveAndroid.destroy();
                }
            }
        };
    }

}