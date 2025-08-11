package com.secureapp.util;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlSafe {
    public static String parseRootName(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setExpandEntityReferences(false); dbf.setXIncludeAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        var doc = db.parse(new org.xml.sax.InputSource(new StringReader(xml)));
        return doc.getDocumentElement().getNodeName();
    }
    public static String transform(String xml, String xsl) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer t = tf.newTransformer(new StreamSource(new StringReader(xsl)));
        StringWriter sw = new StringWriter();
        t.transform(new StreamSource(new StringReader(xml)), new StreamResult(sw));
        return sw.toString();
    }
}
