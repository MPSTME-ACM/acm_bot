package com.mpstmeacm;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot {
    public static void main(String[] args) throws Exception {
        MongoDBUtils.initialize();

        JDABuilder builder = JDABuilder.createDefault(Config.getDiscordToken(), GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES);
        builder.addEventListeners(new Commands(), new EventHandlers());
        builder.build();
    }
}
