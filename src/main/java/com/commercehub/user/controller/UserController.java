package com.commercehub.user.controller;

import com.commercehub.user.dto.UserDTO;
import com.commercehub.user.request.UserCreateRequest;
import com.commercehub.user.request.UserUpdateRequest;
import com.commercehub.user.request.ChangePasswordRequest;
import com.commercehub.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(description = "User api", name = "User")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping
    public ResponseEntity<UserDTO> save(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@RequestBody @Valid UserUpdateRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(userService.update(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
