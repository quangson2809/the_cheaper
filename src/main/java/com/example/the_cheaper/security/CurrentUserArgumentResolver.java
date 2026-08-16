package com.example.the_cheaper.security;

import com.example.the_cheaper.annotation.CurrentUser;
import com.example.the_cheaper.entity.AccountEntity;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolver tự động inject AccountEntity của user đang đăng nhập
 * vào tham số controller được đánh dấu bằng @CurrentUser.
 *
 * Đăng ký trong WebMvcConfig.addArgumentResolvers().
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(AccountEntity.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Bạn chưa đăng nhập hoặc token không hợp lệ");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getAccount();
        }

        throw new org.springframework.security.access.AccessDeniedException(
                "Không thể xác định thông tin người dùng hiện tại");
    }
}
