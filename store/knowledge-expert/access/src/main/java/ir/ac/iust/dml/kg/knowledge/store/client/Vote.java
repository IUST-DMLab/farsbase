package ir.ac.iust.dml.kg.knowledge.store.client;

import javax.xml.bind.annotation.XmlType;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Vote type of triple
 */
@XmlType(name = "Vote", namespace = "http://kg.dml.iust.ac.ir")
public enum Vote {
    None, Reject, Approve, VIPReject, VIPApprove
}
