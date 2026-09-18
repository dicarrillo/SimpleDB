package simpledb;

import java.util.*;

/**
 * The Catalog keeps track of all available tables in the database and their
 * associated schemas.
 * For now, this is a stub catalog that must be populated with tables by a
 * user program before it can be used -- eventually, this should be converted
 * to a catalog that reads a catalog table from disk.
 */

public class Catalog {

    private static class TableInfo {
        final DbFile file;
        final TupleDesc desc;
        final String name;
 
        TableInfo(DbFile file, TupleDesc desc, String name) {
            this.file = file;
            this.desc = desc;
            this.name = name;
        }
    }
 
    // tableid -> TableInfo
    private final Map<Integer, TableInfo> tableIdToInfo;
    // table name -> tableid 
    private final Map<String, Integer> nameToTableId;


    public Catalog() {
        tableIdToInfo = new HashMap<Integer, TableInfo>();
        nameToTableId = new HashMap<String, Integer>();
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.id() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     * @param t the format of tuples that are being added
     * @param name the name of the table -- may be an empty string.  May not be null.  If a name
     * conflict exists, use the last table to be added as the table for a given name.
     */
    public void addTable(DbFile file, TupleDesc t, String name) {
        if (name == null) {
            throw new IllegalArgumentException("table name may not be null");
        }
 
        int id = file.id();
 
        // Last table added with a given name wins the name -> id mapping.
        tableIdToInfo.put(id, new TableInfo(file, t, name));
        nameToTableId.put(name, id);
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.id() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     * @param t the format of tuples that are being added
     */
    public void addTable(DbFile file, TupleDesc t) {
        addTable(file, t, "");
    }

    /**
     * Return the id of the table with a specified name,
     * @throws NoSuchElementException if the table doesn't exist
     */
    public int getTableId(String name) throws NoSuchElementException {
        if (name == null || !nameToTableId.containsKey(name)) {
            throw new NoSuchElementException("no table named " + name);
        }
        return nameToTableId.get(name);
    }

    /**
     * Returns the tuple descriptor (schema) of the specified table
     * @param tableid The id of the table, as specified by the DbFile.id()
     *     function passed to addTable
     */
    public TupleDesc getTupleDesc(int tableid) throws NoSuchElementException {
        TableInfo info = tableIdToInfo.get(tableid);
        if (info == null) {
            throw new NoSuchElementException("no table with id " + tableid);
        }
        return info.desc;
    }

    /**
     * Returns the DbFile that can be used to read the contents of the
     * specified table.
     * @param tableid The id of the table, as specified by the DbFile.id()
     *     function passed to addTable
     */
    public DbFile getDbFile(int tableid) throws NoSuchElementException {
        TableInfo info = tableIdToInfo.get(tableid);
        if (info == null) {
            throw new NoSuchElementException("no table with id " + tableid);
        }
        return info.file;
    }

    /** Delete all tables from the catalog */
    public void clear() {
        tableIdToInfo.clear();
        nameToTableId.clear();
    }

}
