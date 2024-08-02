package LibrarySpec

import baseSpec.BaseSpec
import connectors.LibraryConnector
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import services.LibraryService

import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{
  val mockConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[???](_: ???)(_: ???, _: ???))
        .expects(url, *, *)
        .returning(Future(gameOfThrones.as[???]))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe ???
      }
    }
  }

}