package rebel.plugins

import io.ktor.server.application.*
import io.ktor.server.thymeleaf.*
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.FileTemplateResolver

fun Application.configureThymeleaf() {
    install(Thymeleaf) {
        setTemplateResolver((if (developmentMode) {
            FileTemplateResolver().apply {
                cacheManager = null
                prefix = "src/main/resources/templates/"
            }
        } else {
            ClassLoaderTemplateResolver().apply {
                prefix = "templates/"
            }
        }).apply {
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
}