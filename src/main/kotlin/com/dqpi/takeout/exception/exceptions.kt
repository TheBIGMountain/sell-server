package com.dqpi.takeout.exception

import com.dqpi.takeout.enums.ResultEnum

class BusinessException(
  resultEnum: ResultEnum
): RuntimeException() {
  override val message: String = resultEnum.msg
}