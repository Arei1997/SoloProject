package models
import play.api.libs.json.{Json, OFormat}



case class Book(volumeInfo: VolumeInfo)


object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}