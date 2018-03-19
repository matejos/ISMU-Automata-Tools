/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.fja.common;

/**
 * 
 * @author Bronek
 */
public interface ModelReader {
  Rule[] getAllControl();

  Alphabet[] getAllAlphabet();

  Control[] getAllFinal();

  ModelError getError();

}
