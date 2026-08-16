package com.example.the_cheaper.annotation;

import java.lang.annotation.*;

/**
 * Annotation dùng để inject AccountEntity của user hiện tại
 * từ SecurityContext vào tham số của controller method.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
