package com.bodriopopcorn.dto.response;

import com.bodriopopcorn.entity.PersonRoleType;

public record PersonResponse(Long id, String name, PersonRoleType roleType) {
}
