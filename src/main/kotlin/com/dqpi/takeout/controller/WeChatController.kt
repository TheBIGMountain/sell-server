package com.dqpi.takeout.controller

import com.dqpi.takeout.config.WeChatConfig
import me.chanjar.weixin.common.api.WxConsts.OAUTH2_SCOPE_BASE
import me.chanjar.weixin.common.api.WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN
import me.chanjar.weixin.mp.api.WxMpService
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.net.URI

@RestController
class WeChatController(
  private val wxMpService: WxMpService,
  private val wxOpenService: WxMpService,
  private val weChatConfig: WeChatConfig
) {

  fun auth(req: ServerRequest): Mono<ServerResponse> {
    return wxMpService.oauth2buildAuthorizationUrl(weChatConfig.buyerRedirect, OAUTH2_SCOPE_BASE, "").toMono()
      .flatMap { ServerResponse.permanentRedirect(URI.create(it)).build() }
  }

  fun userInfo(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("code"))
      .map { wxMpService.oauth2getAccessToken(it) }
      .map { URI.create("${weChatConfig.buyerHome}/?openId=${it.openId}") }
      .flatMap { ServerResponse.temporaryRedirect(it).build() }
  }

  fun qrAuth(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("returnUrl"))
      .map { wxOpenService.buildQrConnectUrl(weChatConfig.sellerRedirect, QRCONNECT_SCOPE_SNSAPI_LOGIN, "") }
      .flatMap { ServerResponse.permanentRedirect(URI.create(it)).build() }
  }

  fun qrUserInfo(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("code"))
      .map { wxOpenService.oauth2getAccessToken(it) }
      .map { URI.create("${weChatConfig.sellerHome}/${it.openId}") }
      .flatMap { ServerResponse.temporaryRedirect(it).build() }
  }
}