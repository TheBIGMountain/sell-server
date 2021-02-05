package com.dqpi.takeout.service.impl

import com.dqpi.takeout.entity.Seller
import com.dqpi.takeout.repository.SellerInfoRepository
import com.dqpi.takeout.service.SellerService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono



@Service
class SellerServiceImpl(private val sellerInfoRepository: SellerInfoRepository): SellerService {
  override fun findByOpenId(openId: String): Mono<Seller> {
    return sellerInfoRepository.findByOpenIdIs(openId)
  }
}