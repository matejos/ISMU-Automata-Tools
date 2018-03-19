/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.db;

import cz.muni.fi.xpastirc.fja.config.Configuration;
import cz.muni.fi.xpastirc.fja.servlet.HTMLEscaper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 9.5.2011
 */
public class MySQLHandler implements DBHandler{
    private static MySQLHandler handler = null;
    //defaultni hodnoty, kdyby selhalo nacteni konfigurace.
    private static String dbuser = "root";
    private static String dbpass = "aaaaaa";
    private static String db = "fja_adv";
    private static String server = "localhost";
    private static int log_delete = 200;
    private Connection connect = null;

    protected MySQLHandler() throws ClassNotFoundException, SQLException{
        try{
            Configuration configuration = Configuration.getConfiguration();
            dbuser = configuration.getDbUser();
            dbpass = configuration.getDbPass();
            db = configuration.getDbName();
            server = configuration.getDbServer();
            log_delete = Integer.parseInt(configuration.getLogDelete());
        } catch(IOException e){
            //nic, vezme defaultni
        }
        connect();
        init();



    }

    private void connect() throws SQLException{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException(ex.getMessage());
        }
        connect = DriverManager
                .getConnection("jdbc:mysql://"+server+"?"
			+ "user="+ dbuser +"&password=" + dbpass);
    }

    public long logEqual(int mode, String teacherA, String teacherF, String studentA, String studentF, String ip) throws SQLException {
        return logEqual(mode, teacherA, teacherF, studentA, studentF,ip,null);
    }

    public long logEqual(int mode, String teacherA, String teacherF, String studentA, String studentF, String ip, String user) throws SQLException {
        if (connect==null || connect.isClosed()) connect();
        PreparedStatement ps = connect.prepareStatement(
                "INSERT INTO "+db+".log_equal(mode, teacherA, teacherF, studentA, studentF, ip, user, time_start) VALUES (?,?,?,?,?,?,?,?)");
        ps.setInt(1, mode);
        long time = System.currentTimeMillis();
        ps.setString(2, teacherA);
        ps.setString(3, teacherF);
        ps.setString(4, studentA);
        ps.setString(5, studentF);
        ps.setString(6, ip);
        ps.setString(7, user);
        ps.setTimestamp(8, new Timestamp(time));
        ps.executeUpdate();
        //if mode is other than T/F, we don't need other information
        if (mode != 1) return time;
        ps = connect.prepareStatement("SELECT equal from "+db+".log_equal WHERE teacherA = ? AND teacherF = ? AND studentA = ? AND studentF = ?");
        ps.setString(1, teacherA);
        ps.setString(2, teacherF);
        ps.setString(3, studentA);
        ps.setString(4, studentF);
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            int toReturn = resultSet.getInt("equal");
            if (toReturn == -1){
                logEqualAnswer(time,false);
                return -1;
            }
            if (toReturn == 1){
                logEqualAnswer(time,true);
                return 0;
            }
        }
        return time;


    }

    public void logEqualAnswer(long query, boolean equality) throws SQLException {
        if (connect==null || connect.isClosed()) connect();
        PreparedStatement ps = connect.prepareStatement(
                "UPDATE "+db+".log_equal SET equal=?, time_end=? WHERE time_start=?");
        ps.setInt(1, equality?1:-1);
        ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        ps.setTimestamp(3, new Timestamp(query));
        ps.executeUpdate();
    }

    public long logConvert(int mode, String automaton, String from, String to, String ip, String user) throws SQLException {
        if (connect==null || connect.isClosed()) connect();
        PreparedStatement ps = connect.prepareStatement(
                "INSERT INTO "+db+".log_equal(mode, teacherA, teacherF, studentF, ip, user, time) VALUES (?,?,?,?,?,?,?)");
        ps.setInt(1, mode);
        long time = System.currentTimeMillis();
        ps.setString(2, automaton);
        ps.setString(3, from);
        ps.setString(4, to);
        ps.setString(5, ip);
        ps.setString(6, user);
        ps.setTimestamp(8, new Timestamp(time));
        ps.executeUpdate();
        return time;
    }

    public void logConvertComplete(long query) throws SQLException {
        if (connect==null || connect.isClosed()) connect();
        PreparedStatement ps = connect.prepareStatement(
                "UPDATE "+db+".log_equal SET time_end=? WHERE time_start=?");
        ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        ps.setTimestamp(2, new Timestamp(query));
    }

    public boolean isBanned(String ip) throws SQLException {
        Configuration configuration;
        String isIP;
        try {
            configuration = Configuration.getConfiguration();
            isIP = configuration.getIsAddress();
        } catch (IOException ex) {
            isIP= "147.251.49.*";

        }
        //IS is never banned
        if (ip.matches(isIP)) return false;
        PreparedStatement ps = connect.prepareStatement(
             "SELECT time_start,time_end FROM "+db+".log_equal WHERE ip=?");
        ps.setString(1, ip);
        ResultSet rs = ps.executeQuery();
        int lastHourBad=0;
        int lastHour=0;
        while(rs.next()){
            //pokud bylo >5 neuspesnych pokusu za posledni hodinu
            //nebo >30 pokusu za posledni hodinu
            if (System.currentTimeMillis() - rs.getLong(1) < 1000*60*60){
                if (rs.getLong(2)==0)
                    lastHourBad++;
                lastHour++;
                if (lastHour>= 30 || lastHourBad >=5 ) return true;
            }
        }
        return false;
    }

    public static DBHandler getHandler() throws ClassNotFoundException, SQLException{
        if (handler == null)
            handler = new MySQLHandler();
        return handler;
    }

    private void init() throws SQLException{
        if (connect==null || connect.isClosed()) connect();
        Statement st = connect.createStatement();
        st.execute("CREATE TABLE IF NOT EXISTS "+db+".`log_equal` ("+
            "`id` int(3) NOT NULL auto_increment,"+
            "`mode` int(1) NOT NULL,"+
            "`teacherA`  varchar(1000) collate utf8_czech_ci NOT NULL,"+
            "`teacherF` varchar(3) collate utf8_czech_ci NOT NULL,"+
            "`studentA`  varchar(1000) collate utf8_czech_ci NULL,"+
            "`studentF`  varchar(3) collate utf8_czech_ci NOT NULL,"+
            "`ip` varchar(30) collate utf8_czech_ci NOT NULL,"+
            "`user` varchar(30) collate utf8_czech_ci,"+
            "`time_start` TIMESTAMP DEFAULT 0,"+
            "`time_end` TIMESTAMP DEFAULT 0,"+
            "`equal` int(1) NOT NULL default 0,"+
            "PRIMARY KEY  (`id`),"+
            "UNIQUE KEY `time_start` (`time_start`)"+
            ")DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci");
    }

    public int getNumberOfPages() throws SQLException {
        if (connect==null || connect.isClosed()) connect();
        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM "+db+".log_equal");
        rs.next();
        int count = rs.getInt(1);
        Configuration configuration;
        try {
            configuration = Configuration.getConfiguration();
        } catch (IOException ex) {
            return count / 30 +1;
        }
        return (count / Integer.parseInt(configuration.getLogCount()) +1);
    }

    public List<String> getPage(int page, String orderBy, boolean asc) throws SQLException {
        if (connect == null || connect.isClosed()) connect();
        if (!(orderBy.equals("teacherF") || orderBy.equals("teacherA") || orderBy.equals("studentF")
                || orderBy.equals("studentA")|| orderBy.equals("time_start")))
            throw new SQLException("Can't order by " + orderBy);
        PreparedStatement ps = connect.prepareStatement(
             "SELECT * FROM "+db+".log_equal ORDER BY "+orderBy+(asc?" ASC":" DESC")+" LIMIT ?, ?");
        Configuration configuration;
        int count;
        try {
            configuration = Configuration.getConfiguration();
            count = Integer.parseInt(configuration.getLogCount());
        } catch (IOException ex) {
            count = 30;

        }
        List<String> toReturn = new ArrayList<String>();
        ps.setInt(1, (page-1)*count);
        ps.setInt(2, count);
        ResultSet rs = ps.executeQuery();
        StringBuilder toPush;
        while (rs.next()){
            toPush = new StringBuilder();
            //formalismus ucitele
            toPush.append("<tr><td>").append(rs.getString("teacherF")).append("</td>");
               //Automat učitele
            toPush.append("<td class=\"automaton\">").append(HTMLEscaper.escapeHTML(rs.getString("teacherA"))).append("</td>");
            //Formalismus studenta
            toPush.append("<td>").append(rs.getString("studentF")).append("</td>");
            //Automat studenta/
            toPush.append("<td class=\"automaton\">").append(HTMLEscaper.escapeHTML(rs.getString("StudentA"))).append("</td>");
            //Čas zadání dotazu
            toPush.append("<td>").append(rs.getTimestamp("time_start").toString()).append("</td>");
            //Doba dotazu
            toPush.append("<td>");
                   if (rs.getLong("time_end")!=0)
                       toPush.append(rs.getLong("time_start") - rs.getLong("time_end"));
                   toPush.append("</td>");
            //Výsledek
            toPush.append("<td>").append(rs.getInt("equal")).append("</td></tr>\n");
            toReturn.add(toPush.toString());
        }
        return toReturn;
    }

    public void clean() throws SQLException {
        if (connect == null || connect.isClosed()) connect();
        PreparedStatement ps1 = connect.prepareStatement("SELECT id FROM "+db+".log_equal LIMIT 1");
        ResultSet rs = ps1.executeQuery();
        int idMax;
        if (rs.next())
             idMax = rs.getInt(1);
        else
            idMax = 0;
        idMax += log_delete;
        PreparedStatement ps2 = connect.prepareStatement("DELETE FROM "+db+".log_equal WHERE id<?");
        ps2.setInt(1, idMax);
        ps2.execute();
    }

    public void empty() throws SQLException{
        if (connect == null || connect.isClosed()) connect();
        PreparedStatement ps = connect.prepareStatement("DELETE FROM "+db+".log_equal");
        ps.execute();
    }
}
