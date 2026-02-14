package com.avalonnarrator.domain.setup

import com.avalonnarrator.domain.model.SetupIssueCode
import com.avalonnarrator.domain.roles.RoleId

enum class SetupIssueLevel {
    INFO,
    WARNING,
    ERROR,
}

data class SetupIssue(
    val level: SetupIssueLevel,
    val code: SetupIssueCode,
    val message: String,
    val affectedRoles: Set<RoleId> = emptySet(),
)
