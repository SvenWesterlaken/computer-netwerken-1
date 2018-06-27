package com.svenwesterlaken;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by Sven Westerlaken on 6-6-2017.
 */
class SubjectCodec {

    //Checks if response refers to second line of subject
    static boolean isMultilineSubject(String response) {
        return response.toLowerCase().startsWith(" =?utf-8?") || response.toLowerCase().startsWith(" =?iso-8859-1?");
    }

    //This class decodes UTF-8 & ISO-8859-1 encoded strings into normal UTF-8 strings
    static String decode(String subject) throws UnsupportedEncodingException {
        int end = subject.lastIndexOf("?=");

        if(subject.toLowerCase().contains("=?utf-8?")) {
            int start = subject.toLowerCase().indexOf("=?utf-8?");
            int encodeIndex = start + 8;
            String encoding = subject.substring(encodeIndex, encodeIndex+1);
            String encodedStr = subject.substring(encodeIndex+2, end);

            if(encoding.equalsIgnoreCase("Q")) {
                try {
                    byte[] decoded = new QuotedPrintableCodec().decode(encodedStr.getBytes("UTF-8"));
                    encodedStr = new String(decoded, "UTF-8");
                }  catch (DecoderException e) {
                    e.printStackTrace();
                }

            } else if (encoding.equalsIgnoreCase("B")) {
                byte[] decoded = Base64.getMimeDecoder().decode(encodedStr);
                encodedStr = new String(decoded, "UTF-8");
            }

            if (!subject.toLowerCase().startsWith("=?utf-8?")) {
                subject = subject.substring(0, start) + encodedStr;
            } else if (subject.toLowerCase().startsWith("=?utf-8?")) {
                subject = encodedStr;
            }

        } else if (subject.toLowerCase().contains("=?iso-8859-1?")) {
            int start = subject.toLowerCase().indexOf("=?iso-8859-1?");
            int encodeIndex = start + 13;
            String encoding = subject.substring(encodeIndex, encodeIndex+1);
            String encodedStr = subject.substring(encodeIndex+2, end);

            if(encoding.equalsIgnoreCase("B")) {
                byte[] decoded = Base64.getDecoder().decode(encodedStr.getBytes("ISO-8859-1"));
                subject = new String(decoded, "UTF-8");
            }
        }

        //Because a whitespace is better than a underscore
        subject = subject.replaceAll("_", " ");

        return subject;
    }


}
