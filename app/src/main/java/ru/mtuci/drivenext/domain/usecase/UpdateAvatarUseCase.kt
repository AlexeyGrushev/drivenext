package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.repository.ProfileRepository

class UpdateAvatarUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(imageUri: String): Result<String> = profileRepository.updateAvatar(imageUri)
}
