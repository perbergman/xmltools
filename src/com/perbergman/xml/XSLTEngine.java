package com.perbergman.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

/**
 * 
 * @author Per Bergman
 * 
 */
public class XSLTEngine {

	private final static Log log = LogFactory.getLog(XSLTEngine.class);
	private final static Map<String, Templates> cache = new HashMap<String, Templates>();

	public XSLTEngine() {
	}

	/**
	 * 
	 * @param inputFile
	 * @param sheet
	 * @param params
	 * @return
	 */
	public String transform(File inputFile, String sheet,
			Map<String, String> params) {

		String input = null;
		try {
			List<String> lines = Files.readLines(inputFile, Charsets.UTF_8);
			input = Joiner.on('\n').join(lines).toString();
		} catch (IOException e) {
			log.error("transform file reading", e);
			return null;
		}

		StringWriter outputWriter = new StringWriter();

		try {
			Templates templates = cache.get(sheet);
			if (templates == null) {
				// Load from class path
				InputStream xslt = Thread.currentThread()
						.getContextClassLoader().getResourceAsStream(sheet);

				Source xsltSource = new StreamSource(xslt);
				TransformerFactory factory = TransformerFactory.newInstance();
				templates = factory.newTemplates(xsltSource);
				cache.put(sheet, templates);
			}

			Transformer trans = templates.newTransformer();
			if (params != null) {
				for (String key : params.keySet()) {
					trans.setParameter(key, params.get(key));
				}
			}

			Source xmlSource = new StreamSource(new StringReader(input));
			Result result = new StreamResult(outputWriter);
			trans.transform(xmlSource, result);
		} catch (Exception e) {
			log.error("transform processing", e);
		}

		return outputWriter.toString();
	}

	public static void main(String[] args) {
		XSLTEngine x = new XSLTEngine();
		String output = x.transform(new File("prod.xml"), "map.xsl", null);
		System.out.println(output);
	}
}
