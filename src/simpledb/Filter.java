package simpledb;

import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */
public class Filter extends AbstractDbIterator {

    // Instance variables
    private final Predicate predicate;
    private final DbIterator child;

    /**
     * Constructor accepts a predicate to apply and a child
     * operator to read tuples to filter from.
     *
     * @param p The predicate to filter tuples with
     * @param child The child operator
     */
    public Filter(Predicate p, DbIterator child) {
        this.predicate = p;
        this.child = child;
    }

    /**
     * Returns the TupleDesc of the child operator.
     */
    public TupleDesc getTupleDesc() {
        return child.getTupleDesc();
    }

    /**
     * Opens the filter and its child iterator.
     */
    @Override
    public void open()
            throws DbException, NoSuchElementException, TransactionAbortedException {

        child.open();
    }

    /**
     * Closes the filter and its child iterator.
     */
    @Override
    public void close() {

        super.close();
        child.close();
    }

    /**
     * Rewinds the child iterator to the beginning.
     */
    @Override
    public void rewind() throws DbException, TransactionAbortedException {

        child.rewind();
    }

    /**
     * AbstractDbIterator.readNext implementation.
     * Iterates over tuples from the child operator, applying the predicate
     * to them and returning those that pass the predicate.
     *
     * @return The next tuple that passes the filter,
     *         or null if there are no more tuples.
     */
    @Override
    protected Tuple readNext()
            throws NoSuchElementException, TransactionAbortedException, DbException {

        while (child.hasNext()) {

            Tuple tuple = child.next();

            if (predicate.filter(tuple)) {
                return tuple;
            }
        }

        return null;
    }
}