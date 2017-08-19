package com.sermak.plugin;

import com.sermak.plugin.db.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.sermak.plugin.db.Data.*;
import static com.sermak.plugin.db.Data.playSessions;


public class SessionManager {

    private Calendar calendar = Calendar.getInstance();
    private int id;

    void start() {
        id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Data.varoPlugin, this::manageDays, 0, 60 * 20);
    }

    void stop() {
        Bukkit.getServer().getScheduler().cancelTask(id);
    }

    private void manageDays() {
        calendar.setTime(new Date());
        if (calendar.get(Calendar.DAY_OF_WEEK) != lastDate) {
            lastDate = calendar.get(Calendar.DAY_OF_WEEK);
            newDayOfVaro();
        }
    }

    void newDayOfVaro() {
        daysOfVaro++;
        ArrayList<String> players = new ArrayList<>();
        for (TeamManager.Team t : teams) {
            players.add(t.p1);
            players.add(t.p2);
        }

        players.stream().filter(player -> !deaths.contains(player)).forEach(player -> {
            if (playSessions.containsKey(player)) {
                while (playSessions.get(player).replace("|", "").length() < daysOfVaro) {
                    playSessions.replace(player, playSessions.get(player) + "0");
                }
                playSessions.replace(player, playSessions.get(player) + "|");
            } else {
                playSessions.put(player, "0|");
            }
            while (StringUtils.countMatches(playSessions.get(player), "0") > postplays) {
                playSessions.replace(player, playSessions.get(player).replaceFirst("0", "x"));
            }

            String p = playSessions.get(player).replace("|", "");
            int j = 7;
            if (p.length() < 7) {
                j = p.length();
            }
            String pp = p.substring(p.length() - j, p.length());
            int i = StringUtils.countMatches(pp, "0") + StringUtils.countMatches(pp, "x");
            if (i > inactivityMax) {
                deaths.add(player);
                playSessions.replace(player, playSessions.get(player) + "RIP. Du warst zu lange weg");
                tm.isVaroWon();
            }
        });
    }
}
