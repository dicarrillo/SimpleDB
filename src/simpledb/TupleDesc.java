package simpledb;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc {

    // List holding field type-name pairs
    private ArrayList<TDField> fields = new ArrayList<TDField>();

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields
     * fields, with the first td1.numFields coming from td1 and the remaining
     * from td2.
     * @param td1 The TupleDesc with the first fields of the new TupleDesc
     * @param td2 The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc combine(TupleDesc td1, TupleDesc td2) 
    {
        int td1Size = td1.numFields();
        int td2Size = td2.numFields();
        int totalFields = td1Size + td2Size;

        // Create common types and name arrays
        Type[] typesList = new Type[totalFields];
        String[] namesList = new String[totalFields];

        // Add data from td1
        for (int i = 0; i < td1Size; ++i)
        {
            typesList[i] = td1.getType(i);
            namesList[i] = td1.getFieldName(i);
        }

        // Add data from td2
        for (int i = 0; i < td2Size; ++i)
        {
            typesList[i + td1Size] = td2.getType(i);
            namesList[i] = td2.getFieldName(i);
        }

        // Create new TupleDesc object with TupleDesc constructor
        return new TupleDesc(typesList, namesList);
    }

    /**
     * Constructor.
     * Create a new tuple desc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) 
    {
        for (int i = 0; i < typeAr.length; ++i)
        {
            // Add new type-name pair
            this.fields.add(new TDField(typeAr[i], fieldAr[i]));
        }
    }

    /**
     * Constructor.
     * Create a new tuple desc with typeAr.length fields with fields of the
     * specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) 
    {
        for (int i = 0; i < typeAr.length; ++i)
        {
            // Add new type-name pair with null name
            this.fields.add(new TDField(typeAr[i], null));
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        return this.fields.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException 
    {
        if (i < 0 || i >= this.fields.size()) {
            throw new NoSuchElementException("Argument index is out of bounds.");
        }

        return this.fields.get(i).name;
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException 
    {
        if (name == null) {
            throw new NoSuchElementException("Identified name cannot be null.");
        }

        // Walk through fields list until name is found
        for (int i = 0; i < this.fields.size(); ++i)
        {
            if (name.equals(this.fields.get(i).name))
            {   // Match found
                return i;
            }
        }

        throw new NoSuchElementException("Identified name not found.");
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException 
    {
        if (i < 0 || i >= this.fields.size()) {
            throw new NoSuchElementException("Argument index is out of bounds.");
        }

        return this.fields.get(i).type;
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     * Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() 
    {
        int totalSize = 0;

        for (int i = 0; i < this.fields.size(); ++i)
        {
            // Add byte size of current element to total
            totalSize += this.fields.get(i).type.getLen();
        }

        return totalSize;
    }

    /**
     * Compares the specified object with this TupleDesc for equality.
     * Two TupleDescs are considered equal if they are the same size and if the
     * n-th type in this TupleDesc is equal to the n-th type in td.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) 
    {
        if (!(o instanceof TupleDesc)) 
        {   // Object is of the incorrect type
            return false;
        }

        // Cast object to TupleDesc type
        TupleDesc compDesc = (TupleDesc) o;

        if (compDesc.numFields() != this.numFields()) 
        {   // Incorrect size
            return false;
        }

        for (int i = 0; i < this.fields.size(); ++i)
        {
            if (!this.fields.get(i).type.equals(compDesc.fields.get(i).type))
            {   // Incorrect type
                return false;
            }
        }

        return true;
    }

    // Compute the hash code of a TupleDesc object
    // Two TupleDesc object's hash codes will be equal if TD1.equals(TD2) is true.
    public int hashCode() 
    {
        int hashValue = 0;

        for (int i = 0; i < this.fields.size(); ++i)
        {
            // Update hash value (* 31, + type hash)
            hashValue = hashValue * 31 + this.fields.get(i).type.hashCode();
        }

        return hashValue;
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * @return String describing this descriptor.
     */
    public String toString() 
    {
        String finalString = "";

        for (int i = 0; i < this.fields.size(); ++i)
        {
            finalString = finalString + this.fields.get(i).type.toString() + "(" + this.fields.get(i).name + ")";

            if (i < this.fields.size() - 1) {
                finalString += ", ";
            }
        }

        return finalString;
    }
}

// A TDField object holds the field's type and name
class TDField
{
    Type type;
    String name;

    TDField(Type type, String name)
    {
        this.type = type;
        this.name = name;
    }
}
