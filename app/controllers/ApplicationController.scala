package controllers

import repositories.repositories.DataRepository
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.Inject
import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents,val dataRepository: DataRepository,implicit val ec: ExecutionContext) extends BaseController{
  def index()  = Action { implicit request =>
    Ok("Welcome to the Areis Play Scala API")
  }
  def create() = TODO
  def read(id: String) = TODO
  def update(id: String) = TODO
  def delete(id: String) = TODO
}