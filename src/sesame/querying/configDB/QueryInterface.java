package sesame.querying.configDB;

import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;
import com.franz.agraph.repository.AGTupleQuery;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * Created by Mary on 31.05.2017.
 */
public class QueryInterface {
    private static final String SERVER_URL = "http://vepp4-vm1.inp.nsk.su:10035/";
    private static final String CATALOG = "/";
    private static final String REPOSITORY = "K500ConfigOntology";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "xyzzy";
    private static final String ONTOLOGY_URI = "http://www.semanticweb.org/mary/ontologies/2017/0/control-system-ontology/";

    private AGRepositoryConnection connection = null;

    public QueryInterface(){
        connection = connectToServer();
    }

    private AGRepositoryConnection connectToServer() {
        AGServer server = new AGServer(SERVER_URL, USERNAME, PASSWORD);
        AGRepositoryConnection connection = null;
        try {
            AGRepository repository = server.getCatalog(CATALOG).openRepository(REPOSITORY);
            connection = repository.getConnection();
            System.out.println("Repository " + repository.getRepositoryID() +
                    " contains " + connection.size() + " statements and has indices: " +
                    connection.listIndices() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void getAllStatements() {
        RepositoryResult<Statement> result = null;
        try {
            result = connection.getStatements(null, null, null, false);
            while (result.hasNext()){
                Statement statement = result.next();
                StringBuilder builder = new StringBuilder().append(statement.getSubject()).append(" ")
                        .append(statement.getPredicate()).append(" ").append(statement.getObject());
                System.out.println(builder.toString());

            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

    public String getLocalName(String name) {
        return name.substring(name.lastIndexOf("/") + 1);
    }


    public String getAllDevTypes() {
        System.out.println("List of all CtrlSystDevType objects:");
        URI devTypeClass = connection.getRepository().getValueFactory().createURI(ONTOLOGY_URI, "CtrlSystDevType");
        RepositoryResult<Statement> result = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            result = connection.getStatements(null, RDF.TYPE, devTypeClass, false);
            while (result.hasNext()){
                Statement statement = result.next();
                stringBuilder.append(getLocalName(statement.getSubject().toString())).append("\n");
            }
            System.out.println(stringBuilder.toString());
            System.out.println();

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public String getAllDevices() {
        System.out.println("List of all CtrlSystDevice objects:");
        StringBuilder stringBuilder = new StringBuilder();

        String deviceClassURI = "<" + ONTOLOGY_URI + "CtrlSystDevice" + ">";
        String query = "SELECT ?s WHERE {?s rdf:type " + deviceClassURI + ".}";
        AGTupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
        try {
            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()){
                BindingSet bindingSet = result.next();
                stringBuilder.append(getLocalName(bindingSet.getValue("s").toString())).append("\n");
            }
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
        System.out.println(stringBuilder.toString());
        System.out.println();
        return stringBuilder.toString();
    }

    public String getAllChannelsForDevice(String deviceName) {
        System.out.println("List of all channels of device " + deviceName + ":");
        StringBuilder stringBuilder = new StringBuilder();

        String channelClassURI = "<" + ONTOLOGY_URI + "CtrlSystDevChannel" + ">";
        String deviceURI = "<" + ONTOLOGY_URI + deviceName + ">";
        String hasChannelURI = "<" + ONTOLOGY_URI + "hasChannel" + ">";


        String query = "SELECT ?chan WHERE {?chan rdf:type " + channelClassURI + ". " +
                deviceURI + " " + hasChannelURI + " ?chan .}";


        AGTupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

        try {
            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()){
                BindingSet bindingSet = result.next();
                stringBuilder.append(getLocalName(bindingSet.getValue("chan").toString())).append("\n");
            }
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
        System.out.println(stringBuilder.toString());
        System.out.println();
        return stringBuilder.toString();
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
