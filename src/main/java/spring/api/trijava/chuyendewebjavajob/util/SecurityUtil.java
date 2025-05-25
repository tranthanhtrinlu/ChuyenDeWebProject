package spring.api.trijava.chuyendewebjavajob.util;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResLoginDTO;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    // lấy tham số môi trường trong properties
    @Value("${cdweb.jwt.base64-secret}")
    private String jwtKey;

    @Value("${cdweb.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${cdweb.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    // Người dùng sau khi login thành công, sẽ được server trả về:
    // access_token : token để định danh người dùng (thời gian sống ngắn : 5 phút,
    // 10 phút…)
    // refresh_token : sử dụng refresh token để renew thời gian sống lâu hơn nhiều,
    // thường là 1 ngày, 30 ngày … )
    // => access token được lưu tại Local Storage (frontend dễ dàng truy cập và sử
    // dụng) .
    // Đặt thời gian sống ngắn để giảm thiểu rủi ro
    // Refresh token được lưu tại cookies với mục đích là Server sử dụng (vì cookie
    // luôn được
    // gửi kèm với mỗi lời gọi request) => lưu ở cookies sẽ an toàn hơn
    // (do thời gian sống của token lâu hơn access token

    public String createAccessToken(String email, ResLoginDTO dto) {

        ResLoginDTO.UserInsideToken userInsideToken = new ResLoginDTO.UserInsideToken();
        userInsideToken.setId(dto.getUserLogin().getId());
        userInsideToken.setEmail(dto.getUserLogin().getEmail());
        userInsideToken.setName(dto.getUserLogin().getName());

        // thời gian tạo ra token
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // hardcode permission (for testing)
        List<String> listAuthority = new ArrayList<String>();

        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

        // tạo phần body token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInsideToken)
                .claim("permission", listAuthority)
                .build();

        // tạo phần header token
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    public String createRefreshToken(String email, ResLoginDTO dto) {
        // thời gian tạo ra token
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        ResLoginDTO.UserInsideToken userInsideToken = new ResLoginDTO.UserInsideToken();
        userInsideToken.setId(dto.getUserLogin().getId());
        userInsideToken.setEmail(dto.getUserLogin().getEmail());
        userInsideToken.setName(dto.getUserLogin().getName());

        // tạo phần body token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInsideToken)
                .build();

        // tạo phần header token
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    // tạo key
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                // lấy ra key để giải mã
                .withSecretKey(getSecretKey())
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM)
                .build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> Refresh token error: " + e.getMessage());
            throw e;
        }
    }

    // Lấy ra user đang đăng nhập
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    // mỗi lần gọi 1 request truyền token lên, token lấy từ khi login
    // quá trình giải mã lấy thông tin user
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    // public static boolean isAuthenticated() {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // return authentication != null &&
    // getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    // }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // return (
    // authentication != null && getAuthorities(authentication).anyMatch(authority
    // -> Arrays.asList(authorities).contains(authority))
    // );
    // }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false
     *         otherwise.
     */
    // public static boolean hasCurrentUserNoneOfAuthorities(String... authorities)
    // {
    // return !hasCurrentUserAnyOfAuthorities(authorities);
    // }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    // public static boolean hasCurrentUserThisAuthority(String authority) {
    // return hasCurrentUserAnyOfAuthorities(authority);
    // }

    // private static Stream<String> getAuthorities(Authentication authentication) {
    // return
    // authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    // }
}
