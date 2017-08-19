package com.sermak.plugin;

import com.sermak.plugin.db.Data;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static com.sermak.plugin.db.Data.*;
import static java.lang.System.err;
import static java.lang.System.out;

class VaroCE implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            switch (command.getName()) {
                case "r": {
                    if (args.length < 3) {
                        commandSender.sendMessage("§4/r [player1] [player2] [teamname]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            String team = args[2];
                            for (int i = 3; i < args.length; i++) {
                                team += " " + args[i];
                            }
                            tm.register(args[0], args[1], team);
                            commandSender.sendMessage("§2Team " + team + " registriert");
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "rg": {
                    if (args.length != 0) {
                        commandSender.sendMessage("§4/rg");
                        return false;
                    } else {
                        for (TeamManager.Team t : Data.teams) {
                            commandSender.sendMessage(t.name + ": " + t.p1 + ", " + t.p2);
                        }
                    }
                    break;
                }
                case "rr": {
                    if (args.length < 1) {
                        commandSender.sendMessage("§4/rr [teamname]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            String team = args[0];
                            for (int i = 1; i < args.length; i++) {
                                team += " " + args[i];
                            }
                            tm.remove(team);
                            commandSender.sendMessage("§2Team " + team + " gelöscht");
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "s": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/s <alone/team>");
                        return false;
                    } else {
                        if (varoStarted) {
                            if (deaths.contains(commandSender.getName())) {
                                commandSender.sendMessage("§4Du bist tot");
                            } else if (!Objects.equals(varoWon, "")) {
                                commandSender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Team " + varoWon + " hat Varo gewonnen!");
                            } else if (playSessions.containsKey(commandSender.getName()) && playSessions.get(commandSender.getName()).replace("|", "").length() > daysOfVaro) {
                                commandSender.sendMessage("§4Du hast heute schon gespielt");
                            } else {
                                switch (args[0]) {
                                    case "alone": {
                                        tm.start(commandSender.getName(), false);
//                                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
//                                            Player p = Bukkit.getServer().getPlayer(commandSender.getName());
//                                            tm.freeze(p);
//                                            tm.sendMsg(commandSender.getName(), "§4FORCE FREEZE", (20 * Data.sessionTime * 60));
//                                        }, (20 * Data.sessionTime * 60));
                                        break;
                                    }
                                    case "team": {
                                        tm.start(commandSender.getName(), true);
//                                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
//                                            Player p = Bukkit.getServer().getPlayer(commandSender.getName());
//                                            tm.freeze(p);
//                                            tm.sendMsg(commandSender.getName(), "§4FORCE FREEZE", (20 * Data.sessionTime * 60));
//                                        }, (20 * Data.sessionTime * 60));
                                    }
                                    default:
                                        commandSender.sendMessage("§4/s <alone/team>");
                                }
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat noch nicht begonnen");
                        }
                    }
                    break;
                }
                case "t": {
                    if (args.length != 0) {
                        commandSender.sendMessage("§4/t");
                        return false;
                    } else {
                        if (varoStarted) {
                            if (deaths.contains(commandSender.getName())) {
                                commandSender.sendMessage("§4Du bist tot");
                            } else if (!Objects.equals(varoWon, "")) {
                                commandSender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Team " + varoWon + " hat Varo gewonnen!");
                            } else {
                                tm.getTime((Player) commandSender);
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat noch nicht begonnen");
                        }
                    }
                    break;
                }
                case "setSessionTime": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/setSessionTime [time in minutes]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            try {
                                Data.sessionTime = Integer.parseInt(args[0]);
                            } catch (Exception e) {commandSender.sendMessage("§4/setSessionTime [time in minutes]");}
                            for (Player p:Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.YELLOW + "Sessionszeit wurde auf " + args[0] + " geändert");
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "setPostPlays": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/setPostPlays [amount]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            try {
                                Data.postplays = Integer.parseInt(args[0]);
                            } catch (Exception e) {commandSender.sendMessage("§4/setPostPlays [amount]");}
                            for (Player p:Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.YELLOW + "Nachspiele wurde auf " + args[0] + " geändert");
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "varostart": {
                    if (args.length != 0) {
                        commandSender.sendMessage("§4/varostart");
                        return false;
                    } else {
                        if (!varoStarted) {
                            if (varoSubmits[0] == null && varoSubmits[1] == null && varoSubmits[2] == null) {
                                for (Player p:Bukkit.getOnlinePlayers()) {
                                    p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Jemand will Varo starten. Kontrolliere die Teams mit /rg und überprüfe die Config mit /getconfig. Wenn alles stimmt, tippe ebenfalls /varostart. Zum Start werden 3 Aufrufe von verschiedenen Spielern innerhalb von 30s benötigt");
                                }
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Data.varoPlugin, () -> {
                                    varoSubmits[0] = null;
                                    varoSubmits[1] = null;
                                    varoSubmits[2] = null;
                                    Bukkit.getOnlinePlayers().stream().filter(p -> !varoStarted).forEach(p -> p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Varostart abgebrochen"));
                                }, 30*20);
                            }
                            if (varoSubmits[0] == null) {
                                varoSubmits[0] = commandSender.getName();
                            } else if (varoSubmits[1] == null && !Objects.equals(varoSubmits[0], commandSender.getName())) {
                                for (Player p:Bukkit.getOnlinePlayers()) {
                                    p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + commandSender.getName() + " bestätigte");
                                }
                                varoSubmits[1] = commandSender.getName();
                            } else if (varoSubmits[2] == null && !Objects.equals(varoSubmits[0], commandSender.getName()) && !Objects.equals(varoSubmits[1], commandSender.getName())) {
                                varoSubmits[2] = commandSender.getName();
                                Player pp = null;
                                for (Player p:Bukkit.getOnlinePlayers()) {
                                    p.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + commandSender.getName() + " bestätigte. Das Projekt kann starten");
                                    if (p.isOp()) {pp = p;}
                                }
                                tm.startVaro();
                                if (pp != null) {
                                    pp.performCommand("time set day");
                                }
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "varotoggle": {
                    if (commandSender.isOp()) {
                        if (!varoStarted) {
                            tm.startVaro();
                        } else {/*
                            Data.daysOfVaro = 0;
                            Data.lastDate = -1;
                            Data.playSessions.clear();
                            Data.online.clear();*/
                            varoStarted = false;
                            Bukkit.getOnlinePlayers().forEach(tm::freeze);
                            commandSender.sendMessage(ChatColor.DARK_PURPLE + "Varo stopped");
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "You don't have permission");
                    }
                    return false;
                }
                case "getVaroDay": {
                    if (!varoStarted) {
                        commandSender.sendMessage("§4Varo hat noch nicht begonnen");
                    } else {
                        commandSender.sendMessage(Data.daysOfVaro + "");
                    }
                    return false;
                }
                case "getss": {
                    if (!varoStarted) {
                        commandSender.sendMessage("§4Varo hat noch nicht begonnen");
                    } else {
                        if (playSessions.containsKey(commandSender.getName())) {
                            commandSender.sendMessage(Data.playSessions.get(commandSender.getName()));
                        } else {
                            commandSender.sendMessage("Du hast noch nicht gespielt");
                        }
                    }
                    return false;
                }
                case "getpp": {
                    if (!varoStarted) {
                        commandSender.sendMessage("§4Varo hat noch nicht begonnen");
                    } else {
                        if (playSessions.containsKey(commandSender.getName())) {
                            commandSender.sendMessage(StringUtils.countMatches(Data.playSessions.get(commandSender.getName()), "0") + "");
                        } else {
                            commandSender.sendMessage("Du hast noch nicht gespielt");
                        }
                    }
                    return false;
                }
                case "getDeadPlayers": {
                    if (!varoStarted) {
                        commandSender.sendMessage("§4Varo hat noch nicht begonnen");
                    } else {
                        for (TeamManager.Team t : teams) {
                            if (deaths.contains(t.p1) || deaths.contains(t.p2)) {
                                String s = t.name + ": ";
                                if (deaths.contains(t.p1)) {
                                    s += t.p1 + " ";
                                }
                                if (deaths.contains(t.p2)) {
                                    s += t.p2 + " ";
                                }
                                commandSender.sendMessage(s);
                            }
                        }
                    }
                    return false;
                }
                case "banana": {
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage("§4Nur für Chef");
                    } else {
                        if (args.length != 0) {
                            commandSender.sendMessage("§4/banana (Mit Item in der Hand)");
                            return false;
                        } else {
                            if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.SPLASH_POTION || ((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.LINGERING_POTION) {
                                if (bannedPotions.contains(((Player) commandSender).getInventory().getItemInMainHand() + "")) {
                                    commandSender.sendMessage("Ist bereits gebannt");
                                    return false;
                                } else {
                                    bannedPotions.add(((Player) commandSender).getInventory().getItemInMainHand() + "");
                                    commandSender.sendMessage(((Player) commandSender).getInventory().getItemInMainHand() + " wurde gebannt");
                                }
                            } else {
                                for (ItemStack i : banned) {
                                    if (i.isSimilar(((Player) commandSender).getInventory().getItemInMainHand())) {
                                        commandSender.sendMessage("Ist bereits gebannt");
                                        return false;
                                    }
                                }
                                banned.add(((Player) commandSender).getInventory().getItemInMainHand());
                                commandSender.sendMessage(((Player) commandSender).getInventory().getItemInMainHand() + " wurde gebannt");
                            }
                        }
                    }
                    return false;
                }
                case "rezzz": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/rezzz [Spielername]");
                        return false;
                    }
                    deaths.remove(args[0]);
                    OnlineData d = new OnlineData();
                    d.onlineState = OnlineStates.IDLE;
                    online.put(args[0], d);
                    tm.start(commandSender.getName(), false);
                    String s = playSessions.get(args[0]);
                    playSessions.replace(args[0], s.replace("RIP", ""));
                    return false;
                }
                case "kill": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/kill [Spielername]");
                        return false;
                    }
                    deaths.add(args[0]);
                    OnlineData d = new OnlineData();
                    d.onlineState = OnlineStates.IDLE;
                    online.put(args[0], d);
                    tm.freeze(Bukkit.getServer().getPlayer(args[0]));
                    Bukkit.getServer().getScheduler().cancelTask(hashMapManager.get(args[0]));
                    playSessions.replace(args[0], playSessions.get(args[0]) + "RIP");
                    tm.isVaroWon();
                    return false;
                }
                case "newDay": {
                    if (commandSender.isOp()) {
                        commandSender.sendMessage("§4Nur für Sermak");
                    } else {
                        sessionManager.newDayOfVaro();
                    }
                    return false;
                }
                case "getbanana": {
                    for (ItemStack i : banned) {
                        commandSender.sendMessage(i + "");
                    }
                    bannedPotions.forEach(commandSender::sendMessage);
                    return false;
                }
                case "nobanana": {
                    if (!commandSender.isOp()) {
                        commandSender.sendMessage("§4Nur für Chef");
                    } else {
                        if (args.length != 0) {
                            commandSender.sendMessage("§4/nobanana (Mit Item in der Hand)");
                            return false;
                        } else {
                            if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.SPLASH_POTION || ((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.LINGERING_POTION) {
                                if (bannedPotions.contains(((Player) commandSender).getInventory().getItemInMainHand() + "")) {
                                    bannedPotions.remove(((Player) commandSender).getInventory().getItemInMainHand() + "");
                                    commandSender.sendMessage(((Player) commandSender).getInventory().getItemInMainHand() + " wurde entfernt");
                                    return false;
                                } else {
                                    commandSender.sendMessage("Item ist noch nicht gebannt");
                                }
                            } else {
                                for (ItemStack i : banned) {
                                    if (i.isSimilar(((Player) commandSender).getInventory().getItemInMainHand())) {
                                        banned.remove(((Player) commandSender).getInventory().getItemInMainHand());
                                        commandSender.sendMessage(((Player) commandSender).getInventory().getItemInMainHand() + " wurde entfernt");
                                        return false;
                                    }
                                }
                                commandSender.sendMessage("Item ist noch nicht gebannt");
                            }
                        }
                    }
                    return false;
                }
                case "isbanana": {
                    if (args.length != 0) {
                        commandSender.sendMessage("§4/isbanana (Mit Item in der Hand)");
                        return false;
                    } else {
                        if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.SPLASH_POTION || ((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.LINGERING_POTION) {
                            if (bannedPotions.contains(((Player) commandSender).getInventory().getItemInMainHand() + "")) {
                                commandSender.sendMessage("Item ist eine Banane");
                                return false;
                            } else {
                                commandSender.sendMessage("Item ist keine Banane");
                            }
                        } else {
                            for (ItemStack i : banned) {
                                if (i.isSimilar(((Player) commandSender).getInventory().getItemInMainHand())) {
                                    commandSender.sendMessage("Item ist eine Banane");
                                    return false;
                                }
                            }
                            commandSender.sendMessage("Item ist keine Banane");
                        }
                    }
                    return false;
                }
                case "seed": {
                    commandSender.sendMessage("<_xX1337H4xx0r1337Xx_> #NiceDruschke seed: " + ChatColor.MAGIC + "0000000000000000");
                    break;
                }
                case "unfreeze": {
                    if (commandSender.isOp()) {
                        if (args.length != 1) {
                            commandSender.sendMessage("§4/unfreeze <playername>");
                            return false;
                        } else {
                            for (Player p:Bukkit.getServer().getOnlinePlayers()) {
                                if (Objects.equals(p.getName(), args[0])) {
                                    tm.unfreeze(p);
                                    return false;
                                }
                            }
                            commandSender.sendMessage("§4Player not found");
                        }
                    } else {
                        commandSender.sendMessage("§4Nur für OPs");
                    }
                    break;
                }
                case "freeze": {
                    if (commandSender.isOp()) {
                        if (args.length != 1) {
                            commandSender.sendMessage("§4/freeze <playername>");
                            return false;
                        } else {
                            for (Player p:Bukkit.getServer().getOnlinePlayers()) {
                                if (Objects.equals(p.getName(), args[0])) {
                                    tm.freeze(p);
                                    return false;
                                }
                            }
                            commandSender.sendMessage("§4Player not found");
                        }
                    } else {
                        commandSender.sendMessage("§4Nur für OPs");
                    }
                    break;
                }
                case "c": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/c <playername>");
                        return false;
                    } else {
                        if (daysOfVaro > (coordsDelay - 1)) {
                            if (coords.containsKey(args[0])) {
                                ArrayList<Location> l = coords.get(args[0]);
                                Location ll = l.get(daysOfVaro - coordsDelay);
                                if (ll != null) {
                                    commandSender.sendMessage(ll.getBlock().getX() + ", " + ll.getBlock().getY() + ", "+ ll.getBlock().getZ());
                                }
                            } else {
                                commandSender.sendMessage("§4Player not found");
                            }
                        }
                    }
                    break;
                }
                case "setOvertimeRadius": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/setOvertimeRadius [radius in blocks]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            try {
                                Data.overtimeRadius = Integer.parseInt(args[0]);
                            } catch (Exception e) {commandSender.sendMessage("§4/setOvertimeRadius [radius in blocks]");}
                            for (Player p:Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.YELLOW + "Overtime radius wurde auf " + args[0] + " geändert");
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "setOvertimeLength": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/setOvertimeLength [length in seconds]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            try {
                                Data.overtimeLength = Integer.parseInt(args[0]);
                            } catch (Exception e) {commandSender.sendMessage("§4/setOvertimeLength [length in seconds]");}
                            for (Player p:Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.YELLOW + "Overtime length wurde auf " + args[0] + " geändert");
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "setInactivityMax": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/setInactivityMax [days]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            try {
                                Data.inactivityMax = Integer.parseInt(args[0]);
                            } catch (Exception e) {commandSender.sendMessage("§4/setInactivityMax [days]");}
                            for (Player p:Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.YELLOW + "Maximum days of inactivity wurde auf " + args[0] + " geändert");
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "setCoordsDelay": {
                    if (args.length != 1) {
                        commandSender.sendMessage("§4/setCoordsDelay [days]");
                        return false;
                    } else {
                        if (!varoStarted) {
                            try {
                                Data.coordsDelay = Integer.parseInt(args[0]);
                            } catch (Exception e) {commandSender.sendMessage("§4/setCoordsDelay [days]");}
                            for (Player p:Bukkit.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.YELLOW + "Days after which coords are revealed wurde auf " + args[0] + " geändert");
                            }
                        } else {
                            commandSender.sendMessage("§4Varo hat schon begonnen");
                        }
                    }
                    break;
                }
                case "getconfig": {
                    commandSender.sendMessage("sessionTime: " + sessionTime);
                    commandSender.sendMessage("postplays: " + postplays);
                    commandSender.sendMessage("overtimeRadius: " + overtimeRadius);
                    commandSender.sendMessage("overtimeLength: " + overtimeLength);
                    commandSender.sendMessage("inactivityMax: " + inactivityMax);
                    commandSender.sendMessage("coordsDelay: " + coordsDelay);
                    break;
                }
            }

        } else {
            switch (command.getName()) {
                case "chest": {
                    if (args.length < 1) {
                        commandSender.sendMessage("§4/chest <add/place/remove/get>");
                        return false;
                    } else {
                        switch (args[0]) {
                            case "add": {
                                if (args.length != 4) {
                                    commandSender.sendMessage("§4/chest add [material] [amount] [metadata]");
                                    return false;
                                } else {
                                    try {
                                        chest.add(new ItemStack(Material.getMaterial(args[1].toUpperCase()), Integer.parseInt(args[2]), Short.parseShort(args[3])));
                                    } catch (Exception e) {err.println("Something wrong. See: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");}
                                }
                                break;
                            }
                            case "remove": {
                                if (args.length != 4) {
                                    commandSender.sendMessage("§4/chest remove [material] [amount] [metadata]");
                                    return false;
                                } else {
                                    try {
                                        if (chest.contains(new ItemStack(Material.getMaterial(args[1].toUpperCase()), Integer.parseInt(args[2]), Short.parseShort(args[3])))) {
                                            chest.remove(new ItemStack(Material.getMaterial(args[1].toUpperCase()), Integer.parseInt(args[2]), Short.parseShort(args[3])));
                                        } else {
                                            err.println("Chest does not contain that item");
                                        }
                                    } catch (Exception e) {err.println("Something wrong. See: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");}
                                }
                                break;
                            }
                            case "get": {
                                if (args.length < 1) {
                                    commandSender.sendMessage("§4/chest get");
                                    return false;
                                } else {
                                    try {
                                        chest.forEach(out::println);
                                    } catch (Exception e) {err.println("Something wrong. See: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");}
                                }
                                return false;
                            }
                            case "place": {
                                if (args.length < 4) {
                                    commandSender.sendMessage("§4/chest place x y z");
                                    return false;
                                } else {
                                    try {
                                        Location l = new Location(Bukkit.getWorld("world"), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                                        newChest(l, chest);
                                        lootChests.add(l);
                                        chest.clear();
                                    } catch (Exception e) {err.println("Something wrong.");}
                                }
                                return false;
                            }
                        }
                    }
                    return false;
                }
            }
            commandSender.sendMessage("§4You must be a player on the server!");
        }
        return false;
    }

    void newChest(Location location, ArrayList<ItemStack> itemStacks) {
        if (location.getBlock().getType() != Material.CHEST) location.getBlock().setType(Material.CHEST);
        Chest block = (Chest) location.getBlock().getState();
        ((CraftChest) Bukkit.getWorld("world").getBlockAt(location).getState()).getTileEntity().a("Loot #NiceDruschke");
        Random r = new Random();
        for (ItemStack i : itemStacks) {
            int g = r.nextInt(block.getInventory().getSize());
            if (block.getInventory().getItem(g) == null) {
                block.getInventory().setItem(g, i);
            } else {
                int h = r.nextInt(block.getInventory().getSize());
                if (block.getInventory().getItem(h) == null) {
                    block.getInventory().setItem(h, i);
                } else {
                    int j = r.nextInt(block.getInventory().getSize());
                    if (block.getInventory().getItem(j) == null) {
                        block.getInventory().setItem(j, i);
                    } else {
                        block.getInventory().addItem(i);
                    }
                }
            }
        }
    }
}
