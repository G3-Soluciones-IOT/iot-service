package pe.edu.upc.iot_service.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI iotOpenApi(
            @Value("${documentation.openapi.server-url:}") String serverUrl,
            @Value("${documentation.openapi.server-description:Public Gateway}") String serverDescription) {
        final String jwtScheme    = "bearerAuth";
        final String apiKeyScheme = "apiKeyAuth";

        var openApi = new OpenAPI()
                .info(apiInfo())
                .externalDocs(externalDocs())
                // Both security schemes available — endpoints pick one via @SecurityRequirement
                .addSecurityItem(new SecurityRequirement().addList(jwtScheme))
                .addSecurityItem(new SecurityRequirement().addList(apiKeyScheme))
                .components(new Components()
                        .addSecuritySchemes(jwtScheme,
                                new SecurityScheme()
                                        .name(jwtScheme)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token issued by iam-service. Required for query endpoints and device registration."))
                        .addSecuritySchemes(apiKeyScheme,
                                new SecurityScheme()
                                        .name("X-API-Key")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("API Key generated at device registration. Required for device ingestion endpoints.")));

        if (StringUtils.hasText(serverUrl)) {
            openApi.addServersItem(new Server()
                    .url(serverUrl)
                    .description(serverDescription));
        }

        return openApi;
    }

    private Info apiInfo() {
        return new Info()
                .title("IoT Service API")
                .description("REST API for JameoFit IoT Device Data — Smart Bottle & Smart Scale")
                .version("v1.0.0")
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                .contact(new Contact()
                        .name("JF Technologies")
                        .url("https://github.com/G3-Soluciones-IOT"));
    }

    private ExternalDocumentation externalDocs() {
        return new ExternalDocumentation()
                .description("JameoFit IoT Service Documentation")
                .url("https://github.com/G3-Soluciones-IOT");
    }
}
