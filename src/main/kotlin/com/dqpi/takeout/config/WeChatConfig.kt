package com.dqpi.takeout.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("wechat")
class WeChatConfig(
  val appId: String,
  val mchId: String,
  val mchKey: String,
  val notifyUrl: String,
  val myAppId: String,
  val myAppSecret: String,
  val buyerRedirect: String,
  val buyerHome: String,
  val sellerRedirect: String,
  val sellerHome: String,
  val openId: String,
  val openAppId: String,
  val openAppSecret: String,
  val templateId: String,
  val openIds: Array<String>
)