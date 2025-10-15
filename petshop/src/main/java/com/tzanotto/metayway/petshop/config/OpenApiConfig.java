package com.tzanotto.metayway.petshop.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI petshopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Petshop Metaway API")
                        .description("API do projeto de testes (Java + MySQL + Angular + Docker)")
                        .version("v1")
                        .contact(getContactInfo())
                        .license(getLicense()))
                .servers(getServers())
                .externalDocs(getExtDocumentation());
    }

    private Contact getContactInfo() {
        return new Contact()
                .name("Tiago Zanotto")
                .email("tiagoz.blk@gmail.com");
    }

    private License getLicense() {
        return new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0");
    }

    private List<Server> getServers() {
        List<Server> servers = new ArrayList<>();
        servers.add( new Server().url("http://localhost:8080").description("Local"));
        servers.add(new Server().url("http://localhost").description("Default"));

        return servers;
    }

    private ExternalDocumentation getExtDocumentation() {
        return new ExternalDocumentation()
                .description("Documentação do Projeto")
                .url("http://localhost:8080/swagger-ui.html");
    }
}
