package com.mpstmeacm;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class EventHandlers extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        List<Invite> invites = event.getGuild().retrieveInvites().complete();
        System.out.println("Retrieved invites:");
        String inviteCodeUsed = null;
        System.out.println("My id: " + event.getJDA().getSelfUser().getIdLong());
        for (Invite invite : invites) {
            System.out.println("Invite code: " + invite.getCode() + ", Uses: " + invite.getUses() + ", Inviter ID: " + Objects.requireNonNull(invite.getInviter()).getIdLong());
            if (invite.getUses() > 0 && invite.getInviter().getIdLong()==event.getJDA().getSelfUser().getIdLong()) {
                inviteCodeUsed = invite.getCode();
                System.out.println("Invite code used: " + inviteCodeUsed);
                invite.delete().queue(
                        success -> System.out.println("Invite deleted successfully"),
                        error -> System.err.println("Failed to delete invite: " + error.getMessage())
                );
                break;
            }
        }

        if (inviteCodeUsed != null) {
            try {
                Document document = MongoDBUtils.getUserDetailsByInviteCode(inviteCodeUsed);
                if (document != null && !document.isEmpty()) {
                    System.out.println("Document found: " + document.toJson());
                    String departmentNo = document.getString("departmentNo");
                    String fName = document.getString("fName");
                    assignRolesAndNickname(event.getMember(), getRoleUsingCode(departmentNo), fName);
                } else {
                    System.out.println("No document found for invite code: " + inviteCodeUsed);
                }
            } catch (ExecutionException | InterruptedException e) {
                System.err.println("Error retrieving user details");
                e.printStackTrace();
            }
        } else {
            System.out.println("No valid invite code found.");
        }
    }

    private void assignRolesAndNickname(Member member, String departmentRoleId, String fName) {
        Guild guild = member.getGuild();
        Role executiveRole = guild.getRoleById(Config.getExecutiveRoleId());
        Role departmentRole = guild.getRoleById(departmentRoleId);

        if (executiveRole != null) {
            guild.addRoleToMember(UserSnowflake.fromId(member.getIdLong()), executiveRole).queue(
                    success -> System.out.println("Added executive role to member"),
                    error -> System.err.println("Failed to add executive role: " + error.getMessage())
            );
        } else {
            System.out.println("Executive role not found.");
        }

        if (departmentRole != null) {
            //event.getGuild().addRoleToMember(memberId, jda.getRoleById(yourRole));
            guild.addRoleToMember(UserSnowflake.fromId(member.getIdLong()), departmentRole).queue(
                    success -> System.out.println("Added department role to member"),
                    error -> System.err.println("Failed to add department role: " + error.getMessage())
            );
        } else {
            System.out.println("Department role not found: " + departmentRoleId);
        }

        String newNickname = "[" + getDeptPrefix(departmentRoleId) + "] " + fName;
        member.modifyNickname(newNickname).queue(
                success -> System.out.println("Nickname updated to: " + newNickname),
                error -> System.err.println("Error updating nickname: " + error.getMessage())
        );
    }

    private String getRoleUsingCode(String departmentNo) {
        switch (Integer.parseInt(departmentNo)) {
            case 1:
                return "1265624984626401351";
            case 2:
                return "1265625133578588160";
            case 3:
                return "1265625215472500747";
            case 4:
                return "1265625264386474016";
            case 5:
                return "1265625304894799902";
            case 6:
                return "1265625427096113235";
            case 7:
                return "1265625512248737825";
            case 8:
                return "1265625559686189117";
            default:
                return "";
        }
    }

    private String getDeptPrefix(String departmentId) {
        switch (departmentId) {
            case "1265624984626401351":
            case "1265625133578588160":
                return "Tech";
            case "1265625215472500747":
                return "RnD";
            case "1265625264386474016":
                return "DC";
            case "1265625304894799902":
                return "LnA";
            case "1265625427096113235":
                return "SME";
            case "1265625512248737825":
                return "Mkt";
            case "1265625559686189117":
                return "PR";
            default:
                return "Crew";
        }
    }
}
