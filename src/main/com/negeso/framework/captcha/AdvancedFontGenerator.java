/**
 * @(#)$Id: AdvancedFontGenerator.java,v 1.0, 2007-02-07 18:16:26Z, Volodymyr Snigur$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.captcha;

import java.awt.Font;

import com.octo.captcha.component.image.fontgenerator.DeformedRandomFontGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;

public class AdvancedFontGenerator implements FontGenerator {
    public static final int MIN_FONT_SIZE = 3;
    
    int minFontSize;
    int maxFontSize;
    int wordLength;

    public AdvancedFontGenerator(
            Integer minFontSize, 
            Integer maxFontSize
            
    ) {
        maxFontSize = minFontSize;
        minFontSize = maxFontSize;
    }
    
    public Font getFont() {
        
        FontGenerator fontGeneratorLarge = new DeformedRandomFontGenerator(
                this.getMinFontSize(), 
                this.getMaxFontSize()
        );
        
        Font resultFont = fontGeneratorLarge.getFont();
        
        resultFont = resultFont.deriveFont(Font.TYPE1_FONT);
        resultFont = resultFont.deriveFont(Font.TRUETYPE_FONT);
       
        resultFont = new Font("Times New Roman",Font.ITALIC + Font.TRUETYPE_FONT,18);
        
        return resultFont;
    }
    
    public int getMaxFontSize() {
        return this.maxFontSize;
    }

    public int getMinFontSize() {
        return this.minFontSize;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }
}
