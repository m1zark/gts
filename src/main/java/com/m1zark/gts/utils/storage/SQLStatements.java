/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.pixelmonmod.pixelmon.api.pokemon.Pokemon
 */
package com.m1zark.gts.utils.storage;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.m1zark.gts.Logs.Log;
import com.m1zark.gts.utils.Listing;
import com.m1zark.gts.utils.ListingUtils;
import com.m1zark.gts.utils.Pokemon;
import com.m1zark.gts.utils.configuration.ConfigManager;
import com.m1zark.gts.utils.storage.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLStatements {
    private String mainTable;
    private String cacheTable;
    private String logTable;

    public SQLStatements(String mainTable, String cacheTable, String logTable) {
        this.mainTable = mainTable;
        this.cacheTable = cacheTable;
        this.logTable = logTable;
    }

    public void createTables() {
        try(Connection connection = DataSource.getConnection()) {
            if (connection == null || connection.isClosed()) throw new IllegalStateException("GTS DB connection is null");

            try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this.mainTable + "` (ID INTEGER NOT NULL AUTO_INCREMENT, SellerUUID CHAR(36), Pokemon LONGTEXT, Price MEDIUMTEXT, Ends MEDIUMTEXT, PRIMARY KEY(ID));")) {
                statement.executeUpdate();
            }

            try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this.cacheTable + "` (ID INTEGER NOT NULL AUTO_INCREMENT, PlayerUUID CHAR(36), Pokemon LONGTEXT, PRIMARY KEY(ID));")) {
                statement.executeUpdate();
            }

            try(PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this.logTable + "` (ID INTEGER NOT NULL AUTO_INCREMENT, Log LONGTEXT);")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTables() {
        try(Connection connection = DataSource.getConnection()) {
            if (connection == null || connection.isClosed()) throw new IllegalStateException("GTS DB connection is null");

            try(PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `" + this.mainTable + "`")) {
                statement.executeUpdate();
            }
            try(PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `" + this.cacheTable + "`")) {
                statement.executeUpdate();
            }
            try(PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `" + this.logTable + "`")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCacheListing(UUID uuid, Pokemon pkm) {
        Gson gson = new Gson();
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + this.cacheTable + "`(PlayerUUID, Pokemon) VALUES (?, ?)");){
            statement.setString(1, uuid.toString());
            statement.setString(2, gson.toJson((Object)pkm));
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<com.pixelmonmod.pixelmon.api.pokemon.Pokemon> getCacheListing(UUID uuid) {
        Gson gson = new Gson();
        ArrayList<com.pixelmonmod.pixelmon.api.pokemon.Pokemon> listings = new ArrayList<com.pixelmonmod.pixelmon.api.pokemon.Pokemon>();
        try (Connection connection = DataSource.getConnection();){
            try (ResultSet results = connection.prepareStatement("SELECT * FROM `" + this.cacheTable + "` WHERE PlayerUUID='" + uuid + "'").executeQuery();){
                while (results.next()) {
                    Pokemon pkm = (Pokemon)gson.fromJson(results.getString("Pokemon"), Pokemon.class);
                    listings.add(ListingUtils.getPokemon(pkm.getNBT()));
                }
            }
            ArrayList<com.pixelmonmod.pixelmon.api.pokemon.Pokemon> arrayList = listings;
            return arrayList;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    public void removeCacheListings(UUID uuid) {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement query = connection.prepareStatement("DELETE FROM `" + this.cacheTable + "` WHERE PlayerUUID='" + uuid + "'");){
            query.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewLog(Log log) {
        Gson gson = new Gson();
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + this.logTable + "` (Log) VALUES (?)");){
            statement.setString(1, gson.toJson((Object)log));
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLog(int id) {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement query = connection.prepareStatement("DELETE FROM `" + this.logTable + "` WHERE ID='" + id + "'");){
            query.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAllLogs() {
        try (Connection connection = DataSource.getConnection();){
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("GTS DB connection is null");
            }
            try (PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `" + this.logTable + "`");){
                statement.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Log> getAllLogs() {
        Gson gson = new Gson();
        ArrayList<Log> logs = new ArrayList<Log>();
        try (Connection connection = DataSource.getConnection();){
            try (ResultSet results = connection.prepareStatement("SELECT * FROM `" + this.logTable + "`").executeQuery();){
                while (results.next()) {
                    Log log = (Log)gson.fromJson(results.getString("Log"), Log.class);
                    log.setId(results.getInt("ID"));
                    logs.add(log);
                }
            }
            ArrayList<Log> arrayList = logs;
            return arrayList;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    public void updateListing(int id, boolean admin) {
        try (Connection connection = DataSource.getConnection();){
            long expires = ConfigManager.listingsTime;
            Instant date = admin ? Instant.now().minusSeconds(60L) : Instant.now().plusSeconds(expires);
            try (PreparedStatement query = connection.prepareStatement("UPDATE `" + this.mainTable + "` SET Ends='" + date.toString() + "' WHERE ID='" + id + "'");){
                query.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addListing(UUID uuid, Pokemon pkm, String price, long expires) {
        Gson gson = new Gson();
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + this.mainTable + "`(SellerUUID, Pokemon, Price, Ends) VALUES (?, ?, ?, ?)");){
            statement.setString(1, uuid.toString());
            statement.setString(2, gson.toJson((Object)pkm));
            statement.setString(3, price);
            statement.setString(4, Instant.now().plusSeconds(expires).toString());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<Listing> getAllListings() {
        Gson gson = new Gson();
        ArrayList<Listing> listings = new ArrayList<Listing>();
        try (Connection connection = DataSource.getConnection();){
            try (ResultSet results = connection.prepareStatement("SELECT * FROM `" + this.mainTable + "` ORDER BY ENDS ASC").executeQuery();){
                while (results.next()) {
                    listings.add(new Listing(results.getInt("ID"), UUID.fromString(results.getString("SellerUUID")), (Pokemon)gson.fromJson(results.getString("Pokemon"), Pokemon.class), results.getString("Price"), results.getString("Ends")));
                }
            }
            ArrayList<Listing> arrayList = listings;
            return arrayList;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    public void removeListing(int id) {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement query = connection.prepareStatement("DELETE FROM `" + this.mainTable + "` WHERE ID='" + id + "'");){
            query.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

