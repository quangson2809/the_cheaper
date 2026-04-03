package com.example.the_cheaper.infrastructure.persistence.mapper;

import com.example.the_cheaper.domain.model.*;
import com.example.the_cheaper.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    @Mapping(target = "account", source = "accountId", qualifiedByName = "idToAccountEntity")
    @Mapping(target = "subTotal", source = "subTotal", qualifiedByName = "moneyToBigDecimal")
    @Mapping(target = "finalTotal", source = "finalTotal", qualifiedByName = "moneyToBigDecimal")
    OrderEntity toEntity(Order order);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "subTotal", source = "subTotal", qualifiedByName = "bigDecimalToMoney")
    @Mapping(target = "finalTotal", source = "finalTotal", qualifiedByName = "bigDecimalToMoney")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "order", source = "orderId", qualifiedByName = "idToOrderEntity")
    @Mapping(target = "variant", source = "variantId", qualifiedByName = "idToVariantEntity")
    @Mapping(target = "unitPrice", source = "unitPrice", qualifiedByName = "moneyToBigDecimal")
    OrderItemEntity toEntity(OrderItem item);

    @Mapping(target = "variantId", source = "variant.id")
    @Mapping(target = "unitPrice", source = "unitPrice", qualifiedByName = "bigDecimalToMoney")
    OrderItem toDomain(OrderItemEntity entity);

    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal value) {
        return value != null ? new Money(value) : null;
    }

    @Named("idToAccountEntity")
    default AccountEntity idToAccountEntity(Long id) {
        if (id == null) return null;
        AccountEntity entity = new AccountEntity();
        entity.setId(id);
        return entity;
    }

    @Named("idToOrderEntity")
    default OrderEntity idToOrderEntity(Long id) {
        if (id == null) return null;
        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        return entity;
    }

    @Named("idToVariantEntity")
    default ProductVariantEntity idToVariantEntity(Long id) {
        if (id == null) return null;
        ProductVariantEntity entity = new ProductVariantEntity();
        entity.setId(id);
        return entity;
    }
}
