package spring.api.trijava.chuyendewebjavajob.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import spring.api.trijava.chuyendewebjavajob.util.SecurityUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Value("${cdweb.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {

        String[] whiteList = {
                "/",
                "/api/v1/auth/login", "/api/v1/auth/refresh", "/storage/**",
                "/api/v1/auth/register", "/api/v1/users/create",
                "/api/v1/companies/**", "/api/v1/jobs/**"
        };

        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(whiteList).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/companies/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/jobs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/skills/**").permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        // Bỏ qua JWT processing cho whitelist endpoints
                        .bearerTokenResolver(request -> {
                            String requestURI = request.getRequestURI();
                            // Kiểm tra nếu là whitelist endpoint thì không xử lý Bearer token
                            for (String pattern : whiteList) {
                                if (requestURI.matches(pattern.replace("**", ".*"))) {
                                    return null; // Không xử lý token
                                }
                            }
                            // Xử lý token bình thường cho các endpoint khác
                            String bearerToken = request.getHeader("Authorization");
                            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                                return bearerToken.substring(7);
                            }
                            return null;
                        }))
                .formLogin(f -> f.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // khi decode thành công
    // lấy thông tin từ JWT nạp vào phần quyền hạn
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    // giải mã token decode
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                // lấy ra key để giải mã
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    // lấy key mã hóa encoder, tạo ra token
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    // tạo key
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }
}
