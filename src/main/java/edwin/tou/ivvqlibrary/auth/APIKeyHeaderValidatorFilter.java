package edwin.tou.ivvqlibrary.auth;

import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.services.UserService;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(1)
public class APIKeyHeaderValidatorFilter extends OncePerRequestFilter {

    private String borrowPath;
    private String apiKeyHeader;
    private UserService userService;

    public APIKeyHeaderValidatorFilter(
        String borrowPath,
        String apiKeyHeader,
        UserService userService
    ) {
        this.borrowPath = borrowPath;
        this.apiKeyHeader = apiKeyHeader;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        String apiKeyString = request.getHeader(apiKeyHeader);

        // 400 on no api key provided
        if (apiKeyString == null) {
            response.sendError(
                HttpStatus.BAD_REQUEST.value(),
                "Must specify header: " + apiKeyHeader + " with a value."
            );
            return;
        }

        UUID apiKey;
        try {
            apiKey = UUID.fromString(apiKeyString);
        } catch (IllegalArgumentException e) {
            response.sendError(
                HttpStatus.BAD_REQUEST.value(),
                "Wrong API key format."
            );
            return;
        }

        // 401 on invalid api key
        if (!userService.doesUserExistsWithApiKey(apiKey)) {
            response.sendError(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid API key."
            );
            return;
        }

        User user = userService.findUserByApiKey(apiKey);

        // 403: Only libraire user can consult borrows (GET)
        if (
            HttpMethod.GET.name().equals(request.getMethod()) &&
            path.startsWith(borrowPath) &&
            !user.isLibraire()
        ) {
            response.sendError(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden API key."
            );
            return;
        }

        // Auth valid, can continue
        filterChain.doFilter(request, response);
    }
}
