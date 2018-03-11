package com.navercorp.nbase_arc_java_client;

import org.junit.Before;
import org.junit.Test;

public class HelloNbaseTest {
	HelloNbase hnbase = new HelloNbase("127.0.0.1:2181","test_cluster");
	
	@Before
	public void NbaseSetup() {
		hnbase.NbaseSetup();
	}
	
	@Test
	public void NbaseSearch() {
		hnbase.NbaseSearch();
		hnbase.DBMSTest();
	}
	
}