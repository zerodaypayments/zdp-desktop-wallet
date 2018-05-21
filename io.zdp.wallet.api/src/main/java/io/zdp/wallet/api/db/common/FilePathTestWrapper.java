package io.zdp.wallet.api.db.common;

import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;

public class FilePathTestWrapper extends FilePathWrapper {
	
	private static final String[][] MAPPING = { { ".h2.db", ".zdpdb" }, { ".mv.db", ".zdp" }, { ".lock.db", ".zdp.lock" } };

	@Override
	public String getScheme() {
		return "zdp";
	}

	@Override
	public FilePathWrapper wrap(FilePath base) {
		// base.toString() returns base.name
		FilePathTestWrapper wrapper = (FilePathTestWrapper) super.wrap(base);
		wrapper.name = getPrefix() + wrapExtension(base.toString());
		return wrapper;
	}

	@Override
	protected FilePath unwrap(String path) {
		String newName = path.substring(getScheme().length() + 1);
		newName = unwrapExtension(newName);
		return FilePath.get(newName);
	}

	protected static String wrapExtension(String fileName) {
		for (String[] pair : MAPPING) {
			if (fileName.endsWith(pair[1])) {
				fileName = fileName.substring(0, fileName.length() - pair[1].length()) + pair[0];
				break;
			}
		}
		return fileName;
	}

	protected static String unwrapExtension(String fileName) {
		for (String[] pair : MAPPING) {
			if (fileName.endsWith(pair[0])) {
				fileName = fileName.substring(0, fileName.length() - pair[0].length()) + pair[1];
				break;
			}
		}
		return fileName;
	}
}