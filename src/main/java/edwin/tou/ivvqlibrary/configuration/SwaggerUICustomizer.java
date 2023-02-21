package edwin.tou.ivvqlibrary.configuration;

import edwin.tou.ivvqlibrary.domain.Generated;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Generated(
    reason = "Swagger UI customizer to add API KEY headers in request parameters."
)
public class SwaggerUICustomizer implements OpenApiCustomiser {

    @Value("${api.basePath}")
    private String apiBasePath;

    @Value("${api.keyHeaderName}")
    private String apiKeyHeaderName;

    @Value("${api.isHttps:false}")
    private boolean isHttps;

    @Override
    public void customise(OpenAPI openApi) {
        Parameter apiKeyHeader = new HeaderParameter()
            .name(apiKeyHeaderName)
            .description("User API key header to provide.")
            .required(true);

        if (isHttps) {
            for (Server server : openApi.getServers()) {
                String urlHttps = server.getUrl().replace("http:", "https:");
                server.setUrl(urlHttps);
            }
        }

        openApi
            .getPaths()
            .forEach(
                (uri, path) -> {
                    if (uri.startsWith(apiBasePath) && !uri.contains("users")) {
                        path.addParametersItem(apiKeyHeader);
                    }
                }
            );
    }
}
