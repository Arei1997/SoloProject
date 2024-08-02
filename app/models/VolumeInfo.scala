package models


import play.api.libs.json.{Json, OFormat}




case class VolumeInfo(  _id: String,
                       name: String,
                       description: String,
                       pageCount: Int)

object VolumeInfo {
  implicit val format: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}
