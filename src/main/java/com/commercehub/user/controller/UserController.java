package com.commercehub.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(description = "User api", name = "User")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Operation(summary = "", description = "")
    @GetMapping("/dummy")
    public ResponseEntity<String> getDummyApi() {
        return ResponseEntity.ok("Test dummy api");
    }
}
