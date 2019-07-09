package life.qbic.db

public class NotFoundException extends Exception {

  public NotFoundException(String errorMessage) {
    super(errorMessage)
  }
}
