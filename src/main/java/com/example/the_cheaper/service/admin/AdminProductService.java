package com.example.the_cheaper.service.admin;

import com.example.the_cheaper.dto.request.admin.*;
import com.example.the_cheaper.dto.response.admin.AdminProductOverviewResponse;
import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.dto.response.admin.AdminProductResponse;
import com.example.the_cheaper.exception.NotImplementedException;
import com.example.the_cheaper.external.Storage.FileStorageService;
import com.example.the_cheaper.mapper.admin.AdminProductMapper;
import com.example.the_cheaper.mapper.admin.AdminProductImageMapper;
import com.example.the_cheaper.repository.*;
import com.example.the_cheaper.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final OptionValueRepository optionValueRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final AdminProductMapper adminProductMapper;
    private final AdminProductImageMapper adminProductImageMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    public AdminProductResponse createProduct(AdminProductCreateRequest request, List<MultipartFile> files, AccountEntity currentUser) {
        ProductEntity product = adminProductMapper.toEntity(request);

        setBasicCreateInfo(product, request);

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            createVariant(product, request.getVariants());
        }

        fileStorageService.init();
        createImage(product, files);

        ProductEntity savedProduct = productRepository.save(product);

        if (product.getVariants() != null) {
            productVariantRepository.saveAll(product.getVariants());
        }
        if (product.getImages() != null) {
            productImageRepository.saveAll(product.getImages());
        }

        return adminProductMapper.toDetailResponse(savedProduct);
    }

    public String createSku(Long categoryId, Long brandId, Long materialId) {
        StringBuilder sb = new StringBuilder();
        sb.append(categoryId.toString());
        sb.append("-");
        sb.append(brandId.toString());
        sb.append("-");
        sb.append(materialId.toString());
        sb.append("-");
        sb.append(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return sb.toString();
    }

    public void setBasicCreateInfo(ProductEntity product, AdminProductCreateRequest request) {
        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found")));
        }
        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }
        if (request.getMaterialId() != null) {
            product.setMaterial(materialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found")));
        }
    }

    @Transactional(readOnly = true)
    public Page<AdminProductOverviewResponse> listProducts(AccountEntity currentUser,
                                                           AdminProductFilterRequest request) {
        return productRepository.findProductsByAdminFilter(
                            request.getBrandId(),
                            request.getCategoryId(),
                            request.getMaterialId(),
                            request.getStatus(),
                            request.getSortBy(),
                            PageRequest.of(request.getPage() - 1, request.getLimit())
                        )
                .map(adminProductMapper::toOverviewResponse);
    }

    @Transactional
    public Page<AdminProductOverviewResponse> searchProductsByName(String name, AccountEntity currentUser, int page, int limit) {
        return productRepository.findActiveProductsByNameContainingIgnoreCase(name, PageRequest.of(page - 1, limit))
                .map(adminProductMapper::toOverviewResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdminProductOverviewResponse> listProductsOfFilter(AdminProductFilterRequest request, AccountEntity currentUser) {
        throw new NotImplementedException("chưa triển khai lấy sản phẩm theo danh mục");
    }

    @Transactional(readOnly = true)
    public AdminProductResponse getProductDetail(Long id, AccountEntity currentUser) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return adminProductMapper.toDetailResponse(product);
    }

    @Transactional
    public AdminProductResponse updateProduct(Long id, AdminProductUpdateRequest request, List<MultipartFile> files, AccountEntity currentUser) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        setBasicUpdateInfo(product, request);

        if (request.getVariantUpdates() != null && !request.getVariantUpdates().isEmpty()) {
            updateVariant(product, request.getVariantUpdates());
        }

        if (request.getVariantCreates() != null && !request.getVariantCreates().isEmpty()) {
            createVariant(product, request.getVariantCreates());
        }

        if (request.getVariantDeletes() != null && !request.getVariantDeletes().isEmpty()) {
            deleteVariant(product, request.getVariantDeletes());
        }

        fileStorageService.init();
        createImage(product, files);

        if (request.getImageDeletes() != null && !request.getImageDeletes().isEmpty()) {
            deleteImage(product, request.getImageDeletes());
        }

        return adminProductMapper.toDetailResponse(productRepository.save(product));
    }

    public void setBasicUpdateInfo(ProductEntity product, AdminProductUpdateRequest request) {
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getSalePrice() != null) {
            product.setSalePrice(request.getSalePrice());
        }
        if (request.getComparePrice() != null) {
            product.setComparePrice(request.getComparePrice());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getStatus() != null && request.getStatus() != product.getStatus()) {
            product.setStatus(request.getStatus());
        }
        if (!product.getBrand().getId().equals(request.getBrandId())) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found")));
        }
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }
        if (!product.getMaterial().getId().equals(request.getMaterialId())) {
            product.setMaterial(materialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found")));
        }
    }

    public void updateVariant(ProductEntity product, List<AdminVariantUpdateRequest> variantRequests) {
        for (AdminVariantUpdateRequest variantRequest : variantRequests) {
            ProductVariantEntity variant = productVariantRepository.findById(variantRequest.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
            if (variantRequest.getOverrideSalePrice() != null) {
                variant.setOverridePrice(variantRequest.getOverrideSalePrice());
            }
            if (variantRequest.getStock() != null) {
                variant.setStock(variantRequest.getStock());
            }
            if (variantRequest.getOptionValueAdds() != null && !variantRequest.getOptionValueAdds().isEmpty()) {
                List<OptionValueEntity> optionValues = optionValueRepository.findAllById(variantRequest.getOptionValueSubs());
                variant.getOptionValues().addAll(optionValues);
            }
            if (variantRequest.getOptionValueSubs() != null && !variantRequest.getOptionValueSubs().isEmpty()) {
                List<OptionValueEntity> optionValues = optionValueRepository.findAllById(variantRequest.getOptionValueSubs());
                variant.getOptionValues().removeAll(optionValues);
            }
            productVariantRepository.save(variant);
        }
    }

    public void createVariant(ProductEntity product, List<AdminVariantCreateRequest> variantCreateRequests) {
        if (variantCreateRequests == null || variantCreateRequests.isEmpty()) {
            return;
        }
        List<ProductVariantEntity> newVariants = variantCreateRequests.stream()
                .filter(vReq -> vReq != null && vReq.getOptionValueIds() != null && !vReq.getOptionValueIds().isEmpty())
                .map(vReq -> {
                    List<OptionValueEntity> optionValues = optionValueRepository.findAllById(vReq.getOptionValueIds());
                    String sku = createSku(product.getCategory().getId(), product.getBrand().getId(), product.getMaterial().getId());

                    ProductVariantEntity variant = adminProductMapper.toEntity(sku, vReq, optionValues);
                    variant.setProduct(product);
                    return variant;
                }).toList();

        product.getVariants().addAll(newVariants);
        productVariantRepository.saveAll(newVariants);
    }

    public void deleteVariant(ProductEntity product, List<Long> variantIdDeletes) {
        if (variantIdDeletes != null && !variantIdDeletes.isEmpty()) {
            List<ProductVariantEntity> variantsToDelete = product.getVariants().stream()
                    .filter(v -> variantIdDeletes.contains(v.getId()))
                    .toList();

            product.getVariants().removeAll(variantsToDelete);
            productVariantRepository.deleteAll(variantsToDelete);
        }
    }

    public void createImage(ProductEntity product, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        List<MultipartFile> validFiles = files.stream().filter(f -> !f.isEmpty()).toList();
        if (validFiles.isEmpty()) {
            return;
        }

        System.out.println("===Received valid files: " + validFiles.size() + " for product ID: " + product.getId());

        List<ProductImageEntity> images = new ArrayList<>();

        for (MultipartFile file : validFiles) {
            String filename = fileStorageService.store(file);
            ProductImageEntity image = adminProductImageMapper.toEntity(filename);
            image.setProduct(product);
            images.add(image);
        }

        product.getImages().addAll(images);
        if (product.getId() != null) {
            productImageRepository.saveAll(images);
        }
    }

    public void deleteImage(ProductEntity product, List<Long> imageIdDeletes) {
        if (imageIdDeletes != null && !imageIdDeletes.isEmpty()) {
            List<ProductImageEntity> imagesToDelete = product.getImages().stream()
                    .filter(i -> imageIdDeletes.contains(i.getId()))
                    .toList();

            product.getImages().removeAll(imagesToDelete);
            productImageRepository.deleteAll(imagesToDelete);
            for (ProductImageEntity image : imagesToDelete) {
                fileStorageService.delete(image.getName());
            }
        }
    }

    @Transactional
    public void deleteProduct(Long id, AccountEntity currentUser) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateProductStatus(Long id, int status, AccountEntity currentUser) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setStatus(status);
        productRepository.save(product);
    }
}
