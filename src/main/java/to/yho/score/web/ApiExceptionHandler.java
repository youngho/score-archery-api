package to.yho.score.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import to.yho.score.domain.user.NicknameDuplicateException;
import to.yho.score.domain.user.NicknameInvalidException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NicknameInvalidException.class)
    public ResponseEntity<ErrorResponse> handleNicknameInvalid(NicknameInvalidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(NicknameDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleNicknameDuplicate(NicknameDuplicateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build());
    }
}
