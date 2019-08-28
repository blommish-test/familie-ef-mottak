package no.nav.familie.ef.mottak.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket


@Configuration
class SwaggerDocumentationConfig {

    private val basePackage = "no.nav.familie.ef.mottak"

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder().build()
    }

    /**
     * Builder and primary interface of swagger-spring framework.
     */
    @Bean
    fun customImplementation(): Docket {

        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
    }
}

