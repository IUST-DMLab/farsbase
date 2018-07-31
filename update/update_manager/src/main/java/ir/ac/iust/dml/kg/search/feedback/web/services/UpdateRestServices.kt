/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.web.services

import io.swagger.annotations.Api
import ir.ac.iust.dml.kg.search.feedback.access.entities.UpdateTask
import ir.ac.iust.dml.kg.search.feedback.logic.UpdateRestLogic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/v1/updater/")
@Api(tags = arrayOf("updater"), description = "سرویس‌های بروزرسانی")
class UpdateRestServices {

  @Autowired lateinit var logic: UpdateRestLogic

  @RequestMapping(value = "/search", method = arrayOf(RequestMethod.GET))
  @ResponseBody
  @Throws(Exception::class)
  fun search(@RequestParam(defaultValue = "0") page: Int,
             @RequestParam(defaultValue = "10") pageSize: Int,
             @RequestParam(required = false) module: String?,
             @RequestParam(required = false) path: String?,
             @RequestParam(required = false) minStartDate: Long?,
             @RequestParam(required = false) maxStartDate: Long?,
             @RequestParam(required = false) minEndDate: Long?,
             @RequestParam(required = false) maxEndDate: Long?): Page<UpdateTask> {
    return logic.search(page, pageSize, module, path, minStartDate, maxStartDate, minEndDate, maxEndDate)
  }


}