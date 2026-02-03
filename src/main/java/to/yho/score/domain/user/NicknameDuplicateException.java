package to.yho.score.domain.user;

public class NicknameDuplicateException extends RuntimeException {

    public NicknameDuplicateException(String message) {
        super(message);
    }
}
