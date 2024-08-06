package LibrarySpec

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, Book, VolumeInfo}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}
import services.LibraryService

import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  val mockConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )

  val gameOfThronesVolumeInfo: VolumeInfo = VolumeInfo(
    _id = "someId",
    name = "A Game of Thrones",
    description = "The best book!!!",
    pageCount = 100
  )

  val gameOfThronesBook: Book = Book(gameOfThronesVolumeInfo)

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[VolumeInfo](_: String)(_: OFormat[VolumeInfo], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT[Future, APIError](gameOfThronesVolumeInfo))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Right(gameOfThronesBook)
      }
    }

    "return an error" in {
      val url: String = "testUrl"
      val error = APIError.BadAPIResponse(500, "An error occurred")

      (mockConnector.get[VolumeInfo](_: String)(_: OFormat[VolumeInfo], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT[Future, VolumeInfo](error))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Left(error)
      }
    }
  }
}


//Changed Future(gameOfThrones.as[Book]) to EitherT.rightT[Future, APIError](gameOfThronesVolumeInfo)
//Changed Future.failed(new RuntimeException(errorMessage)) to EitherT.leftT[Future, VolumeInfo](error)
//Used .value to get the Future[Either[APIError, Book]] and match against Right and Left.