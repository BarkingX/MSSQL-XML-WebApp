package com.example.demo.controller;

import com.example.demo.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Set;


@Controller
@RequiredArgsConstructor
public class WebsiteController {
    private final DatabaseService databaseService;

    @RequestMapping({"/", "/index.html"})
    public @NotNull String indexPage(@NotNull Model model) {
        Map<String, Set<String>> catalogSchemasMap = databaseService.getCatalogSchemasMap();

        model.addAttribute("catalogSchemasMap", catalogSchemasMap);
        model.addAttribute("catalogNames", catalogSchemasMap.keySet());
        return "index";
    }
}
