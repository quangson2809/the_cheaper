package com.example.the_cheaper.application.usecase.account;

import com.example.the_cheaper.application.query.SearchQuery;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.interfaces.rest.dto.response.admin.DashboardResponse;
import com.example.the_cheaper.domain.repository.AccountRepository;
import com.example.the_cheaper.domain.repository.OrderRepository;
import com.example.the_cheaper.domain.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetDashboardUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    public GetDashboardUseCase(OrderRepository orderRepository, ProductRepository productRepository, AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
    }

    public DashboardResponse getDashboardStats() {
        throw new NotImplementedException("Chức năng xem dashboard chưa được triển khai");
    }

    public List<Object> globalSearch(SearchQuery query) {
        throw new NotImplementedException("Chức năng tìm kiếm toàn cục chưa được triển khai");
    }

}


