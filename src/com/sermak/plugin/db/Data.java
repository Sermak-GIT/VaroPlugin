package com.sermak.plugin.db;

import com.sermak.plugin.SessionManager;
import com.sermak.plugin.TeamManager;
import com.sermak.plugin.VaroPlugin;
import com.sermak.plugin.db.cdatatypes.HashPair;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static java.lang.System.err;

public class Data {
    public static ArrayList<TeamManager.Team> teams = new ArrayList<>(); //done
    //String, because same Player creates different objects on login
    public static HashMap<String, OnlineData> online = new HashMap<>(); //done
    public static HashMap<String, Integer> hashMapManager = new HashMap<>(); //done
    public static HashMap<String, Integer> hashMapFreeze = new HashMap<>(); //done
    public static HashMap<String, String> playSessions = new HashMap<>(); //done
    public static HashSet<String> deaths = new HashSet<>(); //done
    public static ArrayList<ItemStack> banned = new ArrayList<>(); //done
    public static HashSet<String> bannedPotions = new HashSet<>(); //done
    public static HashPair<Location, String> lockedChests = new HashPair<>(); //done
    public static HashMap<String, Double> overtime = new HashMap<>(); //done
    public static HashMap<String, ArrayList<Location>> coords = new HashMap<>(); //done
    public static ArrayList<ItemStack> chest = new ArrayList<>(); //done
    public static ArrayList<Location> lootChests = new ArrayList<>(); //done

    //Config
    public static int sessionTime = 25; //done
    public static int postplays = 2; //done
    public static int overtimeRadius = 50; //done
    public static int overtimeLength = 15; //done
    public static int inactivityMax = 3; //done
    public static int coordsDelay = 3; //done

    public static String[] varoSubmits = new String[3]; //done
    public static boolean varoStarted = false; //done
    public static String varoWon = ""; //done

    public static int lastDate = -1; //done
    public static int daysOfVaro = -1; //done

    public static SessionManager sessionManager = new SessionManager();
    public static VaroPlugin varoPlugin;
    public static final TeamManager tm = new TeamManager();

    public static class OnlineData implements Serializable {
        public OnlineStates onlineState;
        public Date start;
        public Date end;
    }
    public enum OnlineStates {
        IDLE, ALONE, TEAM
    }
    public static void save() {
        try {
            DBC.setTeams(teams);
        } catch (Exception e) {
            err.println("File teams not found");
        }
        try {
            DBC.setMaps(online, hashMapManager, hashMapFreeze, playSessions, deaths, banned, bannedPotions, lockedChests, overtime, coords, chest, lootChests);
        } catch (IOException e) {
            err.println("File maps not found");
        }
        try {
            DBC.setMisc(sessionTime, varoSubmits, varoStarted, lastDate, daysOfVaro, postplays, varoWon, overtimeRadius, overtimeLength, inactivityMax, coordsDelay);
        } catch (IOException e) {
            err.println("File misc not found");
        }
    }
    public static void load() {
        ArrayList<TeamManager.Team> d = DBC.getTeams();
        if (d != null) { teams = d; }
        Object[] o = DBC.getMaps();
        if (o != null) {
            online = (HashMap<String, OnlineData>) o[0];
            hashMapManager = (HashMap<String, Integer>) o[1];
            hashMapFreeze = (HashMap<String, Integer>) o[2];
            playSessions = (HashMap<String, String>) o[3];
            deaths = (HashSet<String>) o[4];
            banned = (ArrayList<ItemStack>) o[5];
            bannedPotions = (HashSet<String>) o[6];
            lockedChests = (HashPair<Location, String>) o[7];
            overtime = (HashMap<String, Double>) o[8];
            coords = (HashMap<String, ArrayList<Location>>) o[9];
            chest = (ArrayList<ItemStack>) o[10];
            lootChests = (ArrayList<Location>) o[11];

        }
        Object[] oo = DBC.getMisc();
        if (oo != null) {
            sessionTime = (int) oo[0];
            varoSubmits = (String[]) oo[1];
            varoStarted = (boolean) oo[2];
            lastDate = (int) oo[3];
            daysOfVaro = (int) oo[4];
            postplays = (int) oo[5];
            varoWon = (String) oo[6];
            overtimeRadius = (int) oo[7];
            overtimeLength = (int) oo[8];
            inactivityMax = (int) oo[9];
            coordsDelay = (int) oo[10];
        }
    }
}
