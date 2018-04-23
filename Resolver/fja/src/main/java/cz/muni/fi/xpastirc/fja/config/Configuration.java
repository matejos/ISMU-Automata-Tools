/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fja.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 18.10.2010
 */
public class Configuration {

    Properties prop = new Properties();
    private static final String ISONLY = "ReadFromIsOnly";
    private static final String TOTALPATH = "/var/lib/tomcat6/webapps/fjamp/";
    private static final String FILENAME = "config.properties";
    private static final String ISADDRESS = "ISAddress";
    private static final String BANNED_BAD = "BannedBad";
    private static final String BANNED_GOOD = "BannedGood";
    private static final String DBSERVER = "DbServer";
    private static final String DBPASSWORD = "DBbPassword";
    private static final String DBUSER = "DbUser";
    private static final String DBNAME = "DbName";
    private static final String LOGCOUNT = "LogCount";
    private static final String LOGDELETE = "LogDelete";
    private static Configuration conf;
    /*private Configuration() throws IOException{
        prop = new Properties();
        File propFile;
        propFile = new File(TOTALPATH + FILENAME);
        if(!(propFile.exists()))
              propFile.createNewFile();
        prop.load(new FileInputStream(propFile));
    }*/
    
    public static Configuration getConfiguration() throws IOException{
        if (conf == null) conf = new Configuration();
        return conf;
    }
    public boolean getReadFromIsOnly(){
        return Integer.parseInt(prop.getProperty(ISONLY, "0")) != 0 ;
    }

    public void setReadFromIsOnly(boolean readFromIsOnly) throws IOException{
        prop.setProperty(ISONLY, readFromIsOnly?"1":"0");
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena nastaveni cteni z IS na " + readFromIsOnly);
    }
    public String getIsAddress(){
        return prop.getProperty(ISADDRESS,"147.251.49.*");
    }
    public void setIsAddress(String isAddress) throws IOException{
        prop.setProperty(ISADDRESS, isAddress);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena adresy IS");
    }
    
    public String getBannedBad(){
        return prop.getProperty(BANNED_BAD,"5");
    }
    public void setBannedBad(String toSet) throws IOException{
        prop.setProperty(BANNED_BAD, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena spatnych zabanovanych");
    }
    public String getBannedGood(){
        return prop.getProperty(BANNED_GOOD,"50");
    }
    public void setBannedGood(String toSet) throws IOException{
        prop.setProperty(BANNED_GOOD, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena dobrych zabanovanych");
    }
    public String getDbServer(){
        return prop.getProperty(DBSERVER,"localhost");
    }
    public void setDbServer(String toSet) throws IOException{
        prop.setProperty(DBSERVER, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena DB serveru");
    }
    public String getDbName(){
        return prop.getProperty(DBNAME,"fja_adv");
    }
    public void setDbName(String toSet) throws IOException{
        prop.setProperty(DBNAME, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena DB jmena");
    }
    public String getDbUser(){
        return prop.getProperty(DBUSER,"fja");
    }
    public void setDbUser(String toSet) throws IOException{
        prop.setProperty(DBUSER, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena DB uzivatele");
    }
    public String getDbPass(){
        return prop.getProperty(DBPASSWORD,"fhesjloa");
    }
    public void setDbPass(String toSet) throws IOException{
        prop.setProperty(DBPASSWORD, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "Zmena DB hesla");
    }
    public String getLogCount(){
        return prop.getProperty(LOGCOUNT,"30");
    }
    public String getLogDelete(){
         return prop.getProperty(LOGDELETE,"200");
    }
    public void setLogCount(String toSet) throws IOException{
        prop.setProperty(LOGCOUNT, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "logy - pocet na stranu");
    }
    public void setLogDelete(String toSet) throws IOException{
        prop.setProperty(LOGDELETE, toSet);
        prop.store(new FileOutputStream(TOTALPATH + FILENAME), "logy - mazani");
    }

}
