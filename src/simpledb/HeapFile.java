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
    public HeapFile(File f) {
        // some code goes here
    }

    /**
     * Return a Java File corresponding to the data from this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return null;
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
        // some code goes here
        throw new UnsupportedOperationException("implement this");
    }

    /**
     * Returns a Page from the file.
     */
    public Page readPage(PageId pid) throws NoSuchElementException {
        // some code goes here
        return null;
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
        // some code goes here
        return 0;
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
        // some code goes here
        return null;
    }

    /**
     * @return the number of bytes on a page, including the number of bytes
     * in the header.
     */
    public int bytesPerPage() {
        // some code goes here
        return 0;
    }
}

