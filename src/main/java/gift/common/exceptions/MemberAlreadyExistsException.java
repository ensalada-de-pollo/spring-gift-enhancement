package gift.common.exceptions;

public class MemberAlreadyExistsException extends RuntimeException {

    public MemberAlreadyExistsException(String message) {
        super(message);
    }
}
