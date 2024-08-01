package connectors

package models

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(title: String, authors: List[String], publisher: Option[String], publishedDate: Option[String])
case class Book(volumeInfo: VolumeInfo)

object VolumeInfo {
  implicit val format: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}

object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}