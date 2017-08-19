package com.sermak.plugin;

import com.sermak.plugin.db.DBC;
import com.sermak.plugin.db.Data;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

import static com.sermak.plugin.db.Data.online;
import static com.sermak.plugin.db.Data.sessionManager;
import static com.sermak.plugin.db.Data.varoStarted;
import static java.lang.System.out;

public class VaroPlugin extends JavaPlugin {

    private com.sermak.plugin.EventHandler eventHandler = new com.sermak.plugin.EventHandler();
    @Override
    public void onEnable() {
        String path = (DBC.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1)).substring(0, DBC.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).lastIndexOf("/"));
        String[] s = {"abcdefghijklmnopqrstuvwxyz"};
        DBC.FileInderface.fileWrite(path + "/test.txt", s);
        try {
            String[] ss = DBC.FileInderface.fileRead(path + "/test.txt");
            if (ss == s && Objects.equals(ss[0], s[0])) {
                out.println("Keine Berechtigungen");
            } else {
                out.println("Das Plugin hat Berechtigungen! Hurra!");
            }
        } catch (IOException e) {
            out.println("Keine Berechtigungen");
        }
        Data.varoPlugin = this;
        Data.load();
        getServer().getPluginManager().registerEvents(eventHandler, this);
        getServer().getOnlinePlayers().forEach(Data.tm::freeze);
        VaroCE v = new VaroCE();
        getCommand("r").setExecutor(v);
        getCommand("rg").setExecutor(v);
        getCommand("rr").setExecutor(v);
        getCommand("s").setExecutor(v);
        getCommand("t").setExecutor(v);
        getCommand("setSessionTime").setExecutor(v);
        getCommand("varostart").setExecutor(v);
        getCommand("varotoggle").setExecutor(v);
        getCommand("getVaroDay").setExecutor(v);
        getCommand("getss").setExecutor(v);
        getCommand("getpp").setExecutor(v);
        getCommand("setPostPlays").setExecutor(v);
        getCommand("getDeadPlayers").setExecutor(v);
        getCommand("banana").setExecutor(v);
        getCommand("newDay").setExecutor(v);
        getCommand("getbanana").setExecutor(v);
        getCommand("isbanana").setExecutor(v);
        getCommand("nobanana").setExecutor(v);
        getCommand("seed").setExecutor(v);
        getCommand("unfreeze").setExecutor(v);
        getCommand("freeze").setExecutor(v);
        getCommand("c").setExecutor(v);
        getCommand("chest").setExecutor(v);
        getCommand("getconfig").setExecutor(v);
        getCommand("setOvertimeRadius").setExecutor(v);
        getCommand("setOvertimeLength").setExecutor(v);
        getCommand("setInactivityMax").setExecutor(v);
        getCommand("setCoordsDelay").setExecutor(v);
        getCommand("rezzz").setExecutor(v);
        getCommand("kill").setExecutor(v);
        if (varoStarted) {
            sessionManager.start();
        }
    }

    @Override
    public void onDisable() {
        Data.save();
        for (String p:online.keySet()) {
            online.get(p).onlineState = Data.OnlineStates.IDLE;
        }
        if (varoStarted) {
            sessionManager.stop();
        }
    }


}