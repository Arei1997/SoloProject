package LibrarySpec

import baseSpec.BaseSpec
import connectors.LibraryConnector
import models.{Book, VolumeInfo}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}
import services.LibraryService

import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{
  val mockConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "volumeInfo" -> Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
    )
  )

  val gameOfThronesBook: Book = Book(
    VolumeInfo(
      _id = "someId",
      name = "A Game of Thrones",
      description = "The best book!!!",
      pageCount = 100
    )
  )


  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[Book](_: String)(_:OFormat[Book], _: ExecutionContext))
        .expects(url, *, *)
        .returning(Future(gameOfThrones.as[Book]))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "")) { result => //value
        result shouldBe gameOfThronesBook
      }
    }
  }

  "return an error" in {
    val url: String = "testUrl"
    val errorMessage = "An error occurred"

    (mockConnector.get[Book](_: String)(_: play.api.libs.json.OFormat[Book], _: ExecutionContext))
      .expects(url, *, *)
      .returning(Future.failed(new RuntimeException(errorMessage)))
      .once()

    whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").failed) { result =>
      result shouldBe a[RuntimeException]
      result.getMessage shouldBe errorMessage
    }
  }
}

}