package pl.edu.pja.plantare.model.service.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.edu.pja.plantare.model.service.AccountService
import pl.edu.pja.plantare.model.service.ConfigurationService
import pl.edu.pja.plantare.model.service.LogService
import pl.edu.pja.plantare.model.service.StorageService
import pl.edu.pja.plantare.model.service.impl.AccountServiceImpl
import pl.edu.pja.plantare.model.service.impl.ConfigurationServiceImpl
import pl.edu.pja.plantare.model.service.impl.LogServiceImpl
import pl.edu.pja.plantare.model.service.impl.StorageServiceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
  @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

  @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService

  @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

  @Binds abstract fun provideConfigurationService(impl: ConfigurationServiceImpl): ConfigurationService
}
