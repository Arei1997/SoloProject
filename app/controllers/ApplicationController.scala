package controllers

import models.{APIError, DataModel}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import repositories.repositories.DataRepository
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.LibraryService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents,
                                      val dataRepository: DataRepository,
                                      implicit val ec: ExecutionContext,
                                      val service: LibraryService) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(items) => Ok(Json.toJson(items))
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map { createdBook =>
          Created(Json.toJson(createdBook))
        }.recover {
          case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors))))
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Right(item) => Ok(Json.toJson(item))
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id, dataModel).map {
          case Right(updatedItem) => Accepted(Json.toJson(updatedItem))
          case Left(error) => NotFound(Json.toJson(error.reason))
        }.recover {
          case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors))))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.delete(id).map {
      case Right(deletedItem) => Accepted(Json.toJson(deletedItem))
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).value.map {
      case Right(book) => Ok(Json.toJson(book))
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }

  def findByName(name: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.findByName(name).map {
      case Right(item) => Ok(Json.toJson(item))
      case Left(error) => NotFound(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }

  def updateField(id: String, field: String, value: JsValue): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.updateField(id, field, value).map {
      case Right(updatedItem) => Accepted(Json.toJson(updatedItem))
      case Left(error) => NotFound(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }
}
