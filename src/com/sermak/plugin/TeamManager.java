package com.sermak.plugin;

import com.sermak.plugin.db.DBC;
import com.sermak.plugin.db.Data;
import me.confuser.barapi.BarAPI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sermak.plugin.db.Data.*;
import static java.lang.System.err;
import static java.lang.System.out;

public class TeamManager {

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    void register(String p1, String p2, String name) {
        teams.add(new Team(p1, p2, name));
        DBC.setTeams(teams);
    }

    void remove(String name) {
        Team team = null;
        for (Team t : Data.teams) {
            if (t.name.equals(name)) {
                team = t;
                break;
            }
        }
        if (team != null) {
            teams.remove(team);
        }
        DBC.setTeams(teams);
    }

    void start(String playerName, boolean team) {
        Player p = Bukkit.getServer().getPlayer(playerName);
        if (team) {
            for (Team t:teams) {
                Player r = Bukkit.getServer().getPlayer(t.p1);
                Player q = Bukkit.getServer().getPlayer(t.p2);
                if (r != null && q != null && r.isOnline() && q.isOnline()) {
                    if (online.get(r.getName()).onlineState == Data.OnlineStates.IDLE && online.get(q.getName()).onlineState == Data.OnlineStates.IDLE) {
                        startHdl(r.getName(), true);
                        startHdl(q.getName(), true);
                    } else {
                        p.sendMessage("§2Einer von euch spielt schon");
                    }
                } else {
                    p.sendMessage("§2Dein Teamkamerade ist nicht online");
                }
            }
        } else {
            if (online.get(playerName).onlineState == Data.OnlineStates.IDLE) {
                startHdl(p.getName(), false);
            } else {
                p.sendMessage("§2Du spielst schon");
            }
        }
    }

