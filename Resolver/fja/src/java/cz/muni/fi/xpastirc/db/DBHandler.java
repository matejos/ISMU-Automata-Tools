/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.db;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 9.5.2011
 */
public interface DBHandler {
    /**
     * Logs one entry - writes automata, formalisms, IP and time to DB.
     * @param teacherA
     * @param teacherF
     * @param studentA
     * @param studentF
     * @param ip
     * @return positive integer as ID of query in a DB,
     *         -1 for non-equal automatons
     *         0  for equal automatons
     */
    long logEqual(int mode, String teacherA, String teacherF, String studentA, String studentF, String ip) throws SQLException;
        /**
     * Logs one entry - writes automata, formalisms, IP, user and time to DB.
     * @param teacherA
     * @param teacherF
     * @param studentA
     * @param studentF
     * @param ip
     * @return positive integer as ID of query in a DB,
     *         -1 for non-equal automatons
     *         0  for equal automatons
     */
    long logEqual(int mode, String teacherA, String teacherF, String studentA, String studentF, String ip, String user) throws SQLException;
    /**
     * Logs answer to a query with a specified ID.
     * @param query
     * @param equality
     */
    void logEqualAnswer(long query, boolean equality) throws SQLException;

    long logConvert(int mode, String automaton, String from, String to, String ip, String user) throws SQLException;

    void logConvertComplete(long query) throws SQLException;

    boolean isBanned(String ip) throws SQLException;

    int getNumberOfPages() throws SQLException;

    List<String> getPage(int page, String orderBy, boolean asc) throws SQLException;

    void clean() throws SQLException;

    void empty() throws SQLException;
}
