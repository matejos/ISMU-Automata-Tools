/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.auth;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jpam.Pam;
/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 16.4.2011
 */
public class PamAuth implements AuthMethod {

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
        //return ((name != null) && (password != null) && name.equals("Opravnenyuzivatel") && (password.equals("a")));
    }

}
