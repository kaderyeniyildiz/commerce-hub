package com.commercehub.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank
    @Size(max = 72)
    private String currentPassword;

    @NotBlank
    @Size(max = 72)
    private String newPassword;
}
