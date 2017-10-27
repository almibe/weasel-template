package org.almibe.weaseltemplate

interface Cache
interface Condition: Cache

data class StringContent(val data: String): Cache
data class Interpretation(val reference: List<String>): Cache
data class Conditional(val conditions: List<Condition>): Cache
data class If(val reference: List<String>, val content: List<Cache>): Condition
data class ElseIf(val reference: List<String>, val content: List<Cache>): Condition
data class Else(val content: List<Cache>): Condition
data class ListLoop(val reference: List<String>, val variableName: String, val content: List<Cache>): Cache
data class MapLoop(val reference: List<String>, val keyName: String, val valueName: String, val content: List<Cache>): Cache
data class Include(val reference: List<String>, val content: List<Cache>)
