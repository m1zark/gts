/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.service.sql.SqlService
 */
package com.m1zark.gts.utils.storage;

import com.m1zark.gts.GTS;
import com.m1zark.gts.utils.configuration.ConfigManager;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

public class Migrate {
    private static Connection h2Connection = null;
    private static DataSource h2Source = null;
    private static Connection mysqlConnection = null;
    private static DataSource mysqlSource = null;
    private static String MAIN_TABLE = "GTS_Listings";
    private static String CACHE_TABLE = "GTS_Cache";

    private static void connectH2() {
        try {
            if (h2Source == null) {
                h2Source = ((SqlService)Sponge.getServiceManager().provide(SqlService.class).get()).getDataSource("jdbc:h2:" + GTS.getInstance().getConfigDir().toString() + File.separator + "/storage/data");
            }
            h2Connection = h2Source.getConnection();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connectMYSQL() {
        try {
            if (mysqlSource == null) {
                mysqlSource = ((SqlService)Sponge.getServiceManager().provide(SqlService.class).get()).getDataSource("jdbc:mysql://" + ConfigManager.mysqlUsername + ":" + ConfigManager.mysqlPassword + "@" + ConfigManager.mysqlURL);
            }
            mysqlConnection = mysqlSource.getConnection();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void migrateMain() {
        Migrate.connectH2();
        Migrate.connectMYSQL();
        try {
            PreparedStatement insertStatement = h2Connection.prepareStatement("INSERT INTO `" + MAIN_TABLE + "` (SellerUUID, Pokemon, Price, Ends) VALUES (?, ?, ?, ?)");
            Statement statement1 = mysqlConnection.createStatement();
            ResultSet resultSet = statement1.executeQuery("SELECT SellerUUID, Pokemon, Price, Ends FROM `" + MAIN_TABLE + "`");
            while (resultSet.next()) {
                insertStatement.clearParameters();
                insertStatement.setString(1, resultSet.getString("SellerUUID"));
                insertStatement.setString(2, resultSet.getString("Pokemon"));
                insertStatement.setString(3, resultSet.getString("Price"));
                insertStatement.setString(4, resultSet.getString("Ends"));
                insertStatement.executeUpdate();
            }
            mysqlConnection.close();
            h2Connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void migrateCache() {
        Migrate.connectH2();
        Migrate.connectMYSQL();
        try {
            PreparedStatement insertStatement = h2Connection.prepareStatement("INSERT INTO `" + CACHE_TABLE + "`(PlayerUUID, Pokemon) VALUES (?, ?)");
            Statement statement1 = mysqlConnection.createStatement();
            ResultSet resultSet = statement1.executeQuery("SELECT PlayerUUID, Pokemon FROM `" + CACHE_TABLE + "`");
            while (resultSet.next()) {
                insertStatement.clearParameters();
                insertStatement.setString(1, resultSet.getString("PlayerUUID"));
                insertStatement.setString(2, resultSet.getString("Pokemon"));
                insertStatement.executeUpdate();
            }
            mysqlConnection.close();
            h2Connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

