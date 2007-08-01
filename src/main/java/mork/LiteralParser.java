package mork;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;

/**
 * Parses literal Mork data and unescapes bytes according to the format
 * specification.
 * 
 * @author mhaller
 */
public class LiteralParser {

    /**
     * Parses the given content as Mork Literal data
     * 
     * @param reader
     *            the Mork-escaped content
     * @return unescaped literal content value
     * @throws IOException
     */
    public String parse(Reader reader)
        throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(64);
        do {
            int c = reader.read();
            if (c == -1) {
                break;
            }
            // End of literal
            if (c == ')') {
                break;
            }
            // Resolve hex-encoded literal value
            if (c == '$') {
                char[] cbuf = new char[2];
                int i = reader.read(cbuf);
                if (i != 2) {
                    throw new RuntimeException("Could not read Hex-Encoded Literal");
                }
                buf.put((byte)Integer.parseInt(new String(cbuf), 16));
                continue;
            }
            // Resolve Escaping
            if (c == '\\') {
                int d = reader.read();
                if (d == '\n' || d == '\r') {
                    continue;
                } else {
                    buf.put((byte)d);
                    continue;
                }
            }
            // In all other cases - plain character
            buf.put((byte)c);
        } while (true);
        return new String(buf.array(), 0, buf.position(), "UTF-8");
    }

}
