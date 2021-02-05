package com.dqpi.takeout.utils


import com.dqpi.takeout.entity.Category
import com.dqpi.takeout.vo.CategoryVo
import com.dqpi.takeout.vo.PageInfo
import com.dqpi.takeout.vo.ProductVo
import com.dqpi.takeout.vo.ResultVo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.ThreadLocalRandom

fun randomOrderNo() = "${System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(900000) + 100000}"

fun <E> Collection<E>.toPageInfo(pageNum: Int, pageSize: Int, totalCount: Int) = PageInfo(pageNum, pageSize, totalCount, this)

fun Category.toCategoryVo(products: List<ProductVo>) = CategoryVo(categoryName, categoryType, products)

fun <T> Mono<T>.toResultVo() = map { ResultVo(0, "成功", it) }

fun <T> Mono<ResultVo<T>>.toServerResponse() = flatMap { ServerResponse.ok().bodyValue(it) }

fun <T> Mono<T>.transition(operator: TransactionalOperator) = operator.transactional(this)

class LocalDateTimeToTimeStamp: JsonSerializer<LocalDateTime>() {
  override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider?) {
    gen.writeNumber(value.toInstant(ZoneOffset.ofHours(8)).toEpochMilli())
  }
}

class TimeStampToLocalDateTime: JsonDeserializer<LocalDateTime>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
    return if (p.currentName == "createTime")
      LocalDateTime.ofInstant(Instant.ofEpochMilli(p.valueAsString.toLong()), ZoneId.systemDefault())
    else LocalDateTime.now()
  }
}





