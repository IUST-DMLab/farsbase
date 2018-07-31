/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.access.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import ir.ac.iust.dml.kg.raw.utils.Module
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "updateTask")
data class UpdateTask(
    @Id @JsonIgnore var uid: ObjectId? = null,
    @Indexed var module: Module? = null,
    @Indexed var startTime: Long? = null,
    @Indexed var endTime: Long? = null,
    @Indexed var path: String? = null,
    var runnerId: String? = null
)