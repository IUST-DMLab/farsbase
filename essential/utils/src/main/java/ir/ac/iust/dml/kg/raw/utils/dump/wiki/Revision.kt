/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils.dump.wiki

import javax.xml.bind.annotation.XmlElement

data class Revision(
    @XmlElement var id: Long? = null,
    @XmlElement(name = "parentid") var parentId: Long? = null,
    @XmlElement var timestamp: String? = null,
    @XmlElement var contributor: Contributor? = null,
    @XmlElement var comment: String? = null,
    @XmlElement var sha1: String? = null,
    @XmlElement var model: String? = null,
    @XmlElement var format: String? = null,
    @XmlElement var text: String? = null
)