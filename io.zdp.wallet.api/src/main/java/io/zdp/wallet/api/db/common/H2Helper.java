package io.zdp.wallet.api.db.common;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2Helper {

	private static final Logger log = LoggerFactory.getLogger(H2Helper.class);

	public static boolean isValidH2Database(File file, String password) {

		if (false == file.exists()) {
			return true;
		}

		try {

			Class.forName("org.h2.Driver");

			String url = "jdbc:h2:zdp:" + file.getAbsolutePath() + ";CIPHER=AES;IFEXISTS=TRUE";
			Connection conn = DriverManager.getConnection(url, "zdp", password);

			conn.close();

			return true;

		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return false;

	}
}
