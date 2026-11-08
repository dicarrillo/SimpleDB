package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection
 * of tuples in no particular order.  Tuples are stored on pages, each of
 * which is a fixed size, and the file is simply a collection of those
 * pages. HeapFile works closely with HeapPage.  The format of HeapPages
 * is described in the HeapPage constructor.
 *
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
    /**
     * Constructor.
     * Creates a new heap file that stores pages in the specified buffer pool.
     *
     * @param f The file that stores the on-disk backing store for this DbFile.
     */

    private final File f;

    public HeapFile(File f) {
        this.f = f;
    }

    /**
     * Return a Java File corresponding to the data from this HeapFile on disk.
     */
    public File getFile() {
        return f;
    }

    /**
     * @return an ID uniquely identifying this HeapFile
     *  (Implementation note:  you will need to generate this tableid somewhere,
     *    ensure that each HeapFile has a "unique id," and that you always
     *    return the same value for a particular HeapFile.  The implementation we
     *    suggest you use could hash the absolute file name of the file underlying
     *    the heapfile, i.e. f.getAbsoluteFile().hashCode()
     *    )
     */
    public int id() {
        return f.getAbsoluteFile().hashCode();
        // throw new UnsupportedOperationException("implement this");
    }

    /**
     * Returns a Page from the file.
     */
    public Page readPage(PageId pid) throws NoSuchElementException {
        byte[] data = new byte[bytesPerPage()];
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            raf.seek((long) pid.pageno() * bytesPerPage());
            raf.readFully(data);
            return new HeapPage((HeapPageId) pid, data);
        } catch (IOException e) {
            throw new NoSuchElementException("Could not read page " + pid.pageno() + " from file.");
        }
    }

    /**
     * Writes the given page to the appropriate location in the file.
     */
    public void writePage(Page page) throws IOException {
        // not necessary for this project
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        return (int) (f.length() / bytesPerPage());
    }

    /**
     * Adds the specified tuple to the table under the specified TransactionId.
     *
     * @throws DbException
     * @throws IOException
     * @return An ArrayList contain the pages that were modified
     */
    public ArrayList<Page> addTuple(TransactionId tid, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
    	// not necessary for this project
        return null;
    }

    /**
     * Deletes the specified tuple from the table, under the specified
     * TransactionId.
     */
    public Page deleteTuple(TransactionId tid, Tuple t)
        throws DbException, TransactionAbortedException {
    	// not necessary for this project
        return null;
    }

    /**
     * An iterator over all tuples on this file, over all pages.
     * Note that this iterator should use BufferPool.getPage(), rather than HeapFile.getPage()
     * to iterate through pages.
     */
    public DbFileIterator iterator(TransactionId tid) {
        return new DbFileIterator() {
            private int pageNo = 0;
            private Iterator<Tuple> it = null;
            private boolean open = false;

            public void open() throws DbException, TransactionAbortedException {
                open = true;
                pageNo = 0;
                it = loadPage(0);
            }

            private Iterator<Tuple> loadPage(int n)
                    throws DbException, TransactionAbortedException {
                HeapPageId pid = new HeapPageId(id(), n);
                HeapPage p = (HeapPage) Database.getBufferPool()
                        .getPage(tid, pid, Permissions.READ_ONLY);
                return p.iterator();
            }

            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (!open) return false;
                while (!it.hasNext() && pageNo + 1 < numPages()) {
                    pageNo++;
                    it = loadPage(pageNo);
                }
                return it.hasNext();
            }

            public Tuple next() throws DbException, TransactionAbortedException,
                    NoSuchElementException {
                if (!hasNext()) throw new NoSuchElementException();
                return it.next();
            }

            public void rewind() throws DbException, TransactionAbortedException {
                close();
                open();
            }

            public void close() {
                open = false;
                it = null;
            }
        };
    }
    /**
     * @return the number of bytes on a page, including the number of bytes
     * in the header.
     */
    public int bytesPerPage() {
        int numSlots = BufferPool.PAGE_SIZE / Database.getCatalog().getTupleDesc(id()).getSize();
        int headerBytes = (numSlots / 32 + 1) * 4;
        return BufferPool.PAGE_SIZE + headerBytes;
    }
}

