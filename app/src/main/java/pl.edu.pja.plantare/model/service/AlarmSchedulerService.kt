package pl.edu.pja.plantare.model.service

import pl.edu.pja.plantare.model.Plant

interface AlarmSchedulerService {
    fun schedule(plant: Plant)
    fun cancel(plant: Plant)
}