package com.dqpi.takeout.form

import com.dqpi.takeout.dto.CartDTO

class OrderForm(
  val name: String,
  val phone: String,
  val address: String,
  val openId: String,
  val cartDTO: CartDTO
)

