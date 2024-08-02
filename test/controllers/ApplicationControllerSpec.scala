package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}
import repositories.repositories.DataRepository

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext,
    service,
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())

  "ApplicationController .index" should {

    "return 404 and a not found message" in {
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) should include("Unable to find any books")
    }
  }

  "ApplicationController .create" should {

    "create a book in the database" in {
      beforeEach()
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
      afterEach()
    }

    "return BadRequest for invalid JSON" in {
      beforeEach()
      val invalidJson: JsValue = Json.parse("""{ "invalid": "json" }""")
      val request: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody[JsValue](invalidJson)
      val result: Future[Result] = TestApplicationController.create()(request)

      status(result) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  "ApplicationController .read" should {

    "return 200 OK and the book when found" in {
      beforeEach()
      val book = DataModel("1", "Test Book", "A description of the test book", 100)
      await(repository.create(book))

      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.toJson(book)
      afterEach()
    }

    "return 404 Not Found when the book is not found" in {
      beforeEach()
      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) should include("Unable to find item with id 1")
      afterEach()
    }

    "find a book in the database by id" in {
      beforeEach()
      val createRequest: FakeRequest[JsValue] = FakeRequest(POST, "/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(createRequest)

      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
      afterEach()
    }
  }

  "ApplicationController .update" should {

    "update a book in the database" in {
      beforeEach()
      val initialBook = DataModel("1", "Initial Book", "Initial description", 100)
      await(repository.create(initialBook))

      val updatedBook = DataModel("1", "Updated Book", "Updated description", 150)
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](Json.toJson(updatedBook))
      val updatedResult: Future[Result] = TestApplicationController.update("1")(request)

      status(updatedResult) shouldBe Status.ACCEPTED
      contentAsJson(updatedResult) shouldBe Json.toJson(updatedBook)
      afterEach()
    }

    "return 404 Not Found when trying to update a non-existent book" in {
      beforeEach()
      val updatedBook = DataModel("1", "Updated Book", "Updated description", 150)
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](Json.toJson(updatedBook))
      val updatedResult: Future[Result] = TestApplicationController.update("1")(request)

      status(updatedResult) shouldBe Status.NOT_FOUND
      afterEach()
    }

    "return BadRequest for invalid JSON" in {
      beforeEach()
      val invalidJson: JsValue = Json.parse("""{ "invalid": "json" }""")
      val request: FakeRequest[JsValue] = FakeRequest(PUT, "/api/1").withBody[JsValue](invalidJson)
      val result: Future[Result] = TestApplicationController.update("1")(request)

      status(result) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  "ApplicationController .delete" should {

    "delete a book from the database" in {
      beforeEach()
      val book = DataModel("1", "Book to delete", "Description", 100)
      await(repository.create(book))

      val deleteResult: Future[Result] = TestApplicationController.delete("1")(FakeRequest())
      status(deleteResult) shouldBe Status.ACCEPTED

      val readResult = TestApplicationController.read("1")(FakeRequest())
      status(readResult) shouldBe Status.NOT_FOUND
      afterEach()
    }

    "return 404 Not Found when trying to delete a non-existent book" in {
      beforeEach()
      val deleteResult: Future[Result] = TestApplicationController.delete("1")(FakeRequest())
      status(deleteResult) shouldBe Status.NOT_FOUND
      afterEach()
    }
  }
}
