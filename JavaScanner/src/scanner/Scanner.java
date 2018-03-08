package scanner;

import java.io.IOException;

public interface Scanner {
	void next() throws IOException, ScannerException;

	boolean hasNext() throws IOException;

	String group();

	String group(int group);
}