package cz.muni.fi.fja;

import cz.muni.fi.fja.common.ModelError;

public interface Device {

  ModelError getError();

  boolean isError();

  String toStringInOneRow();
  
}
