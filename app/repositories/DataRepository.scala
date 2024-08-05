package repositories.repositories

import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import org.mongodb.scala.result.UpdateResult
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[DataModel](
    collectionName = "dataModels",
    mongoComponent = mongoComponent,
    domainFormat = DataModel.formats,
    indexes = Seq(IndexModel(Indexes.ascending("_id"))),
    replaceIndexes = false
  ) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    collection.find().toFuture().map { books =>
      if (books.nonEmpty) Right(books)
      else Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(book: DataModel): Future[DataModel] =
    collection.insertOne(book).toFuture().map(_ => book)

  private def byID(id: String): Bson = Filters.equal("_id", id)

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byID(id)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, "Book cannot be read"))
    }

  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(false)
    ).toFuture().map { updateResult =>
      if (updateResult.getMatchedCount > 0) Right(book)
      else Left(APIError.BadAPIResponse(404, "Book cannot be updated"))
    }

  def delete(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.findOneAndDelete(byID(id)).toFutureOption().map {
      case Some(deletedItem) => Right(deletedItem)
      case None => Left(APIError.BadAPIResponse(404, "Book cannot be deleted"))
    }

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ())
}
