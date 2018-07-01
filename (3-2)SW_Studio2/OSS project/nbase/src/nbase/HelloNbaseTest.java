package nbase;

public class HelloNbaseTest {
	HelloNbase hnbase = new HelloNbase("127.0.0.1:2181","test_cluster");
	
	public void NbaseSetup() {
		hnbase.NbaseSetup();
	}
	
	public void NbaseSearch() {
		hnbase.NbaseSearch();
	}
	
	public void DBTest() {
		hnbase.DBMSTest();
	}
}
