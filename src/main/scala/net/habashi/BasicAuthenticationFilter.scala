package net.habashi

import akka.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result, Results}
import sun.misc.BASE64Decoder

import scala.concurrent.{ExecutionContext, Future}

/**
  * Enables BasicAuthentication implemented as Filter for applications built with Play!.
  *
  * @param username to validate
  * @param password to validate
  * @param mat      implicit materializer
  * @param ec       used executionContext
  */
class BasicAuthenticationFilter(val username: String, val password: String)
                               (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  private object Constants {
    lazy val AuthorizationHeaderName = "authorization"
    lazy val BasicAuthenticationIdentifier = "Basic "
  }

  private lazy val unauthorizedResult = Future.successful {
    Results.Unauthorized.withHeaders(("WWW-Authenticate", """Basic realm="BasicAuthentication""""))
  }

  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    requestHeader.headers.get(Constants.AuthorizationHeaderName) match {
      case Some(authorizationBody) =>
        if (authorizationBody.startsWith(Constants.BasicAuthenticationIdentifier)) {
          val basicAuthenticationBody = authorizationBody.replace(Constants.BasicAuthenticationIdentifier, "")
          val decodedPayload = decodeBase64(basicAuthenticationBody)

          if (validateUsernamePassword(decodedPayload)) {
            nextFilter(requestHeader)
          } else {
            unauthorizedResult
          }
        } else {
          unauthorizedResult
        }
      case None => unauthorizedResult
    }
  }

  private def decodeBase64(string: String): String = {
    val decodedByteArray = new BASE64Decoder().decodeBuffer(string)
    new String(decodedByteArray, "UTF-8")
  }

  private def validateUsernamePassword(payload: String): Boolean = {
    val usernamePassword = payload.split(":")

    usernamePassword.length == 2 &&
      usernamePassword(0) == username &&
      usernamePassword(1) == password
  }

}
