package rolf.nomnoms.nomnoms.model

class ModelNoms(val itemId: Long, val name: String, val subtitle: String, val description: String, val latestDate: Long )
class ModelNomEvent(val itemId: Long, val nomId: Long, val date: Long)