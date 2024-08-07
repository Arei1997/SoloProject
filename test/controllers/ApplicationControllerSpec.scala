package controllers

import baseSpec.BaseSpecWithApplication
import models.{APIError, DataModel}
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import services.RepositoryService

import scala.concurrent.{ExecutionContext, Future}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.mockito.MockitoSugar

class ApplicationControllerSpec extends BaseSpecWithApplication with MockitoSugar {

  val mockRepositoryService: RepositoryService = mock[RepositoryService]
  val TestApplicationController = new ApplicationController(
    component,
    mockRepositoryService,
    executionContext,
    service
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockRepositoryService)
  }

  "ApplicationController .index" should {

    "return 404 and a not found message" in {
      when(mockRepositoryService.index()).thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Unable to find any books"))))

      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) should include("Unable to find any books")
    }
  }

  "ApplicationController .create" should {

    "create a book in the database" in {
      when(mockRepositoryService.create(any[DataModel])).thenReturn(Future.successful(dataModel))

      val request: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
    }

    "return BadRequest for invalid JSON" in {
      val invalidJson: JsValue = Json.parse("""{ "invalid": "json" }""")
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody[JsValue](invalidJson)
      val result: Future[Result] = TestApplicationController.create()(request)

      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .read" should {

    "return 200 OK and the book when found" in {
      when(mockRepositoryService.read(any[String])).thenReturn(Future.successful(Right(dataModel)))

      val result = TestApplicationController.read("abcd")(FakeRequest())
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.toJson(dataModel)
    }

    "return 404 Not Found when the book is not found" in {
      when(mockRepositoryService.read(any[String])).thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Unable to find item with id 1"))))

      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) should include("Unable to find item with id 1")
    }

    "find a book in the database by id" in {
      when(mockRepositoryService.create(any[DataModel])).thenReturn(Future.successful(dataModel))
      when(mockRepositoryService.read("abcd")).thenReturn(Future.successful(Right(dataModel)))

      val createRequest: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
    }
  }

  "ApplicationController .update" should {

    "update a book in the database" in {
      when(mockRepositoryService.update(any[String], any[DataModel])).thenReturn(Future.successful(Right(dataModel)))

      val updatedBook = DataModel("1", "Updated Book", "Updated description", 150)
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](Json.toJson(updatedBook))
      val updatedResult: Future[Result] = TestApplicationController.update("1")(request)

      status(updatedResult) shouldBe Status.ACCEPTED
      contentAsJson(updatedResult) shouldBe Json.toJson(updatedBook)
    }

    "return 404 Not Found when trying to update a non-existent book" in {
      when(mockRepositoryService.update(any[String], any[DataModel])).thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Book cannot be updated"))))

      val updatedBook = DataModel("1", "Updated Book", "Updated description", 150)
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](Json.toJson(updatedBook))
      val updatedResult: Future[Result] = TestApplicationController.update("1")(request)

      status(updatedResult) shouldBe Status.NOT_FOUND
    }

    "return BadRequest for invalid JSON" in {
      val invalidJson: JsValue = Json.parse("""{ "invalid": "json" }""")
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](invalidJson)
      val result: Future[Result] = TestApplicationController.update("1")(request)

      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .delete" should {

    "delete a book from the database" in {
      when(mockRepositoryService.delete(any[String])).thenReturn(Future.successful(Right(dataModel)))
      when(mockRepositoryService.read(any[String])).thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Book cannot be read"))))

      val book = DataModel("1", "Book to delete", "Description", 100)
      await(mockRepositoryService.create(book))

      val deleteResult: Future[Result] = TestApplicationController.delete("1")(FakeRequest())
      status(deleteResult) shouldBe Status.ACCEPTED

      val readResult = TestApplicationController.read("1")(FakeRequest())
      status(readResult) shouldBe Status.NOT_FOUND
    }

    "return 404 Not Found when trying to delete a non-existent book" in {
      when(mockRepositoryService.delete(any[String])).thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Book cannot be deleted"))))

      val deleteResult: Future[Result] = TestApplicationController.delete("1")(FakeRequest())
      status(deleteResult) shouldBe Status.NOT_FOUND
    }
  }
}
