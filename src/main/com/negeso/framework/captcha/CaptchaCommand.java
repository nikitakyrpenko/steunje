/**
 * @(#)$Id: CaptchaCommand.java,v 1.2, 2007-02-23 16:42:37Z, Volodymyr Snigur$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.textpaster.SimpleTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.wordgenerator.RandomWordGenerator;
import com.octo.captcha.engine.image.gimpy.SimpleListImageCaptchaEngine;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;


public class CaptchaCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(CaptchaCommand.class);
    
    public ResponseContext execute() {
        logger.debug("+");
        
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
        if (!request.getSessionData().isSessionStarted()){
            response.setResultName(RESULT_FAILURE);
            logger.warn("- session was not started");
            return response;
        }
         
        RandomWordGenerator wordGenerator =  new RandomWordGenerator(
                "azertyuiopqsdfghjklmwxcvbn0123456789QWERTYUIOPASDFGHJKLZXCVBNM"
        );

        Integer width = Env.getIntProperty("guestbook.captcha.width",145);
        Integer height = Env.getIntProperty("guestbook.captcha.heigth",76);
        
        Integer fontMin = Env.getIntProperty("guestbook.captcha.fontMin",20);
        Integer fontMax = Env.getIntProperty("guestbook.captcha.fontMax",30);
        
        
//      BackgroundGenerator background = new FunkyBackgroundGenerator(width, height);
        BackgroundGenerator background = new GradientBackgroundGenerator(
                width, 
                height,
                new Color(200,0,0),
                new Color(255,255,255)
        );
       // FontGenerator fontGenerator = new DeformedRandomFontGenerator(fontMin, fontMax);
         FontGenerator fontGenerator = new AdvancedFontGenerator(
                 fontMin,
                 fontMax
         );
        TextPaster textPaster = new SimpleTextPaster(6,7, Color.blue);
        ComposedWordToImage word2image =  new ComposedWordToImage(
                                             fontGenerator, 
                                             background, 
                                             textPaster
                                             );
        ImageCaptchaFactory factory = new GimpyFactory(wordGenerator, word2image);

        SimpleListImageCaptchaEngine simpleEngine = new 
                                                 SimpleListImageCaptchaEngine();
        simpleEngine.addFactory(factory);
        ImageCaptcha pixCaptcha = simpleEngine.getNextImageCaptcha(Locale.US);
        
        request.getSession().setAttribute("captcha",pixCaptcha);

        BufferedImage img = pixCaptcha.getImageChallenge();
       
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
        } catch (Exception e) {
           logger.error("",e);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        int iBytesToCopy = baos.size();
        response.getResultMap().put("stream-length", iBytesToCopy);    
        response.getResultMap().put("stream", (InputStream)bais);
        response.getResultMap().put("mime-type", "image/jpeg");
        response.setResultName(RESULT_SUCCESS);
        
        logger.debug("-");
        return response;
    }
	

}
