package com.redmath.bankingapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/findByUname")
    public ResponseEntity<Map<String, User>> getUserByUname(@RequestParam(name = "uname") String uname)
    {
        User user = userService.findByUname(uname);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", user));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<Map<String, User>> getUserByAccountId(@RequestParam(name = "accountId") Long accountId, Authentication auth)
    {
        User user = userService.getUserByAccountId(accountId, auth);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", user));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/changePassword")
    public ResponseEntity<Map<String, User>> changePasswordByAccountId(@RequestParam(name = "accountId") Long accountId, @RequestBody String newPassword, Authentication auth)
    {
        User updatedUser = userService.changePasswordByAccountId(accountId, newPassword, auth);
        if (updatedUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", updatedUser));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
