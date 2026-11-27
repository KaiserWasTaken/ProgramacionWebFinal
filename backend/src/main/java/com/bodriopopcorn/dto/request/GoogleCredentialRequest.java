package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoogleCredentialRequest(@NotBlank @Size(max = 10000) String credential) { }
