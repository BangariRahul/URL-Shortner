package com.rahul.URLShortner.controller;

import com.rahul.URLShortner.Error.InvalidUrlError;
import com.rahul.URLShortner.common.UrlUtil;
import com.rahul.URLShortner.dto.OrignalUrl;
import com.rahul.URLShortner.dto.ShortUrl;
import com.rahul.URLShortner.service.UrlEntityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping
public class UrlEntityController {

    Logger logger = LoggerFactory.getLogger(UrlEntityController.class);

    @Autowired
    UrlEntityService urlEntityService;


    @PostMapping("/shortUrl`")
    public ResponseEntity<Object> saveUrl(@RequestBody OrignalUrl orignalUrl , HttpServletRequest request){


        // Validation checks to determine if the supplied URL is valid
        UrlValidator validator = new UrlValidator(
                new String[]{"http", "https"}
        );
        String url = orignalUrl.getOrignalUrl();
        if (!validator.isValid(url)) {
            logger.error("Malformed Url provided");

            InvalidUrlError error = new InvalidUrlError("url", orignalUrl.getOrignalUrl(), "Invalid URL");

            // returns a custom body with error message and bad request status code
            return ResponseEntity.badRequest().body(error);
        }

        String baseUrl = null;

        try {
            baseUrl = UrlUtil.getBaseUrl(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            logger.error("Malformed request url");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request url is invalid", e);
        }

        // Retrieving the Shortened url and concatenating with protocol://domain:port
        ShortUrl shortUrl = urlEntityService.getShortUrl(orignalUrl);
        shortUrl.setShortUrl(baseUrl + shortUrl.getShortUrl());

        logger.debug(String.format("ShortUrl for FullUrl %s is %s", orignalUrl.getOrignalUrl(), shortUrl.getShortUrl()));

        return new ResponseEntity<>(shortUrl, HttpStatus.OK);
    }

    @GetMapping("/{shortenString}")
    public void redirectToFullUrl(HttpServletResponse response, @PathVariable String shortenString) {
        try {
            OrignalUrl orignalUrl = urlEntityService.getOrignalUrl(shortenString);

            logger.info(String.format("Redirecting to %s", orignalUrl.getOrignalUrl()));

            // Redirects the reponse to the full url
            response.sendRedirect(orignalUrl.getOrignalUrl());
        } catch (NoSuchElementException e) {
            logger.error(String.format("No URL found for %s in the db", shortenString));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Url not found", e);
        } catch (IOException e) {
            logger.error("Could not redirect to the full url");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not redirect to the full url", e);
        }
    }





}
