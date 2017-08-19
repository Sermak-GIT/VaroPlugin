package com.sermak.plugin;

import com.sermak.plugin.db.Data;
import me.confuser.barapi.BarAPI;
import org.apache.logging.log4j.core.net.Priority;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.Objects;

import static com.sermak.plugin.TeamManager.getTeam;
import static com.sermak.plugin.db.Data.*;
import static java.lang.System.out;

class EventHandler implements Listener {

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerJoinEvent event) {
        Data.save();
        event.getPlayer().sendMessage("Hallo " + event.getPlayer().getName());
        if (varoStarted) {
            if (Objects.equals(varoWon, "")) {
                if (deaths.contains(event.getPlayer().getName())) {
                    event.getPlayer().sendMessage("§4Du bist tot");
                } else if (!online.containsKey(event.getPlayer().getName())) {
                    Data.OnlineData d = new Data.OnlineData();
                    d.onlineState = Data.OnlineStates.IDLE;
                    online.put(event.getPlayer().getName(), d);
                    tm.freeze(event.getPlayer());
                    if (playSessions.containsKey(event.getPlayer().getName()) && playSessions.get(event.getPlayer().getName()).replace("|", "").length() > daysOfVaro) {
                        event.getPlayer().sendMessage("§4Du hast heute schon gespielt");
                    } else {
                        teamMessage(event);
                    }
                } else if (online.get(event.getPlayer().getName()).onlineState == Data.OnlineStates.IDLE) {
                    tm.freeze(event.getPlayer());
                    if (playSessions.containsKey(event.getPlayer().getName()) && playSessions.get(event.getPlayer().getName()).replace("|", "").length() > daysOfVaro) {
                        event.getPlayer().sendMessage("§4Du hast heute schon gespielt");
                    } else {
                        teamMessage(event);
                    }
                } else {
                    event.getPlayer().sendMessage("§6Willkommen zurück. Du hast noch:");
                    tm.manage(event.getPlayer().getName());
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> tm.checkEnd(event.getPlayer().getName()), 200);
                    if (overtime.containsKey(event.getPlayer().getName())) {
                        overtime.remove(event.getPlayer().getName());
                    }
                }
            } else {
                event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Team " + varoWon + " hat Varo gewonnen!");
            }
        } else {
            tm.freeze(event.getPlayer());
            Data.OnlineData d = new Data.OnlineData();
            d.onlineState = Data.OnlineStates.IDLE;
            online.put(event.getPlayer().getName(), d);
            event.getPlayer().sendMessage("Varo hat noch nicht angefangen. Lese die Plugindokumentation und frage deinen Admin oder Bossmoderator");
        }
    }

    private void teamMessage(PlayerJoinEvent event) {
        int id = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> teams.stream().filter(t -> Objects.equals(t.p1, event.getPlayer().getDisplayName()) || Objects.equals(t.p2, event.getPlayer().getDisplayName())).forEach(t -> {
            event.getPlayer().sendMessage("§5Willkommen Team " + t.name);
            Player p = Bukkit.getServer().getPlayer(t.p1);
            Player q = Bukkit.getServer().getPlayer(t.p2);
            if (p != null && q != null && p.isOnline() && q.isOnline()) {
                event.getPlayer().sendMessage("§6Tippt \"/s team\" ein, um euer Spiel zu starten");
            } else {
                event.getPlayer().sendMessage("§6Tippe \"/s alone\" ein, um dein Spiel zu starten");
            }
        }), 5 * 20);
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (online.containsKey(event.getPlayer().getName()) && online.get(event.getPlayer().getName()).onlineState != Data.OnlineStates.IDLE) {
            out.println(event.getPlayer().getName() + " quited during session");
        }
        if (coords.containsKey(event.getPlayer().getName()) && coords.get(event.getPlayer().getName()) != null) {
            ArrayList<Location> l = coords.get(event.getPlayer().getName());
            while (l.size() < daysOfVaro) { l.add(null); }
            l.add(daysOfVaro, event.getPlayer().getLocation());
            coords.replace(event.getPlayer().getName(), l);
        } else if (coords.containsKey(event.getPlayer().getName())){
            ArrayList<Location> l = new ArrayList<>();
            while (l.size() < daysOfVaro) { l.add(null); }
            l.add(daysOfVaro, event.getPlayer().getLocation());
            coords.replace(event.getPlayer().getName(), l);
        } else {
            ArrayList<Location> l = new ArrayList<>();
            while (l.size() < daysOfVaro) { l.add(null); }
            l.add(daysOfVaro, event.getPlayer().getLocation());
            coords.put(event.getPlayer().getName(), l);
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        banned.stream().filter(i -> i.isSimilar(e.getItem())).forEach(i -> e.setCancelled(true));
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        deaths.add(e.getEntity().getName());
        OnlineData d = new OnlineData();
        d.onlineState = OnlineStates.IDLE;
        online.put(e.getEntity().getName(), d);
        tm.freeze(e.getEntity());
        Bukkit.getServer().getScheduler().cancelTask(hashMapManager.get(e.getEntity().getName()));
        playSessions.replace(e.getEntity().getName(), playSessions.get(e.getEntity().getName()) + "RIP");
        tm.isVaroWon();
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionSplash(PotionSplashEvent e) {
        if (bannedPotions.contains(e.getEntity().getItem() + "" + e.getEntity().getItem())) {
            out.println("Illegal potion thrown: " + e.getEntity().getItem());
            e.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onBrew(BrewEvent e) {

    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent e) {
        if (bannedPotions.contains(e.getEntity().getItem() + "")) {
            out.println("Illegal potion thrown: " + e.getEntity().getItem());
            e.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (lootChests.contains(event.getClickedBlock().getLocation())) {
                if(event.getClickedBlock().getType() != Material.CHEST) lootChests.remove(event.getClickedBlock().getLocation());
            } else {
                if(event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                    if (lockedChests.containsFirst(event.getClickedBlock().getLocation())) {
                        String team = lockedChests.getSecond(event.getClickedBlock().getLocation());
                        if (!Objects.equals(getTeam(event.getPlayer().getName()), team)) {
                            event.getPlayer().sendMessage("Diese Kiste gehört Team " + team);
                            event.setCancelled(true);
                        }
                    } else if (event.getPlayer().isSneaking()) {
                        if (lockedChests.containsSecond(getTeam(event.getPlayer().getName()))) {
                            event.getPlayer().sendMessage("Kiste wurde aktualisiert");
                            lockedChests.put(event.getClickedBlock().getLocation(), getTeam(event.getPlayer().getName()));
                        } else {
                            event.getPlayer().sendMessage("Kiste wurde gesetzt");
                            lockedChests.put(event.getClickedBlock().getLocation(), getTeam(event.getPlayer().getName()));
                        }
                    }
                }
            }
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.TRAPPED_CHEST) {
            if (lockedChests.containsFirst(event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosion(EntityExplodeEvent event) {
        event.blockList().stream().filter(b -> lockedChests.containsFirst(b.getLocation()) && (b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST)).forEach(b -> {
            try { event.blockList().remove(b); } catch (Exception ignored) {}
        });
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
        event.setSaveChunk(true);
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onEbyEDamage(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player) && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player defender = (Player) event.getEntity();
            if (Objects.equals(getTeam(attacker.getName()), getTeam(defender.getName()))) {
                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.HIGHEST)
    public void onPaae(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}
