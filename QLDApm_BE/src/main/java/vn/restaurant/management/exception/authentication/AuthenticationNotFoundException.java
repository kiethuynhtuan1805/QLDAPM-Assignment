package vn.restaurant.management.exception.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationNotFoundException extends Exception{
    public AuthenticationNotFoundException(String message) {
        super(message);
    };
}
