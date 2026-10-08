package com.commercehub.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(description = "Dummy api", name = "Dummy")
@RestController
@RequestMapping("/api/v1/dummys")
@RequiredArgsConstructor
public class DummyController {

    @Operation(summary = "", description = "")
    @GetMapping
    public ResponseEntity<String> getDummyApi() {
        return ResponseEntity.ok("Test dummy api");
    }
}
