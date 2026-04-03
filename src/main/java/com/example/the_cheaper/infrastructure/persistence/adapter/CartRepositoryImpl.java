package com.example.the_cheaper.infrastructure.persistence.adapter;

import com.example.the_cheaper.domain.model.Cart;
import com.example.the_cheaper.domain.repository.CartRepository;
import com.example.the_cheaper.infrastructure.persistence.entity.CartEntity;
import com.example.the_cheaper.infrastructure.persistence.mapper.CartPersistenceMapper;
import com.example.the_cheaper.infrastructure.persistence.repository.JpaCartRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CartRepositoryImpl implements CartRepository {

    private final JpaCartRepository jpaCartRepository;
    private final CartPersistenceMapper cartPersistenceMapper;

    public CartRepositoryImpl(
            JpaCartRepository jpaCartRepository,
            CartPersistenceMapper cartPersistenceMapper
    ) {
        this.jpaCartRepository = jpaCartRepository;
        this.cartPersistenceMapper = cartPersistenceMapper;
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity cartEntity = cartPersistenceMapper.toEntity(cart);
        CartEntity savedEntity = jpaCartRepository.save(cartEntity);
        return cartPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return jpaCartRepository.findById(id)
                .map(cartPersistenceMapper::toDomain);
    }

    @Override
    public List<Cart> findAll() {
        return jpaCartRepository.findAll().stream()
                .map(cartPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaCartRepository.deleteById(id);
    }

    @Override
    public void delete(Cart cart) {
        jpaCartRepository.delete(cartPersistenceMapper.toEntity(cart));
    }

    @Override
    public Optional<Cart> findByAccountId(Long accountId) {
        return jpaCartRepository.findByAccountId(accountId)
                .map(cartPersistenceMapper::toDomain);
    }

}
