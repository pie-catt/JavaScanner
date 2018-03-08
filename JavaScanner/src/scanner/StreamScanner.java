package scanner;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class StreamScanner implements Scanner, AutoCloseable {
    private final Matcher matcher;
    private final BufferedReader buffReader;
    // the last result of invocation of next(); initially, all queries must throw IllegalStateException 
    private MatchResult result = Pattern.compile("").matcher("").toMatchResult();
    private String line; // currently processed line

    private String skip() { // to be called only if lookingAt() has failed
	String skipped;
	if (matcher.find()) { // still valid tokens in the region
	    skipped = line.substring(matcher.regionStart(), matcher.start());
	    matcher.region(matcher.start(), matcher.regionEnd());
	} else { // no valid tokens in the region
	    skipped = line.substring(matcher.regionStart(), matcher.regionEnd());
	    matcher.region(matcher.regionEnd(), matcher.regionEnd());
	}
	return skipped;
    }

    public StreamScanner(String regex, Reader reader) {
	matcher = Pattern.compile(regex).matcher("");
	buffReader = new BufferedReader(reader);
    }

    @Override
    public void next() throws IOException, ScannerException {
        /* try to advance the stream;
	   if a lexeme is recognized, then result is updated so that
	   the scanner is able to properly manage subsequent queries;
	   throws ScannerException if 
	   - the end of the stream is reached
	   - no lexeme is recognized 
	   in the last case, method skip is called to advance the stream
	*/
    	if (!hasNext())
			throw new ScannerException("Unexpected end of the stream");
		boolean matched = matcher.lookingAt();
		result = matcher.toMatchResult();
		if (!matched)
			throw new ScannerException("Unrecognized string ", skip());
		else
			matcher.region(matcher.end(), matcher.regionEnd());
    }

    @Override
    public boolean hasNext() throws IOException {
	/* return true iff the end of the stream has not been reached yet */ 
    	if (matcher.regionStart() == matcher.regionEnd()) {
			line = buffReader.readLine();
			if (line == null) {
				matcher.reset("");
				return false;
			}
			matcher.reset(line + " ");
		}
		return true;
    }

    @Override
    public String group() {
	return result.group();
    }

    @Override
    public String group(int group) {
	return result.group(group);
    }

    @Override
    public void close() throws Exception {
	buffReader.close();
    }

}
