package net.habashi

import akka.stream.Materializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, Outcome, fixture}
import org.scalatestplus.play.OneAppPerSuite
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.test.FakeRequest

import scala.concurrent.{ExecutionContext, Future}

class BasicAuthenticationFilterTest extends fixture.FlatSpec with OneAppPerSuite with Matchers with ScalaFutures {

  implicit lazy val im: ExecutionContext = scala.concurrent.ExecutionContext.global

  implicit lazy val materializer: Materializer = app.materializer

  private val authenticatedResult = Results.Ok

  private val unauthenticatedResult: Result = Results.Unauthorized.withHeaders(("WWW-Authenticate", """Basic realm="BasicAuthentication""""))

  private val nextFilter = (requestHeader: RequestHeader) => Future.successful(authenticatedResult)

  case class FixtureParam(basicAuthenticationFilter: BasicAuthenticationFilter)

  override protected def withFixture(test: OneArgTest): Outcome = {
    val username: String = "Clark Kent"
    val password: String = "Pikachu"

    val basicAuthenticationFilter = new BasicAuthenticationFilter(username, password)
    val fixtureParam = FixtureParam(basicAuthenticationFilter)

    super.withFixture(test.toNoArgTest(fixtureParam))
  }

  "A request without header" must "result in 401 Unauthorized" in {
    fixParam => {
      // given
      val requestHeader = FakeRequest()

      // when
      val result = fixParam.basicAuthenticationFilter.apply(nextFilter)(requestHeader)

      // then
      ScalaFutures.whenReady(result) {
        r =>
          r shouldBe unauthenticatedResult
      }
    }
  }

  "A request without an Authorization-Header starting with the Basic-Identifier" must "result in 401 Unauthorized" in {
    fixParam => {
      // given
      val requestHeader = FakeRequest().withHeaders(("authorization", "Some Clark Kent:Pikachu"))

      // when
      val result = fixParam.basicAuthenticationFilter.apply(nextFilter)(requestHeader)

      // then
      ScalaFutures.whenReady(result) {
        r =>
          r shouldBe unauthenticatedResult
      }
    }
  }

  "A request not authenticated with a Base64 decoded string" must "result in 401 Unauthorized" in {
    fixParam => {
      // given
      val requestHeader = FakeRequest().withHeaders(("authorization", "Basic Clark Kent:Pikachu"))

      // when
      val result = fixParam.basicAuthenticationFilter.apply(nextFilter)(requestHeader)

      // then
      ScalaFutures.whenReady(result) {
        r =>
          r shouldBe unauthenticatedResult
      }
    }
  }

  "A well authenticated request" must "proceed with the nextFilters" in {
    fixParam => {
      // given
      val requestHeader = FakeRequest().withHeaders(("authorization", "Basic Q2xhcmsgS2VudDpQaWthY2h1"))

      // when
      val result = fixParam.basicAuthenticationFilter.apply(nextFilter)(requestHeader)

      // then
      ScalaFutures.whenReady(result) {
        r =>
          r shouldBe authenticatedResult
      }
    }
  }

}
