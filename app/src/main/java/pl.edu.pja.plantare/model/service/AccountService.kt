package pl.edu.pja.plantare.model.service

import kotlinx.coroutines.flow.Flow
import pl.edu.pja.plantare.model.User

interface AccountService {
  val currentUserId: String
  val hasUser: Boolean

  val currentUser: Flow<User>

  suspend fun authenticate(email: String, password: String)

  suspend fun sendRecoveryEmail(email: String)

  suspend fun createAnonymousAccount()

  suspend fun linkAccount(email: String, password: String)

  suspend fun deleteAccount()

  suspend fun signOut()
}
