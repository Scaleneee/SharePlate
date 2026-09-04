package com.example.shareplate.data.supabase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.shareplate.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {

    @RequiresApi(Build.VERSION_CODES.O)
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

            val normalizedEmail = email.trim().lowercase()

            // Create Supabase Auth account
            SupabaseProvider.client.auth.signUpWith(Email) {
                this.email = normalizedEmail
                this.password = password
            }

            // Confirm Email is OFF,
            // so Supabase should automatically log the user in
            val user =
                SupabaseProvider.client.auth.currentUserOrNull() ?: throw IllegalStateException(
                    "Unable to get newly created user."
                )

            val profile = Profile(
                userId = user.id,
                name = fullName.trim(),
                email = normalizedEmail,
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

            // Insert SharePlate profile
            SupabaseProvider.client.from("users").insert(profile)

            Result.success(profile)

        } catch (e: Throwable) {

            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Profile> {
        return try {
            SupabaseProvider.client.auth.signInWith(Email) {
                this.email = email.trim().lowercase()
                this.password = password
            }

            val user = SupabaseProvider.client.auth.currentUserOrNull()
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
            SupabaseProvider.client.auth.resetPasswordForEmail(email, redirectUrl = redirectTo)
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentProfile(): Profile? {
        val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id ?: return null
        return fetchProfile(userId)
    }

    suspend fun updateProfile(profile: Profile): Result<Profile> {
        return try {
            SupabaseProvider.client.from("users").upsert(profile)
            Result.success(profile)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            SupabaseProvider.client.auth.updateUser {
                password = newPassword
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        SupabaseProvider.client.auth.signOut()
    }

    private suspend fun fetchProfile(userId: String): Profile? =
        SupabaseProvider.client.from("users")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Profile>()
            .firstOrNull()
}
