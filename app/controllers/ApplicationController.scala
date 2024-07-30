package controllers
import play.api.mvc.{BaseController, ControllerComponents}
import javax.inject.Inject
import javax.inject._

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{
  def index() = TODO
  def create() = TODO

  def read(id: String) = Action {
    Ok("hi im arei")
  }
  def update(id: String) = TODO
  def delete(id: String) = TODO
}