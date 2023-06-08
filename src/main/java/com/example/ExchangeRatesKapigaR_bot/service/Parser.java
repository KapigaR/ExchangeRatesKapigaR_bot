package com.example.ExchangeRatesKapigaR_bot.service;

import java.text.SimpleDateFormat;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Parser {

    public static String parse(String message) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDateStr = dateFormat.format(date);

        String url = "https://cbr.ru/scripts/XML_daily.asp?date_req=" + currentDateStr + ".xml";

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            doc.getDocumentElement().normalize();
            if (message == "/info") {
                List<String> searchInfo = getValuteInfo(doc, xpath);
                return "\n" + searchInfo;
            } else {
                List<String> searchNominal = getValuteNominal(doc, xpath, message);
                List<String> searchName = getValuteName(doc, xpath, message);
                List<String> searchValue = getValuteValue(doc, xpath, message);
                String controlM = String.valueOf(searchName);
                if (controlM == "[]") {
                    return null;
                } else {
                    return "Валюта: " + searchName + "\n" +
                            "Количество: " + searchNominal + "\n" +
                            "Цена: " + searchValue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> getValuteNominal(Document doc, XPath xpath, String message) {
        List<String> list = new ArrayList<>();
        try {
            XPathExpression xPathExpression = xpath.compile("/ValCurs /Valute[CharCode='" + message + "']/Nominal/text()");
            NodeList nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                list.add(nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        if (list == null) {
            return null;
        } else {
            return list;
        }
    }

    private static List<String> getValuteName(Document doc, XPath xpath, String message) {
        List<String> list = new ArrayList<>();
        try {
            XPathExpression xPathExpression1 = xpath.compile("/ValCurs /Valute[CharCode='" + message + "']/Name/text()");
            NodeList nodes = (NodeList) xPathExpression1.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                list.add(nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<String> getValuteValue(Document doc, XPath xpath, String message) {
        List<String> list = new ArrayList<>();
        try {
            XPathExpression xPathExpression = xpath.compile("/ValCurs /Valute[CharCode='" + message + "']/Value/text()");
            NodeList nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                list.add(nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<String> getValuteInfo(Document doc, XPath xpath) {
        List<String> list = new ArrayList<>();
        try {
            XPathExpression xPathExpression = xpath.compile("/ValCurs /Valute/CharCode/text()");
            NodeList nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                list.add(nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return list;
    }
}
