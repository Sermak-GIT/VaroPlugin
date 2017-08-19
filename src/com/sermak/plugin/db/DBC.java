package com.sermak.plugin.db;

import com.sermak.plugin.TeamManager.Team;
import com.sermak.plugin.db.cdatatypes.HashPair;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;

import static java.lang.System.err;

public class DBC {

    private static String path = (DBC.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1)).substring(0, DBC.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).lastIndexOf("/"));

    public static void setTeams(ArrayList<Team> teams) {
        FileInderface.fileWrite(path + "/teams.xml", new String[]{writeTeamFile(teams)});
    }

    private static Object fromString(String s) throws IOException ,
            ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    static ArrayList<Team> getTeams() {
        try {
            String[] sss = FileInderface.fileRead(path + "/teams.xml");
            String ss = "";
            for (String s : sss) {
                ss += s + "\n";
            }
            return genTeamList(ss);
        } catch (IOException e) {
            err.println("File teams not found");
            return new ArrayList<>();
        }
    }

    static Object[] getMaps() {
        try {
            Object[] a = new Object[12];
            String[] s = FileInderface.fileRead(path + "/maps.db");
            a[0] = fromString(s[1]);
            a[1] = fromString(s[3]);
            a[2] = fromString(s[5]);
            a[3] = fromString(s[7]);
            a[4] = fromString(s[9]);
            a[5] = fromString(s[11]);
            a[6] = fromString(s[13]);
            a[7] = fromString(s[15]);
            a[8] = fromString(s[17]);
            a[9] = fromString(s[19]);
            a[10] = fromString(s[21]);
            a[11] = fromString(s[23]);
            return a;
        } catch (Exception e) {
            err.println("File maps not found");
        }
        return null;
    }

    static void setMaps(HashMap<String, Data.OnlineData> online, HashMap<String, Integer> hashMapManager, HashMap<String, Integer> hashMapFreeze, HashMap<String, String> playSessions, HashSet<String> deaths, ArrayList<ItemStack> banned, HashSet<String> bannedPotions, HashPair<Location, String> lockedChests, HashMap<String, Double> overtime, HashMap<String, ArrayList<Location>> coords, ArrayList<ItemStack> chest, ArrayList<Location> lootChests) throws IOException {
        String[] q = new String[25];
        q[0] = "---";
        q[1] = toString(online);
        q[2] = "---";
        q[3] = toString(hashMapManager);
        q[4] = "---";
        q[5] = toString(hashMapFreeze);
        q[6] = "---";
        q[7] = toString(playSessions);
        q[8] = "---";
        q[9] = toString(deaths);
        q[10] = "---";
        q[11] = toString(banned);
        q[12] = "---";
        q[13] = toString(bannedPotions);
        q[14] = "---";
        q[15] = toString(lockedChests);
        q[16] = "---";
        q[17] = toString(overtime);
        q[18] = "---";
        q[19] = toString(coords);
        q[20] = "---";
        q[21] = toString(chest);
        q[22] = "---";
        q[23] = toString(lootChests);
        q[24] = "---";
        FileInderface.fileWrite(path + "/maps.db", q);
    }

    static Object[] getMisc() {
        try {
            Object[] a = new Object[11];
            String[] s = FileInderface.fileRead(path + "/misc.db");
            a[0] = fromString(s[1]);
            a[1] = fromString(s[3]);
            a[2] = fromString(s[5]);
            a[3] = fromString(s[7]);
            a[4] = fromString(s[9]);
            a[5] = fromString(s[11]);
            a[6] = fromString(s[13]);
            a[7] = fromString(s[15]);
            a[8] = fromString(s[17]);
            a[9] = fromString(s[19]);
            a[10] = fromString(s[21]);
            return a;
        } catch (Exception e) {
            err.println("File misc not found");
        }
        return null;
    }

    static void setMisc(int sessionTime, String[] varoSubmits, boolean varoStarted, int lastDate, int daysOfVaro, int postplays, String varoWon, int overtimeRadius, int overtimeLength, int inactivityMax, int coordsDelay) throws IOException {
        String[] q = new String[23];
        q[0] = "---";
        q[1] = toString(sessionTime);
        q[2] = "---";
        q[3] = toString(varoSubmits);
        q[4] = "---";
        q[5] = toString(varoStarted);
        q[6] = "---";
        q[7] = toString(lastDate);
        q[8] = "---";
        q[9] = toString(daysOfVaro);
        q[10] = "---";
        q[11] = toString(postplays);
        q[12] = "---";
        q[13] = toString(varoWon);
        q[14] = "---";
        q[15] = toString(overtimeRadius);
        q[16] = "---";
        q[17] = toString(overtimeLength);
        q[18] = "---";
        q[19] = toString(inactivityMax);
        q[20] = "---";
        q[21] = toString(coordsDelay);
        q[22] = "---";
        FileInderface.fileWrite(path + "/misc.db", q);
    }

    public static class FileInderface {

        public static void fileWrite(String file, String[] toPrint) {
            try {
                FileWriter fw = new FileWriter(file);
                BufferedWriter pw = new BufferedWriter(fw);
                for (String toPrint1 : toPrint) {
                    pw.write(toPrint1);
                    pw.newLine();
                }
                pw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String[] fileRead(String file) throws IOException {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            int lines = 0;
            while (br.readLine() != null) {
                lines++;
            }
            br.close();
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String[] storeInto = new String[lines];
            String current;
            int i=0;
            while ((current = br.readLine()) != null && i<storeInto.length) {
                storeInto[i]=current;
                i++;
            }
            fr.close();
            br.close();
            return storeInto;
        }

    }
    private static String writeTeamFile(ArrayList<Team> t) {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
        s += "<Teams>\n";
        for (Team team : t) {
            s += "\t<Team>\n";

            s += "\t\t<p1>";
            s += team.p1;
            s += "</p1>\n";

            s += "\t\t<p2>";
            s += team.p2;
            s += "</p2>\n";

            s += "\t\t<name>";
            s += team.name;
            s += "</name>\n";

            s += "\t</Team>\n";
        }
        s += "</Teams>";

        return s;
    }
    private static ArrayList<Team> genTeamList(String s) {
        ArrayList<Team> t = new ArrayList<>();
        try {
            s = s.substring(s.indexOf("<Team>"));
            s = s.substring(0, s.lastIndexOf("<")).trim();
            String[] ss = s.split("</Team>");
            for (String sss : ss) {
                sss = sss.substring(s.indexOf("<p1>"));
                String[] z = sss.split("\n");
                Team tt;
                tt = new Team("", "", "");
                int i = 0;
                for (String zz: z) {
                    if (!Objects.equals(zz.trim(), "")) {
                        zz = zz.substring(zz.indexOf(">") + 1, zz.lastIndexOf("<"));
                        switch (i) {
                            case 0: {
                                tt.p1 = zz.trim();
                            }
                            case 1: {
                                tt.p2 = zz.trim();
                            }
                            case 2: {
                                tt.name = zz.trim();
                            }
                        }
                        i++;
                    }
                }
                t.add(tt);
            }
            return t;
        } catch (Exception e) {
            System.err.println("Teamfile is empty!");
            return t;
        }
    }
}
