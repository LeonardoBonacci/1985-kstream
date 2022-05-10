package guru.bonacci.heroes.accountrest.streams;

import java.util.Optional;
import java.util.OptionalInt;

import guru.bonacci.heroes.accountrest.model.AccountData;

public class GetAccountDataResult {

    private static GetAccountDataResult NOT_FOUND = new GetAccountDataResult(null, null, null);

    private final AccountData result;
    private final String host;
    private final Integer port;

    private GetAccountDataResult(AccountData result, String host, Integer port) {
        this.result = result;
        this.host = host;
        this.port = port;
    }

    public static GetAccountDataResult found(AccountData data) {
        return new GetAccountDataResult(data, null, null);
    }

    public static GetAccountDataResult foundRemotely(String host, int port) {
        return new GetAccountDataResult(null, host, port);
    }

    public static GetAccountDataResult notFound() {
        return NOT_FOUND;
    }

    public Optional<AccountData> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public OptionalInt getPort() {
        return port != null ? OptionalInt.of(port) : OptionalInt.empty();
    }
}
