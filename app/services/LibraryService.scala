package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, Book, VolumeInfo}
import play.api.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector) extends Logging {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, Book] = {
    val result = connector.get[VolumeInfo](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search:$term"))


    result.map{VolumeInfo =>
      Book(VolumeInfo)
    }.leftMap{ error =>
      logger.error(s"error fetching book : ${error.reason}")
      error
    }
  }


}