    private void startHdl(String playerName, boolean team) {
        Player p = Bukkit.getServer().getPlayer(playerName);
        if (playSessions.containsKey(playerName)) {
            if (playSessions.get(playerName).contains("0")) {
                playSessions.replace(playerName, playSessions.get(playerName).replaceFirst("0", "n"));
                p.sendMessage("Du holst eine Session nach");
            } else {
                playSessions.replace(playerName, playSessions.get(playerName) + "p");
            }
        } else {
            playSessions.put(playerName, "p");
        }
        unfreeze(p);
        if (team) {
            online.get(playerName).onlineState = OnlineStates.TEAM;
        } else {
            online.get(playerName).onlineState = OnlineStates.ALONE;
        }
        calendar.setTime(new Date());
        online.get(playerName).start = calendar.getTime();
        calendar.add(Calendar.MINUTE, Data.sessionTime);
        online.get(playerName).end = calendar.getTime();
        hashMapManager.put(p.getName(), Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Data.varoPlugin, () -> manage(playerName), 0, 60 * 20));
        notifyTime(playerName);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 4, false, false, Color.AQUA));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 4, false, false, Color.AQUA));
        p.sendMessage("§3Session started");
    }

    void manage(String playerName) {
        boolean myproblem = true;
        Player p = Bukkit.getServer().getPlayer(playerName);
        if (p == null) return;
        if (p.getGameMode() != GameMode.SURVIVAL) {
            p.setGameMode(GameMode.SURVIVAL);
        }
        calendar.setTime(new Date());
        err.println(calendar.getTime().getTime() + " " + Data.online.get(p.getName()).end.getTime());
        if (calendar.getTime().getTime() - Data.online.get(p.getName()).end.getTime() > 0) {
            if (playerInRange(playerName)) {
                myproblem = false;
                overtimeMngr(playerName);
            } else {
                timeUp(p);
            }
        } else {
            p.sendMessage("§3" + sdf.format(Data.online.get(p.getName()).end.getTime() - calendar.getTime().getTime()));
            unfreeze(p);
        }
        if (myproblem) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
                calendar.setTime(new Date());
                try {
                    if (calendar.getTime().getTime() - Data.online.get(p.getName()).end.getTime() > 0) {
                        if (playerInRange(playerName)) {
                            overtimeMngr(p.getName());
                        } else {
                            timeUp(p);
                        }
                    }
                } catch (Exception ignored) {}
            }, 20);
        }
    }

    private void overtimeMngr(String pn) {
        try {
            Player p = Bukkit.getServer().getPlayer(pn);
            if (playerInRange(pn)) {overtime.put(p.getName(), overtimeLength * 2.0);}
            if (overtime.get(p.getName()) <= 0) {
                timeUp(p);
            } else {
                float f = (float) (overtime.get(p.getName()) / (overtimeLength * 2.0));
                while (BarAPI.hasBar(p)) {
                    BarAPI.removeBar(p);
                }
                BarAPI.setMessage(p, "OVERTIME", f * 100);
                overtime.replace(p.getName(), overtime.get(p.getName()) - 1f);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> overtimeMngr(pn), 20);
            }
        } catch (Exception ignored) {}
    }

    private boolean playerInRange(String pn) {
        try {
            for (Entity e : Bukkit.getServer().getPlayer(pn).getNearbyEntities(overtimeRadius, overtimeRadius, overtimeRadius)) {
                if (e.getType() == EntityType.PLAYER) {
                    if (Objects.equals(getTeam(e.getName()), getTeam(pn))) return true;
                }
            }
            return false;
        } catch (Exception e) {return false;}
    }

    void timeUp(Player p) {
        freeze(p);
        p.sendMessage("§3Zeit ist um");
        OnlineData d = new OnlineData();
        d.onlineState = OnlineStates.IDLE;
        online.put(p.getName(), d);
        Bukkit.getServer().getScheduler().cancelTask(hashMapManager.get(p.getName()));
        while (BarAPI.hasBar(p)) {
            BarAPI.removeBar(p);
        }
    }

    int getTime(String p) {
        return Integer.parseInt(sdf.format(Data.online.get(p).end.getTime() - calendar.getTime().getTime()).substring(3));
    }

    void checkEnd(String playerName) {
        Player p = Bukkit.getServer().getPlayer(playerName);
        if (p == null) return;
        if (p.getGameMode() != GameMode.SURVIVAL) {
            p.setGameMode(GameMode.SURVIVAL);
        }
        calendar.setTime(new Date());
        try {
            if (calendar.getTime().getTime() - Data.online.get(p.getName()).end.getTime() > 0) {
                OnlineData d = new OnlineData();
                d.onlineState = OnlineStates.IDLE;
                online.put(p.getName(), d);
                BarAPI.removeBar(p);
                p.sendMessage("§3Zeit ist vorbei");
                freeze(p);

                Bukkit.getServer().getScheduler().cancelTask(hashMapManager.get(p.getName()));
            }
        } catch (Exception ignored) {}

    }

    void getTime(Player p) {
        if (Data.online.get(p.getName()).onlineState != Data.OnlineStates.IDLE) {
            calendar.setTime(new Date());
            p.sendMessage("§3" + sdf.format(Data.online.get(p.getName()).end.getTime() - calendar.getTime().getTime()));
        } else {
            p.sendMessage("§4Du hast noch nicht angefangen");
        }
    }

    void freeze(Player p) {
        p.setInvulnerable(true);
        p.setWalkSpeed(0);
        p.setFlySpeed(0);
        p.setGameMode(GameMode.SPECTATOR);
        hashMapFreeze.put(p.getName(), Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Data.varoPlugin, () -> {
                try {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, Color.AQUA));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false, Color.AQUA));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, Color.AQUA));
                } catch (Exception ignored) {}
            }, 0, 60 * 2));
    }

    void unfreeze(Player p) {
        p.setInvulnerable(false);
        p.setWalkSpeed(0.2F);
        p.setFlySpeed(0.1f);
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        if (hashMapFreeze.containsKey(p.getName())) {
            Bukkit.getServer().getScheduler().cancelTask(hashMapFreeze.get(p.getName()));
        }
        p.setGameMode(GameMode.SURVIVAL);
    }

    private void notifyTime(String pName) {
        notify(pName, "§2Noch 5 Minuten", 60*5*20);
        notify(pName, "§2Noch 1 Minute", 60*20);
        notify(pName, "§2Noch 30 Sekunden", 30*20);
        notify(pName, "§2Noch 10 Sekunden", 10*20);
        notify(pName, "§2Noch 5 Sekunden", 5*20);
        notify(pName, "§2Noch 4 Sekunden", 4*20);
        notify(pName, "§2Noch 3 Sekunden", 3*20);
        notify(pName, "§2Noch 2 Sekunden", 2*20);
        notify(pName, "§2Noch 1 Sekunde", 20);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
            out.println("Forced freezed " + pName);
            Player p = Bukkit.getServer().getPlayer(pName);
            tm.freeze(p);
            sendMsg(pName, "§4FORCE FREEZE", (20 * Data.sessionTime * 60));
        }, (20 * Data.sessionTime * 60));
    }

    private void notify(String pName, String msg, int ticks) {
        if (Data.sessionTime >= ticks/(20*60)) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
                try {
                    Player p = Bukkit.getServer().getPlayer(pName);
                    p.sendMessage(msg);
                } catch (Exception e){ }
            }, (20 * Data.sessionTime * 60) - ticks);
        }
    }

    public void sendMsg(String pName, String msg, int ticks) {
        if (Data.sessionTime >= ticks/(20*60)) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
                try {
                    Player p = Bukkit.getServer().getPlayer(pName);
                    p.sendMessage(msg);
                } catch (Exception e){
                }}, ticks);
        }
    }

    void startVaro() {
        for (Player p:Bukkit.getOnlinePlayers()) {
            for (int i = 10; i > 0; i--) {
                sendMsg(p.getName(), ChatColor.AQUA + "" + i, (11 - i)*20);
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
            varoStarted = true;
            daysOfVaro = 0;
            try {
                for (Player p:Bukkit.getServer().getOnlinePlayers()) {
                    start(p.getName(), false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }}, 10*20);
        calendar.setTime(new Date());
        lastDate = calendar.get(Calendar.DAY_OF_WEEK);
        sessionManager.start();
    }

    void isVaroWon() {
        ArrayList<String> s = teams.stream().filter(team -> !deaths.contains(team.p1) || !deaths.contains(team.p2)).map(team -> team.name).collect(Collectors.toCollection(ArrayList::new));
        if(s.size() == 1) {
            varoWon = s.get(0);
            for (Player p:Bukkit.getOnlinePlayers()) {
                p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Team " + varoWon + " hat Varo gewonnen!");
            }
        }
    }

    public static class Team {
        public String p1;
        public String p2;
        public String name;

        public Team(String p1, String p2, String name) {
            this.p1 = p1;
            this.p2 = p2;
            this.name = name;
        }
    }

    static String getTeam(String player) {
        for (Team t : teams) {
            if (Objects.equals(t.p1, player) || Objects.equals(t.p2, player)) {
                return t.name;
            }
        }
        return "";
    }
}
