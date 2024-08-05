package services

import connectors.LibraryConnector
import models.{APIError, Book}
import play.api.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector) extends Logging {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): Future[Book] = {
    val result = connector.get[Book](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search:$term"))

    result.value.flatMap {
      case Right(book) => Future.successful(book)
      case Left(error) =>
        logger.error(s"Error fetching book: ${error.reason}")
        Future.failed(new RuntimeException(error.reason))
    }
  }
}
