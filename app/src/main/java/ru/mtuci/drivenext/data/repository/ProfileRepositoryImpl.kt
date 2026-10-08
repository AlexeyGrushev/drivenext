package ru.mtuci.drivenext.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.time.LocalDate
import kotlin.time.ExperimentalTime
import ru.mtuci.drivenext.data.remote.ImageReader
import ru.mtuci.drivenext.data.remote.ProfileDto
import ru.mtuci.drivenext.data.remote.ProfileInsertDto
import ru.mtuci.drivenext.data.remote.SupabaseProvider
import ru.mtuci.drivenext.data.remote.safeCall
import ru.mtuci.drivenext.domain.model.AppException
import ru.mtuci.drivenext.domain.model.Gender
import ru.mtuci.drivenext.domain.model.RegistrationData
import ru.mtuci.drivenext.domain.model.UserProfile
import ru.mtuci.drivenext.domain.repository.ProfileRepository

@OptIn(ExperimentalTime::class)
class ProfileRepositoryImpl(
    private val provider: SupabaseProvider,
    private val imageReader: ImageReader,
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfile> =
        safeCall("Не удалось загрузить профиль. Попробуйте снова.") {
            val client = provider.requireClient()
            val user = client.auth.currentUserOrNull()
                ?: throw AppException("Сессия истекла. Войдите в аккаунт снова.")
            val dto = client.from(TABLE)
                .select { filter { eq("id", user.id) } }
                .decodeSingleOrNull<ProfileDto>()
            val isGoogle = user.identities.orEmpty().any { it.provider == Google.name.lowercase() }
            val email = user.email ?: dto?.email.orEmpty()
            UserProfile(
                id = user.id,
                email = email,
                lastName = dto?.lastName.orEmpty(),
                firstName = dto?.firstName.orEmpty(),
                middleName = dto?.middleName,
                birthDate = dto?.birthDate?.let(::parseIsoDate),
                gender = Gender.fromDb(dto?.gender),
                avatarUrl = dto?.avatarUrl,
                googleEmail = if (isGoogle) email else null,
                joinedAt = (dto?.createdAt ?: user.createdAt?.toString())?.let(::parseIsoDate),
            )
        }

    override suspend fun saveProfile(userId: String, data: RegistrationData): Result<Unit> =
        safeCall("Не удалось сохранить данные профиля. Попробуйте снова.") {
            val storage = provider.requireClient().storage
            val avatarUrl = data.avatarUri?.let { uri ->
                val path = "$userId/avatar_${System.currentTimeMillis()}.jpg"
                storage.from(AVATARS).upload(path, imageReader.readJpeg(uri)) { upsert = true }
                storage.from(AVATARS).publicUrl(path)
            }
            val licensePath = "$userId/license.jpg"
            storage.from(DOCUMENTS).upload(licensePath, imageReader.readJpeg(data.licensePhotoUri)) { upsert = true }
            val passportPath = "$userId/passport.jpg"
            storage.from(DOCUMENTS).upload(passportPath, imageReader.readJpeg(data.passportPhotoUri)) { upsert = true }

            provider.requireClient().from(TABLE).insert(
                ProfileInsertDto(
                    id = userId,
                    email = data.email.trim(),
                    lastName = data.lastName.trim(),
                    firstName = data.firstName.trim(),
                    middleName = data.middleName?.trim()?.ifBlank { null },
                    birthDate = data.birthDate.toString(),
                    gender = data.gender.dbValue,
                    avatarUrl = avatarUrl,
                    licenseNumber = data.licenseNumber,
                    licenseIssueDate = data.licenseIssueDate.toString(),
                    licensePhotoPath = licensePath,
                    passportPhotoPath = passportPath,
                )
            )
        }

    override suspend fun updateAvatar(imageUri: String): Result<String> =
        safeCall("Не удалось загрузить аватар. Попробуйте снова.") {
            val client = provider.requireClient()
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw AppException("Сессия истекла. Войдите в аккаунт снова.")
            val path = "$userId/avatar_${System.currentTimeMillis()}.jpg"
            client.storage.from(AVATARS).upload(path, imageReader.readJpeg(imageUri)) { upsert = true }
            val url = client.storage.from(AVATARS).publicUrl(path)
            client.from(TABLE).update({ set("avatar_url", url) }) { filter { eq("id", userId) } }
            url
        }

    private fun parseIsoDate(text: String): LocalDate? =
        runCatching { LocalDate.parse(text.take(10)) }.getOrNull()

    private companion object {
        const val TABLE = "profiles"
        const val AVATARS = "avatars"
        const val DOCUMENTS = "documents"
    }
}
