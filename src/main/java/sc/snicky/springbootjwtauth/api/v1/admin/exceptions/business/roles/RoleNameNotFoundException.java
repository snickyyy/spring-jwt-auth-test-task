package sc.snicky.springbootjwtauth.api.v1.admin.exceptions.business.roles;

import sc.snicky.springbootjwtauth.api.v1.admin.exceptions.NotFoundException;

public class RoleNameNotFoundException extends NotFoundException {
    public RoleNameNotFoundException(String message) {
        super(message);
    }
}
