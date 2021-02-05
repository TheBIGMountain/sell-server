package com.dqpi.takeout.service.impl

import com.dqpi.takeout.entity.Category
import com.dqpi.takeout.repository.CategoryRepository
import com.dqpi.takeout.service.CategoryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CategoryServiceImpl(private val categoryRepository: CategoryRepository): CategoryService {
  override fun findOne(categoryId: Int): Mono<Category> {
    return categoryRepository.findById(categoryId)
  }

  override fun findAll(): Flux<Category> {
    return categoryRepository.findAll()
  }

  override fun save(category: Category): Mono<Category> {
    return categoryRepository.save(category)
  }
}
