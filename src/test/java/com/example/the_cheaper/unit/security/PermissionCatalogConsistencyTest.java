package com.example.the_cheaper.unit.security;

import com.example.the_cheaper.config.PermissionCatalog;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

class PermissionCatalogConsistencyTest {
    @Test void everyPreAuthorizePermissionAndOrderAccessCodeExistsInCanonicalCatalog() throws Exception {
        var used = new HashSet<String>();
        var annotations = Pattern.compile("@PreAuthorize\\(\"([^\"]+)\"\\)");
        var codes = Pattern.compile("'([A-Z]+_[A-Z_]+)'");
        try (var files = Files.walk(Path.of("src/main/java"))) {
            for (Path path : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                var matches = annotations.matcher(Files.readString(path));
                while (matches.find()) {
                    var literals = codes.matcher(matches.group(1));
                    while (literals.find()) used.add(literals.group(1));
                }
            }
        }
        var orderAccess = Files.readString(Path.of("src/main/java/com/example/the_cheaper/service/authorization/OrderAccess.java"));
        var orderCodes = Pattern.compile("\"(ORDER_[A-Z_]+)\"").matcher(orderAccess);
        while (orderCodes.find()) used.add(orderCodes.group(1));
        assertThat(used).contains("DASHBOARD_READ", "ROLE_PERMISSION_GRANT", "ACCOUNT_STATUS_UPDATE", "ORDER_CONFIRM");
        assertThat(PermissionCatalog.CODES).containsAll(used);
        assertThat(PermissionCatalog.DEFINITIONS).extracting(PermissionCatalog.Definition::code).doesNotHaveDuplicates();
        assertThat(used).doesNotContain("ORDER_UPDATE", "ROLE_ASSIGN_PERMISSION", "ACCOUNT_ASSIGN_ROLE", "ACCOUNT_UPDATE");
    }
}
