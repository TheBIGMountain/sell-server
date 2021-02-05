package com.dqpi.takeout.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 新订单通知卖家
 */
@Configuration
class Websocket {
  companion object {
    @JvmStatic
    private val sessionSet = CopyOnWriteArraySet<WebSocketSession>()
    @JvmStatic
    fun sendMessage() = sessionSet.forEach {
      it.send(it.textMessage("您有新的订单").toMono()).subscribe()
    }
  }

  @Bean
  fun handlerMapping() = SimpleUrlHandlerMapping().apply {
    order = Ordered.HIGHEST_PRECEDENCE
    urlMap = mapOf("/websocket" to WebSocketHandler { session ->
      sessionSet.add(session)
      session.receive().then()
    })
  }
}



