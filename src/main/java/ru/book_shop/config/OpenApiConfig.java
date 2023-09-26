package ru.book_shop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Книжный интернет магазин",
                version = "1.0.0"
        ),
        servers = {@Server(url = "/", description = "api")}
)
public class OpenApiConfig {}
