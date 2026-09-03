package com.example.shareplate.data.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        role: String,
        businessName: String?,
        businessAddress: String?,
        deliveryAddress: String?,
        organizationName: String?,
        organizationRegNo: String?
    ): Result<Profile> {
        return try {
            val user = SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            } ?: throw IllegalStateException(
                "Account created. Check your email to confirm before logging in."
            )

            val profile = Profile(
                userId = user.id,
                name = fullName.trim(),
                email = email.trim().lowercase(),
                phone = phoneNumber.trim(),
                role = role,
                organisationName = when (role) {
                    "SELLER" -> businessName
                    "NGO" -> organizationName
                    else -> null
                },
                address = when (role) {
                    "SELLER" -> businessAddress
                    "BUYER" -> deliveryAddress
                    else -> null
                },
                createdAt = java.time.OffsetDateTime.now().toString()
            )

            SupabaseClient.client.from("users").insert(profile)
            Result.success(profile)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Profile> {
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email.trim().lowercase()
                this.password = password
            }

            val user = SupabaseClient.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("Incorrect email or password.")

            val profile = fetchProfile(user.id)
                ?: throw IllegalStateException("Profile not found for this account.")

            Result.success(profile)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, redirectTo: String?): Result<Unit> {
        return try {
            SupabaseClient.client.auth.resetPasswordForEmail(email, redirectUrl = redirectTo)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentProfile(): Profile? {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return null
        return fetchProfile(userId)
    }

    suspend fun updateProfile(profile: Profile): Result<Profile> {
        return try {
            SupabaseClient.client.from("users").upsert(profile)
            Result.success(profile)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            SupabaseClient.client.auth.updateUser {
                password = newPassword
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        SupabaseClient.client.auth.signOut()
    }

    private suspend fun fetchProfile(userId: String): Profile? =
        SupabaseClient.client.from("users")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Profile>()
            .firstOrNull()
}
