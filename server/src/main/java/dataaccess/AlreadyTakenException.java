package dataaccess;

import javax.xml.crypto.Data;

public class AlreadyTakenException extends DataAccessException {
  public AlreadyTakenException(String message) {
    super(message);
  }
}
