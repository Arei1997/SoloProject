package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext
import repositories.repositories.DataRepository

class ApplicationControllerSpec extends BaseSpecWithApplication{

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    // Clean up the database before each test
    await(repository.deleteAll())
  }

  "ApplicationController .index" should {

    "return 404 and a not found message" in {
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) should include("Unable to find any books")
    }


  "ApplicationController .create()" should {

  }
  "ApplicationController .read()" should {
    "return 200 OK and the book when found" in {
      // Insert a book into the repository
      val book = DataModel("1", "Test Book", "A description of the test book", 100)
      await(repository.create(book))

      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.toJson(book)
    }

    "return 404 Not Found when the book is not found" in {
      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) should include("Unable to find item with id 1")
    }

  }

  "ApplicationController .update()" should {

  }

  "ApplicationController .delete()" should {

  }

}
}

