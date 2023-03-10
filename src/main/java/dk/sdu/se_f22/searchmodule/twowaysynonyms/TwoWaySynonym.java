package dk.sdu.se_f22.searchmodule.twowaysynonyms;

import dk.sdu.se_f22.sharedlibrary.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class TwoWaySynonym implements DatabaseOperator {
    private TwoWaySynonym() {}
    public static TwoWaySynonym getInstance() {
        return TwoWaySynonymHolder.INSTANCE;
    }

    private static class TwoWaySynonymHolder {
        private static final TwoWaySynonym INSTANCE = new TwoWaySynonym();
    }

    /**
     * Add a new synonym and create a new synonym group.
     * @param synonym           Word to add
     * @return                  UUID of created Synonym
     */
    @Override
    public String create(String synonym) {
        String uuid = UUID.randomUUID().toString();
        String format = "INSERT INTO twoway_synonym (uuid, synonym) VALUES (?, ?)";
        ArrayList<Object> args = new ArrayList<>(){{
            add(uuid);
            add(synonym);
        }};
        Connection conn = getConnection();
        PreparedStatement statement = getPreparedStatement(format, args, conn);
        return (updateDatabase(statement, conn) > 0) ? uuid : null;
    }

    /**
     * Add a new synonym to an existing synonym group.
     * @param synonym       Word to add
     * @param groupMember   Existing related Synonym
     * @return              UUID of created Synonym
     */
    @Override
    public String create(String synonym, String groupMember) {
        Integer group_id = read(groupMember).groupId();
        String uuid = UUID.randomUUID().toString();
        String format = "INSERT INTO twoway_synonym (uuid, synonym, group_id) VALUES (?, ?, ?)";
        ArrayList<Object> args = new ArrayList<>(){{
            add(uuid);
            add(synonym);
            add(group_id);
        }};
        Connection conn = getConnection();
        PreparedStatement statement = getPreparedStatement(format, args, conn);
        return (updateDatabase(statement, conn) > 0) ? uuid : null;
    }

    /**
     * Retrieve the data from the database related to the given synonym
     * @param groupId   Word to receive synonyms for
     * @return          ArrayList of all matching synonyms
     */
    @Override
    public ArrayList<Synonym> readAll(int groupId) {
        String statementFormat = "SELECT * FROM twoway_synonym WHERE group_id = ?";
        ArrayList<Object> args = new ArrayList<>(){{add(groupId);}};
        Connection conn = getConnection();
        PreparedStatement stmt = getPreparedStatement(statementFormat, args, conn);
        ArrayList<Synonym> synonyms = readDatabase(stmt, conn);
        return synonyms;
    }

    /**
     * Retrieve a single synonym from the database
     * @param synonym   Word to search for
     * @return          Synonym Record of found synonym
     */
    @Override
    public Synonym read(String synonym) {
        String statementFormat = "SELECT * FROM twoway_synonym WHERE synonym = ?";
        ArrayList<Object> args = new ArrayList<>(){{add(synonym);}};
        Connection conn = getConnection();
        PreparedStatement stmt = getPreparedStatement(statementFormat, args, conn);
        ArrayList<Synonym> synonyms = readDatabase(stmt, conn);
        if (!synonyms.isEmpty()) {
            return synonyms.get(0); // Hacky hacky :)
        }
        return null;
    }

    /**
     * Update a synonyms group ID
     * @param synonym           Word to update
     * @param relatedSynonym    Related Synonym
     * @return                  UUID of updated Synonym
     */
    @Override
    public boolean updateGroupID(String synonym, String relatedSynonym) {
        Integer group_id = read(relatedSynonym).groupId();
        String format = "UPDATE twoway_synonym SET group_id = ? WHERE synonym = ?";
        ArrayList<Object> args = new ArrayList<>(){{
            add(group_id);
            add(synonym);
        }};
        Connection conn = getConnection();
        PreparedStatement statement = getPreparedStatement(format, args, conn);
        return (updateDatabase(statement, conn) > 0);
    }

    /**
     * Update spelling of a synonym
     * @param synonym           Word to update
     * @param correctedSpelling Corrected spelling of Synonym
     * @return                  UUID of updated Synonym
     */
    @Override
    public boolean updateSpelling(String synonym, String correctedSpelling) {
        String statementFormat = "UPDATE twoway_synonym SET synonym = ? WHERE synonym = ?";
        ArrayList<Object> args = new ArrayList<>(){{
            add(correctedSpelling);
            add(synonym);
        }};
        Connection conn = getConnection();
        PreparedStatement stmt = getPreparedStatement(statementFormat, args, conn);
        return updateDatabase(stmt, conn) > 0;
    }

    /**
     * Delete a synonym from the database
     * @param synonym           Word to delete
     * @return                  Boolean value
     */
    @Override
    public boolean delete(String synonym) {
        String statementFormat = "DELETE FROM twoway_synonym WHERE synonym = ?";
        ArrayList<Object> args = new ArrayList<>(){{add(synonym);}};
        Connection conn = getConnection();
        PreparedStatement stmt = getPreparedStatement(statementFormat, args, conn);
        return updateDatabase(stmt, conn) > 0;
    }

    /**
     * Goes through a list of tokens, to find all
     * their respective two-way synonyms.
     * @param tokens    List of keywords to find synonyms for
     * @return          List of found synonyms and tokens
     */
    @Override
    public ArrayList<String> filter(ArrayList<String> tokens) {
        ArrayList<String> args = new ArrayList<>();
        Set<Integer> unique = new TreeSet<>();

        for(String token : tokens){
            Synonym synonymTemp = read(token);
            if(synonymTemp == null) {
                args.add(token);
                continue;
            }
            var statement = unique.add(synonymTemp.groupId());
        }

        for(Integer groupId : unique){
            readAll(groupId).forEach(s -> args.add(s.synonym()));
        }

        return args;
    }

    /**
     * @param  format        Format of the statement that is to be executed
     * @param  parameters    Params to be mapped in the Query
     * @return               A fully prepared statement if no exception is caught
     */
    private PreparedStatement getPreparedStatement(String format, ArrayList<Object> parameters, Connection conn){
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(format);
            prepareStatement(statement, parameters);
        } catch(SQLException exception){
            System.out.println("Formatting statement failed");
        }
        return statement;
    }

    /**
     * Helper function for preparing statements
     * @param statement     SQL Query
     * @param parameters    Params to be mapped in Query
     */
    private void prepareStatement(PreparedStatement statement, ArrayList<Object> parameters) {
        try {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i+1, parameters.get(i));
            }
        } catch (SQLException throwables) {
            System.out.println("Preparing statement failed");
        }
    }

    /**
     * An execute statement function that queries to the database
     *
     * @param statement     Prepared statement that should be executed
     * @return              returns a resultset from the query
     */
    private ArrayList<Synonym> readDatabase(PreparedStatement statement, Connection conn){
        try {
            ResultSet result = statement.executeQuery();
            ArrayList<Synonym> synonyms = resultSetFormatting(result);
            freeConnection(conn);
            return synonyms;
        } catch (SQLTimeoutException throwables){
            System.out.println("Driver timed out, statement execution took too long");
            return new ArrayList<>();
        } catch (SQLException throwables){
            System.out.println("Query could not be executed");
            return new ArrayList<>();
        }
    }

    private ArrayList<Synonym> resultSetFormatting(ResultSet rs){
        ArrayList<Synonym> synonymArrayList = new ArrayList<>();
        try {
            while (rs.next()) {
                synonymArrayList.add(new Synonym(
                        rs.getString(1), rs.getString(2), rs.getInt(3)
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return synonymArrayList;
    }

    /**
     * An execute statement function that queries to the database
     *
     * @param statement     Prepared statement that should be executed
     * @return              returns an int of effected rows from the query
     */
    private int updateDatabase(PreparedStatement statement, Connection conn){
        try {
            int result = statement.executeUpdate();
            freeConnection(conn);
            return result;
        } catch (SQLTimeoutException throwables){
            System.out.println("Driver timed out, statement execution took too long");
            return 0;
        } catch (SQLException throwables){
            System.out.println(throwables.getMessage());
            System.out.println("Update could not be executed");
            return 0;
        }
    }

    private Connection getConnection() {
        try {
            return DBConnection.getPooledConnection();
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database");
            e.printStackTrace();
            return null;
        }
    }

    private void freeConnection(Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Couldn't free connection");
            e.printStackTrace();
        }
    }
}
