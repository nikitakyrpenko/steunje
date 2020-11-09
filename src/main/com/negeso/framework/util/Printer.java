package com.negeso.framework.util;

import org.w3c.dom.Document;
import java.io.IOException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.apache.xml.serialize.*;
/**
 * Created by NEGESO.
 * User: Seriy Vitaliy
 * Date: 20.10.2008
 * Time: 16:19:42
 */
public class Printer {

    public static void printXML(Document document){
        try {
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            XMLSerializer serializer = new XMLSerializer(System.out, format);
            serializer.serialize(document);
        }
        catch (FactoryConfigurationError e) {
            System.out.println("Could not locate a JAXP factory class");
        }
        catch (DOMException e) {
            System.err.println(e);
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }
}

