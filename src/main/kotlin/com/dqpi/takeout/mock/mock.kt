package com.dqpi.takeout.mock

import com.dqpi.takeout.entity.*
import com.dqpi.takeout.repository.*
import com.dqpi.takeout.utils.randomOrderNo
import reactor.kotlin.core.publisher.toFlux
import java.util.concurrent.ThreadLocalRandom

fun CategoryRepository.saveAll() {
  val categoryNames = arrayOf(
    "热销榜", "单人精彩套餐", "冰爽饮品限时特惠", "精选热菜", "爽口凉菜",
    "精选套餐", "果拼果汁", "小吃主食", "特色粥品"
  )
  mutableListOf<Category>().apply {
    repeat(9) {
      add(Category(
        categoryName = categoryNames[it],
        categoryType = it
      ))
    }
  }.toFlux().let { saveAll(it).subscribe() }
}

fun SellerInfoRepository.saveAll(openIds: Array<String>) {
  openIds.map { Seller(username = "无名氏", password = "123456", openId = it) }
    .toFlux()
    .let { saveAll(it).subscribe() }
}

private val orderNos = mutableListOf<String>().apply {
  repeat(30) { add(randomOrderNo()) }
}.toTypedArray()

fun OrderDetailRepository.saveAll() {
  mutableListOf<OrderDetail>().apply {
    repeat(30) {
      add(OrderDetail(
        orderNo = orderNos[it],
        productId = it + 1,
        productQuantity = 1,
        productName = products[it + 1].name
      ))
    }
  }.toFlux().let { saveAll(it).subscribe() }
}

fun RatingRepository.saveAll() {
  val usernames = arrayOf("3******c", "2******3", "3******b")
  val rateTimes = arrayOf("1469281964000", "1469271264000", "1469261964000")
  val rateTypes = arrayOf(0, 0, 1)
  val texts = arrayOf("很好喝的粥", "", "")
  mutableListOf<Rating>().apply {
    repeat(3) {
      add(Rating(
        productId = 1,
        username = usernames[it],
        rateTime = rateTimes[it],
        rateType = rateTypes[it],
        text = texts[it],
        avatar = "http://static.galileo.xiaojukeji.com/static/tms/default_header.png",
      ))
    }
  }.toFlux().let { saveAll(it).subscribe() }
}

fun OrderRepository.saveAll() {
  val buyerNames = arrayOf(
    "刘一", "钱二", "张三", "李四", "王五", "赵六", "陈七", "周八", "吴九", "郑十",
  )
  mutableListOf<Order>().apply {
    repeat(30) {
      add(Order(
        orderNo = orderNos[it],
        buyerName = buyerNames[ThreadLocalRandom.current().nextInt(0, 10)],
        buyerAddress = "东北石油大学",
        buyerPhone = "12345678910",
        buyerOpenId = "oXQzq6np0N6CrjHBHxfz7diwv5-w",
        amount = 0.01.toBigDecimal()
      ))
    }
  }.toFlux().let { saveAll(it).subscribe() }
}

fun ProductInfoRepository.saveAll() {
  products.toFlux().let { saveAll(it).subscribe() }
}

