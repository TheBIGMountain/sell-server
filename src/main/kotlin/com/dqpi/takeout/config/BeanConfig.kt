package com.dqpi.takeout.config


import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

@Configuration
class BeanConfig {
  /**
   * 事务处理
   */
  @Bean
  fun transitionOperator(transactionManager: R2dbcTransactionManager)
  = TransactionalOperator.create(transactionManager)

  /**
   * 微信第三方开发库 -> 微信授权
   */
  @Bean
  fun wxMpService(weChatConfig: WeChatConfig) = WxMpServiceImpl().apply {
    wxMpConfigStorage = WxMpInMemoryConfigStorage().apply {
      appId = weChatConfig.myAppId
      secret = weChatConfig.myAppSecret
    }
  }

  /**
   * 微信第三方开发库 -> 微信登录
   */
  @Bean
  fun wxOpenService(weChatConfig: WeChatConfig) = WxMpServiceImpl().apply {
    wxMpConfigStorage = WxMpInMemoryConfigStorage().apply {
      appId = weChatConfig.openAppId
      secret = weChatConfig.openAppSecret
    }
  }
}
