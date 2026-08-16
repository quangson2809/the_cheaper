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

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

        private final RoleRepository roleRepository;
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
                if (roleRepository.count() > 0) {
                        log.info("Database already seeded. Skipping...");
                        return;
                }
                log.info("Starting database seeding...");

                // ===================== PAYMENT METHODS (3 bản ghi) =====================
                paymentMethodRepository.saveAll(List.of(
                                PaymentMethodEntity.builder().code("COD").name("Thanh to\u00e1n khi nh\u1eadn h\u00e0ng").status(1).build(),
                                PaymentMethodEntity.builder().code("MOMO").name("V\u00ed MoMo").status(1).build(),
                                PaymentMethodEntity.builder().code("VNPAY").name("VNPay").status(1).build()));
                log.info("Seeded 3 payment methods");

                // ===================== ROLES (2 bản ghi) =====================
                RoleEntity roleUser = roleRepository.save(RoleEntity.builder().name(Shared.USER_ROLE).build());
                RoleEntity roleAdmin = roleRepository.save(RoleEntity.builder().name(Shared.ADMIN_ROLE).build());
                log.info("Seeded {} roles", roleRepository.count());

                // ===================== ACCOUNTS (10 bản ghi) =====================
                String hashedPassword = passwordEncoder.encode("123456");
                List<AccountEntity> accounts = accountRepository.saveAll(List.of(
                                AccountEntity.builder().name("Nguyễn Văn An").status(1).email("an.nguyen@gmail.com")
                                                .phone("0901234567").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(150).build(),
                                AccountEntity.builder().name("Trần Thị Bình").status(1).email("binh.tran@gmail.com")
                                                .phone("0912345678").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(320).build(),
                                AccountEntity.builder().name("Lê Văn Cường").status(1).email("cuong.le@gmail.com")
                                                .phone("0923456789").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(80).build(),
                                AccountEntity.builder().name("Phạm Thị Dung").status(1).email("dung.pham@gmail.com")
                                                .phone("0934567890").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(500).build(),
                                AccountEntity.builder().name("Hoàng Văn Em").status(1).email("em.hoang@gmail.com")
                                                .phone("0945678901").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(210).build(),
                                AccountEntity.builder().name("Vũ Thị Phương").status(1).email("phuong.vu@gmail.com")
                                                .phone("0956789012").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(0).build(),
                                AccountEntity.builder().name("Đặng Văn Giang").status(1).email("giang.dang@gmail.com")
                                                .phone("0967890123").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(95).build(),
                                AccountEntity.builder().name("Bùi Thị Hoa").status(1).email("hoa.bui@gmail.com")
                                                .phone("0978901234").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(440).build(),
                                AccountEntity.builder().name("Ngô Văn Inh").status(1).email("inh.ngo@gmail.com")
                                                .phone("0989012345").passwordHash(hashedPassword).role(roleUser)
                                                .rewardPoint(60).build(),
                                AccountEntity.builder().name("Admin Hệ Thống").status(1).email("admin@gmail.com")
                                                .phone("0990000000").passwordHash(hashedPassword).role(roleAdmin)
                                                .rewardPoint(0).build()));
                log.info("Seeded {} accounts", accounts.size());

                // ===================== BRANDS (10 bản ghi) =====================
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
                                BrandEntity.builder().name("Lacoste").build()));
                log.info("Seeded {} brands", brands.size());

                // ===================== CATEGORIES (10 bản ghi) =====================
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
                                CategoryEntity.builder().name("Giày Tennis").build()));
                log.info("Seeded {} categories", categories.size());

                // ===================== OPTION ATTRIBUTES (10 bản ghi) =====================
                OptionAttributeEntity attrSize = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Kích Cỡ").build());
                OptionAttributeEntity attrColor = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Màu Sắc").build());
                OptionAttributeEntity attrMaterial = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Chất Liệu").build());
                OptionAttributeEntity attrStyle = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Kiểu Dáng").build());
                OptionAttributeEntity attrGender = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Giới Tính").build());
                OptionAttributeEntity attrWidth = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Độ Rộng").build());
                OptionAttributeEntity attrSole = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Loại Đế").build());
                OptionAttributeEntity attrClosure = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Khóa Cài").build());
                OptionAttributeEntity attrUpper = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Phần Mũ Giày").build());
                OptionAttributeEntity attrSeason = optionAttributeRepository
                                .save(OptionAttributeEntity.builder().name("Mùa Phù Hợp").build());
                log.info("Seeded 10 option attributes");

                // ===================== OPTION VALUES (20 bản ghi) =====================
                OptionValueEntity size38 = optionValueRepository
                                .save(OptionValueEntity.builder().value("38").optionAttribute(attrSize).build());
                OptionValueEntity size39 = optionValueRepository
                                .save(OptionValueEntity.builder().value("39").optionAttribute(attrSize).build());
                OptionValueEntity size40 = optionValueRepository
                                .save(OptionValueEntity.builder().value("40").optionAttribute(attrSize).build());
                OptionValueEntity size41 = optionValueRepository
                                .save(OptionValueEntity.builder().value("41").optionAttribute(attrSize).build());
                OptionValueEntity size42 = optionValueRepository
                                .save(OptionValueEntity.builder().value("42").optionAttribute(attrSize).build());
                OptionValueEntity colorBlack = optionValueRepository
                                .save(OptionValueEntity.builder().value("Đen").optionAttribute(attrColor).build());
                OptionValueEntity colorWhite = optionValueRepository
                                .save(OptionValueEntity.builder().value("Trắng").optionAttribute(attrColor).build());
                OptionValueEntity colorRed = optionValueRepository
                                .save(OptionValueEntity.builder().value("Đỏ").optionAttribute(attrColor).build());
                OptionValueEntity colorBlue = optionValueRepository.save(
                                OptionValueEntity.builder().value("Xanh Dương").optionAttribute(attrColor).build());
                OptionValueEntity colorGray = optionValueRepository
                                .save(OptionValueEntity.builder().value("Xám").optionAttribute(attrColor).build());
                OptionValueEntity matLeather = optionValueRepository.save(
                                OptionValueEntity.builder().value("Da Thật").optionAttribute(attrMaterial).build());
                OptionValueEntity matMesh = optionValueRepository.save(OptionValueEntity.builder()
                                .value("Lưới Thoáng Khí").optionAttribute(attrMaterial).build());
                OptionValueEntity matCanvas = optionValueRepository.save(
                                OptionValueEntity.builder().value("Vải Canvas").optionAttribute(attrMaterial).build());
                OptionValueEntity styleLow = optionValueRepository
                                .save(OptionValueEntity.builder().value("Cổ Thấp").optionAttribute(attrStyle).build());
                OptionValueEntity styleHigh = optionValueRepository
                                .save(OptionValueEntity.builder().value("Cổ Cao").optionAttribute(attrStyle).build());
                OptionValueEntity genderMale = optionValueRepository
                                .save(OptionValueEntity.builder().value("Nam").optionAttribute(attrGender).build());
                OptionValueEntity genderFemale = optionValueRepository
                                .save(OptionValueEntity.builder().value("Nữ").optionAttribute(attrGender).build());
                OptionValueEntity genderUnisex = optionValueRepository
                                .save(OptionValueEntity.builder().value("Unisex").optionAttribute(attrGender).build());
                OptionValueEntity soleRubber = optionValueRepository
                                .save(OptionValueEntity.builder().value("Đế Cao Su").optionAttribute(attrSole).build());
                OptionValueEntity soleFoam = optionValueRepository
                                .save(OptionValueEntity.builder().value("Đế Foam").optionAttribute(attrSole).build());
                log.info("Seeded 20 option values");

                // ===================== MATERIALS (10 bản ghi) =====================
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
                                MaterialEntity.builder().name("Full-grain leather").build()));
                log.info("Seeded 10 materials");

                // ===================== PRODUCTS (10 bản ghi) =====================
                ProductEntity p1 = productRepository.save(ProductEntity.builder()
                                .name("Nike Air Max 270")
                                .description("Giày chạy bộ cao cấp với đệm Air Max mang lại cảm giác êm ái tuyệt vời.")
                                .material(materials.get(0))
                                .comparePrice(new BigDecimal("3500000"))
                                .salePrice(new BigDecimal("2990000"))
                                .brand(brands.get(0))
                                .category(categories.get(0))
                                .isDeleted(false)
                                .build());

                ProductEntity p2 = productRepository.save(ProductEntity.builder()
                                .name("Adidas Ultraboost 22")
                                .description("Công nghệ Boost tạo ra cảm giác phản hồi năng lượng tuyệt vời cho người chạy bộ.")
                                .material(materials.get(1)).comparePrice(new BigDecimal("4200000"))
                                .salePrice(new BigDecimal("3800000"))
                                .brand(brands.get(1)).category(categories.get(0)).isDeleted(false).build());

                ProductEntity p3 = productRepository.save(ProductEntity.builder()
                                .name("Puma RS-X Reinvention")
                                .description("Thiết kế retro cách tân với hệ thống đệm RS mạnh mẽ, phong cách đường phố hiện đại.")
                                .material(materials.get(2)).comparePrice(new BigDecimal("2800000"))
                                .salePrice(new BigDecimal("2300000"))
                                .brand(brands.get(2)).category(categories.get(3)).isDeleted(false).build());

                ProductEntity p4 = productRepository.save(ProductEntity.builder()
                                .name("Nike Phantom GX Elite FG")
                                .description("Giày bóng đá chuyên nghiệp với công nghệ Flyknit ôm sát bàn chân.")
                                .material(materials.get(3)).comparePrice(new BigDecimal("6500000"))
                                .salePrice(new BigDecimal("5800000"))
                                .brand(brands.get(0)).category(categories.get(1)).isDeleted(false).build());

                ProductEntity p5 = productRepository.save(ProductEntity.builder()
                                .name("Adidas Dame 8")
                                .description("Giày bóng rổ của ngôi sao Damian Lillard với đế Lightstrike Pro siêu nhẹ.")
                                .material(materials.get(4)).comparePrice(new BigDecimal("3800000"))
                                .salePrice(new BigDecimal("3200000"))
                                .brand(brands.get(1)).category(categories.get(2)).isDeleted(false).build());

                ProductEntity p6 = productRepository.save(ProductEntity.builder()
                                .name("Converse Chuck Taylor All Star Classic")
                                .description("Biểu tượng thời trang mọi thời đại with thiết kế canvas kinh điển không bao giờ lỗi mốt.")
                                .material(materials.get(5)).comparePrice(new BigDecimal("1500000"))
                                .salePrice(new BigDecimal("1200000"))
                                .brand(brands.get(5)).category(categories.get(3)).isDeleted(false).build());

                ProductEntity p7 = productRepository.save(ProductEntity.builder()
                                .name("Vans Old Skool Classic")
                                .description("Giày skate cổ điển với sọc Side Stripe đặc trưng của thương hiệu Vans.")
                                .material(materials.get(6)).comparePrice(new BigDecimal("1800000"))
                                .salePrice(new BigDecimal("1500000"))
                                .brand(brands.get(6)).category(categories.get(3)).isDeleted(false).build());

                ProductEntity p8 = productRepository.save(ProductEntity.builder()
                                .name("New Balance 574 Core")
                                .description("Giày thể thao cổ điển thoải mái cho mọi hoạt động hàng ngày với đế ENCAP.")
                                .material(materials.get(7)).comparePrice(new BigDecimal("2200000"))
                                .salePrice(new BigDecimal("1900000"))
                                .brand(brands.get(4)).category(categories.get(4)).isDeleted(false).build());

                ProductEntity p9 = productRepository.save(ProductEntity.builder()
                                .name("Under Armour HOVR Sonic 5")
                                .description("Giày chạy bộ kết nối công nghệ MapMyRun theo dõi hiệu suất tập luyện.")
                                .material(materials.get(8)).comparePrice(new BigDecimal("3000000"))
                                .salePrice(new BigDecimal("2600000"))
                                .brand(brands.get(7)).category(categories.get(0)).isDeleted(false).build());

                ProductEntity p10 = productRepository.save(ProductEntity.builder()
                                .name("Reebok Classic Leather Legacy")
                                .description("Phiên bản hiện đại của mẫu giày classic huyền thoại với chất liệu da cao cấp.")
                                .material(materials.get(9)).comparePrice(new BigDecimal("2500000"))
                                .salePrice(new BigDecimal("2100000"))
                                .brand(brands.get(3)).category(categories.get(3)).isDeleted(false).build());

                log.info("Seeded 10 products");

                // ===================== PRODUCT IMAGES (20 bản ghi) =====================
                productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));

                // Lưu images thông qua cascade (thêm vào list của product)
                p1.getImages().add(ProductImageEntity.builder().name("air-max-270-black.jpg")
                                .alt("Nike Air Max 270 Đen").product(p1).build());
                p1.getImages().add(ProductImageEntity.builder().name("air-max-270-white.jpg")
                                .alt("Nike Air Max 270 Trắng").product(p1).build());
                p2.getImages().add(ProductImageEntity.builder().name("ultraboost-22-main.jpg")
                                .alt("Adidas Ultraboost 22").product(p2).build());
                p2.getImages().add(ProductImageEntity.builder().name("ultraboost-22-side.jpg")
                                .alt("Adidas Ultraboost 22 Góc Cạnh").product(p2).build());
                p3.getImages().add(ProductImageEntity.builder().name("rs-x-reinvention.jpg")
                                .alt("Puma RS-X Reinvention").product(p3).build());
                p4.getImages().add(ProductImageEntity.builder().name("phantom-gx-elite.jpg")
                                .alt("Nike Phantom GX Elite FG").product(p4).build());
                p5.getImages().add(ProductImageEntity.builder().name("dame-8-main.jpg").alt("Adidas Dame 8").product(p5)
                                .build());
                p6.getImages().add(ProductImageEntity.builder().name("chuck-taylor-classic.jpg")
                                .alt("Converse Chuck Taylor Classic").product(p6).build());
                p6.getImages().add(ProductImageEntity.builder().name("chuck-taylor-black.jpg")
                                .alt("Converse Chuck Taylor Đen").product(p6).build());
                p7.getImages().add(ProductImageEntity.builder().name("vans-old-skool.jpg").alt("Vans Old Skool Classic")
                                .product(p7).build());
                p7.getImages().add(ProductImageEntity.builder().name("vans-old-skool-navy.jpg")
                                .alt("Vans Old Skool Navy").product(p7).build());
                p8.getImages().add(ProductImageEntity.builder().name("nb-574-core.jpg").alt("New Balance 574 Core")
                                .product(p8).build());
                p9.getImages().add(ProductImageEntity.builder().name("hovr-sonic-5.jpg").alt("UA HOVR Sonic 5")
                                .product(p9).build());
                p9.getImages().add(ProductImageEntity.builder().name("hovr-sonic-5-blue.jpg")
                                .alt("UA HOVR Sonic 5 Xanh").product(p9).build());
                p10.getImages().add(ProductImageEntity.builder().name("reebok-classic-leather.jpg")
                                .alt("Reebok Classic Leather Legacy").product(p10).build());
                productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
                log.info("Seeded product images");

                // ===================== PRODUCT VARIANTS (20 bản ghi) =====================
                ProductVariantEntity v1 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("NIKE-AM270-BLK-40").stock(50).sold(0).overridePrice(null)
                                .product(p1).optionValues(List.of(size40, colorBlack, styleLow)).build());
                ProductVariantEntity v2 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("NIKE-AM270-WHT-41").stock(35).sold(0).overridePrice(null)
                                .product(p1).optionValues(List.of(size41, colorWhite, styleLow)).build());
                ProductVariantEntity v3 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("ADID-UB22-GRY-40").stock(40).sold(0).overridePrice(new BigDecimal("3600000"))
                                .product(p2).optionValues(List.of(size40, colorGray)).build());
                ProductVariantEntity v4 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("ADID-UB22-BLK-42").stock(25).sold(0).overridePrice(null)
                                .product(p2).optionValues(List.of(size42, colorBlack)).build());
                ProductVariantEntity v5 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("PUMA-RSX-RED-39").stock(30).sold(0).overridePrice(new BigDecimal("2100000"))
                                .product(p3).optionValues(List.of(size39, colorRed)).build());
                ProductVariantEntity v6 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("NIKE-PHG-BLK-41").stock(20).sold(0).overridePrice(null)
                                .product(p4).optionValues(List.of(size41, colorBlack)).build());
                ProductVariantEntity v7 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("NIKE-PHG-WHT-42").stock(15).sold(0).overridePrice(null)
                                .product(p4).optionValues(List.of(size42, colorWhite)).build());
                ProductVariantEntity v8 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("ADID-D8-BLU-40").stock(45).sold(0).overridePrice(null)
                                .product(p5).optionValues(List.of(size40, colorBlue)).build());
                ProductVariantEntity v9 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("CONV-CT-BLK-38").stock(60).sold(0).overridePrice(null)
                                .product(p6).optionValues(List.of(size38, colorBlack, styleHigh, matCanvas)).build());
                ProductVariantEntity v10 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("CONV-CT-WHT-39").stock(55).sold(0).overridePrice(null)
                                .product(p6).optionValues(List.of(size39, colorWhite, styleHigh, matCanvas)).build());
                ProductVariantEntity v11 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("VANS-OS-BLK-40").stock(70).sold(0).overridePrice(null)
                                .product(p7).optionValues(List.of(size40, colorBlack, styleLow)).build());
                ProductVariantEntity v12 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("VANS-OS-WHT-41").stock(65).sold(0).overridePrice(null)
                                .product(p7).optionValues(List.of(size41, colorWhite, styleLow)).build());
                ProductVariantEntity v13 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("NB-574-GRY-39").stock(40).sold(0).overridePrice(new BigDecimal("1750000"))
                                .product(p8).optionValues(List.of(size39, colorGray)).build());
                ProductVariantEntity v14 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("NB-574-BLU-42").stock(30).sold(0).overridePrice(null)
                                .product(p8).optionValues(List.of(size42, colorBlue)).build());
                ProductVariantEntity v15 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("UA-HS5-BLK-40").stock(35).sold(0).overridePrice(null)
                                .product(p9).optionValues(List.of(size40, colorBlack, matMesh)).build());
                ProductVariantEntity v16 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("UA-HS5-BLU-41").stock(28).sold(0).overridePrice(new BigDecimal("2400000"))
                                .product(p9).optionValues(List.of(size41, colorBlue, matMesh)).build());
                ProductVariantEntity v17 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("REEB-CL-WHT-38").stock(45).sold(0).overridePrice(null)
                                .product(p10).optionValues(List.of(size38, colorWhite, matLeather)).build());
                ProductVariantEntity v18 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("REEB-CL-BLK-40").stock(38).sold(0).overridePrice(null)
                                .product(p10).optionValues(List.of(size40, colorBlack, matLeather)).build());
                ProductVariantEntity v19 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("PUMA-RSX-BLK-41").stock(22).sold(0).overridePrice(null)
                                .product(p3).optionValues(List.of(size41, colorBlack)).build());
                ProductVariantEntity v20 = productVariantRepository.save(ProductVariantEntity.builder()
                                .sku("ADID-D8-RED-42").stock(18).sold(0).overridePrice(new BigDecimal("3000000"))
                                .product(p5).optionValues(List.of(size42, colorRed)).build());
                log.info("Seeded 20 product variants");

                // ===================== ADDRESSES (10 bản ghi) =====================
                List<AddressEntity> addresses = List.of(
                                AddressEntity.builder().homeNumber("12")
                                        .street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM")
                                                .account(accounts.get(0)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(1)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(2)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(3)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(4)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(5)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(6)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(7)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(8)).build(),
                                AddressEntity.builder().homeNumber("12").street("Trần Hưng Đạo").district("Quận 5").city("TP.HCM").account(accounts.get(0)).build());
                accounts.get(0).getAddresses().addAll(addresses.subList(0, 1));
                accounts.get(1).getAddresses().addAll(addresses.subList(1, 2));
                accounts.get(2).getAddresses().addAll(addresses.subList(2, 3));
                accounts.get(3).getAddresses().addAll(addresses.subList(3, 4));
                accounts.get(4).getAddresses().addAll(addresses.subList(4, 5));
                accounts.get(5).getAddresses().addAll(addresses.subList(5, 6));
                accounts.get(6).getAddresses().addAll(addresses.subList(6, 7));
                accounts.get(7).getAddresses().addAll(addresses.subList(7, 8));
                accounts.get(8).getAddresses().addAll(addresses.subList(8, 9));
                accounts.get(0).getAddresses().addAll(addresses.subList(9, 10));
                accountRepository.saveAll(accounts);
                log.info("Seeded 10 addresses");

                // ===================== CARTS (10 bản ghi) =====================
                List<CartEntity> carts = cartRepository.saveAll(List.of(
                                CartEntity.builder().account(accounts.get(0)).build(),
                                CartEntity.builder().account(accounts.get(1)).build(),
                                CartEntity.builder().account(accounts.get(2)).build(),
                                CartEntity.builder().account(accounts.get(3)).build(),
                                CartEntity.builder().account(accounts.get(4)).build(),
                                CartEntity.builder().account(accounts.get(5)).build(),
                                CartEntity.builder().account(accounts.get(6)).build(),
                                CartEntity.builder().account(accounts.get(7)).build(),
                                CartEntity.builder().account(accounts.get(8)).build(),
                                CartEntity.builder().account(accounts.get(9)).build()));
                log.info("Seeded {} carts", carts.size());

                // ===================== CART ITEMS (10 bản ghi) =====================
                carts.get(0).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(0)).variant(v1).quantity(2).build());
                carts.get(0).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(0)).variant(v9).quantity(1).build());
                carts.get(1).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(1)).variant(v3).quantity(1).build());
                carts.get(2).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(2)).variant(v11).quantity(2).build());
                carts.get(3).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(3)).variant(v17).quantity(1).build());
                carts.get(4).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(4)).variant(v5).quantity(3).build());
                carts.get(5).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(5)).variant(v8).quantity(1).build());
                carts.get(6).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(6)).variant(v13).quantity(2).build());
                carts.get(7).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(7)).variant(v15).quantity(1).build());
                carts.get(8).getItems()
                                .add(CartItemEntity.builder().cart(carts.get(8)).variant(v20).quantity(1).build());
                cartRepository.saveAll(carts);
                log.info("Seeded 10 cart items");

                // ===================== ORDERS (10 bản ghi) =====================

                List<OrderEntity> orders = orderRepository.saveAll(List.of(
                                OrderEntity.builder().account(accounts.get(0)).status(OrderStatus.DELIVERED)
                                                .finalAmount(new BigDecimal("2990000"))
                                                .receiver("Nguyễn Văn An").phone("0901234567")
                                                .location("123 Nguyễn Huệ, Quận 1, TP.HCM")
                                                .paymentStatus(1)
                                                .paymentMethodCode("MOMO").build(),

                                OrderEntity.builder().account(accounts.get(1)).status(OrderStatus.SHIPPING)
                                                .finalAmount(new BigDecimal("3800000"))
                                                .receiver("Trần Thị Bình").phone("0912345678").paymentMethodCode("MOMO")
                                                .paymentStatus(1)
                                                .location("45 Trần Hưng Đ0, Quận 5, TP.HCM").build(),

                                OrderEntity.builder().account(accounts.get(2)).status(OrderStatus.PROCESSING)
                                                .finalAmount(new BigDecimal("1200000")).paymentMethodCode("MOMO")
                                                .receiver("Lê Văn Cường").phone("0923456789")
                                                .paymentStatus(0)
                                                .location("88 Lê Lợi, Quận 3, TP.HCM").build(),

                                OrderEntity.builder().account(accounts.get(3)).status(OrderStatus.PROCESSING)
                                                .finalAmount(new BigDecimal("5800000")).paymentMethodCode("MOMO")
                                                .receiver("Phạm Thị Dung").phone("0934567890")
                                                .paymentStatus(1)
                                                .location("12 Hoàng Diệu, Quận 4, TP.HCM").build(),

                                OrderEntity.builder().account(accounts.get(4)).status(OrderStatus.PENDING)
                                                .finalAmount(new BigDecimal("2300000")).paymentMethodCode("MOMO")
                                                .receiver("Hoàng Văn Em").phone("0945678901")
                                                .paymentStatus(0)
                                                .location("67 Cách Mạng Tháng 8, Quận 10").build(),

                                OrderEntity.builder().account(accounts.get(5)).status(OrderStatus.DELIVERED)
                                                .finalAmount(new BigDecimal("1500000")).paymentMethodCode("MOMO")
                                                .receiver("Vũ Thị Phương").phone("0956789012")
                                                .paymentStatus(1)
                                                .location("30 Phan Đình Phùng, Phú Nhuận").build(),

                                OrderEntity.builder().account(accounts.get(6)).status(OrderStatus.CANCELED)
                                                .finalAmount(new BigDecimal("3200000")).paymentMethodCode("MOMO")
                                                .receiver("Đặng Văn Giang").phone("0967890123")
                                                .paymentStatus(0)
                                                .location("99 Đinh Tiên Hoàng, Bình Thạnh").build(),

                                OrderEntity.builder().account(accounts.get(7)).status(OrderStatus.REFUNDED)
                                                .finalAmount(new BigDecimal("1900000")).paymentMethodCode("MOMO")
                                                .receiver("Bùi Thị Hoa").phone("0978901234")
                                                .paymentStatus(0)
                                                .location("15 Lý Thường Kiệt, Tân Bình").build(),

                                OrderEntity.builder().account(accounts.get(0)).status(OrderStatus.DELIVERED)
                                                .finalAmount(new BigDecimal("6600000")).paymentMethodCode("MOMO")
                                                .receiver("Nguyễn Văn An").phone("0901234567")
                                                .paymentStatus(1)
                                                .location("123 Nguyễn Huệ, Quận 1, TP.HCM").build(),

                                OrderEntity.builder().account(accounts.get(8)).status(OrderStatus.PROCESSING)
                                                .finalAmount(new BigDecimal("2600000")).paymentMethodCode("MOMO")
                                                .receiver("Ngô Văn Inh").phone("0989012345")
                                                .paymentStatus(1)
                                                .location("22 Trường Chinh, Quận 12").build()));
                log.info("Seeded {} orders", orders.size());

                // ===================== ORDER ITEMS (15 bản ghi) =====================
                orders.get(0).getItems().add(OrderItemEntity.builder().order(orders.get(0)).variant(v1).quantity(1).price(new BigDecimal(122212))
                               .build());
                orders.get(1).getItems().add(OrderItemEntity.builder().order(orders.get(1)).variant(v3).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(2).getItems().add(OrderItemEntity.builder().order(orders.get(2)).variant(v9).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(3).getItems().add(OrderItemEntity.builder().order(orders.get(3)).variant(v6).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(4).getItems().add(OrderItemEntity.builder().order(orders.get(4)).variant(v5).quantity(1).price(new BigDecimal(122212))
                               .build());
                orders.get(5).getItems().add(OrderItemEntity.builder().order(orders.get(5)).variant(v10).quantity(1).price(new BigDecimal(122212))
                               .build());
                orders.get(5).getItems().add(OrderItemEntity.builder().order(orders.get(5)).variant(v12).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(6).getItems().add(OrderItemEntity.builder().order(orders.get(6)).variant(v8).quantity(1).price(new BigDecimal(122212))
                             .build());
                orders.get(7).getItems().add(OrderItemEntity.builder().order(orders.get(7)).variant(v13).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(8).getItems().add(OrderItemEntity.builder().order(orders.get(8)).variant(v4).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(8).getItems().add(OrderItemEntity.builder().order(orders.get(8)).variant(v7).quantity(1).price(new BigDecimal(122212))
                               .build());
                orders.get(9).getItems().add(OrderItemEntity.builder().order(orders.get(9)).variant(v15).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(0).getItems().add(OrderItemEntity.builder().order(orders.get(0)).variant(v11).quantity(1).price(new BigDecimal(122212))
                              .build());
                orders.get(2).getItems().add(OrderItemEntity.builder().order(orders.get(2)).variant(v17).quantity(1).price(new BigDecimal(122212))
                                .build());
                orders.get(1).getItems().add(OrderItemEntity.builder().order(orders.get(1)).variant(v20).quantity(1).price(new BigDecimal(122212))
                                .build());
                orderRepository.saveAll(orders);
                log.info("Seeded 15 order items");

                // ===================== PAYMENTS (10 bản ghi) =====================
                paymentRepository.saveAll(List.of(
                                PaymentEntity.builder().order(orders.get(0)).amount(new BigDecimal("2990000"))
                                                .method("MOMO").status("COMPLETED").build(),
                                PaymentEntity.builder().order(orders.get(1)).amount(new BigDecimal("3800000"))
                                                .method("BANK_TRANSFER").status("COMPLETED").build(),
                                PaymentEntity.builder().order(orders.get(2)).amount(new BigDecimal("1200000"))
                                                .method("COD").status("PENDING").build(),
                                PaymentEntity.builder().order(orders.get(3)).amount(new BigDecimal("5800000"))
                                                .method("CREDIT_CARD").status("COMPLETED").build(),
                                PaymentEntity.builder().order(orders.get(4)).amount(new BigDecimal("2300000"))
                                                .method("COD").status("PENDING").build(),
                                PaymentEntity.builder().order(orders.get(5)).amount(new BigDecimal("1500000"))
                                                .method("ZALOPAY").status("COMPLETED").build(),
                                PaymentEntity.builder().order(orders.get(6)).amount(new BigDecimal("3200000"))
                                                .method("MOMO").status("REFUNDED").build(),
                                PaymentEntity.builder().order(orders.get(7)).amount(new BigDecimal("1900000"))
                                                .method("BANK_TRANSFER").status("REFUNDED").build(),
                                PaymentEntity.builder().order(orders.get(8)).amount(new BigDecimal("6600000"))
                                                .method("CREDIT_CARD").status("COMPLETED").build(),
                                PaymentEntity.builder().order(orders.get(9)).amount(new BigDecimal("2600000"))
                                                .method("VNPAY").status("COMPLETED").build()));
                log.info("Seeded 10 payments");

                // ===================== REVIEWS (10 bản ghi) =====================
                reviewRepository.saveAll(List.of(
                                ReviewEntity.builder().account(accounts.get(0)).product(p1).content(
                                                "Giày rất êm, đi chạy bộ tuyệt vời! Mua lần 2 rồi vẫn không thất vọng.")
                                                .rating(5).build(),
                                ReviewEntity.builder().account(accounts.get(1)).product(p2).content(
                                                "Chất lượng tốt, đúng size. Đệm boost cực kỳ thoải mái khi chạy dài.")
                                                .rating(5).build(),
                                ReviewEntity.builder().account(accounts.get(2)).product(p6)
                                                .content("Kiểu dáng đẹp, hợp trend. Tuy nhiên canvas dễ bẩn một chút.")
                                                .rating(4).build(),
                                ReviewEntity.builder().account(accounts.get(3)).product(p4).content(
                                                "Giày bóng đá chất lượng cao, bám sân tốt. Xứng đáng với giá tiền.")
                                                .rating(5).build(),
                                ReviewEntity.builder().account(accounts.get(4)).product(p3).content(
                                                "Màu sắc đẹp, nhưng size hơi to hơn bình thường, nên xuống 1 size.")
                                                .rating(3).build(),
                                ReviewEntity.builder().account(accounts.get(5)).product(p6)
                                                .content("Mua cho con gái, bé rất thích. Chất liệu thoáng mát.")
                                                .rating(5).build(),
                                ReviewEntity.builder().account(accounts.get(6)).product(p5)
                                                .content("Giày bóng rổ nhẹ, phản lực tốt. Giao hàng nhanh.").rating(4)
                                                .build(),
                                ReviewEntity.builder().account(accounts.get(7)).product(p8).content(
                                                "Giày đi bộ hàng ngày rất phù hợp. Đế đệm tốt, đi lâu không mỏi chân.")
                                                .rating(4).build(),
                                ReviewEntity.builder().account(accounts.get(0)).product(p7).content(
                                                "Classic muôn thuở! Phối đồ rất dễ. Chất lượng bền như kỳ vọng.")
                                                .rating(5).build(),
                                ReviewEntity.builder().account(accounts.get(8)).product(p9)
                                                .content("Kết nối app theo dõi tập luyện rất hay. Giày nhẹ và thoáng.")
                                                .rating(4).build()));
                log.info("Seeded 10 reviews");

                log.info("✅ Database seeding completed successfully!");
        }
}

