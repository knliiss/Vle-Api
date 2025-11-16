package dev.knalis.vleapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        Components components = new Components()
                .addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                .addParameters("X-Bootstrap-Secret",
                        new Parameter()
                                .name("X-Bootstrap-Secret")
                                .in("header")
                                .required(false)
                                .description("Required only when creating the very first ADMINISTRATOR and admin.bootstrap-secret is configured.")
                                .schema(new StringSchema()))
                .addSchemas("ProblemDetail", new io.swagger.v3.oas.models.media.Schema<>()
                        .addProperty("type", new StringSchema().example("https://http.dev/problems/bad-request"))
                        .addProperty("title", new StringSchema().example("Bad request"))
                        .addProperty("status", new io.swagger.v3.oas.models.media.IntegerSchema().example(400))
                        .addProperty("detail", new StringSchema().example("Validation failed"))
                )
                .addSchemas("SubmissionStatus", new io.swagger.v3.oas.models.media.StringSchema()._enum(List.of("ADDED","OVERDUE","GRADED")));

        Info info = new Info()
                .title("VleApi")
                .version("v0.0.1")
                .description("VleApi backend API documentation.\n\n" +
                        "Roles: ADMINISTRATOR, TEACHER, STUDENT. Access rules are enforced via JWT and role-based checks.\n" +
                        "Use Authorization: Bearer <access_token> header for protected endpoints.")
                .contact(new Contact().name("VleApi Team").email("support@example.com"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                .termsOfService("https://example.com/terms");

        ExternalDocumentation externalDocs = new ExternalDocumentation()
                .description("Project repository")
                .url("https://example.com/vleapi-repo");

        List<Server> servers = List.of(
                new Server().url("http://localhost:8060").description("Local dev")
        );

        return new OpenAPI()
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(info)
                .externalDocs(externalDocs)
                .servers(servers);
    }
}
