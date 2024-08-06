package com.mpstmeacm;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getDiscordToken() {
        return dotenv.get("DISCORD_TOKEN");
    }

    public static String getFirebaseConfigPath() {
        return dotenv.get("FIREBASE_CONFIG_PATH");
    }

    public static String getChannelId() {
        return dotenv.get("CHANNEL_ID");
    }

    public static String getExecutiveRoleId() {
        return dotenv.get("EXECUTIVE_ROLE_ID");
    }

    public static String getResendToken(){
        return dotenv.get("RESEND_TOKEN");
    }

    public static String getConnectionString() {
        return dotenv.get("CONNECTION_STRING");
    }
}
