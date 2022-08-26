package account.exceptions;

public class AccountServiceException extends RuntimeException {

    public AccountServiceException(String message) {
        super(message);
    }
}