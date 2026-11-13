package simpledb;

import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntAggregator implements Aggregator {

    private final int gbfield;
    private final int afield;
    private final Op what;
    private final TupleDesc desc;
    // Keyed by group value (null when there is no grouping)
    private final Map<Field, Integer> values = new LinkedHashMap<>();
    private final Map<Field, Integer> counts = new LinkedHashMap<>();

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what the aggregation operator
     */

    public IntAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        this.gbfield = gbfield;
        this.afield = afield;
        this.what = what;
        this.desc = gbfield == NO_GROUPING
                ? new TupleDesc(new Type[] { Type.INT_TYPE })
                : new TupleDesc(new Type[] { gbfieldtype, Type.INT_TYPE });
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void merge(Tuple tup) {
        Field group = gbfield == NO_GROUPING ? null : tup.getField(gbfield);
        int value = ((IntField) tup.getField(afield)).getValue();
        counts.merge(group, 1, Integer::sum);
        switch (what) {
            case MIN: values.merge(group, value, Math::min); break;
            case MAX: values.merge(group, value, Math::max); break;
            default:  values.merge(group, value, Integer::sum); // SUM, AVG, COUNT
        }
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        List<Tuple> tuples = new ArrayList<>();
        for (Field group : values.keySet()) {
            int result = values.get(group);
            if (what == Op.COUNT) result = counts.get(group);
            if (what == Op.AVG) result /= counts.get(group);

            Tuple t = new Tuple(desc);
            if (gbfield == NO_GROUPING) {
                t.setField(0, new IntField(result));
            } else {
                t.setField(0, group);
                t.setField(1, new IntField(result));
            }
            tuples.add(t);
        }
        return new TupleIterator(desc, tuples);
    }

}