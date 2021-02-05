package com.dqpi.takeout.vo

import com.dqpi.takeout.entity.Rating
import java.math.BigDecimal


class PageInfo<E>(
  val pageNum: Int,
  val pageSize: Int,
  val totalSize: Int,
  val content: Collection<E>
)

class CategoryVo(
  val name: String,
  val type: Int,
  val foods: List<ProductVo>
)

class ProductVo(
  val id: Int,
  val name: String,
  val price: BigDecimal,
  val description: String,
  val icon: String,
  val sellCount: Int,
  val rating: Int,
  val categoryType: Int,
  val ratings: List<Rating>
)

class ResultVo<T>(
  val code: Int,
  val msg: String,
  val data: T
)
