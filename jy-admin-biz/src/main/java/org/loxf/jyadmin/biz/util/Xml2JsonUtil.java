package org.loxf.jyadmin.biz.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName: Xml2JsonUtil
 * @author tandingbo
 * @date 2016年4月27日 下午2:19:56
 * 
 */
public class Xml2JsonUtil {
	/**
	 * 转换一个xml格式的字符串到json格式
	 * 
	 * @param xml 格式的字符串
	 * @return 成功返回json 格式的字符串;失败反回null
	 */
	public static String xml2JSON(String xml) {
		try {
			return JSON.toJSONString(xml2Map(xml));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, Object> xml2Map(String xmlStr) {
		// 解析结果
		Map<String, Object> result = new HashMap<String, Object>();
		// 解析到
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();

		try {
			InputStream is = new ByteArrayInputStream(xmlStr.getBytes("utf-8"));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			// 根节点
			Element root = document.getDocumentElement();
			// 获取所有子节点
			NodeList rootChild = root.getChildNodes();
			// 循环解析子节点
			for (int i = 0; i < rootChild.getLength(); i++) {
				// 子节点
				Node info = rootChild.item(i);
				if (info.hasChildNodes()) {
					Map<String, Object> mapChildNodes = new HashMap<String, Object>();
					// 子节点下所有节点
					NodeList clist = info.getChildNodes();
					for (int j = 0; j < clist.getLength(); j++) {
						Node c = clist.item(j);
						if (c instanceof Element) {
							mapChildNodes.put(c.getNodeName(), c.getTextContent());
						}
					}

					// 构建解析结果
					String key = info.getNodeName();
					if (result.containsKey(key)) {
						Object o = result.get(key);
						if (o instanceof Map) {
							listMap.add((Map<String, Object>) o);
						} else {
							listMap = (List<Map<String, Object>>) o;
							listMap.add(mapChildNodes);
						}
						result.put(key, listMap);
					} else {
						result.put(key, mapChildNodes);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
