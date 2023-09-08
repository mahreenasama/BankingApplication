package com.redmath.bankingapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{accId}")
    public ResponseEntity<Map<String, User>> getUserByAccountId(@PathVariable("accId") Long accId, Authentication auth)
    {
        User user = userService.getUserByAccountId(accId, auth);
        if (user != null) {
            return ResponseEntity.ok(Map.of("content", user));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("changePassword/{accId}")
    public ResponseEntity<Map<String, User>> changePasswordByAccountId(@PathVariable("accId") Long accId, @RequestBody String newPassword, Authentication auth)
    {
        User updatedUser = userService.changePasswordByAccountId(accId, newPassword, auth);
        if (updatedUser != null) {
            return ResponseEntity.ok(Map.of("content", updatedUser));
        }
        return ResponseEntity.notFound().build();
    }



}
