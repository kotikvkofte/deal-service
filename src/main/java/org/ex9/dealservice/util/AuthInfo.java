package org.ex9.dealservice.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для получения информации об аутентифицированном пользователе.
 * @author Краковцев Артём
 */
public final class AuthInfo {

    private AuthInfo() {
    }

    /**
     * Получает имя авторизированного пользователя.
     * @return имя пользователя
     */
    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    /**
     * Получает список ролей авторизованного пользователя
     * @return список ролей.
     */
    public static List<String> getRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
    }

    /**
     * Получает список доступных для пользователя ролей
     * @return список доступных ролей.
     */
    public static List<String> getAllowedTypes(List<String> types) {
        List<String> roles = AuthInfo.getRoles();
        boolean isSuperUser = roles.contains("SUPERUSER") || roles.contains("DEAL_SUPERUSER");
        if (!isSuperUser) {
            if (types == null || types.contains("OTHER")) {
                throw new AccessDeniedException("Access denied");
            }
            Map<String, String> roleToType = Map.of(
                    "CREDIT_USER", "CREDIT",
                    "OVERDRAFT_USER", "OVERDRAFT"
            );
            List<String> allowedTypes = roleToType.entrySet().stream()
                    .filter(entry -> roles.contains(entry.getKey()) && types.contains(entry.getValue()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            if (allowedTypes.isEmpty()) {
                throw new AccessDeniedException("Access denied");
            }
            return allowedTypes;
        }
        return types;
    }

}
