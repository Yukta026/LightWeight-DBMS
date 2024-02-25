import java.util.*;

class Table
{
    /* Storing Table Name in String table_name*/
    public static String table_name;

    /* Storing Columns names in String array columns*/
    public static String[] columns;

    /* Storing Rows data in Map<Integer, String[]> record format*/
    public static Map<Integer, String[]> record;

    /*
     * @params  table_name - name of table
     * @params  columns - columns of table
     */
    public Table(String table_name,String[] columns)
    {
        this.table_name = table_name;
        this.columns = columns;
        this.record= new HashMap<>();
    }

    public Table() {
        String[] columns;
    }

    /*
     * @params table_data - row data provided by execution of insert query
     */
    public void insertRecord(String[] table_data)
    {
        record.put(record.size()+1, table_data);
        System.out.println(record);

    }

    /*
     *  printTable function called to display table contents when select query is executed
     */
    public void printTable()
    {
        System.out.println("Table: " + table_name);
        System.out.println("Columns: " + String.join(", ", columns));
        for (Map.Entry<Integer, String[]> entry : record.entrySet())
        {
            System.out.println("Row " + entry.getKey() + ": " + String.join(", ", entry.getValue()));
        }
    }
}