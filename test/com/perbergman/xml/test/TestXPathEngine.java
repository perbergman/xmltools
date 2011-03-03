package com.perbergman.xml.test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Node;

import com.perbergman.xml.XPathEngine;

public class TestXPathEngine {

	// private final static String J2C =
	// "http://www.ibm.com/websphere/appserver/schemas/5.0/resources.j2c.xmi";
	private final static String JDBC = "http://www.ibm.com/websphere/appserver/schemas/5.0/resources.jdbc.xmi";
	private final static String JMS = "http://www.ibm.com/websphere/appserver/schemas/5.0/resources.jms.xmi";

	// private final static String JMS_MQ =
	// "http://www.ibm.com/websphere/appserver/schemas/5.0/resources.jms.mqseries.xmi";
	// private final static String MAIL =
	// "http://www.ibm.com/websphere/appserver/schemas/5.0/resources.mail.xmi";
	// private final static String URL =
	// "http://www.ibm.com/websphere/appserver/schemas/5.0/resources.url.xmi";

	@Test
	public void test01() {
		File f = new File("test/resources.xml");
		XPathEngine e = new XPathEngine(f);
		List<String> skip = Arrays.asList("description", "xmi:id");

		for (Node n : e.query("//*" + e.toNS(JDBC))) {
			System.out.println("// " + JDBC + " " + e.findAttrValue(n, "name"));
			for (Node fac : e.query(n, "factories")) {
				e.dump(fac, 0, skip);
				for (Node pSet : e.query(fac, "propertySet/resourceProperties")) {
					e.dump(pSet, 1, skip);
				}
			}
		}

		for (Node n : e.query("//*" + e.toNS(JMS))) {
			System.out.println("// " + JMS + " " + e.findAttrValue(n, "name"));
			for (Node fac : e.query(n, "factories")) {
				e.dump(fac, 0, skip);
				for (Node pSet : e.query(fac, "propertySet/resourceProperties")) {
					e.dump(pSet, 1, skip);
				}
			}
		}

	}

	public void visit(Node n, List<String> expr, XPathEngine e) {
		if (expr.size() == 0) {
			return;
		}
		String head = expr.remove(0);
		for (Node node : e.query(n, head)) {
			e.dump(node, 0, null);
			this.visit(node, expr, e);
		}

	}
}
