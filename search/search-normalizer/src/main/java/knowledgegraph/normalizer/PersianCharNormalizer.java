/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledgegraph.normalizer;

import java.util.List;

/**
 * Persian character normalizer
 * @author morteza.khaleghi
 */
public class PersianCharNormalizer {

    private static final char FARSI_YEH = '\u06CC';

    private static final char ARABIC_YEH = '\u064A';

    private static final char YEH_BARREE = '\u06D2';

    private static final char YEH_BARREE_HAMZA_ABOV = '\u06D3';

    private static final char YEH_MAKSURA = '\u0649';

    private static final char YEH_HAMZA_ABOV = '\u0626';

    private static final char YEH_POINT_BELOV = '\u06D0';

    private static final char FARSI_KEH = '\u06A9';

    private static final char ARABIC_KAF = '\u0643';

    private static final char HEH_YEH_ABOV = '\u06C0';

    private static final char HEH_GOAL = '\u06C1';

    private static final char HEH_GOAL_HAMZA_ABOV = '\u06C2';
    
    private static final char HEH_DOCHASHME = '\u06BE';

    private static final char HEH = '\u0647';
    
    private static final char WAW = '\u0648';
    
    private static final char WAW_HAMZA_ABOV = '\u0624';
    
    private static final char WAW_HIGHT_HAMZA = '\u0676';

    private static final char ALEF = '\u0627';
    private static final char ALEF_MADDA_ABOV = '\u0622';
    private static final char ALEF_HAMZA_ABOV = '\u0623';

    private boolean enableNormalYEH = false,
            enableNormalKAF = false,
            enableNormalHE = false,
            enableNormalALEF = false,
            enableNormalWAW = false,
            enableNormalNUMBERS = false;

    /**
     * create PersianCharNormalizer with all normalization options enabled
     */
    public PersianCharNormalizer() {
        enableNormalYEH = true;
        enableNormalKAF = true;
        enableNormalHE = true;
        enableNormalALEF = true;
        enableNormalWAW = true;
        enableNormalNUMBERS = true;
    }

    /**
     * create PersianCharNormalizer with desired options
     * @param normalizerOptions list of PersianCharNormalizer.Option enum
     */
    public PersianCharNormalizer(List<Option> normalizerOptions) {
        initNormalizerOptions(normalizerOptions);
    }

    private void initNormalizerOptions(List<Option> normilizerOptions) {
        if (normilizerOptions != null) {
            for (Option option : normilizerOptions) {
                switch (option) {
                    case NORMAL_ALEF:
                        enableNormalALEF = true;
                        break;
                    case NORMAL_HE:
                        enableNormalHE = true;
                        break;
                    case NORMAL_KAF:
                        enableNormalKAF = true;
                        break;
                    case NORMAL_NUMBERS:
                        enableNormalNUMBERS = true;
                        break;
                    case NORMAL_YEH:
                        enableNormalYEH = true;
                        break;
                    case NORMAL_WAW:
                        enableNormalWAW = true;
                        break;
                }

            }
        }
    }

    /**
     *normalize input string based of defined PersianCharNormalizer Options
     * @param text input string
     * @return normalized string
     */
    public String normalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        char[] textCharArray = text.toCharArray();

        for (int i = 0; i < textCharArray.length; i++) {
            textCharArray[i] = normalize(textCharArray[i]);

        }

        return charArrayToString(textCharArray);
    }

    private char normalize(char c) {
        if (enableNormalALEF) {
            c = normalizeAlef(c);
        }
        if (enableNormalNUMBERS) {
            c = normalizeNumbers(c);
        }
        if (enableNormalHE) {
            c = normalizeHe(c);
        }
        if (enableNormalYEH) {
            c = normalizeYeh(c);
        }
        if (enableNormalKAF) {
            c = normalizeKaf(c);
        }
        
        if (enableNormalWAW) {
            c = normalizeWaw(c);
        }

        return c;
    }

    private char normalizeAlef(char c) {
        if (c == ALEF_MADDA_ABOV || c == ALEF_HAMZA_ABOV) {
            c = ALEF;
        }
        return c;
    }

    private char normalizeYeh(char c) {
        if (c == ARABIC_YEH || c == YEH_BARREE
                || c == YEH_BARREE_HAMZA_ABOV || c == YEH_MAKSURA
                /*|| c == YEH_HAMZA_ABOV*/ || c == YEH_POINT_BELOV) {
            c = FARSI_YEH;
        }
        return c;
    }

    private char normalizeKaf(char c) {
        if (c == ARABIC_KAF) {
            c = FARSI_KEH;
        }
        return c;
    }

    private char normalizeHe(char c) {
        if (c == HEH_YEH_ABOV || c == HEH_GOAL 
                || c == HEH_GOAL_HAMZA_ABOV || c == HEH_DOCHASHME) {
            c = HEH;
        }
        return c;
    }
    
    private char normalizeWaw(char c) {
        if (c == WAW_HAMZA_ABOV || c == WAW_HIGHT_HAMZA) {
            c = WAW;
        }
        return c;
    }

    private char normalizeNumbers(char c) {
        //arabic numbers
        switch (c) {
            case '\u0660':
                return '0';
            case '\u0661':
                return '1';
            case '\u0662':
                return '2';
            case '\u0663':
                return '3';
            case '\u0664':
                return '4';
            case '\u0665':
                return '5';
            case '\u0666':
                return '6';
            case '\u0667':
                return '7';
            case '\u0668':
                return '8';
            case '\u0669':
                return '9';
        }
        //persian numbers
        switch (c) {
            case '\u06F0':
                return '0';
            case '\u06F1':
                return '1';
            case '\u06F2':
                return '2';
            case '\u06F3':
                return '3';
            case '\u06F4':
                return '4';
            case '\u06F5':
                return '5';
            case '\u06F6':
                return '6';
            case '\u06F7':
                return '7';
            case '\u06F8':
                return '8';
            case '\u06F9':
                return '9';
            default:
                return c;
        }
    }

    private String charArrayToString(char[] textCharArray) {
        StringBuilder textBuilder = new StringBuilder(textCharArray.length);
        for (int i = 0; i < textCharArray.length; i++) {
            textBuilder.append(textCharArray[i]);
        }
        return textBuilder.toString();
    }

    /**
     * Persian Char normalizer options
     */
    public enum Option {

        /**
         *convert various type of arabic Yeh to persian Yeh ('ي','ے','ۓ','ى','ئ','ې') => 'ی'
         */
        NORMAL_YEH,

        /**
         *convert arabic Kaf to persian Kaf ('ك') => 'ک'
         */
        NORMAL_KAF,

        /**
         *convert various type of He to persian He ('ۀ','ہ','ۂ','ھ') => 'ه'
         */
        NORMAL_HE,

        /**
         *convert Alef with Madda and Alef with Hamza to normal Alef ('آ','أ') => 'ا'
         */
        NORMAL_ALEF,

        /**
         *convert Waw with Hamza to normal Waw ('ؤ','ٶ') => 'و'
         */
        NORMAL_WAW,

        /**
         * convert arabic and persian numbers to english numbers ('١','٢',...) => '1','2',...
         */
        NORMAL_NUMBERS
    }
}
