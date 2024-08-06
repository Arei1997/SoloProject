package repositories.repositories

import com.google.inject.ImplementedBy
import models.{APIError, DataModel}

import scala.concurrent.Future

@ImplementedBy(classOf[DataRepository])
trait DataRepositoryTrait {
  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]
  def create(book: DataModel): Future[DataModel]
  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModgtel]]
  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]]
  def delete(id: String): Future[Either[APIError.BadAPIResponse, DataModel]]
  def findByField(fieldName: String, value: String): Future[Either[APIError.BadAPIResponse, DataModel]]
}
