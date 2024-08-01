package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import repositories.repositories.DataRepository
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents,val dataRepository: DataRepository,implicit val ec: ExecutionContext) extends BaseController{
  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }
  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Right(item: DataModel) => Ok(Json.toJson(item))
      case Left(error) => Status(error)(Json.toJson(s"Unable to find item with id $id"))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id, dataModel).flatMap { updateResult =>
          if (updateResult.getMatchedCount > 0) {
            dataRepository.read(id).map {
              case Right(updatedItem) => Accepted(Json.toJson(updatedItem))
              case Left(_) => NotFound(Json.toJson(s"Cannot find item with id($id)"))
            }
          } else {
            Future.successful(NotFound(Json.toJson(s"Cannot find item with id($id)")))
          }
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors))))
    }
  }


  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.delete(id).map {
      case Right(_) => Accepted
      case Left(error) => Status(error)(Json.toJson(s"Unable to delete item with id $id"))
    }
  }
}