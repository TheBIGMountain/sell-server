package com.dqpi.takeout.controller

import com.dqpi.takeout.service.CategoryService
import com.dqpi.takeout.service.ProductInfoService
import com.dqpi.takeout.utils.toCategoryVo
import com.dqpi.takeout.utils.toResultVo
import com.dqpi.takeout.utils.toServerResponse
import com.dqpi.takeout.vo.ProductVo
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


@RestController
class ProductController(
  @Value("#{@productInfoServiceImpl}")
  private val productInfoService: ProductInfoService,
  @Value("#{@categoryServiceImpl}")
  private val categoryService: CategoryService,
) {

  fun list(req: ServerRequest): Mono<ServerResponse> {
    // key -> 商品类目类型, value -> 该类目下的返回前端的商品集合
    val map = ConcurrentHashMap<Int, CopyOnWriteArrayList<ProductVo>>()
    // 查询在售的所有商品
    return productInfoService.findAllOnSell()
      // 并行处理
      .parallel()
      // 运行在并行线程池中
      .runOn(Schedulers.parallel())
      // 创建每个该类目下的商品集合
      .doOnNext { it.categoryType.let { t -> if (map[t] == null) map[t] = CopyOnWriteArrayList() } }
      // 添加到相应类型下的商品集合中
      .doOnNext { map[it.categoryType]!!.add(it) }
      // 等待结果
      .then().then(Unit.toMono())
      // 查询数据库类目信息
      .thenMany(categoryService.findAll().filter { map.keys.contains(it.categoryType) })
      // 映射为前端所需的目录对象
      .map { it.toCategoryVo(map[it.categoryType]!!) }
      // 转换相应数据
      .collectList().toResultVo().toServerResponse()
  }
}




