package simpledb;

import java.util.NoSuchElementException;

/**
 * The Aggregation operator that computes an aggregate (e.g., sum, avg, max,
 * min) over a single column, grouped by a single column.
 */
public class Aggregate extends AbstractDbIterator {

    private DbIterator child;
    private final int afield;
    private final int gfield;
    private final Aggregator.Op aop;
    private final TupleDesc desc;
    private DbIterator results;

    /**
     * Constructor.
     *
     * @param child The DbIterator that is feeding this operator with tuples.
     * @param afield The column over which to compute an aggregate.
     * @param gfield The column over which to group by, or Aggregator.NO_GROUPING
     * @param aop The aggregation operator to use
     */
    public Aggregate(DbIterator child, int afield, int gfield, Aggregator.Op aop) {
        this.child = child;
        this.afield = afield;
        this.gfield = gfield;
        this.aop = aop;
        this.desc = gfield == Aggregator.NO_GROUPING
                ? new TupleDesc(new Type[] { Type.INT_TYPE })
                : new TupleDesc(new Type[] { child.getTupleDesc().getType(gfield), Type.INT_TYPE });
    }

    /**
     * @return the index of the group by field, or Aggregator.NO_GROUPING
     */
    public int groupField() {
        return gfield;
    }

    /**
     * @return the name of the group by field in the output tuples, or null if
     *         there is no grouping
     */
    public String groupFieldName() {
        return gfield == Aggregator.NO_GROUPING ? null : child.getTupleDesc().getFieldName(gfield);
    }

    /**
     * @return the index of the aggregate field
     */
    public int aggregateField() {
        return afield;
    }

    /**
     * @return the name of the aggregate field in the output tuples
     */
    public String aggregateFieldName() {
        return child.getTupleDesc().getFieldName(afield);
    }

    /**
     * @return the aggregate operator
     */
    public Aggregator.Op aggregateOp() {
        return aop;
    }

    public static String aggName(Aggregator.Op aop) {
        return aop.toString();
    }

    public void open() throws NoSuchElementException, DbException, TransactionAbortedException {
        TupleDesc childDesc = child.getTupleDesc();
        Type groupType = gfield == Aggregator.NO_GROUPING ? null : childDesc.getType(gfield);
        Aggregator aggregator = childDesc.getType(afield) == Type.INT_TYPE
                ? new IntAggregator(gfield, groupType, afield, aop)
                : new StringAggregator(gfield, groupType, afield, aop);

        child.open();
        while (child.hasNext()) {
            aggregator.merge(child.next());
        }
        results = aggregator.iterator();
        results.open();
    }

    /**
     * Returns the next tuple. If there is a group by field, then the first
     * field is the group by field, and the second field is the
     * result of computing the aggregate. If there is no group by field, then
     * the result tuple should contain one field representing the result of the
     * aggregate. Should return null if there are no more tuples.
     */
    protected Tuple readNext() throws TransactionAbortedException, DbException {
        return results.hasNext() ? results.next() : null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        results.rewind();
    }

    /**
     * Returns the TupleDesc of this Aggregate. If there is no group by field,
     * this will have one field - the aggregate column. If there is a group by
     * field, the first field will be the group by field, and the second will be
     * the aggregate value column.
     */
    public TupleDesc getTupleDesc() {
        return desc;
    }

    public void close() {
        if (results != null) results.close();
        child.close();
    }

}