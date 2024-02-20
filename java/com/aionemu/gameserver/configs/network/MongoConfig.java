package com.aionemu.gameserver.configs.network;

import com.aionemu.commons.configuration.Property;

public class MongoConfig {

    @Property(key = "mongo.anonymous", defaultValue = "true")
    public static boolean ANONYMOUS;

    @Property(key = "mongo.account", defaultValue = "admin")
    public static String ACCOUNT;

    @Property(key = "mongo.password", defaultValue = "12345")
    public static String PASSWORD;

    @Property(key = "mongo.host", defaultValue = "localhost")
    public static String HOST;

    @Property(key = "mongo.port", defaultValue = "27017")
    public static int PORT;

    @Property(key = "mongo.db.name", defaultValue = "gameserver")
    public static String DATABASE_NAME;

    @Property(key = "mongo.db.auth.name", defaultValue = "admin")
    public static String DATABASE_AUTH_NAME;

    @Property(key = "mongo.db.logging", defaultValue = "true")
    public static boolean ALLOW_DATABASE_LOGGING;

    @Property(key = "mongo.connection.per.host", defaultValue = "10")
    public static int CONNECTIONS_PER_HOST;

    @Property(key = "mongo.thread.count", defaultValue = "5")
    public static int THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER;

    @Property(key = "mongo.max.wait.time", defaultValue = "120000")
    public static int MAX_WAIT_TIME;

    @Property(key = "mongo.connect.time.out", defaultValue = "10000")
    public static int CONNECT_TIME_OUT;
}
