package com.example.the_cheaper.application.usecase.admin;

import com.example.the_cheaper.application.command.CreateBrandCommand;
import com.example.the_cheaper.application.command.UpdateBrandCommand;
import com.example.the_cheaper.domain.exception.NotImplementedException;
import com.example.the_cheaper.domain.model.Brand;
import com.example.the_cheaper.domain.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageBrandUseCase {

    private final BrandRepository brandRepository;

    public ManageBrandUseCase(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Brand createBrand(CreateBrandCommand command) {
        throw new NotImplementedException("Chức năng tạo thương hiệu chưa được triển khai");
    }

    public Brand updateBrand(UpdateBrandCommand command) {
        throw new NotImplementedException("Chức năng cập nhật thương hiệu chưa được triển khai");
    }

    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    public List<Brand> listBrands() {
        return brandRepository.findAll();
    }
}
