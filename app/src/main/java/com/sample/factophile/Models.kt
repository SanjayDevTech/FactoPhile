package com.sample.factophile

data class Topics(val id: String = "", val title: String = "", val img: String = "")

data class Facts(val id: String = "", val topic_id: String ="", val fact: String = "")