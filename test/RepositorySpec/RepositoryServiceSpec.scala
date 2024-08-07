package services

import models.{APIError, DataModel}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.mockito.MockitoSugar
import repositories.repositories.DataRepositoryTrait

import scala.concurrent.Future

class RepositoryServiceSpec extends AsyncWordSpec with Matchers with ScalaFutures with MockitoSugar {

  val mockRepository: DataRepositoryTrait = mock[DataRepositoryTrait]
  val service = new RepositoryService(mockRepository)

  val dataModel = DataModel("1", "Test Book", "Test Description", 100)

  "RepositoryService" should {

    "return a list of books for index" in {
      when(mockRepository.index()).thenReturn(Future.successful(Right(Seq(dataModel))))

      service.index().map { result =>
        result shouldBe Right(Seq(dataModel))
      }
    }

    "create a book" in {
      when(mockRepository.create(any[DataModel])).thenReturn(Future.successful(dataModel))

      service.create(dataModel).map { result =>
        result shouldBe dataModel
      }
    }

    "read a book by id" in {
      when(mockRepository.read(any[String])).thenReturn(Future.successful(Right(dataModel)))

      service.read("1").map { result =>
        result shouldBe Right(dataModel)
      }
    }

    "update a book" in {
      when(mockRepository.update(any[String], any[DataModel])).thenReturn(Future.successful(Right(dataModel)))

      service.update("1", dataModel).map { result =>
        result shouldBe Right(dataModel)
      }
    }

    "delete a book" in {
      when(mockRepository.delete(any[String])).thenReturn(Future.successful(Right(dataModel)))

      service.delete("1").map { result =>
        result shouldBe Right(dataModel)
      }
    }

    "find a book by field" in {
      when(mockRepository.findByField(any[String], any[String])).thenReturn(Future.successful(Right(dataModel)))

      service.findByField("name", "Test Book").map { result =>
        result shouldBe Right(dataModel)
      }
    }
  }
}
