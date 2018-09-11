package org.loxf.jyadmin.base.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmlUtil {
    private static Logger log = LoggerFactory.getLogger(XmlUtil.class);

    private XmlUtil() {

    }

    /**
     * @param xmlRootName
     * @param xmlData
     * @return
     * @Description: 构建xml字符串数据
     */
    public static String createXml(String xmlRootName, Map<String, String> xmlData) {
        String xmlStr = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // 根节点
            Element root = document.createElement(xmlRootName);
            document.appendChild(root);

            // 子节点数据
            for (Map.Entry<String, String> entry : xmlData.entrySet()) {
                if(StringUtils.isNotBlank(entry.getValue())){
                    Element item = document.createElement(entry.getKey());
                    item.appendChild(document.createTextNode(entry.getValue()));
                    root.appendChild(item);
                }
            }

            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transFormer = transFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);

            transFormer.transform(domSource, new StreamResult(bos));
            xmlStr = bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return xmlStr;
    }

    public static void main(String[] args) {
        String xmlRootName = "sdfg";
        // String xmlRootName = "gamezcTOPorder";
        // String xmlRootName = "gamezcTOPcancel";
        Map<String, String> data = new HashMap<String, String>();
        data.put("orderId", "2130547689");
        data.put("time", "201507290913");
        data.put("status", "SUCCESS");
        data.put("failedReason", "do not have the order");
        data.put("orderNo", "231029383");
        data.put("failedCode", "0101");
        data.put("coopOrderSnap", "moshou|qufu1");
        String xmls = createXml(xmlRootName, data);
        System.out.println(xmls);
        String s="0.01";
        Double d=new Double(s);
        d=d*100;
        System.out.println(d.intValue());
    }
}
