package pl.edu.pja.plantare.model.service

interface LogService {
  fun logNonFatalCrash(throwable: Throwable)
}
