/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils.dump.wiki

import javax.xml.bind.annotation.XmlElement

data class Contributor(
    @XmlElement var username: String? = null,
    @XmlElement var id: Long? = null)