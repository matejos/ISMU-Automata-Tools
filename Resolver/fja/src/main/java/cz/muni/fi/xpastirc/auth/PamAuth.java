/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.auth;

import net.sf.jpam.Pam;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 16.4.2011
 */
public class PamAuth implements AuthMethod {

    private final String authGroup = "tomcat6";

    public boolean login(String name, String password) {
        try {
            System.setProperty("java.library.path", "/usr/local/lib");
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
            if (name == null || password == null) {
                return false;
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PamAuth.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PamAuth.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(PamAuth.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PamAuth.class.getName()).log(Level.SEVERE, null, ex);
        }

        Pam pam = new Pam();
        return pam.authenticateSuccessful(name, password);
    }

    public boolean authorize(String name) throws IOException {
        UserInfo userInfo = new UserInfo();
        return userInfo.belongs2group(name, authGroup);
    }

}
