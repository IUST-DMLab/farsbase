/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils

enum class Module {
  wiki, mapper_auto_labeling, web_table_extractor,
  raw_mapper_entity_adder, raw_dependency_pattern, raw_rule_based,
  raw_distant_supervision_logistic, raw_distant_supervision_deep,
  manual, expert, sameAs
}