/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.zaxxer.hikari.HikariConfig
 *  com.zaxxer.hikari.HikariDataSource
 */
package com.m1zark.gts.utils.storage;

import com.m1zark.gts.GTS;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.storage.SQLStatements;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DataSource
extends SQLStatements {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public DataSource(String mainTable, String cacheTable, String logTable) {
        super(mainTable, cacheTable, logTable);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void shutdown() {
        if (ds != null) {
            ds.close();
        }
    }

    static {
        if (ConfigManager.getStorageType().equalsIgnoreCase("h2")) {
            config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
            config.addDataSourceProperty("URL", (Object)("jdbc:h2:" + GTS.getInstance().getConfigDir() + "/storage/data;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MSSQLServer"));
        } else {
            config.setJdbcUrl("jdbc:mysql://" + ConfigManager.mysqlURL);
            config.setUsername(ConfigManager.mysqlUsername);
            config.setPassword(ConfigManager.mysqlPassword);
            config.addDataSourceProperty("alwaysSendSetIsolation", (Object)false);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", (Object)2048);
            config.addDataSourceProperty("prepStmtCacheSize", (Object)250);
            config.addDataSourceProperty("cachePrepStmts", (Object)true);
            config.addDataSourceProperty("useServerPrepStmts", (Object)true);
            config.addDataSourceProperty("cacheCallableStmts", (Object)true);
            config.addDataSourceProperty("cacheServerConfiguration", (Object)true);
            config.addDataSourceProperty("elideSetAutoCommits", (Object)true);
            config.addDataSourceProperty("useLocalSessionState", (Object)true);
        }
        config.setPoolName("GTS");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000L);
        config.setConnectionTimeout(5000L);
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(10L));
        config.setConnectionTestQuery("/* GTS ping */ SELECT 1");
        config.setInitializationFailTimeout(1L);
        ds = new HikariDataSource(config);
    }
}

