package simpledb;
import java.util.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {

    private final TransactionId tid;
    private final int tableid;
    private final String tableAlias;

    // Iterator over the underlying DbFile; null until open() is called
    private DbFileIterator it = null;

    /**
     * Constructor.
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     *
     * @param tid The transaction this scan is running as a part of.
     * @param tableid the table to scan.
     * @param tableAlias the alias of this table (needed by the parser);
     *         the returned tupleDesc should have fields with name tableAlias.fieldName
     *         (note: this class is not responsible for handling a case where tableAlias
     *         or fieldName are null.  It shouldn't crash if they are, but the resulting
     *         name can be null.fieldName, tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        this.tid = tid;
        this.tableid = tableid;
        this.tableAlias = tableAlias;
    }

    /**
     * Opens this sequential scan.
     * Needs to be called before getNext().
     */
    public void open()
        throws DbException, TransactionAbortedException {
        it = Database.getCatalog().getDbFile(tableid).iterator(tid);
        it.open();
    }

    /**
     * Implementation of DbIterator.getTupleDesc method.
     * Should return a tupleDesc with field names from the underlying HeapFile with field
     *   names prefaced by the passed in tableAlias string
     */
    public TupleDesc getTupleDesc() {
        TupleDesc base = Database.getCatalog().getTupleDesc(tableid);
        int n = base.numFields();

        Type[] types = new Type[n];
        String[] names = new String[n];
        for (int i = 0; i < n; ++i) {
            types[i] = base.getType(i);
            // String concatenation turns a null alias/name into "null",
            // giving null.fieldName / tableAlias.null / null.null as allowed above.
            names[i] = tableAlias + "." + base.getFieldName(i);
        }
        return new TupleDesc(types, names);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        if (it == null) {
            return false;
        }
        return it.hasNext();
    }

    /**
     * Implementation of DbIterator.getNext method.
     * Return the next tuple in the scan.
     *
     * @throws NoSuchElementException if there are no more tuples (or the scan is not open)
     */
    public Tuple next()
        throws NoSuchElementException, TransactionAbortedException, DbException {
        if (it == null) {
            throw new NoSuchElementException("SeqScan is not open");
        }
        return it.next();
    }

    /**
     * Closes the sequential scan.
     */
    public void close() {
        if (it != null) {
            it.close();
            it = null;
        }
    }

    /**
     * Rewinds the sequential back to the first record.
     */
    public void rewind()
        throws DbException, NoSuchElementException, TransactionAbortedException {
        if (it == null) {
            throw new IllegalStateException("SeqScan is not open");
        }
        it.rewind();
    }
}