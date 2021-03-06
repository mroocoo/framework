/*
 * Copyright 2014-2015 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftweb
package mongodb
package record

import net.liftweb.common._
import net.liftweb.record.field._

import org.specs2.mutable.Specification

import com.mongodb._


package mongoclientsaverecords {

  import field._

  class SaveDoc private () extends MongoRecord[SaveDoc] with ObjectIdPk[SaveDoc] {
    def meta = SaveDoc

    object name extends StringField(this, 12)
  }
  object SaveDoc extends SaveDoc with MongoMetaRecord[SaveDoc] {
    import BsonDSL._

    createIndex(("name" -> 1), true) // unique name
  }
}


/**
  * Systems under specification for MongoClientSave.
  */
class MongoClientSaveSpec extends Specification with MongoTestKit {
  "MongoClientSave Specification".title

  import mongoclientsaverecords._

  "MongoMetaRecord with Mongo save" in {

    checkMongoIsRunning

    val sd1 = SaveDoc.createRecord.name("MongoSession")
    val sd2 = SaveDoc.createRecord.name("MongoSession")
    val sd3 = SaveDoc.createRecord.name("MongoDB")

    // save to db
    sd1.save()
    sd2.save(false) // no exception thrown
    sd2.save(true) must throwA[MongoException]
    sd2.saveBox() must beLike {
      case Failure(msg, _, _) => msg must contain("E11000")
    }
    sd3.save()

    success
  }
}
