package repositories.repositories

import models.DataModel
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.result
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[Int, Seq[DataModel]]]  =
    collection.find().toFuture().map{
      case books: Seq[DataModel] => Right(books)
      case _ => Left(404)
    }

  def create(book: DataModel): Future[DataModel] =
    collection
      .insertOne(book)
      .toFuture()
      .map(_ => book)

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  def read(id: String): Future[Either[Int, DataModel]] =
    collection.find(byID(id)).headOption.map {
      case Some(data) => Right(data)
      case None => Left(404)
    }

  def update(id: String, book: DataModel): Future[result.UpdateResult] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(false)
    ).toFuture()

  def delete(id: String): Future[Either[Int, Unit]] =
    collection.deleteOne(byID(id)).toFuture().map { result =>
      if (result.getDeletedCount > 0) Right(())
      else Left(404)
    }

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) // Needed for tests
}
