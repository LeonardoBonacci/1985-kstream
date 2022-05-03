package guru.bonacci.heroes.account;

import java.util.Optional;
import java.util.OptionalInt;

import guru.bonacci.heroes.domain.TransferValidationResult;

public class GetTransferValidationResult {

  private static GetTransferValidationResult NOT_FOUND = new GetTransferValidationResult(null, null, null);

  private final TransferValidationResult result;
  private final String host;
  private final Integer port;

  private GetTransferValidationResult(TransferValidationResult result, String host, Integer port) {
      this.result = result;
      this.host = host;
      this.port = port;
  }

  public static GetTransferValidationResult found(TransferValidationResult data) {
      return new GetTransferValidationResult(data, null, null);
  }

  public static GetTransferValidationResult foundRemotely(String host, int port) {
      return new GetTransferValidationResult(null, host, port);
  }

  public static GetTransferValidationResult notFound() {
      return NOT_FOUND;
  }

  public Optional<TransferValidationResult> getResult() {
      return Optional.ofNullable(result);
  }

  public Optional<String> getHost() {
      return Optional.ofNullable(host);
  }

  public OptionalInt getPort() {
      return port != null ? OptionalInt.of(port) : OptionalInt.empty();
  }
}
