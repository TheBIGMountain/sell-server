package com.dqpi.takeout.router

import com.dqpi.takeout.controller.*
import com.dqpi.takeout.utils.toResultVo
import com.dqpi.takeout.utils.toServerResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.reactive.function.server.router
import reactor.kotlin.core.publisher.toMono

@Configuration
class Router(private val redisTemplate: ReactiveRedisTemplate<String, String>) {

  @Bean
  fun productRouter(productController: ProductController,
                    orderController: OrderController,
                    mockController: MockController) = router {
    "/buyer".nest {
      "/product".nest {
        GET("/list", productController::list)
      }
      "/order".nest {
        POST("/create", orderController::create)
        GET("/list", orderController::list)
        GET("/detail", orderController::detail)
        POST("/cancel", orderController::cancel)
      }
      GET("/sellerInfo", mockController::getSellerInfo)
      GET("/ratings", mockController::getRatings)
    }
  }

  @Bean
  fun weChatRouter(weChatController: WeChatController) = router {
    "/wechat".nest {
      GET("/auth", weChatController::auth)
      GET("/userInfo", weChatController::userInfo)
      GET("/qrAuth", weChatController::qrAuth)
      GET("/qrUserInfo", weChatController::qrUserInfo)
    }
  }

  @Bean
  fun sellerRouter(sellerController: SellerController) = router {
    GET("/seller/login", sellerController::login)
    "/seller".nest {
      GET("/logout", sellerController::logout)
      "/order".nest {
        GET("/list", sellerController::orderList)
        PUT("/cancel", sellerController::orderCancel)
        GET("/detail", sellerController::orderDetail)
        PUT("/finish", sellerController::orderFinish)
      }
      "/product".nest {
        GET("/list", sellerController::productList)
        PUT("/onSell", sellerController::productOnSell)
        PUT("/notSell", sellerController::productNotSell)
        GET("/detail", sellerController::productInfo)
        POST("/save", sellerController::productSave)
      }
      "/category".nest {
        GET("/list", sellerController::categoryList)
      }

      filter { req, next ->
        req.cookies().getFirst("token").toMono()
          .flatMap { redisTemplate.opsForValue().get(it.value) }
          .flatMap { next(req) }
          .switchIfEmpty("用户未登录".toMono().toResultVo().toServerResponse())
      }
    }
  }

  @Bean
  fun payRouter(payController: PayController) = router {
    "/pay".nest {
      GET("/create", payController::create)
      POST("/notify", payController::notify)
    }
  }
}

