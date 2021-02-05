package com.dqpi.takeout

import com.dqpi.takeout.config.WeChatConfig
import com.dqpi.takeout.mock.saveAll
import com.dqpi.takeout.repository.*
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.core.DatabaseClient
import redis.embedded.RedisServer
import javax.annotation.PreDestroy

@EnableConfigurationProperties(WeChatConfig::class)
@SpringBootApplication
class TakeoutApplication {

  private val redisServer = RedisServer()

  @Bean
  fun initDatabase(databaseClient: DatabaseClient,
                   productInfoRepository: ProductInfoRepository,
                   categoryRepository: CategoryRepository,
                   orderRepository: OrderRepository,
                   orderDetailRepository: OrderDetailRepository,
                   sellerInfoRepository: SellerInfoRepository,
                   ratingRepository: RatingRepository,
                   weChatConfig: WeChatConfig)
  = ApplicationRunner {
    // 创建数据库表
    ClassPathResource("sql.txt").inputStream.use {
      val sql = it.readAllBytes().toString(Charsets.UTF_8)
      databaseClient.sql(sql).then().subscribe()
    }
    // 添加mock数据
    productInfoRepository.saveAll()
    categoryRepository.saveAll()
    orderRepository.saveAll()
    orderDetailRepository.saveAll()
    sellerInfoRepository.saveAll(weChatConfig.openIds)
    ratingRepository.saveAll()
    // 启动内嵌redis
    redisServer.start()
  }

  @PreDestroy
  fun destroy() { redisServer.stop() }
}


fun main(args: Array<String>) {
  runApplication<TakeoutApplication>(*args)
}
