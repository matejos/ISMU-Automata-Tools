/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fawebinterface.comparing;

/**
 *
 * @author Ado
 */
public class ENFAException extends Exception{

    public ENFAException() {
    }

    public ENFAException(String message) {
        super(message);
    }

    public ENFAException(String message, Throwable cause) {
        super(message, cause);
    }

    public ENFAException(Throwable cause) {
        super(cause);
    }
    
}