private val products = arrayOf(
  ProductInfo(
    name = "皮蛋瘦肉粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/c/cd/c12745ed8a5171e13b427dbc39401jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 229,
    rating = 100
  ),
  ProductInfo(
    name = "扁豆焖面",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/c/6b/29e3d29b0db63d36f7c500bca31d8jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 188,
    rating = 96
  ),
  ProductInfo(
    name = "葱花饼",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/f/28/a51e7b18751bcdf871648a23fd3b4jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 124,
    rating = 85
  ),
  ProductInfo(
    name = "牛肉馅饼",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/d/b9/bcab0e8ad97758e65ae5a62b2664ejpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 114,
    rating = 91
  ),
  ProductInfo(
    name = "招牌猪肉白菜锅贴",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/7/72/9a580c1462ca1e4d3c07e112bc035jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 101,
    rating = 78
  ),
  ProductInfo(
    name = "南瓜粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/8/a6/453f65f16b1391942af11511b7a90jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 91,
    rating = 100
  ),
  ProductInfo(
    name = "红豆薏米美肤粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/d/22/260bd78ee6ac6051136c5447fe307jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 86,
    rating = 100
  ),
  ProductInfo(
    name = "八宝酱菜",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/9/b5/469d8854f9a3a03797933fd01398bjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 84,
    rating = 100
  ),
  ProductInfo(
    name = "红枣山药糙米粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/9/b5/469d8854f9a3a03797933fd01398bjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 81,
    rating = 91
  ),
  ProductInfo(
    name = "糊塌子",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/0/05/097a2a59fd2a2292d08067e16380cjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 0,
    status = 0,
    sellCount = 80,
    rating = 93
  ),
  ProductInfo(
    name = "红枣山药粥套餐",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/6/72/cb844f0bb60c502c6d5c05e0bddf5jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 1,
    status = 0,
    sellCount = 17,
    rating = 100
  ),
  ProductInfo(
    name = "VC无限橙果汁",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/e/c6/f348e811772016ae24e968238bcbfjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 2,
    status = 0,
    sellCount = 15,
    rating = 100
  ),
  ProductInfo(
    name = "娃娃菜炖豆腐",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/d/2d/b1eb45b305635d9dd04ddf157165fjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 3,
    status = 0,
    sellCount = 43,
    rating = 92
  ),
  ProductInfo(
    name = "手撕包菜",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/9/c6/f3bc84468820121112e79583c24efjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 3,
    status = 0,
    sellCount = 29,
    rating = 100
  ),
  ProductInfo(
    name = "香酥黄金鱼/3条",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/4/e7/8277a6a2ea0a2e97710290499fc41jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 3,
    status = 0,
    sellCount = 15,
    rating = 100
  ),
  ProductInfo(
    name = "八宝酱菜",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/9/b5/469d8854f9a3a03797933fd01398bjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 4,
    status = 0,
    sellCount = 84,
    rating = 100
  ),
  ProductInfo(
    name = "拍黄瓜",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/6/54/f654985b4e185f06eb07f8fa2b2e8jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 4,
    status = 0,
    sellCount = 28,
    rating = 100
  ),
  ProductInfo(
    name = "红豆薏米粥套餐",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/f/49/27f26ed00c025b2200a9ccbb7e67ejpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 5,
    status = 0,
    sellCount = 3,
    rating = 100
  ),
  ProductInfo(
    name = "皮蛋瘦肉粥套餐",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/8/96/f444a8087f0e940ef264617f9d98ajpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 5,
    status = 0,
    sellCount = 12,
    rating = 100
  ),
  ProductInfo(
    name = "蜜瓜圣女萝莉杯",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/b/5f/b3b04c259d5ec9fa52e1856ee50dajpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 6,
    status = 0,
    sellCount = 1,
    rating = 100
  ),
  ProductInfo(
    name = "加多宝",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/b/9f/5e6c99c593cf65229225c5661bcdejpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 6,
    status = 0,
    sellCount = 7,
    rating = 100
  ),
  ProductInfo(
    name = "VC无限橙果汁",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/e/c6/f348e811772016ae24e968238bcbfjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 6,
    status = 0,
    sellCount = 15,
    rating = 100
  ),
  ProductInfo(
    name = "扁豆焖面",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/c/6b/29e3d29b0db63d36f7c500bca31d8jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 7,
    status = 0,
    sellCount = 188,
    rating = 96
  ),
  ProductInfo(
    name = "葱花饼",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/f/28/a51e7b18751bcdf871648a23fd3b4jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 7,
    status = 0,
    sellCount = 124,
    rating = 85
  ),
  ProductInfo(
    name = "牛肉馅饼",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/d/b9/bcab0e8ad97758e65ae5a62b2664ejpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 7,
    status = 0,
    sellCount = 114,
    rating = 91
  ),
  ProductInfo(
    name = "招牌猪肉白菜锅贴/10个",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/7/72/9a580c1462ca1e4d3c07e112bc035jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 7,
    status = 0,
    sellCount = 101,
    rating = 78
  ),
  ProductInfo(
    name = "糊塌子",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/0/05/097a2a59fd2a2292d08067e16380cjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 7,
    status = 0,
    sellCount = 80,
    rating = 93
  ),
  ProductInfo(
    name = "皮蛋瘦肉粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/c/cd/c12745ed8a5171e13b427dbc39401jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 8,
    status = 0,
    sellCount = 229,
    rating = 100
  ),
  ProductInfo(
    name = "南瓜粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/8/a6/453f65f16b1391942af11511b7a90jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 8,
    status = 0,
    sellCount = 91,
    rating = 100
  ),
  ProductInfo(
    name = "红豆薏米美肤粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/d/22/260bd78ee6ac6051136c5447fe307jpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 8,
    status = 0,
    sellCount = 86,
    rating = 100
  ),
  ProductInfo(
    name = "红枣山药糙米粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/9/b5/469d8854f9a3a03797933fd01398bjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 8,
    status = 0,
    sellCount = 81,
    rating = 91
  ),
  ProductInfo(
    name = "鲜蔬菌菇粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/e/a3/5317c68dd618929b6ac05804e429ajpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 8,
    status = 0,
    sellCount = 56,
    rating = 100
  ),
  ProductInfo(
    name = "田园蔬菜粥",
    price = 0.01.toBigDecimal(),
    description = "有待填写",
    stock = 100,
    icon = "http://fuss10.elemecdn.com/a/94/7371083792c19df00e546b29e344cjpeg.jpeg?imageView2/1/w/114/h/114",
    categoryType = 8,
    status = 0,
    sellCount = 33,
    rating = 100
  ),
)