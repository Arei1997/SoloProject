package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import repositories.repositories.DataRepository

class ApplicationControllerSpec extends BaseSpecWithApplication{

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext
  )

  "ApplicationController .index" should {

    "return 200 OK and a welcome message" in {
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe Status.OK
      contentAsString(result) should include ("Welcome to the Areis Play Scala API")
    }

  "ApplicationController .create()" should {

  }
  "ApplicationController .read()" should {

  }

  "ApplicationController .update()" should {

  }

  "ApplicationController .delete()" should {

  }

}
}

