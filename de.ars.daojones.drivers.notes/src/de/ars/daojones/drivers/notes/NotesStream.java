package de.ars.daojones.drivers.notes;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;
import lotus.domino.Stream;

/**
 * A class that wraps a Notes {@link Stream} to an {@link InputStream}
 *
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class NotesStream extends InputStream {

    private static final Logger logger = Logger.getLogger(NotesStream.class.getName());
    private final Stream stream;

    /**
     * @param stream
     */
    public NotesStream(final Stream stream) {
        super();
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        try {
            final byte[] result = this.stream.read(1);
            return result.length<1 ? -1 : (result[0]+256)%256;
        } catch (NotesException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        // Parameter Check
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
               ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        
        try {
            final byte[] bytesRead = this.stream.read(len);
            System.arraycopy(bytesRead, 0, b, off, bytesRead.length);
            return bytesRead.length;
        } catch (NotesException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public int available() throws IOException {
        try {
            return this.stream.getBytes();
        } catch (NotesException e) {
            logger.log(Level.SEVERE, "The length of the stream could not be read!", e);
            return -1;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.stream.close();
            this.stream.recycle();
        } catch (NotesException e) {
            logger.log(Level.WARNING, "Was not able to close stream!", e);
        }
        super.close();
    }

    /* *****************************************************
     *   M A R K / R E S E T   F U N C T I O N A L I T Y   *
     ***************************************************** */
    private static class Mark {
        private final int startPosition;
        private final int endPosition;
        /**
         * @param startPosition
         * @param endPosition
         */
        public Mark(final int startPosition, final int endPosition) {
            super();
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
        public int getEndPosition() {
            return endPosition;
        }
        public int getStartPosition() {
            return startPosition;
        }
    }
    
    private List<Mark> marks = new LinkedList<Mark>();
    
    @Override
    public synchronized void reset() throws IOException {
        try {
            final int pos = this.stream.getPosition();
            for(ListIterator<Mark> it = marks.listIterator(); it.hasPrevious(); ) {
                final Mark mark = it.previous();
                if(pos>mark.getEndPosition()) {
                    it.remove();
                } else {
                    this.stream.setPosition(mark.getStartPosition());
                    break;
                }
            }
            throw new IOException("There is no valid mark!");
        } catch (NotesException e) {
            logger.log(Level.WARNING, "The stream could not be reset!", e);
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            final int pos = this.stream.getPosition();
            this.marks.add(new Mark(pos, pos+readlimit));
        } catch (NotesException e) {
            logger.log(Level.WARNING, "The mark could not be set!", e);
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }
    
}
