package pl.edu.pja.plantare.model.service.impl

import pl.edu.pja.plantare.model.service.LogService
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.Firebase
import javax.inject.Inject

class LogServiceImpl @Inject constructor() : LogService {
  override fun logNonFatalCrash(throwable: Throwable) =
    Firebase.crashlytics.recordException(throwable)
}
