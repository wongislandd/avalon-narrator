package com.avalonnarrator.domain.usecase.setup

import com.avalonnarrator.domain.roles.RoleId

class DerivePlayerCountUseCase {
    operator fun invoke(
        selectedSpecialRoles: Set<RoleId>,
        loyalServantCount: Int,
        minionCount: Int,
    ): Int = selectedSpecialRoles.size + loyalServantCount + minionCount
}
