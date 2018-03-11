package com.navercorp.arcus;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.ArcusClient;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.collection.CollectionAttributes;
import net.spy.memcached.collection.ElementValueType;
import net.spy.memcached.internal.CollectionFuture;

public class HelloArcus {

	private String arcusAdmin;
	private String serviceCode;
	private ArcusClient arcusClient;
	private Connection con = null;
	private ResultSet rs;
	private String url = "jdbc:mysql://127.0.0.1:3306/world";
	private String user = "root";
	private String pwd = "1234";
	private String[] countries = { "USA", "CHN", "KOR", "JPN", "FRA", "IND" };

	public HelloArcus(String arcusAdmin, String serviceCode) {
		this.arcusAdmin = arcusAdmin;
		this.serviceCode = serviceCode;

		// log4j logger를 사용하도록 설정합니다.
		// 코드에 직접 추가하지 않고 아래의 JVM 환경변수를 사용해도 됩니다.
		// -Dnet.spy.log.LoggerImpl=net.spy.memcached.compat.log.Log4JLogger

		// System.setProperty("net.spy.log.LoggerImpl",
		// "net.spy.memcached.compat.log.Log4JLogger");

		// Arcus 클라이언트 객체를 생성합니다.
		// - arcusAdmin : Arcus 캐시 서버들의 그룹을 관리하는 admin 서버(ZooKeeper)의 주소입니다.
		// - serviceCode : 사용자에게 할당된 Arcus 캐시 서버들의 집합에 대한 코드값입니다.
		// - connectionFactoryBuilder : 클라이언트 생성 옵션을 지정할 수 있습니다.
		//
		// 정리하면 arcusAdmin과 serviceCode의 조합을 통해 유일한 캐시 서버들의 집합을 얻어 연결할 수 있는 것입니다.
		this.arcusClient = ArcusClient.createArcusClient(arcusAdmin, serviceCode, new ConnectionFactoryBuilder());

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, pwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ArcusSetup() {
		String sql = "select * " + "from city;";
		CollectionFuture<Boolean> future = null;
		CollectionAttributes attribute = new CollectionAttributes();

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				future = arcusClient.asyncSopInsert("city:" + rs.getString("countryCode"), rs.getInt("id"), attribute);
				future = arcusClient.asyncLopInsert(rs.getString("countryCode") + ":" + rs.getInt("id"), 0,
						rs.getString("name"), attribute);
				future = arcusClient.asyncLopInsert(rs.getString("countryCode") + ":" + rs.getInt("id"), 1,
						String.valueOf(rs.getInt("population")), attribute);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> ArcusSearch() {
		Set<Object> citys = null;
		List<String> result = new ArrayList<String>();

		CollectionFuture<Set<Object>> future = null;
		CollectionFuture<List<Object>> future2 = null;

		long start = System.currentTimeMillis();

		for (String country : countries) {

			try {
				future = arcusClient.asyncSopGet("city:" + country, 1000, false, false);

				if (future == null)
					return null;
				citys = future.get(3000L, TimeUnit.MILLISECONDS);
				if (citys != null) {
					for (Object o : citys) {
						int id = (Integer) o;
						future2 = arcusClient.asyncLopGet(country + ":" + id, 0, 1, false, false);

						List<Object> city = future2.get();

						String name = (String) city.get(0);
						int population = Integer.parseInt((String) city.get(1));

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
