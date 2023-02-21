package edwin.tou.ivvqlibrary.configuration;

import edwin.tou.ivvqlibrary.auth.APIKeyHeaderValidatorFilter;
import edwin.tou.ivvqlibrary.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {

    @Value("${api.basePath}")
    private String apiBasePath;

    @Value("${api.keyHeaderName}")
    private String apiKeyHeaderName;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public APIKeyHeaderValidatorFilter apiKeyFilter(UserService userService) {
        return new APIKeyHeaderValidatorFilter(
            getBorrowPath(),
            apiKeyHeaderName,
            userService
        );
    }

    @Bean
    public FilterRegistrationBean<APIKeyHeaderValidatorFilter> apiKeyHeaderFilter(
        APIKeyHeaderValidatorFilter apiKeyFilter
    ) {
        FilterRegistrationBean<APIKeyHeaderValidatorFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(apiKeyFilter);
        registrationBean.addUrlPatterns(
            getBorrowPath() + "/*",
            getBookPath() + "/*"
        );
        return registrationBean;
    }

    private String getBookPath() {
        return apiBasePath + "/books";
    }

    private String getBorrowPath() {
        return apiBasePath + "/borrows";
    }
}
