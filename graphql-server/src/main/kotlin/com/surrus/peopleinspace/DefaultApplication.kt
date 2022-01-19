package com.surrus.peopleinspace

import com.surrus.common.di.initKoin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext


val koin = initKoin(enableNetworkLogs = true).koin

@SpringBootApplication
class DefaultApplication {
}

fun runServer(): ConfigurableApplicationContext {
  return runApplication<DefaultApplication>()
}



