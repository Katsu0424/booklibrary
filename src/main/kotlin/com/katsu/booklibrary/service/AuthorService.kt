package com.katsu.booklibrary.service

import com.katsu.booklibrary.domain.Author
import com.katsu.booklibrary.dto.CreateAuthorRequest
import com.katsu.booklibrary.dto.UpdateAuthorRequest
import com.katsu.booklibrary.exception.NotFoundException
import com.katsu.booklibrary.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
) {
    @Transactional
    fun create(request: CreateAuthorRequest): Author {
        return authorRepository.insert(request.name, request.birthDate)
    }

    @Transactional
    fun update(
        id: Long,
        request: UpdateAuthorRequest,
    ): Author {
        if (authorRepository.findById(id) == null) {
            throw NotFoundException("author $id not found")
        }
        return authorRepository.update(id, request.name, request.birthDate)
    }
}
