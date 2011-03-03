package com.perbergman.xml.test;

import java.io.File;

import org.junit.Test;

import com.perbergman.xml.XSLTEngine;

public class TestXSLTEngine {

	@Test
	public void test01() {
		XSLTEngine x = new XSLTEngine();
		String output = x.transform(new File("test/prod.xml"), "map.xsl", null);
		System.out.println(output);
	}
}
