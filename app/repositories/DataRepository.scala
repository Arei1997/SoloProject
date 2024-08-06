package repositories.repositories

import com.google.inject.ImplementedBy
import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DataRepository])
trait DataRepositoryTrait {
  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]
  def create(book: DataModel): Future[DataModel]
  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]]
  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]]
  def delete(id: String): Future[Either[APIError.BadAPIResponse, DataModel]]
  def findByField(fieldName: String, value: String): Future[Either[APIError.BadAPIResponse, DataModel]]
}

@Singleton
class DataRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[DataModel](
    collectionName = "dataModels",
    mongoComponent = mongoComponent,
    domainFormat = DataModel.formats,
    indexes = Seq(IndexModel(Indexes.ascending("_id"))),
    replaceIndexes = false
  ) with DataRepositoryTrait {

  override def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    collection.find().toFuture().map { books =>
      if (books.nonEmpty) Right(books)
      else Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  override def create(book: DataModel): Future[DataModel] =
    collection.insertOne(book).toFuture().map(_ => book).recover {
      case ex => throw new Exception(s"Error creating book: ${ex.getMessage}")
    }

  private def byID(id: String): Bson = Filters.equal("_id", id)

  override def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byID(id)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, "Book cannot be read"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  override def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(false)
    ).toFuture().map { updateResult =>
      if (updateResult.getMatchedCount > 0) Right(book)
      else Left(APIError.BadAPIResponse(404, "Book cannot be updated"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  override def delete(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.findOneAndDelete(byID(id)).toFutureOption().map {
      case Some(deletedItem) => Right(deletedItem)
      case None => Left(APIError.BadAPIResponse(404, "Book cannot be deleted"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  override def findByField(fieldName: String, value: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(Filters.equal(fieldName, value)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, s"Book not found with $fieldName = $value"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }
}
