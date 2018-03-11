package com.navercorp.arcus;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class HelloArcusTest {

	HelloArcus helloArcus = new HelloArcus("127.0.0.1:2181", "test");
	
	@Before
	public void sayHello() {
		helloArcus.ArcusSetup();
		//helloArcus.DBMSTest();

	}
	
	@Test
	public void listenHello() {
		List<String> result = helloArcus.ArcusSearch();
		System.out.println("RESULT IS " + result.size());
		System.out.println(result);
		helloArcus.DBMSTest();
	}
	

	
}