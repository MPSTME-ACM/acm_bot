package com.mpstmeacm;

import com.google.cloud.firestore.DocumentSnapshot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventHandlers extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        List<Invite> invites = event.getGuild().retrieveInvites().complete();
        String inviteCodeUsed = null;

        for (Invite invite : invites) {
            if (invite.getUses() > 0) {
                inviteCodeUsed = invite.getCode();
                break;
            }
        }

        if (inviteCodeUsed != null) {
            try {
                DocumentSnapshot document = FirebaseUtils.getUserDetailsByInviteCode(inviteCodeUsed);
                if (document.exists()) {
                    String departmentNo = document.getString("departmentNo");
                    String fName = document.getString("fName");
                    assignRolesAndNickname(event.getMember(), getRoleUsingCode(departmentNo), fName);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void assignRolesAndNickname(Member member, String departmentRoleId, String fName) {
        Guild guild = member.getGuild();
        Role executiveRole = guild.getRoleById(Config.getExecutiveRoleId());
        Role departmentRole = guild.getRoleById(departmentRoleId);

        member.getRoles().add(executiveRole);
        member.getRoles().add(departmentRole);

        String newNickname = "["+getDeptPrefix(departmentRoleId)+"]" + " " + fName;
        member.modifyNickname(newNickname).queue();
    }


    /***
     * List for reference for index to id
     * 1 - Dev - 1265624984626401351
     * 2 - CP -  1265625133578588160
     * 3 - RnD - 1265625215472500747
     * 4 - DC - 1265625264386474016
     * 5 - LnA - 1265625304894799902
     * 6 - SME - 1265625427096113235
     * 7 - Mkt - 1265625512248737825
     * 8 - PR - 1265625559686189117
     * ***/

    private String getRoleUsingCode(String departmentNo){
        switch (Integer.parseInt(departmentNo)){
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

    private String getDeptPrefix(String departmentId){
        switch (departmentId){
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
