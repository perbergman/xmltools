package com.perbergman.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * 
 * @author Per Bergman
 * 
 */
public class XPathEngine {

	private final static Log log = LogFactory.getLog(XPathEngine.class);

	private final static Ordering<Node> BY_NODE_NAME = new Ordering<Node>() {
		public int compare(Node left, Node right) {
			String leftName = left.getNodeName();
			String rightName = right.getNodeName();
			return leftName.compareTo(rightName);
		}
	};

	private final static Ordering<Object> BY_STRING = Ordering.usingToString();

	private final XPath xpath = XPathFactory.newInstance().newXPath();
	private File path;

	public XPathEngine(File path) {
		this.path = path;
	}

	/**
	 * Stream cannot be reused!
	 * 
	 * @return
	 */
	private InputSource getInputSource() {
		InputSource ret = null;
		try {
			ret = new InputSource(new FileInputStream(path));
		} catch (Exception e) {
			log.error("getInputSource " + path, e);
			throw new RuntimeException(e);
		}
		return ret;
	}

	public void dump(List<Node> nl) {
		for (Node n : nl) {
			this.dump(n);
		}
	}

	public void dump(Node node) {
		this.dump(node, 0, null);
	}

	public void dump(Node node, int tabs, List<String> skip) {
		String prefix = "";
		List<String> line = Lists.newArrayList();
		boolean isDisplay = true;

		if (tabs > 0) {
			prefix = Strings.repeat("\t", tabs);
		}

		for (Node a : BY_STRING.sortedCopy(this.nodifyAttributes(node))) {
			// for (Node a :
			// BY_NODE_NAME.sortedCopy(this.nodifyAttributes(node))) {
			String value = a.getNodeValue();
			if (!Strings.isNullOrEmpty(value)) {
				String name = a.getNodeName().trim();
				if (skip == null || !skip.contains(name)) {
					line.add(name + "=" + value.trim());
				}
			}
		}

		if (isDisplay) {
			System.out.println(prefix + Joiner.on(',').join(line));
		}
	}

	public List<Node> query(String expression) {
		NodeList nodes = null;
		try {
			InputSource is = this.getInputSource();
			if (is != null) {
				nodes = (NodeList) xpath.evaluate(expression, is,
						XPathConstants.NODESET);
			}
		} catch (XPathExpressionException e) {
			log.error("Error - query " + expression + " in " + path);
		}
		return this.nodify(nodes);
	}

	public Node queryOne(String expression) {
		return this.getOne(this.query(expression));
	}

	public Node queryOne(Node owner, String expression) {
		return this.getOne(this.query(owner, expression));
	}

	private Node getOne(List<Node> nodes) {
		if (nodes == null || nodes.size() == 0) {
			throw new RuntimeException("found no nodes");
		}
		return nodes.get(0);
	}

	public List<Node> query(Node node, String expression) {
		NodeList nodes = null;
		try {
			nodes = (NodeList) xpath.evaluate(expression, node,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			System.err.println("Error - query " + expression + " on " + node
					+ " in " + path);
		}
		return this.nodify(nodes);
	}

	/**
	 * Return the attribute node for the given attribute name.
	 * 
	 * @param owner
	 * @param name
	 * @return
	 */
	public Node findAttr(Node owner, String name) {
		return owner.getAttributes().getNamedItem(name);
	}

	public String findAttrValue(Node owner, String name) {
		return this.findAttr(owner, name).getNodeValue();
	}

	private List<Node> nodify(NodeList nl) {
		List<Node> ret = Lists.newArrayList();
		if (nl == null) {
			return ret;
		}
		for (int i = 0; i < nl.getLength(); i++) {
			ret.add(nl.item(i));
		}
		return ret;
	}

	public List<Node> nodifyAttributes(Node n) {
		List<Node> ret = Lists.newArrayList();
		if (n == null) {
			return ret;
		}
		NamedNodeMap al = n.getAttributes();
		for (int i = 0; i < al.getLength(); i++) {
			ret.add(al.item(i));
		}
		return ret;
	}

	public String toNS(String ns) {
		return "[namespace-uri()='" + ns + "']";
	}

	public static void main(String[] args) {
		File f = new File("resources.xml");
		XPathEngine e = new XPathEngine(f);
		System.out.println(e.query("//*[starts-with(name(), 'RAFW_')]"));
	}
}
