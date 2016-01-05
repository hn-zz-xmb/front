package com.meishi.front.common.util;

import com.meishi.util.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 映射数据库，自动生成Entity
 * 
 * @author WWF
 */
public class CreateJfinalEntityUtil {

	private static Connection conn = null;
	private static final String URL;
	private static final String JDBC_DRIVER;
	private static final String USER_NAME;
	private static final String PASSWORD;
	private static final String DATABASENAME;
	private static final String PACKAGENAME;
	static {
		DATABASENAME = "meishi_fabms";
		// URL = "jdbc:postgresql://localhost:5432/" + DATABASENAME;
		// JDBC_DRIVER = "org.postgresql.Driver";
		// USER_NAME = "数据库帐号";
		// PASSWORD = "密码";
		// PACKAGENAME = "xidian.wwf.entity";

		URL = "jdbc:jtds:sqlserver://192.168.2.168:1433/" + DATABASENAME;
		JDBC_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
		USER_NAME = "sa";
		PASSWORD = "37meishiwang";
		PACKAGENAME = "com.meishi.fabms.model";
	}

	/**
	 * 获得连接
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 关闭数据库
	 */
	public static void closeConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成字段静态声明
	 * 
	 * @param connection
	 * @param tableName
	 * @return
	 */
	private static String CreateEntityString(String tableName) {
		String result = "package " + PACKAGENAME + ";\n\n";
		result += "import com.jfinal.plugin.activerecord.Model; \n\n";
		result += "/**\n";
		result += "*<pre>\n";
		result += "*tableName:" + tableName + "\n";
		// String sql =
		// "select column_name from information_schema.columns where table_name = '"
		// + tableName + "';";
		// String sql = "Select Name FROM SysColumns Where id=Object_Id('"
		// + tableName + "')";

		String sql = "Select a.Name,g.value from SysColumns a"
				+ "	left join syscomments e" + "	on a.cdefault=e.id"
				+ "		left join sys.extended_properties g "
				+ "		on a.id=g.major_id and a.colid=g.minor_id ";

		sql += "Where a.id=Object_Id('" + tableName + "')";
		try {
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				if (resultSet.getString(1) != null
						&& !resultSet.getString(1).isEmpty()) {
					String column = resultSet.getString(1);
					String comment = resultSet.getString(2) == null ? ""
							: resultSet.getString(2);
					result += "* " + column + "   " + comment + "\n";
				}
			}
			resultSet.close();
			statement.close();

			result += "*</pre>\n";
			result += "*/\n";
			result += "public class " + StringUtil.splitString2(tableName)
					+ " extends Model<" + StringUtil.splitString2(tableName)
					+ ">{\n\n";
			result += "    private static final long serialVersionUID = 1L;\n";
			result += "    public static final "
					+ StringUtil.splitString2(tableName) + " dao = new "
					+ StringUtil.splitString2(tableName) + "();\n\n";
			// result += "    /**表名**/ \n";
			// result += "    public static String TABLE = \"" + tableName +
			// "\";";
			result += "\n }";
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得数据库的所有表名
	 * 
	 * @return
	 */
	public static List<String> getAllTables() {
		// String sql = "select relname as TABLE_NAME from pg_class c "
		// +
		// "where  relkind = 'r' and relname not like 'pg_%' and relname not like 'sql_%' order by relname";
		String sql = "Select Name FROM meishi_fabms.sys.SysObjects Where XType='U' orDER BY Name ";
		try {
			List<String> result = new ArrayList<String>();
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				if (resultSet.getString(1) != null
						&& !resultSet.getString(1).isEmpty()) {
					result.add(resultSet.getString(1));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * 生成Entity
	 */
	public static void CreateEntity(String tableName) {
		try {
			getConnection();
			if(tableName!=null && !"".equals(tableName)) {
				File createFolder = new File(System.getProperty("user.dir")
						+ "/src/" + PACKAGENAME.replace(".", "/"));

				// 路径不存在，生成文件夹
				if (!createFolder.exists()) {
					createFolder.mkdirs();
				}
				System.out.println(createFolder);
				String entityString = CreateEntityString(tableName.trim());
				File entity = new File(createFolder.getAbsolutePath() + "/"
						+ StringUtil.splitString2(tableName) + ".java");
				if (!entity.exists()) {
					// 写入文件
					BufferedWriter out = new BufferedWriter(new FileWriter(
							entity, true));
					out.write(entityString);
					out.close();
					out = null;
					entity = null;
				}
			}else{
				List<String> tables = getAllTables();
				for (int i = 0; i < tables.size(); i++) {
					File createFolder = new File(System.getProperty("user.dir")
							+ "/src/" + PACKAGENAME.replace(".", "/"));

					// 路径不存在，生成文件夹
					if (!createFolder.exists()) {
						createFolder.mkdirs();
					}
					System.out.println(createFolder);
					String entityString = CreateEntityString(tables.get(i).trim());
					File entity = new File(createFolder.getAbsolutePath() + "/"
							+ StringUtil.splitString2(tables.get(i)) + ".java");
					if (!entity.exists()) {
						// 写入文件
						BufferedWriter out = new BufferedWriter(new FileWriter(
								entity, true));
						out.write(entityString);
						out.close();
						out = null;
						entity = null;
					}
				}
			}
			closeConnection();
			System.out.println("生成成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CreateEntity("");
	}

}