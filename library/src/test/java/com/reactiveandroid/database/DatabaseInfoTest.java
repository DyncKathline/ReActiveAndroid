package com.reactiveandroid.database;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestUtils;
import com.reactiveandroid.test.databases.TestDatabase;
import com.reactiveandroid.test.models.AbstractTestModel;
import com.reactiveandroid.test.models.JoinModel;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;

public class DatabaseInfoTest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testScanningModels() {
        DatabaseConfig config = new DatabaseConfig.Builder(TestDatabase.class).build();
        DatabaseInfo databaseInfo = new DatabaseInfo(TestUtils.getApplication(), config);

        assertEquals(9, databaseInfo.getTableInfos().size());
    }

    @Test
    public void testTablesFromConfig() {
        DatabaseConfig config = new DatabaseConfig.Builder(TestDatabase.class)
                .addModelClasses(TestModel.class, JoinModel.class)
                .build();
        DatabaseInfo databaseInfo = new DatabaseInfo(TestUtils.getApplication(), config);

        assertEquals(2, databaseInfo.getTableInfos().size());
    }

    @Test
    public void testNonExistingTableInfo() {
        DatabaseConfig config = new DatabaseConfig.Builder(TestDatabase.class)
                .addModelClasses(TestModel.class)
                .build();
        DatabaseInfo databaseInfo = new DatabaseInfo(TestUtils.getApplication(), config);

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Table info for class JoinModel not found");
        databaseInfo.getTableInfo(JoinModel.class);
    }

}