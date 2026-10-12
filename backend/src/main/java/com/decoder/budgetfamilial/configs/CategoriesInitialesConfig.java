package com.decoder.budgetfamilial.configs;

import com.decoder.budgetfamilial.services.CategorieService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoriesInitialesConfig implements ApplicationRunner {

    private final CategorieService categorieService;

    public CategoriesInitialesConfig(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @Override
    public void run(ApplicationArguments args) {
        categorieService.initialiserCategoriesParDefaut();
    }
}
