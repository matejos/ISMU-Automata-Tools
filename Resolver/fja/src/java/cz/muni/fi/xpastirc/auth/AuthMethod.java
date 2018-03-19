/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.auth;

/**
 *
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 19.10.2010
 */
public interface AuthMethod {
    boolean login(String name, String password);
}
