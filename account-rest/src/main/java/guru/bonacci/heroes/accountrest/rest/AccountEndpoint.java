package guru.bonacci.heroes.accountrest.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import guru.bonacci.heroes.accountrest.streams.GetAccountDataResult;
import guru.bonacci.heroes.accountrest.streams.InteractiveQueries;
import guru.bonacci.heroes.accountrest.streams.PipelineMetadata;

@ApplicationScoped
@Path("/accounts")
public class AccountEndpoint {

    @Inject
    InteractiveQueries interactiveQueries;

    @ConfigProperty(name = "quarkus.http.ssl-port")
    int sslPort;

    @GET
    @Path("/data/{id}")
    public Response getAccountData(String poolAccountId) {
        GetAccountDataResult result = interactiveQueries.getAccountData(poolAccountId);

        if (result.getResult().isPresent()) {
            return Response.ok(result.getResult().get()).build();
        } else if (result.getHost().isPresent()) {
            URI otherUri = getOtherUri(result.getHost().get(), result.getPort().getAsInt(), poolAccountId);
            return Response.seeOther(otherUri).build();
        } else {
            return Response.status(Status.NOT_FOUND.getStatusCode(), "No data found for account " + poolAccountId).build();
        }
    }

    @GET
    @Path("/meta-data")
    public List<PipelineMetadata> getMetaData() {
        return interactiveQueries.getMetaData();
    }

    private URI getOtherUri(String host, int port, String id) {
        try {
            String scheme = (port == sslPort) ? "https" : "http";
            return new URI(scheme + "://" + host + ":" + port + "/accounts/data/" + id);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
