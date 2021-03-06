package play.api.libs.ws.ahc

import akka.stream.Materializer
import com.typesafe.sslconfig.ssl.SystemConfiguration
import play.api.libs.ws.{WSClient, WSRequest}

/**
 *
 */
class AhcWSClient(underlyingClient: StandaloneAhcWSClient) extends WSClient {
  /**
   * The underlying implementation of the client, if any.  You must cast explicitly to the type you want.
   *
   * @tparam T the type you are expecting (i.e. isInstanceOf)
   * @return the backing class.
   */
  override def underlying[T]: T = underlyingClient.underlying[T]

  /**
   * Generates a request.
   *
   * @param url The base URL to make HTTP requests to.
   * @return a request
   */
  override def url(url: String): WSRequest = {
    val plain = underlyingClient.url(url).asInstanceOf[StandaloneAhcWSRequest]
    new AhcWSRequest(plain)
  }

  /** Closes this client, and releases underlying resources. */
  override def close(): Unit = underlyingClient.close()
}

object AhcWSClient {

  private[ahc] val loggerFactory = new AhcLoggerFactory

  /**
   * Convenient factory method that uses a [[play.api.libs.ws.WSClientConfig]] value for configuration instead of
   * an [[http://static.javadoc.io/org.asynchttpclient/async-http-client/2.0.0/org/asynchttpclient/AsyncHttpClientConfig.html org.asynchttpclient.AsyncHttpClientConfig]].
   *
   * Typical usage:
   *
   * {{{
   *   implicit val materializer = ...
   *   val client = AhcWSClient()
   *   val request = client.url(someUrl).get()
   *   request.foreach { response =>
   *     doSomething(response)
   *     client.close()
   *   }
   * }}}
   *
   * @param config configuration settings
   */
  def apply(config: AhcWSClientConfig = AhcWSClientConfig())(implicit materializer: Materializer): AhcWSClient = {
    new AhcWSClient(StandaloneAhcWSClient(config))
  }
}
