import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Query extends Table
{
    /* Initializing table variables */
    private Map<String, Table> tables= new HashMap<>();
    private String table_name;
    private static Map<String, List<Table>> db_record;


    /* Declaring string variable for accessing text file */
    static final String user_log_file = "user_log.txt";

    /* For storing user input query and data*/
    private  List<String> transactionQueries;
    private List<String> querydata;

    /* Boolean variable db_created checks for only one database creation */
    private static boolean db_created = false;

    /* Initializing variables*/
    public Query()
    {
        tables = tables;
        transactionQueries = new ArrayList<>();
        querydata = new ArrayList<>();
        db_record = new HashMap<>();
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException
    {
        /* Creating new instances of classes */
        Query query = new Query();
        Scanner sc = new Scanner(System.in);

        /* While loop execution for getting user input queries */
        boolean execution = true;
        while(execution)
        {
            System.out.println("Enter SQL Query or type 'quit' to quit: ");
            String input_query = sc.nextLine();
            if (input_query.equalsIgnoreCase("quit"))
            {
                execution = false;
                return;
            }
            query.executeQuery(input_query);
        }
    }

    /*
    * executeQuery function handles all DML ,DDL and transactions commands
    * @params input_query - query input by user
    * @return transactionQueries - contains committed data to store into persistent storage
    */
    public List executeQuery(String input_query) throws IOException
    {
        /* Splitting user input query and storing command name in cmd_name
        * For example - input_query = CREATE DATABASE data;
        * cmd_name = CREATE
        */
        String[] input_query_array = input_query.split(" ",2);
        String cmd_name = input_query_array[0].toUpperCase();
        String parameters = input_query_array.length > 1 ? input_query_array[1] : "";

        /* Matching cmd_name against list of available queries*/
        switch(cmd_name){

            /* DDL commands */
            case "CREATE":
                transactionQueries.add(input_query);
                createqueryexec(parameters);
                break;

            case "DROP":
                transactionQueries.add(input_query);
                dropqueryexec(parameters);
                break;

            case "TRUNCATE":
                transactionQueries.add(input_query);
                truncatequeryexec(parameters);
                break;

            /* DML commands */
            case "INSERT":
                transactionQueries.add(input_query);
                insertqueryexec(parameters);
                break;

            case "SELECT":
                transactionQueries.add(input_query);
                selectqueryexec(parameters);
                break;

            case "UPDATE":
                transactionQueries.add(input_query);
                updatequeryexec(parameters);
                break;

            case "DELETE":
                transactionQueries.add(input_query);
                deletequeryexec(parameters);
                break;

            /* Transaction commands */
            case "BEGIN":
                begintransaction(parameters);
                break;

            case "COMMIT":
                committransaction(parameters);
                break;

            case "ROLLBACK":
                rollbacktransaction(parameters);
                break;

            case "END":
                endtransaction(parameters);
                break;

            default:
                System.out.println("Invalid SQL Query!");
        }
        return transactionQueries;
    }

    private  void truncatequeryexec(String parameters)
    {
        /* Splitting query by spaces and storing in parameter_parts */
        String[] parameter_parts = parameters.split(" ");

        /*
        * Checking for validation of input query
        * For example - parameters = TRUNCATE TABLE table1;
        */
        if (parameter_parts.length < 1 || parameter_parts[0].equalsIgnoreCase("TABLE"))
        {
            String table_Name = parameter_parts[1];
            Table fetchTable = tables.get(table_Name);

            /* Checking whether table exists in database */
            if (fetchTable == null)
            {
                System.out.println("Table doesn't exist");
                return;
            }

            /* Truncating table from database */
            tables.put(table_Name, null);
            System.out.println(tables);
        }
    }

    private void rollbacktransaction(String parameters)
    {
        /* Emptying data structure on rollback call */
        transactionQueries.clear();
    }

    private void committransaction(String parameters)
    {
        try(BufferedWriter bf= new BufferedWriter(new FileWriter(user_log_file,true)))
        {
            /* Writing queries and query data in persistent storage text file i.e. user_log.txt */
            for(int i=0;i<transactionQueries.size();i++)
            {
                String data = transactionQueries.get(i);
                bf.write("Query "+(i+1)+" - ");
                bf.write(data);
                bf.newLine();
            }
            for(int i=0;i<querydata.size();i++)
            {
                String data1 = querydata.get(i);
                bf.write("Query Data"+(i+1)+" - ");
                bf.write(data1);
                bf.newLine();
            }
        }
        catch (IOException e)
        {
                throw new RuntimeException(e);
        }
    }

    private  void begintransaction(String parameters)
    {
        String[] parameter_parts = parameters.split(" ");
        /*
         * Transaction input query validation
         * For example - Query = BEGIN TRANSACTION ;
         */
        if (parameter_parts.length < 1 || !parameter_parts[0].equalsIgnoreCase("TRANSACTION"))
        {
            System.out.println("Invalid SQL Query!");
            return;
        }
        System.out.println("Transaction Started");
    }

    private  void endtransaction(String parameters)
    {
        String[] parameter_parts = parameters.split(" ");
        /*
         * Transaction input query validation
         * For example - Query = END TRANSACTION ;
         */
        if (parameter_parts.length < 1 || !parameter_parts[0].equalsIgnoreCase("TRANSACTION"))
        {
            System.out.println("Invalid SQL Query!");
            return;
        }
        System.out.println("Transaction Ended");
    }

    private  void dropqueryexec(String parameters)
    {
        String[] parameter_parts = parameters.split(" ");

        /*
         * Checking for query validation
         * For example - Query = DROP TABLE table1 ;
         */
        if (parameter_parts.length < 1 || !parameter_parts[0].equalsIgnoreCase("TABLE"))
        {
            System.out.println("Invalid SQL Query!");
        }
        String table_Name = parameter_parts[1];

        /* Checking for existence of table */
        if (!tables.containsKey(table_Name))
        {
                System.out.println("Table doesn't exist");
                return;
        }
        /* Removing table from data structure */
        tables.remove(table_Name);
        System.out.println(tables);
    }


    private  void deletequeryexec(String parameters)
    {
        String[] parameter_parts = parameters.split(" ");

        /* Checking fo query validation
        * For example - Query = DELETE FROM table1 WHERE b = 2 ;
        */
        if (parameter_parts.length < 4 || parameter_parts[0].equalsIgnoreCase("FROM") || parameter_parts[2].equalsIgnoreCase("WHERE"))
        {
            /* Splitting query to get condition */
            int whereIndex = parameters.indexOf("WHERE");
            int where_condition_index = parameters.lastIndexOf("=");
            String col_name = parameters.substring(whereIndex+6,where_condition_index).trim();
            String col_value = parameters.substring(where_condition_index+1,parameters.indexOf(";")).trim();
            System.out.println("Col name: "+col_name);
            System.out.println("Col value: "+col_value);

            /* Fetching index of record to be deleted */
            int condition_value_index = 0;
            for (int j = 0; j < columns.length; j++)
            {
                String s = columns[j];
                String[] sp = s.split(" ");
                for(String match : sp){
                    if(col_name.equals(match))
                    {
                        condition_value_index = j;
                        System.out.println("condition value index: "+condition_value_index);
                        System.out.println("Given column exists");
                        break;
                    }
                }
            }

            /* Storing records of table that match condition */
            List<Integer> condition_value_keys = new ArrayList<>();
            for (Map.Entry<Integer, String[]> entry : record.entrySet())
            {
                String[] values = entry.getValue();
                if (values[condition_value_index].equals(col_value))
                {
                    condition_value_keys.add(entry.getKey());
                }
            }

            /* Removing records from data structure */
            for (Integer key : condition_value_keys)
            {
                record.remove(key);
                querydata.add("Deleted record: "+condition_value_keys);
            }
            System.out.println("Record deleted successfully!");
        }
        else
        {
            System.out.println("Invalid SQL Query!");
            return;
        }
    }

    private  void updatequeryexec(String parameters)
    {
        String[] parameter_parts = parameters.split(" ");

        /* Checking for update query validation
        * For example - Query = update table1 SET a = 3 WHERE b = 2 ;
        * Checking whether SET is present at appropriate index in query
        * */
        if (parameter_parts.length < 3 || !parameter_parts[1].equalsIgnoreCase("SET"))
        {
            System.out.println("Invalid SQL Query!");
            return;
        }

        /* Splitting query on the basis of index position of SET and WHERE in query */
        int setIndex = parameters.indexOf("SET");
        int equalIndex = parameters.indexOf("=");
        int whereIndex = parameters.indexOf("WHERE");
        int where_condition_index = parameters.lastIndexOf("=");

        String col_name = parameters.substring(setIndex + 4,equalIndex).trim();
        String new_value = parameters.substring(equalIndex + 1,whereIndex).trim();
        String condition_value = parameters.substring(where_condition_index + 1,parameters.indexOf(";")).trim();
        String[] condition = parameters.split("WHERE ");
        String where_condition = condition[1];
        String[] where_condition_split = where_condition.split(" ");
        String where_condition_col = where_condition_split[0].trim();

        String table_Name = parameter_parts[0];
        Table fetchTable = tables.get(table_Name);

        /*
        * Finding and fetching index of column whose value is to be updated
        * For example - Query - update table1 SET a = 3 WHERE b = 2 ;
        * Finding whether b is a column of table1
        */
        if (fetchTable == null) {
            System.out.println("Table has no entries");
            return;
        }

        /*
         * Finding and fetching index of condition column
         * For example - Query - update table1 SET a = 3 WHERE b = 2 ;
         * Finding whether a is a column of table1
         */
        int new_value_index = 0;

        for (int i = 0; i < columns.length; i++)
        {
            String s = columns[i];
           String[] sp = s.split(" ");
           for(String match : sp)
           {
               if(col_name.equals(match))
               {
                   new_value_index = i;
                   break;
               }
           }
        }

        /* Finding index of given column of condition */
       int condition_value_index = 0;
        for (int j = 0; j < columns.length; j++) {
            String s = columns[j];
            String[] sp = s.split(" ");
            for(String match : sp)
            {
                if(where_condition_col.equals(match))
                {
                    condition_value_index = j;
                    break;
                }

            }
        }

       /* Updating record */
        List<Integer> condition_value_keys = new ArrayList<>();
        for (Map.Entry<Integer, String[]> entry : record.entrySet()) {
            String[] values = entry.getValue();
            if (values[condition_value_index].equals(condition_value)) {

                condition_value_keys.add(entry.getKey());
            }
        }
        for (Integer key : condition_value_keys)
        {
            String[] old_Value = record.get(key);
            old_Value[new_value_index] = new_value;
            querydata.add("Updated Row: "+ Arrays.toString(old_Value));

        }
        System.out.println("Record updated successfully!");
    }



    private void selectqueryexec(String query)
    {
        String[] parts = query.split(" ");

        /* Checking for input select query validation
        * For example- Query = SELECT * FROM table1 ;
        * Checking whether FROM is present in query
        * */
        if (parts.length < 3 || !parts[1].equalsIgnoreCase("FROM")) {
            System.out.println("Invalid SELECT query.");
            return;
        }
        String tableName = parts[2];
        Table table = tables.get(tableName);

        /* Checking whether table exists */
        if (table == null) {
            System.out.println("Table not found: " + tableName);
            return;
        }
        /* Printing table contents */
        table.printTable();
    }



    private  void insertqueryexec(String parameters)
    {
        String[] parameter_parts = parameters.split(" ");

        /* Validation checking of given insert query */
        if(parameter_parts.length < 4 || !parameter_parts[0].equalsIgnoreCase("INTO") || !parameter_parts[2].equalsIgnoreCase("VALUES"))
        {
            System.out.println("Invalid SQL Query!");
            return;
        }
        String table_Name = parameter_parts[1];
        Table fetchTable = tables.get(table_Name);

        /* Checking if table already exists for new record insertion */
        if(fetchTable == null)
        {
            System.out.println("Table doesn't exists");
            return;
        }
        String[] table_data = parameters.substring(parameters.indexOf('(') + 1,parameters.lastIndexOf(')')).split(",");
        fetchTable.insertRecord(table_data);
        System.out.println("Data inserted successfully!");
        querydata.add("Row :"+ Arrays.toString(table_data));
    }

    private void createqueryexec(String parameters) throws IOException
    {
        String[] parameter_parts = parameters.split(" ");

        /* Checking if user input create query is for creating database or table */
        if(parameter_parts.length < 1 || parameter_parts[0].equalsIgnoreCase("DATABASE"))
        {
            createDatabase(parameter_parts);
            return;
        }
        else if(parameter_parts.length < 1 || parameter_parts[0].equalsIgnoreCase("TABLE") ){
            String table_name = parameter_parts[1];
            String[] columns = parameters.substring(parameters.indexOf('(') + 1,parameters.lastIndexOf(')')).split(",");
            Table table = new Table(table_name.trim(), columns);
            tables.put(table_name,table);
            System.out.println("Table "+table_name+" created successfully! ");
            querydata.add("Table: "+table_name);
            querydata.add("Columns: "+Arrays.toString(columns));
            return;
        }
        else
        {
            System.out.println("Invalid SQL Query!");
        }
    }

    private  void createDatabase(String[] parameter_parts) throws IOException
    {
        /*
         * Extracting and storing database name from user_query
         * For example - parameter_parts = CREATE DATABASE data ;
         * database_name = data
         * */
        String database_name = parameter_parts[1];

        /* Checking if database is already created */
        if(db_created)
        {
            System.out.println("Only one database creation allowed!");
        }
        else
        {
            System.out.println("Database "+database_name+" created successfully!");
        }
        /* Setting db_created value to true once user creates a database */
        db_created = true;
    }
}
