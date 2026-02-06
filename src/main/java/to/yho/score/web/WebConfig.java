package to.yho.score.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:4200", "http://127.0.0.1:4200",
            "http://localhost", "http://127.0.0.1",
            "capacitor://localhost", "ionic://localhost",
            "null",
            "http://158.179.161.203", "https://158.179.161.203",
            "http://158.179.161.203:80", "http://158.179.161.203:443"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(ALLOWED_ORIGINS.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * CORS 필터를 적용해 오류 응답(4xx/5xx)에도 Access-Control-Allow-Origin 헤더가 포함되도록 함.
     * MVC CORS 설정만으로는 에러 응답 시 헤더가 빠져 브라우저에서 CORS 오류가 발생할 수 있음.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(ALLOWED_ORIGINS);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
