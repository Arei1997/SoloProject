package repositories.repositories

import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model._
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
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  def create(book: DataModel): Future[DataModel] =
    collection.insertOne(book).toFuture().map(_ => book).recover {
      case ex => throw new Exception(s"Error creating book: ${ex.getMessage}")
    }

  private def byID(id: String): Bson = Filters.equal("_id", id)

  def read(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(byID(id)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, "Book cannot be read"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  def update(id: String, book: DataModel): Future[Either[APIError.BadAPIResponse, DataModel]] =
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

  def delete(id: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.findOneAndDelete(byID(id)).toFutureOption().map {
      case Some(deletedItem) => Right(deletedItem)
      case None => Left(APIError.BadAPIResponse(404, "Book cannot be deleted"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()).recover {
    case ex => throw new Exception(s"Error deleting all books: ${ex.getMessage}")
  }

  def findByName(name: String): Future[Either[APIError.BadAPIResponse, DataModel]] =
    collection.find(Filters.equal("name", name)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, "Book not found"))
    }.recover {
      case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
    }

  def updateField(id: String, field: String, value: Any): Future[Either[APIError.BadAPIResponse, DataModel]] = {
    val update = Updates.set(field, value)
    collection.findOneAndUpdate(byID(id), update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER))
      .toFutureOption().map {
        case Some(updatedItem) => Right(updatedItem)
        case None => Left(APIError.BadAPIResponse(404, "Book not found"))
      }.recover {
        case ex => Left(APIError.BadAPIResponse(500, ex.getMessage))
      }
  }
}
