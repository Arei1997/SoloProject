package services

import models.{APIError, DataModel}
import repositories.repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepositoryService @Inject()(dataRepository: DataRepository)(implicit ec: ExecutionContext) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] = {
    dataRepository.index()
  }

  def create(dataModel: DataModel): Future[DataModel] = {
    dataRepository.create(dataModel)
  }

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    dataRepository.read(id)
  }

  def update(id: String, dataModel: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    dataRepository.update(id, dataModel)
  }

  def delete(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    dataRepository.delete(id)
  }

  def findByField(fieldName: String, value: String): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    dataRepository.findByField(fieldName, value)
  }
}
