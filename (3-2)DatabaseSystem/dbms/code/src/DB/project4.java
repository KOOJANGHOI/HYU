package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;
import java.util.List;
import java.util.StringJoiner;

public class project4 {
	private static Scanner userinput;
	private static Connection conn;
	private static Statement stmt;
	private static ResultSet rs;
	private static ResultSetMetaData rsmd;
	private static ArrayList<String> tbllist;

	// Driver Loading
	public static void LoadingDriver() {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (Exception e) {
			System.out.println("Error in LoadingDriver(): " + e);
			return;
		}
	}

	// Connection to DataBase
	public static void ConnectDB() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MUSIC", "root", "dkahflg1");
		} catch (Exception e) {
			System.out.println("Error in ConnectDB(): " + e);
			return;
		}
	}

	// Create statement
	public static void CreateStatement() {
		try {
			stmt = conn.createStatement();
		} catch (Exception e) {
			System.out.println("Error in CreateStatement: " + e);
			return;
		}
	}

	// Store all table name initially
	public static void StoreTableName() {
		try (ResultSet rs = conn.getMetaData().getTables(null, null, null, null)) {
			while (rs.next()) {
				String tblName = rs.getString("TABLE_NAME");
				tbllist.add(tblName);
			}
		} catch (Exception e) {
			System.out.println("Error in StoreTableName(): " + e);
			return;
		}
	}

	// Close Connection to DataBase
	public static void CloseConnection() {
		try {
			if (rs != null)
				rs.close();

			if (stmt != null)
				stmt.close();

			if (conn != null)
				conn.close();
		} catch (Exception e) {
			System.out.println("Error in CloseConnection(): " + e);
			return;
		}
	}

	// 테이블 이름을 인자로 받아서 INSERT QUERY 수행
	public static void InsertQuery(String tblName) {
		userinput = new Scanner(System.in);
		// 쿼리문 준비
		String sql = "INSERT INTO " + tblName + " VALUES(";

		// 해당 테이블의 column 정보 가져온다
		int col = 0;
		try {
			rs = stmt.executeQuery("SELECT * FROM " + tblName);
			rsmd = rs.getMetaData();
			col = rsmd.getColumnCount();
		} catch (SQLException se) {
			System.out.println("error!!!");

		}

		// 각 column 마다 값을 입력받는다
		for (int i = 1; i <= col; i++) {
			try {
				System.out.print("Input[" + rsmd.getColumnName(i) + "]:");
				String data = userinput.nextLine();
				// 입력 == INTEGER인 경우
				if (rsmd.getColumnType(i) == 4) {
					sql += data;
				}
				// 입력 == VARCHAR()인 경우
				else if (rsmd.getColumnType(i) == 12) {
					sql += "'" + data + "'";
				}
				// 입력 == DATE인 경우
				else if (rsmd.getColumnType(i) == 93) {
					sql += "'" + data + "'";
				} else {

				}
				if (i != col) {
					sql += ",";
				} else {
					sql += ");";
				}
			} catch (SQLException e) {
				System.out.println("SQLException in InsertQuery()!!!");
			}
		}

		// 쿼리문 실행
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			System.out.println("SQL Exception!! Retry(syntax error or break constraint)");
		}

	}

	// 테이블 이름을 인자로 받아서 UPDATE QUERY 수행
	public static void UpdateQuery(String tblName) {
		userinput = new Scanner(System.in);
		// 쿼리문 준비
		String sql = "UPDATE " + tblName + " SET ";

		// 해당 테이블의 column 정보 가져온다
		int col = 0;
		try {
			rs = stmt.executeQuery("SELECT * FROM " + tblName);
			rsmd = rs.getMetaData();
			col = rsmd.getColumnCount();
		} catch (SQLException se) {
			System.out.println("error!!!");

		}

		// SET 절
		// 각 column 마다 SET절에 넣을 값을 입력받는다
		boolean tmp = true;
		for (int i = 1; i <= col; i++) {
			try {
				System.out.print("SET[" + rsmd.getColumnName(i) + "][INPUT/N]:");
				String data = userinput.nextLine();
				if (data.equalsIgnoreCase("N")) {

				} else {
					if (tmp) {
						tmp = false;
					} else {
						sql += " AND ";
					}
					// 입력 == INTEGER인 경우
					if (rsmd.getColumnType(i) == 4) {
						sql += "" + rsmd.getColumnName(i) + "=" + data;
					}
					// 입력 == VARCHAR()인 경우
					else if (rsmd.getColumnType(i) == 12) {
						sql += "" + rsmd.getColumnName(i) + "='" + data + "'";
					}
					// 입력 == DATE인 경우
					else if (rsmd.getColumnType(i) == 93) {
						sql += "" + rsmd.getColumnName(i) + "='" + data + "'";
					} else {

					}

				}
			} catch (SQLException e) {
				System.out.println("SQLException in InsertQuery()!!!");
			}
		}

		// WHERE 절
		// 각 column 마다 WHERE절에 넣을 값을 입력받는다
		sql += " WHERE ";
		tmp = true;
		for (int i = 1; i <= col; i++) {
			try {
				System.out.print("OPTION[" + rsmd.getColumnName(i) + "][INPUT/N]:");
				String data = userinput.nextLine();
				if (data.equalsIgnoreCase("N")) {

				} else {
					if (tmp) {
						sql += "" + rsmd.getColumnName(i) + "=" + data;
						tmp = false;
					} else {
						sql += " AND " + rsmd.getColumnName(i) + "=" + data;
					}
				}
			} catch (SQLException e) {
				System.out.println("SQLException in UpdateQuery()!!!");
			}
		}
		sql += ";";

		// 쿼리문 실행
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			System.out.println("SQL Exception!! Retry(syntax error or break constraint)");
		}

	}

	// 테이블 이름을 인자로 받아서 DELETE QUERY 수행
	public static void DeleteQuery(String tblName) {
		userinput = new Scanner(System.in);
		// 쿼리문 준비
		String sql = "DELETE FROM " + tblName + " WHERE ";

		// 해당 테이블의 column 정보 가져온다
		int col = 0;
		try {
			rs = stmt.executeQuery("SELECT * FROM " + tblName);
			rsmd = rs.getMetaData();
			col = rsmd.getColumnCount();
		} catch (SQLException se) {
			System.out.println("error!!!");

		}

		// WHERE 절
		// 각 column 마다 WHERE절에 넣을 값을 입력받는다
		boolean tmp = true;
		for (int i = 1; i <= col; i++) {
			try {
				System.out.print("OPTION[" + rsmd.getColumnName(i) + "][INPUT/N]:");
				String data = userinput.nextLine();
				if (data.equalsIgnoreCase("N")) {

				} else {
					if (tmp) {
						sql += "" + rsmd.getColumnName(i) + "=" + data;
						tmp = false;
					} else {
						sql += " AND " + rsmd.getColumnName(i) + "=" + data;
					}
				}
			} catch (SQLException e) {
				System.out.println("SQLException in DeleteQuery()!!!");
			}
		}
		sql += ";";

		// 쿼리문 실행
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			System.out.println("SQL Exception!! Retry(syntax error or break constraint)");
		}
	}

	// 테이블 이름을 인자로 받아서 SELECT QUERY 수행
	public static void SelectQuery(String tblName) {
		userinput = new Scanner(System.in);
		// 쿼리문 준비
		String sql = "SELECT ";

		// 해당 테이블의 column 정보 가져온다
		int col = 0;
		try {
			rs = stmt.executeQuery("SELECT * FROM " + tblName);
			rsmd = rs.getMetaData();
			col = rsmd.getColumnCount();
		} catch (SQLException se) {
			System.out.println("error!!!");

		}

		// SELECT 절
		// 각 column 마다 SELECT절에 넣을 값을 입력받는다
		boolean tmp = true;
		for (int i = 1; i <= col; i++) {
			try {
				System.out.print("SELECT[" + rsmd.getColumnName(i) + "][Y/N]:");
				String data = userinput.nextLine();
				if (data.equalsIgnoreCase("N")) {

				} else {
					if (tmp) {
						tmp = false;
					} else {
						sql += ",";
					}
					sql += "" + rsmd.getColumnName(i);
				}
			} catch (SQLException e) {
				System.out.println("SQLException in SelectQuery()!!!");
			}
		}

		// FROM 절
		sql += " FROM " + tblName + " WHERE ";

		// WHERE 절
		tmp = true;
		// 각 column 마다 WHERE절에 넣을 값을 입력받는다
		for (int i = 1; i <= col; i++) {
			try {
				System.out.print("OPTION[" + rsmd.getColumnName(i) + "][INPUT/N]:");
				String data = userinput.nextLine();
				if (data.equalsIgnoreCase("N")) {

				} else {
					if (tmp) {
						sql += "" + rsmd.getColumnName(i) + data;
						tmp = false;
					} else {
						sql += " AND " + rsmd.getColumnName(i) + data;
					}
				}
			} catch (SQLException e) {
				System.out.println("SQLException in InsertQuery()!!!");
			}
		}
		sql += ";";
		
		// 쿼리 실행하면서 SELECT 결과를 출력
		try {
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			System.out.println("\n------------------------------[Result]-----------------------------------");
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(rsmd.getColumnName(i) + ": " + columnValue + " ");
				}
				System.out.println("");
			}
		} catch (SQLException se) {
			System.out.println("SQL Exception!! Retry(syntax error or break constraint)");
		}
	}

	// 테이블 이름을 인자로 받아서 테이블을 생성하는 메소드
	public static void CreateTableQuery(String tblName) {
		userinput = new Scanner(System.in);
		boolean chk = true;
		String sql = "";
		// 에러가 없으면 while문 종료
		while (chk) {
			try {
				sql = "CREATE TABLE " + tblName + " (";
				// 애트리뷰트의 갯수를 입력받는다
				System.out.print("Input[# of Attribute]:");
				int attrNum = Integer.parseInt(userinput.nextLine());
				// 애트리뷰트의 갯수만큼 문자열 배열을 만든다. 애트리뷰트마다 한줄씩 저장하기 위함
				String[] attrArray = new String[attrNum];
				// 애트리뷰트 갯수만큼 for-loop
				for (int i = 0; i < attrNum; i++) {
					// 애트리뷰트 이름 , 데이터형 , NOT NULL 제약조건을 입력받아서 배열에 저장
					attrArray[i] = "";
					// 애트리뷰트 이름 입력
					System.out.print("Input[Name of Attribute]:");
					String attrName = userinput.nextLine();
					attrArray[i] += attrName + " ";
					// 애트리뷰트 타입 입력
					System.out.print("Input[Type of Attribute]:");
					String attrType = userinput.nextLine();
					attrArray[i] += attrType + " ";
					// NOT NULL 제약조건 여부 확인
					System.out.print("Input[NOT NULL][Y/N]:");
					String nnl = userinput.nextLine();
					if (nnl.equalsIgnoreCase("Y")) {
						attrArray[i] += "NOT NULL";
					} else if (nnl.equalsIgnoreCase("N")) {
						attrArray[i] += "";
					} else {
						// Y,N 이 아니면 예외처리
						throw new SQLException();
					}
				}

				// 기본키 절 사용 여부 확인
				System.out.print("Input[PK][Y/N]:");
				// 기본키 절를 담을 문자열
				String pk = "";
				String oppk = userinput.nextLine();
				// 기본키 절 사용
				if (oppk.equalsIgnoreCase("Y")) {
					// 기본키 제약조건 이름을 정할건지 여부 확인
					System.out.print("Input[CONSTRAINT][NAME/N]:");
					String opconst = userinput.nextLine();
					if (opconst.equalsIgnoreCase("N")) {
					} else {
						// 넣는다면, 문자열에 추가
						pk = "CONSTRAINT " + opconst + " ";
					}
					// 기본키 입력 받고, 문자열에 추가
					System.out.print("Input[PK]:");
					pk += "PRIMARY KEY (";
					pk += userinput.nextLine();
					pk += ")";
				} else if (oppk.equalsIgnoreCase("N")) {
					// 기본키 절 안쓸거면 아무것도 안한다
				} else {
					// Y/N 둘다 아니면 예외처리
					throw new SQLException();
				}

				// 외래키 절 사용 여부 확인
				System.out.print("Input[FK][Y/N]:");
				String opfk = userinput.nextLine();
				int fkNum = 0;
				String[] fkArray = null;
				// 외래키 절 사용
				if (opfk.equalsIgnoreCase("Y")) {
					// 외래키 절 갯수 입력
					System.out.print("Input[# of FK CLAUSE]:");
					fkNum = Integer.parseInt(userinput.nextLine());
					// 그 갯수만큼 문자열 배열 생성
					fkArray = new String[fkNum];
					for (int i = 0; i < fkNum; i++) {
						// 외래키 제약조건 이름 사용 여부 확인
						System.out.print("Input[CONSTRAINT][NAME/N]:");
						String opconst = userinput.nextLine();
						if (opconst.equalsIgnoreCase("N")) {
						} else {
							// 이름 넣을거면 문자열 배열에 추가
							fkArray[i] = "CONSTRAINT " + opconst + " ";
						}
						// 외래키 입력
						System.out.print("Input[FK]:");
						String fk = userinput.nextLine();
						// 참조할 테이블 이름 입력
						System.out.print("Input[REFERENCE TABLE]:");
						String reftbl = userinput.nextLine();
						// 참조할 테이블의 애트리뷰트 입력
						System.out.print("Input[REFERENCE ATTRIBUTE]:");
						String refattr = userinput.nextLine();
						// 쿼리문 만들어서 문자열 배열에 저장
						fkArray[i] += "FOREIGN KEY (" + fk + ") REFERENCES " + reftbl + "(" + refattr + ")";

						// ON DELETE 옵션 사용한다면 option을 입력받고 아니면 N을 입력
						System.out.print("Input[ON DELETE][OPTION/N]:");
						String opondel = userinput.nextLine();
						if (opondel.equalsIgnoreCase("N")) {

						} else {
							// 문자열 배열에 옵션 추가
							fkArray[i] += " ON DELETE " + opondel;
						}

						// ON UPDATE 옵션 사용한다면 option을 입력받고 아니면 N을 입력
						System.out.print("Input[ON UPDATE][OPTION/N]:");
						String oponup = userinput.nextLine();
						if (oponup.equalsIgnoreCase("N")) {

						} else {
							// 문자열 배열에 옵션 추가
							fkArray[i] += " ON UPDATE " + oponup;
						}
					}
				} else if (opfk.equalsIgnoreCase("N")) {
					// 외래키 절 사용 안하면 아무것도 안한다.
				} else {
					// Y/N 이 아니면 예외처리
					throw new SQLException();
				}

				/* 쿼리문을 한 문자열로 만드는 과정 */
				for (int i = 0; i < attrNum - 1; i++) {
					sql += attrArray[i] + " , ";
				}
				sql += attrArray[attrNum - 1] + " ";

				if (oppk.equalsIgnoreCase("Y")) {
					sql += "," + pk;
					if (opfk.equalsIgnoreCase("Y")) {
						sql += " , ";
						for (int i = 0; i < fkNum - 1; i++) {
							sql += fkArray[i] + " , ";
						}
						sql += fkArray[fkNum - 1] + ")";
					} else {
						sql += ")";
					}
				} else if (opfk.equalsIgnoreCase("Y")) {
					sql += " , ";
					for (int i = 0; i < fkNum - 1; i++) {
						sql += fkArray[i] + " , ";
					}
					sql += fkArray[fkNum - 1] + ")";
				} else {
					sql += ")";
				}
				// 쿼리 실행
				stmt.executeUpdate(sql);
				chk = false;
			} catch (SQLException se) {
				// 실행한 쿼리가 에러가 있을경우(Syntax error 혹은 제약조건 위배 등등) 다시 입력받도록(while loop 재실행)
				System.out.println("SQL Exception!! Retry(syntax error or break constraint)");
				chk = true;
			}
		}
	}

	// 테이블 이름을 인자로 받아서 테이블을 삭제하는 메소드
	public static void DeleteTableQuery(String tblName) {
		userinput = new Scanner(System.in);
		boolean chk = true;
		String sql = "";
		// 에러가 없으면 while문 종료
		while (chk) {
			try {
				// 테이블 삭제시 옵션이 있는지 확인하고,
				System.out.print("Input[Option][Y/N]:");
				String op = userinput.nextLine();
				// 옵션이 있으면,
				if (op.equalsIgnoreCase("Y")) {
					System.out.print("Input[Option]:");
					String option = userinput.nextLine();
					// 추가하고,
					sql = "DROP TABLE " + tblName + " " + option;
				} else {
					// 없으면 그냥 DROP TABLE + 테이블이름
					sql = "DROP TABLE " + tblName;
				}
				// 쿼리 실행
				stmt.executeUpdate(sql);
				chk = false;
			} catch (SQLException se) {
				// 실행한 쿼리가 에러가 있을경우(Syntax error 혹은 제약조건 위배 등등) 다시 입력받도록(while loop 재실행)
				System.out.println("SQL Exception!! Retry(syntax error or break constraint)");
				chk = true;
			}
		}
	}

	// 메인 메뉴
	public static int mainMenu() {
		System.out.println("\n------------------------------[Choose Menu]------------------------------");
		System.out.println("0. Exit");
		System.out.println("1. Show Table List");
		System.out.println("2. Create New Table");
		System.out.println("3. Delete a Table");
		System.out.println("4. Choose Table");
		System.out.println("-------------------------------------------------------------------------");
		System.out.print("Input[Menu]: ");
		return userinput.nextInt();
	}

	// 메인 메뉴에서의 처리
	public static void process_main(int com) {
		String tblName;
		switch (com) {
		case 0:
			break;
		case 1:
			// 1. 테이블 목록을 본다
			System.out.println("\n------------------------------[Result]-----------------------------------");
			// 인터넷에서 찾은 코드
			try (ResultSet rs = conn.getMetaData().getTables(null, null, null, null)) {
				while (rs.next()) {
					tblName = rs.getString("TABLE_NAME");
					System.out.println(tblName);
				}
			} catch (SQLException e) {
				System.out.println("SQLException: " + e);
			}
			System.out.println("-------------------------------------------------------------------------");
			break;
		case 2:
			// 2. 새로운 테이블 생성
			// 테이블 이름 입력받아서
			System.out.print("Input[Table Name]: ");
			String createTbl = userinput.next();
			// 같은 이름의 테이블이 존재하면 break;
			if (tbllist.contains(createTbl)) {
				System.out.println("\nThe table alreay exist in Database!!!");
				break;
			}
			// 아니면 ,
			CreateTableQuery(createTbl);
			// 에러가 있으면 위의 메소드에서 catch 되었음
			System.out.println("Table '" + createTbl + "' is created!!!");
			// 생성된 테이블 이름을 ArrayList에 추가
			tbllist.add(createTbl);
			break;
		case 3:
			// 3. 테이블 삭제
			System.out.print("Input[Table Name]: ");
			// 테이블 이름을 입력 받아서
			String deleteTbl = userinput.next();
			// 같은 이름의 테이블이 존재하지 않으면 break;
			if (!tbllist.contains(deleteTbl)) {
				System.out.println("\nThe table does not exist in Database!!!");
				break;
			}
			// 아니면,
			DeleteTableQuery(deleteTbl);
			// 에러가 있으면 위의 메소드에서 catch 되었음
			System.out.println("Table '" + deleteTbl + "' is deleted!!!");
			// 생성된 테이블 이름을 ArrayList에서 제거
			if (tbllist.contains(deleteTbl)) {
				tbllist.remove(deleteTbl);
			} else {
				System.out.println("must remove table name. but not exist");
			}
			break;
		case 4:
			// 4. 테이블 선택
			int userChoice = 1;
			System.out.print("Input[Table Name]: ");
			// 테이블 이름을 입력 받아서
			String chooseTbl = userinput.next();
			// 같은 이름의 테이블이 존재하지 않으면 break;
			if (!tbllist.contains(chooseTbl)) {
				System.out.println("\nThe table does not exist in Database!!!");
				break;
			}
			// 테이블에 대한 메뉴로 가서, 0번(Exit)을 입력할 때 까지 while-loop
			while (userChoice != 0) {
				userChoice = tablemenu(chooseTbl);
				try {
					process_table(chooseTbl, userChoice);
				} catch (SQLException e) {
					System.out.println("SQLException: " + e);
				}
			}
			break;
		default:
			// 1~4번이 아 잘못된 입력
			System.out.println("Wrong command!!!");
			break;
		}
	}

	// 각 테이블에 대한 메뉴
	public static int tablemenu(String tblName) {
		System.out.println("\n------------------------------[TABLE:" + tblName + "]----------------------------");
		System.out.println("0. Return to previous menu");
		System.out.println("1. View Table");
		System.out.println("2. Insert");
		System.out.println("3. Update");
		System.out.println("4. Delete");
		System.out.println("5. Select");
		System.out.println("-------------------------------------------------------------------------");
		System.out.print("Input[Menu]: ");
		return userinput.nextInt();
	}

	// 각 테이블에 대한 처리
	public static void process_table(String tblName, int com) throws SQLException {
		switch (com) {
		case 0:
			break;
		case 1:
			// 1. 테이블 보기
			DBTablePrinter.printTable(conn, tblName);
			break;
		case 2:
			// 2. INSERT QUERY
			InsertQuery(tblName);
			System.out.println("\n[SUCCESS]:INSERT IN " + tblName + " !!!");
			break;
		case 3:
			// 3. UPDATE QUERY
			UpdateQuery(tblName);
			System.out.println("\n[SUCCESS]:UPDATE IN " + tblName + " !!!");
			break;
		case 4:
			// 4. DELETE QUERY
			DeleteQuery(tblName);
			System.out.println("\n[SUCCESS]:DELETE IN " + tblName + " !!!");
			break;
		case 5:
			// 5. SELECT QUERY
			SelectQuery(tblName);
			break;
		default:
			// Wrong Command
			System.out.println("Wrong command!!!");
			break;
		}
	}

	public static void main(String[] args) throws SQLException {
		userinput = new Scanner(System.in);
		int userChoice = 1;
		tbllist = new ArrayList<String>();
		LoadingDriver();
		ConnectDB();
		CreateStatement();
		StoreTableName();
		// Done until user choose #0(Exit)
		while (userChoice != 0) {
			userChoice = mainMenu();
			process_main(userChoice);
		}
		userinput.close(); // Scanner close
		CloseConnection(); // Connection close
		System.out.println("Exit!!");
	}

	// DB에 있는 테이블을 출력하기 위한 소스
	// 출처 : https://github.com/htorun/dbtableprinter
	static class DBTablePrinter {
		private static final int DEFAULT_MAX_ROWS = 100;
		private static final int DEFAULT_MAX_TEXT_COL_WIDTH = 150;
		public static final int CATEGORY_STRING = 1;
		public static final int CATEGORY_INTEGER = 2;
		public static final int CATEGORY_DOUBLE = 3;
		public static final int CATEGORY_DATETIME = 4;
		public static final int CATEGORY_BOOLEAN = 5;
		public static final int CATEGORY_OTHER = 0;

		public DBTablePrinter() {
			super();
		}

		static class Column {
			private String label;
			private int type;
			private String typeName;
			private int width = 0;
			private List<String> values = new ArrayList<>();
			private String justifyFlag = "";
			private int typeCategory = 0;

			public Column(String label, int type, String typeName) {
				this.label = label;
				this.type = type;
				this.typeName = typeName;
			}

			public String getLabel() {
				return label;
			}

			public int getType() {
				return type;
			}

			public String getTypeName() {
				return typeName;
			}

			public int getWidth() {
				return width;
			}

			public void setWidth(int width) {
				this.width = width;
			}

			public void addValue(String value) {
				values.add(value);
			}

			public String getValue(int i) {
				return values.get(i);
			}

			public String getJustifyFlag() {
				return justifyFlag;
			}

			public void justifyLeft() {
				this.justifyFlag = "-";
			}

			public int getTypeCategory() {
				return typeCategory;
			}

			public void setTypeCategory(int typeCategory) {
				this.typeCategory = typeCategory;
			}
		}

		public static void printTable(Connection conn, String tableName) {
			printTable(conn, tableName, DEFAULT_MAX_ROWS, DEFAULT_MAX_TEXT_COL_WIDTH);
		}

		public void printTable(Connection conn, String tableName, int maxRows) {
			printTable(conn, tableName, maxRows, DEFAULT_MAX_TEXT_COL_WIDTH);
		}

		public static void printTable(Connection conn, String tableName, int maxRows, int maxStringColWidth) {
			if (conn == null) {
				System.err.println("DBTablePrinter Error: No connection to database (Connection is null)!");
				return;
			}
			if (tableName == null) {
				System.err.println("DBTablePrinter Error: No table name (tableName is null)!");
				return;
			}
			if (tableName.length() == 0) {
				System.err.println("DBTablePrinter Error: Empty table name!");
				return;
			}
			if (maxRows < 1) {
				System.err.println("DBTablePrinter Info: Invalid max. rows number. Using default!");
				maxRows = DEFAULT_MAX_ROWS;
			}

			Statement stmt = null;
			ResultSet rs = null;
			try {
				if (conn.isClosed()) {
					System.err.println("DBTablePrinter Error: Connection is closed!");
					return;
				}

				String sqlSelectAll = "SELECT * FROM " + tableName + " LIMIT " + maxRows;
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sqlSelectAll);

				printResultSet(rs, maxStringColWidth);

			} catch (SQLException e) {
				System.err.println("SQL exception in DBTablePrinter. Message:");
				System.err.println(e.getMessage());
			} finally {
				try {
					if (stmt != null) {
						stmt.close();
					}
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException ignore) {
					// ignore
				}
			}
		}

		public void printResultSet(ResultSet rs) {
			printResultSet(rs, DEFAULT_MAX_TEXT_COL_WIDTH);
		}

		public static void printResultSet(ResultSet rs, int maxStringColWidth) {
			try {
				if (rs == null) {
					System.err.println("DBTablePrinter Error: Result set is null!");
					return;
				}
				if (rs.isClosed()) {
					System.err.println("DBTablePrinter Error: Result Set is closed!");
					return;
				}
				if (maxStringColWidth < 1) {
					System.err.println("DBTablePrinter Info: Invalid max. varchar column width. Using default!");
					maxStringColWidth = DEFAULT_MAX_TEXT_COL_WIDTH;
				}

				ResultSetMetaData rsmd;
				rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();

				List<Column> columns = new ArrayList<>(columnCount);

				List<String> tableNames = new ArrayList<>(columnCount);

				for (int i = 1; i <= columnCount; i++) {
					Column c = new Column(rsmd.getColumnLabel(i), rsmd.getColumnType(i), rsmd.getColumnTypeName(i));
					c.setWidth(c.getLabel().length());
					c.setTypeCategory(whichCategory(c.getType()));
					columns.add(c);

					if (!tableNames.contains(rsmd.getTableName(i))) {
						tableNames.add(rsmd.getTableName(i));
					}
				}

				int rowCount = 0;
				while (rs.next()) {

					for (int i = 0; i < columnCount; i++) {
						Column c = columns.get(i);
						String value;
						int category = c.getTypeCategory();

						if (category == CATEGORY_OTHER) {

							value = "(" + c.getTypeName() + ")";

						} else {
							value = rs.getString(i + 1) == null ? "NULL" : rs.getString(i + 1);
						}
						switch (category) {
						case CATEGORY_DOUBLE:

							if (!value.equals("NULL")) {
								Double dValue = rs.getDouble(i + 1);
								value = String.format("%.3f", dValue);
							}
							break;

						case CATEGORY_STRING:

							c.justifyLeft();
							if (value.length() > maxStringColWidth) {
								value = value.substring(0, maxStringColWidth - 3) + "...";
							}
							break;
						}
						c.setWidth(value.length() > c.getWidth() ? value.length() : c.getWidth());
						c.addValue(value);
					}
					rowCount++;

				}

				StringBuilder strToPrint = new StringBuilder();
				StringBuilder rowSeparator = new StringBuilder();
				for (Column c : columns) {
					int width = c.getWidth();

					String toPrint;
					String name = c.getLabel();
					int diff = width - name.length();

					if ((diff % 2) == 1) {
						width++;
						diff++;
						c.setWidth(width);
					}

					int paddingSize = diff / 2;

					String padding = new String(new char[paddingSize]).replace("\0", " ");

					toPrint = "| " + padding + name + padding + " ";

					strToPrint.append(toPrint);

					rowSeparator.append("+");
					rowSeparator.append(new String(new char[width + 2]).replace("\0", "-"));
				}

				String lineSeparator = System.getProperty("line.separator");

				lineSeparator = lineSeparator == null ? "\n" : lineSeparator;

				rowSeparator.append("+").append(lineSeparator);

				strToPrint.append("|").append(lineSeparator);
				strToPrint.insert(0, rowSeparator);
				strToPrint.append(rowSeparator);

				StringJoiner sj = new StringJoiner(", ");
				for (String name : tableNames) {
					sj.add(name);
				}

				System.out.println("\n------------------------------[Result]-----------------------------------");

				System.out.print(strToPrint.toString());

				String format;

				for (int i = 0; i < rowCount; i++) {
					for (Column c : columns) {
						format = String.format("| %%%s%ds ", c.getJustifyFlag(), c.getWidth());
						System.out.print(String.format(format, c.getValue(i)));
					}

					System.out.println("|");
					System.out.print(rowSeparator);
				}

				System.out.println();

			} catch (SQLException e) {
				System.err.println("SQL exception in DBTablePrinter. Message:");
				System.err.println(e.getMessage());
			}
		}

		private static int whichCategory(int type) {
			switch (type) {
			case Types.BIGINT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				return CATEGORY_INTEGER;

			case Types.REAL:
			case Types.DOUBLE:
			case Types.DECIMAL:
				return CATEGORY_DOUBLE;

			case Types.DATE:
			case Types.TIME:
			case Types.TIME_WITH_TIMEZONE:
			case Types.TIMESTAMP:
			case Types.TIMESTAMP_WITH_TIMEZONE:
				return CATEGORY_DATETIME;

			case Types.BOOLEAN:
				return CATEGORY_BOOLEAN;

			case Types.VARCHAR:
			case Types.NVARCHAR:
			case Types.LONGVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.CHAR:
			case Types.NCHAR:
				return CATEGORY_STRING;

			default:
				return CATEGORY_OTHER;
			}
		}
	}
}
