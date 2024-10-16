package com.rahul.URLShortner.service;

import com.rahul.URLShortner.common.ShorteningUtil;
import com.rahul.URLShortner.dto.OrignalUrl;
import com.rahul.URLShortner.dto.ShortUrl;
import com.rahul.URLShortner.model.UrlEntity;
import com.rahul.URLShortner.repo.UrlEntityRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlEntityService {

    Logger logger = LoggerFactory.getLogger(UrlEntityService.class);
    @Autowired
    UrlEntityRepo urlEntityRepo;


    private UrlEntity get(Long id) {
        logger.info(String.format("Fetching Url from database for Id %d", id));
        UrlEntity urlEntity = urlEntityRepo.findById(id).get();
        return urlEntity;
    }
    public ShortUrl getShortUrl(OrignalUrl orignalUrl) {

        logger.info("Checking if the url already exists");
        List<UrlEntity> savedUrls = null;
        savedUrls = checkFullUrlAlreadyExists(orignalUrl);

        UrlEntity savedUrl = null;

        if (savedUrls.isEmpty()) {
            logger.info(String.format("Saving Url %s to database", orignalUrl.getOrignalUrl()));
            savedUrl = this.save(orignalUrl);
            logger.debug(savedUrl.toString());
        }
        else {
            savedUrl = savedUrls.get(0);
            logger.info(String.format("url: %s already exists in the database. skipped insert", savedUrl));
        }

        logger.debug(String.format("Converting Base 10 %d to Base 62 string", savedUrl.getID()));
        String shortUrlText = ShorteningUtil.idToStr(savedUrl.getID());
        logger.info(String.format("Converted Base 10 %d to Base 62 string %s", savedUrl.getID(), shortUrlText));

        return new ShortUrl(shortUrlText);
    }


    public OrignalUrl getOrignalUrl(String shortenString) {
        logger.debug("Converting Base 62 string %s to Base 10 id");
        Long id = ShorteningUtil.strToId(shortenString);
        logger.info(String.format("Converted Base 62 string %s to Base 10 id %s", shortenString, id));

        logger.info(String.format("Retrieving full url for %d", id));
        return new OrignalUrl(this.get(id).getOrignalUrl());
    }



    private List<UrlEntity> checkFullUrlAlreadyExists(OrignalUrl orignalUrl) {
        return urlEntityRepo.findUrlByOrignalUrl(orignalUrl.getOrignalUrl());
    }

    private UrlEntity save(OrignalUrl orignalUrl) {
        return urlEntityRepo.save(new UrlEntity(orignalUrl.getOrignalUrl()));
    }

}
