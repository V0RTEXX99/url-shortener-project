package com.aayaniqbal.spring_boot_url_shortener.web.controllers;

import com.aayaniqbal.spring_boot_url_shortener.ApplicationProperties;
import com.aayaniqbal.spring_boot_url_shortener.domain.entities.ShortUrl;
import com.aayaniqbal.spring_boot_url_shortener.domain.entities.User;
import com.aayaniqbal.spring_boot_url_shortener.domain.exceptions.ShortUrlNotFoundException;
import com.aayaniqbal.spring_boot_url_shortener.domain.models.CreateShortUrlCmd;
import com.aayaniqbal.spring_boot_url_shortener.domain.models.ShortUrlDto;
import com.aayaniqbal.spring_boot_url_shortener.domain.services.ShortUrlService;
import com.aayaniqbal.spring_boot_url_shortener.web.dtos.CreateShortUrlForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ShortUrlService shortUrlService;
    private final ApplicationProperties properties;
    private final SecurityUtils securityUtils;

    public HomeController(ShortUrlService shortUrlService, ApplicationProperties properties, SecurityUtils securityUtils) {
        this.shortUrlService = shortUrlService;
        this.properties = properties;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/")
    public String home(Model model) {

        List<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls();

        model.addAttribute("shortUrls", shortUrls );
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute(
                "createShortUrlForm",
                new CreateShortUrlForm("", false, null)
        );
        return "index";
    }

    @PostMapping("/short-urls")
    public String createShortUrl(

            @ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model

    ) {

        if(bindingResult.hasErrors()) {
            List<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls();
            model.addAttribute("shortUrls", shortUrls );
            model.addAttribute("baseUrl", properties.baseUrl());
            return "index";
        }

        try {
            Long userId = securityUtils.getCurrentUserId();

            CreateShortUrlCmd cmd = new CreateShortUrlCmd(
                    form.originalUrl(),
                    form.isPrivate(),
                    form.expirationInDays(),
                    userId
            );
            ShortUrlDto shortUrlDto = shortUrlService.createShortUrl(cmd);
            redirectAttributes.addFlashAttribute("successMessage", "Short URL created successfully " + properties.baseUrl() + "/s/" + shortUrlDto.shortKey());

        } catch (Exception e) {
            log.error("Failed to create short URL for: {}", form.originalUrl(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create short URL");
        }


        return "redirect:/";

    }

    @GetMapping("/s/{shortKey}")
    public String redirectToOriginalUrl(@PathVariable String shortKey) {

        Long userId =  securityUtils.getCurrentUserId();

        Optional<ShortUrlDto> shortUrlDtoOptional = shortUrlService.accessShortUrl(shortKey, userId);

        if(shortUrlDtoOptional.isEmpty()) {
            throw new ShortUrlNotFoundException("Invalid Short key " + shortKey);
        }
        ShortUrlDto shortUrlDto = shortUrlDtoOptional.get();
        return "redirect:" + shortUrlDto.originalUrl();

    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

}
