package com.mpstmeacm;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

import java.util.Objects;

public class Commands extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ");
        if (command.length == 0) return;  // Guard against empty commands

        if (command[0].equalsIgnoreCase("!register")) {
            if (!event.getChannel().getId().equals("1270107518626168862")) {
                event.getChannel().sendMessage("Invalid channel!").queue();
                return;
            }
            if (command.length != 4) {
                event.getChannel().sendMessage("Please provide First Name, email, and DepartmentNo. \n Example usage: `!register Kartik kartik@acmmpstme.com 2`").queue();
                return;
            }

            String fName = command[1];
            String email = command[2];
            String departmentNo = command[3];

            if (!isValidEmail(email)) {
                event.getChannel().sendMessage("Email ID is invalid.").queue();
                return;
            }

            if (!isValidDepartmentNo(departmentNo)) {
                event.getChannel().sendMessage("Department number must be between 1 and 8.").queue();
                return;
            }

            try {
                String inviteCode = generateInviteAndStoreCode(Objects.requireNonNull(event.getGuild().getTextChannelById(Config.getChannelId())), email, fName, departmentNo);
                event.getChannel().sendMessage("Details received and stored with invite code: " + inviteCode).queue();
            } catch (Exception e) {
                event.getChannel().sendMessage("An error occurred while processing your request.").queue();
                e.printStackTrace();
            }
        }
    }

    private String generateInviteAndStoreCode(TextChannel channel, String email, String fName, String departmentNo) {
        InviteAction inviteAction = channel.createInvite().setMaxAge(7 * 24 * 60 * 60).setMaxUses(2).setTemporary(false).setUnique(true);
        Invite invite = inviteAction.complete();
        String inviteCode = invite.getCode();
        String inviteLink = invite.getUrl();

        MongoDBUtils.storeUserDetails(fName, email, departmentNo, inviteCode);
        EmailUtils.sendEmail(email, fName, inviteLink);
        return inviteCode;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidDepartmentNo(String departmentNo) {
        try {
            int deptNo = Integer.parseInt(departmentNo);
            return deptNo >= 1 && deptNo <= 8;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
