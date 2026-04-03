package com.example.the_cheaper.application.usecase.admin;

import com.example.the_cheaper.application.command.CreateProductCommand;
import com.example.the_cheaper.application.command.UpdateProductCommand;
import com.example.the_cheaper.domain.exception.ResourceNotFoundException;
import com.example.the_cheaper.domain.model.Product;
import com.example.the_cheaper.domain.repository.ProductRepository;
import com.example.the_cheaper.domain.service.ProductDomainService;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.exception.ResourceAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ManageProductUseCase {

    private final ProductRepository productRepository;
    private final ProductDomainService productDomainService;

    public ManageProductUseCase(
            ProductRepository productRepository,
            ProductDomainService productDomainService) {
        this.productRepository = productRepository;
        this.productDomainService = productDomainService;
    }

    public Product createProduct(CreateProductCommand command) {
        if (productRepository.existsByName(command.name())) {
            throw new ResourceAlreadyExistsException("Tên sản phẩm đã tồn tại");
        }
        throw new NotImplementedException("Chức năng tạo sản phẩm chưa được triển khai");
    }

    public Product updateProduct(UpdateProductCommand command) {
        throw new NotImplementedException("Chức năng cập nhật sản phẩm chưa được triển khai");
    }

    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sản phẩm không tồn tại");
        }
        productRepository.deleteById(id);
    }

    public Product getProductDetail(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> listProducts(int page, int limit) {
        return productRepository.findAll();
    }
}
