package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.UserProfile
import ru.mtuci.drivenext.domain.repository.ProfileRepository

class GetProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(): Result<UserProfile> = profileRepository.getProfile()
}
