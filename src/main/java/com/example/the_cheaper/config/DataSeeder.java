package com.example.the_cheaper.config;

import com.example.the_cheaper.entity.*;
import com.example.the_cheaper.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AccountRepository accountRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final OptionAttributeRepository optionAttributeRepository;
    private final OptionValueRepository optionValueRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final MaterialRepository materialRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting database seeding...");

        paymentMethodRepository.saveAll(List.of(
                PaymentMethodEntity.builder().code("COD").name("Thanh toán khi nhận hàng").status(1).build(),
                PaymentMethodEntity.builder().code("MOMO").name("Ví MoMo").status(1).build(),
                PaymentMethodEntity.builder().code("VNPAY").name("VNPay").status(1).build()
        ));

        RoleEntity roleUser = findOrCreateRole(
                Shared.USER_ROLE,
                "Khách hàng sử dụng các chức năng phía client"
        );
        RoleEntity roleAdmin = findOrCreateRole(
                Shared.ADMIN_ROLE,
                "Quản trị viên hệ thống"
        );

        Map<String, PermissionEntity> permissions = seedPermissions();
        seedRolePermissions(roleUser, roleAdmin, permissions);

        String hashedPassword = passwordEncoder.encode("123456");
        List<AccountEntity> accounts = accountRepository.saveAll(List.of(
                AccountEntity.builder().name("Nguyễn Văn An").status(1).email("an.nguyen@gmail.com")
                        .phone("0901234567").passwordHash(hashedPassword).rewardPoint(150).build(),
                AccountEntity.builder().name("Trần Thị Bình").status(1).email("binh.tran@gmail.com")
                        .phone("0912345678").passwordHash(hashedPassword).rewardPoint(320).build(),
                AccountEntity.builder().name("Lê Văn Cường").status(1).email("cuong.le@gmail.com")
                        .phone("0923456789").passwordHash(hashedPassword).rewardPoint(80).build(),
                AccountEntity.builder().name("Phạm Thị Dung").status(1).email("dung.pham@gmail.com")
                        .phone("0934567890").passwordHash(hashedPassword).rewardPoint(500).build(),
                AccountEntity.builder().name("Hoàng Văn Em").status(1).email("em.hoang@gmail.com")
                        .phone("0945678901").passwordHash(hashedPassword).rewardPoint(210).build(),
                AccountEntity.builder().name("Vũ Thị Phương").status(1).email("phuong.vu@gmail.com")
                        .phone("0956789012").passwordHash(hashedPassword).rewardPoint(0).build(),
                AccountEntity.builder().name("Đặng Văn Giang").status(1).email("giang.dang@gmail.com")
                        .phone("0967890123").passwordHash(hashedPassword).rewardPoint(95).build(),
                AccountEntity.builder().name("Bùi Thị Hoa").status(1).email("hoa.bui@gmail.com")
                        .phone("0978901234").passwordHash(hashedPassword).rewardPoint(440).build(),
                AccountEntity.builder().name("Ngô Văn Inh").status(1).email("inh.ngo@gmail.com")
                        .phone("0989012345").passwordHash(hashedPassword).rewardPoint(60).build(),
                AccountEntity.builder().name("Admin Hệ Thống").status(1).email("admin@gmail.com")
                        .phone("0990000000").passwordHash(hashedPassword).rewardPoint(0).build()
        ));

        seedAccountRoles(roleUser, roleAdmin);

        List<BrandEntity> brands = brandRepository.saveAll(List.of(
                BrandEntity.builder().name("Nike").build(),
                BrandEntity.builder().name("Adidas").build(),
                BrandEntity.builder().name("Puma").build(),
                BrandEntity.builder().name("Reebok").build(),
                BrandEntity.builder().name("New Balance").build(),
                BrandEntity.builder().name("Converse").build(),
                BrandEntity.builder().name("Vans").build(),
                BrandEntity.builder().name("Under Armour").build(),
                BrandEntity.builder().name("Fila").build(),
                BrandEntity.builder().name("Lacoste").build()
        ));

        List<CategoryEntity> categories = categoryRepository.saveAll(List.of(
                CategoryEntity.builder().name("Giày Chạy Bộ").build(),
                CategoryEntity.builder().name("Giày Bóng Đá").build(),
                CategoryEntity.builder().name("Giày Bóng Rổ").build(),
                CategoryEntity.builder().name("Giày Thể Thao Thường Ngày").build(),
                CategoryEntity.builder().name("Giày Đi Bộ").build(),
                CategoryEntity.builder().name("Áo Thể Thao").build(),
                CategoryEntity.builder().name("Quần Thể Thao").build(),
                CategoryEntity.builder().name("Phụ Kiện Thể Thao").build(),
                CategoryEntity.builder().name("Giày Leo Núi").build(),
                CategoryEntity.builder().name("Giày Tennis").build()
        ));

        OptionAttributeEntity attrSize = optionAttributeRepository.save(OptionAttributeEntity.builder().name("Kích Cỡ").build());
        OptionAttributeEntity attrColor = optionAttributeRepository.save(OptionAttributeEntity.builder().name("Màu Sắc").build());
        OptionAttributeEntity attrMaterial = optionAttributeRepository.save(OptionAttributeEntity.builder().name("Chất Liệu").build());
        OptionAttributeEntity attrStyle = optionAttributeRepository.save(OptionAttributeEntity.builder().name("Kiểu Dáng").build());
        OptionAttributeEntity attrGender = optionAttributeRepository.save(OptionAttributeEntity.builder().name("Giới Tính").build());
        optionAttributeRepository.save(OptionAttributeEntity.builder().name("Độ Rộng").build());
        OptionAttributeEntity attrSole = optionAttributeRepository.save(OptionAttributeEntity.builder().name("Loại Đế").build());
        optionAttributeRepository.save(OptionAttributeEntity.builder().name("Khóa Cài").build());
        optionAttributeRepository.save(OptionAttributeEntity.builder().name("Phần Mũ Giày").build());
        optionAttributeRepository.save(OptionAttributeEntity.builder().name("Mùa Phù Hợp").build());

        OptionValueEntity size38 = optionValueRepository.save(OptionValueEntity.builder().value("38").optionAttribute(attrSize).build());
        OptionValueEntity size39 = optionValueRepository.save(OptionValueEntity.builder().value("39").optionAttribute(attrSize).build());
        OptionValueEntity size40 = optionValueRepository.save(OptionValueEntity.builder().value("40").optionAttribute(attrSize).build());
        OptionValueEntity size41 = optionValueRepository.save(OptionValueEntity.builder().value("41").optionAttribute(attrSize).build());
        OptionValueEntity size42 = optionValueRepository.save(OptionValueEntity.builder().value("42").optionAttribute(attrSize).build());
        OptionValueEntity colorBlack = optionValueRepository.save(OptionValueEntity.builder().value("Đen").optionAttribute(attrColor).build());
        OptionValueEntity colorWhite = optionValueRepository.save(OptionValueEntity.builder().value("Trắng").optionAttribute(attrColor).build());
        OptionValueEntity colorRed = optionValueRepository.save(OptionValueEntity.builder().value("Đỏ").optionAttribute(attrColor).build());
        OptionValueEntity colorBlue = optionValueRepository.save(OptionValueEntity.builder().value("Xanh Dương").optionAttribute(attrColor).build());
        OptionValueEntity colorGray = optionValueRepository.save(OptionValueEntity.builder().value("Xám").optionAttribute(attrColor).build());
        OptionValueEntity matLeather = optionValueRepository.save(OptionValueEntity.builder().value("Da Thật").optionAttribute(attrMaterial).build());
        OptionValueEntity matMesh = optionValueRepository.save(OptionValueEntity.builder().value("Lưới Thoáng Khí").optionAttribute(attrMaterial).build());
        OptionValueEntity matCanvas = optionValueRepository.save(OptionValueEntity.builder().value("Vải Canvas").optionAttribute(attrMaterial).build());
        OptionValueEntity styleLow = optionValueRepository.save(OptionValueEntity.builder().value("Cổ Thấp").optionAttribute(attrStyle).build());
        OptionValueEntity styleHigh = optionValueRepository.save(OptionValueEntity.builder().value("Cổ Cao").optionAttribute(attrStyle).build());
        optionValueRepository.save(OptionValueEntity.builder().value("Nam").optionAttribute(attrGender).build());
        optionValueRepository.save(OptionValueEntity.builder().value("Nữ").optionAttribute(attrGender).build());
        optionValueRepository.save(OptionValueEntity.builder().value("Unisex").optionAttribute(attrGender).build());
        optionValueRepository.save(OptionValueEntity.builder().value("Đế Cao Su").optionAttribute(attrSole).build());
        optionValueRepository.save(OptionValueEntity.builder().value("Đế Foam").optionAttribute(attrSole).build());

        List<MaterialEntity> materials = materialRepository.saveAll(List.of(
                MaterialEntity.builder().name("Lưới thoáng khí kết hợp da tổng hợp").build(),
                MaterialEntity.builder().name("Primeknit+").build(),
                MaterialEntity.builder().name("Vải dệt và da tổng hợp").build(),
                MaterialEntity.builder().name("Flyknit").build(),
                MaterialEntity.builder().name("Synthetic upper").build(),
                MaterialEntity.builder().name("Vải Canvas 100%").build(),
                MaterialEntity.builder().name("Canvas và Da Lộn").build(),
                MaterialEntity.builder().name("Suede và Mesh").build(),
                MaterialEntity.builder().name("Mesh thoáng khí").build(),
                MaterialEntity.builder().name("Full-grain leather").build()
        ));

        ProductEntity p1 = productRepository.save(ProductEntity.builder().name("Nike Air Max 270")
                .description("Giày chạy bộ cao cấp với đệm Air Max mang lại cảm giác êm ái tuyệt vời.")
                .material(materials.get(0)).comparePrice(new BigDecimal("3500000")).salePrice(new BigDecimal("2990000"))
                .brand(brands.get(0)).category(categories.get(0)).isDeleted(false).build());
        ProductEntity p2 = productRepository.save(ProductEntity.builder().name("Adidas Ultraboost 22")
                .description("Công nghệ Boost tạo ra cảm giác phản hồi năng lượng tuyệt vời cho người chạy bộ.")
                .material(materials.get(1)).comparePrice(new BigDecimal("4200000")).salePrice(new BigDecimal("3800000"))
                .brand(brands.get(1)).category(categories.get(0)).isDeleted(false).build());
        ProductEntity p3 = productRepository.save(ProductEntity.builder().name("Puma RS-X Reinvention")
                .description("Thiết kế retro cách tân với hệ thống đệm RS mạnh mẽ, phong cách đường phố hiện đại.")
                .material(materials.get(2)).comparePrice(new BigDecimal("2800000")).salePrice(new BigDecimal("2300000"))
                .brand(brands.get(2)).category(categories.get(3)).isDeleted(false).build());
        ProductEntity p4 = productRepository.save(ProductEntity.builder().name("Nike Phantom GX Elite FG")
                .description("Giày bóng đá chuyên nghiệp với công nghệ Flyknit ôm sát bàn chân.")
                .material(materials.get(3)).comparePrice(new BigDecimal("6500000")).salePrice(new BigDecimal("5800000"))
                .brand(brands.get(0)).category(categories.get(1)).isDeleted(false).build());
        ProductEntity p5 = productRepository.save(ProductEntity.builder().name("Adidas Dame 8")
                .description("Giày bóng rổ của ngôi sao Damian Lillard với đế Lightstrike Pro siêu nhẹ.")
                .material(materials.get(4)).comparePrice(new BigDecimal("3800000")).salePrice(new BigDecimal("3200000"))
                .brand(brands.get(1)).category(categories.get(2)).isDeleted(false).build());
        ProductEntity p6 = productRepository.save(ProductEntity.builder().name("Converse Chuck Taylor All Star Classic")
                .description("Biểu tượng thời trang mọi thời đại với thiết kế canvas kinh điển không bao giờ lỗi mốt.")
                .material(materials.get(5)).comparePrice(new BigDecimal("1500000")).salePrice(new BigDecimal("1200000"))
                .brand(brands.get(5)).category(categories.get(3)).isDeleted(false).build());
        ProductEntity p7 = productRepository.save(ProductEntity.builder().name("Vans Old Skool Classic")
                .description("Giày skate cổ điển với sọc Side Stripe đặc trưng của thương hiệu Vans.")
                .material(materials.get(6)).comparePrice(new BigDecimal("1800000")).salePrice(new BigDecimal("1500000"))
                .brand(brands.get(6)).category(categories.get(3)).isDeleted(false).build());
        ProductEntity p8 = productRepository.save(ProductEntity.builder().name("New Balance 574 Core")
                .description("Giày thể thao cổ điển thoải mái cho mọi hoạt động hàng ngày với đế ENCAP.")
                .material(materials.get(7)).comparePrice(new BigDecimal("2200000")).salePrice(new BigDecimal("1900000"))
                .brand(brands.get(4)).category(categories.get(4)).isDeleted(false).build());
        ProductEntity p9 = productRepository.save(ProductEntity.builder().name("Under Armour HOVR Sonic 5")
                .description("Giày chạy bộ kết nối công nghệ MapMyRun theo dõi hiệu suất tập luyện.")
                .material(materials.get(8)).comparePrice(new BigDecimal("3000000")).salePrice(new BigDecimal("2600000"))
                .brand(brands.get(7)).category(categories.get(0)).isDeleted(false).build());
        ProductEntity p10 = productRepository.save(ProductEntity.builder().name("Reebok Classic Leather Legacy")
                .description("Phiên bản hiện đại của mẫu giày classic huyền thoại với chất liệu da cao cấp.")
                .material(materials.get(9)).comparePrice(new BigDecimal("2500000")).salePrice(new BigDecimal("2100000"))
                .brand(brands.get(3)).category(categories.get(3)).isDeleted(false).build());

        p1.getImages().add(ProductImageEntity.builder().name("air-max-270-black.jpg").alt("Nike Air Max 270 Đen").product(p1).build());
        p1.getImages().add(ProductImageEntity.builder().name("air-max-270-white.jpg").alt("Nike Air Max 270 Trắng").product(p1).build());
        p2.getImages().add(ProductImageEntity.builder().name("ultraboost-22-main.jpg").alt("Adidas Ultraboost 22").product(p2).build());
        p3.getImages().add(ProductImageEntity.builder().name("puma-rsx-main.jpg").alt("Puma RS-X").product(p3).build());
        p4.getImages().add(ProductImageEntity.builder().name("phantom-gx-main.jpg").alt("Nike Phantom GX Elite FG").product(p4).build());
        p5.getImages().add(ProductImageEntity.builder().name("dame-8-main.jpg").alt("Adidas Dame 8").product(p5).build());
        p6.getImages().add(ProductImageEntity.builder().name("chuck-taylor-main.jpg").alt("Converse Chuck Taylor").product(p6).build());
        p7.getImages().add(ProductImageEntity.builder().name("vans-old-skool-main.jpg").alt("Vans Old Skool").product(p7).build());
        p8.getImages().add(ProductImageEntity.builder().name("nb-574-main.jpg").alt("New Balance 574").product(p8).build());
        p9.getImages().add(ProductImageEntity.builder().name("ua-hovr-main.jpg").alt("Under Armour HOVR Sonic 5").product(p9).build());
        p10.getImages().add(ProductImageEntity.builder().name("reebok-classic-main.jpg").alt("Reebok Classic Leather Legacy").product(p10).build());
        productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));

        log.info("Database seeding completed.");
    }

    private RoleEntity findOrCreateRole(String name, String description) {
        RoleEntity role = roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .name(name)
                        .description(description)
                        .build()));

        if (role.getDescription() == null || role.getDescription().isBlank()) {
            role.setDescription(description);
            role = roleRepository.save(role);
        }

        return role;
    }

    private Map<String, PermissionEntity> seedPermissions() {
        List<PermissionDefinition> definitions = List.of(
                new PermissionDefinition("ACCOUNT_READ", "Xem tài khoản", "Xem danh sách và thông tin tài khoản"),
                new PermissionDefinition("ACCOUNT_CREATE", "Tạo tài khoản", "Tạo tài khoản mới"),
                new PermissionDefinition("ACCOUNT_UPDATE", "Cập nhật tài khoản", "Cập nhật thông tin tài khoản"),
                new PermissionDefinition("ACCOUNT_DELETE", "Xóa tài khoản", "Xóa tài khoản đã ngừng hoạt động"),
                new PermissionDefinition("ACCOUNT_ASSIGN_ROLE", "Gán role cho tài khoản", "Gán hoặc thay đổi role của tài khoản"),
                new PermissionDefinition("ROLE_READ", "Xem role", "Xem danh sách và thông tin role"),
                new PermissionDefinition("ROLE_CREATE", "Tạo role", "Tạo role mới"),
                new PermissionDefinition("ROLE_UPDATE", "Cập nhật role", "Cập nhật thông tin role"),
                new PermissionDefinition("ROLE_DELETE", "Xóa role", "Xóa role"),
                new PermissionDefinition("ROLE_ASSIGN_PERMISSION", "Gán permission cho role", "Gán hoặc thu hồi permission của role"),
                new PermissionDefinition("PERMISSION_READ", "Xem permission", "Xem danh sách và thông tin permission"),
                new PermissionDefinition("PERMISSION_CREATE", "Tạo permission", "Tạo permission mới"),
                new PermissionDefinition("PERMISSION_UPDATE", "Cập nhật permission", "Cập nhật thông tin permission"),
                new PermissionDefinition("PERMISSION_DELETE", "Xóa permission", "Xóa permission"),
                new PermissionDefinition("ORDER_READ", "Xem đơn hàng", "Xem danh sách và chi tiết đơn hàng phía quản trị"),
                new PermissionDefinition("ORDER_UPDATE", "Cập nhật đơn hàng", "Cập nhật trạng thái đơn hàng phía quản trị")
        );

        Map<String, PermissionEntity> result = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(PermissionEntity::getCode, Function.identity()));

        for (PermissionDefinition definition : definitions) {
            result.computeIfAbsent(definition.code(), code -> permissionRepository.save(
                    PermissionEntity.builder()
                            .name(definition.name())
                            .code(code)
                            .description(definition.description())
                            .build()
            ));
        }

        return result;
    }

    private void seedRolePermissions(
            RoleEntity userRole,
            RoleEntity adminRole,
            Map<String, PermissionEntity> permissions
    ) {
        permissions.values().forEach(permission -> ensureRolePermission(adminRole, permission));

        ensureRolePermission(userRole, permissions.get("ACCOUNT_READ"));
        ensureRolePermission(userRole, permissions.get("PRODUCT_READ"));
    }

    private void ensureRolePermission(RoleEntity role, PermissionEntity permission) {
        if (role == null || permission == null) {
            return;
        }

        if (!rolePermissionRepository.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
            rolePermissionRepository.save(RolePermissionEntity.builder()
                    .role(role)
                    .permission(permission)
                    .build());
        }
    }

    private void seedAccountRoles(RoleEntity userRole, RoleEntity adminRole) {
        accountRepository.findByEmail("admin@gmail.com")
                .ifPresent(account -> ensureAccountRole(account, adminRole));

        accountRepository.findAll().stream()
                .filter(account -> !"admin@gmail.com".equalsIgnoreCase(account.getEmail()))
                .forEach(account -> ensureAccountRole(account, userRole));
    }

    private void ensureAccountRole(AccountEntity account, RoleEntity role) {
        if (!accountRoleRepository.existsByAccountIdAndRoleId(account.getId(), role.getId())) {
            accountRoleRepository.save(AccountRoleEntity.builder()
                    .account(account)
                    .role(role)
                    .build());
        }
    }

    private record PermissionDefinition(String code, String name, String description) {
    }
}
