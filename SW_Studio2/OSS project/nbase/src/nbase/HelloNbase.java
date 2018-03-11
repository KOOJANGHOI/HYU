package nbase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.navercorp.redis.cluster.gateway.GatewayClient;
import com.navercorp.redis.cluster.gateway.GatewayConfig;

public class HelloNbase {
	private final GatewayConfig config = new GatewayConfig();
	private GatewayClient client;
	private String zkAddress = null;
	private String clusterName = null;
	private Connection con = null;
	private ResultSet rs;
	private String url = "jdbc:mysql://127.0.0.1:3306/world";
	private String user = "root";
	private String pwd = "dkahflg1";
	private String[] countries = { "USA", "CHN", "KOR", "JPN", "FRA", "IND" };

	public HelloNbase(String zkAddress, String clusterName) {
		this.zkAddress = zkAddress;
		this.clusterName = clusterName;
		this.config.setZkAddress(zkAddress);
		this.config.setClusterName(clusterName);
		this.client = new GatewayClient(config);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, pwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void NbaseSetup() {
		String sql = "select * " + "from city;";
		Long result = (long) 0;

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {

				result = client.sadd("city:" + rs.getString("countryCode"), Integer.toString(rs.getInt("id")));
				result = client.lpush(rs.getString("countryCode") + ":" + rs.getInt("id"), rs.getString("name"));
				result = client.lpush(rs.getString("countryCode") + ":" + rs.getInt("id"),
						Integer.toString(rs.getInt("population")));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> NbaseSearch() {
		Set<String> citys = null;
		List<String> result = new ArrayList<String>();
		String name = null;
		int population = 0;

		long start = System.currentTimeMillis();

		for (String country : countries) {
			try {
				citys = client.smembers("city:" + country);
				if (citys != null) {
					for (Object o : citys) {
						int id = (Integer) o;
						name = client.lindex(country + ":" + id, 0);
						population = Integer.parseInt(client.lindex(country + ":" + id, 1));

						if (population >= 1000000) {
							result.add(name);
						}
					}
				} else {
					System.out.println("city is null");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("[time elapse] " + (end - start) + " ms");

		return result;
	}

	public void DBMSTest() {
		long start = System.currentTimeMillis();
		String sql = "select * " + "from city " + "where CountryCode in (?, ?, ?, ?, ?, ?) and population >= 1000000;";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, "USA");
			ps.setString(2, "CHN");
			ps.setString(3, "KOR");
			ps.setString(4, "JPN");
			ps.setString(5, "FRA");
			ps.setString(6, "IND");

			rs = ps.executeQuery();

			System.out.println();
			while (rs.next()) {
				System.out.print(rs.getString("name") + " ");
			}
			System.out.println();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("[DBMS] time elapse " + (end - start) + " ms");
	}
}
