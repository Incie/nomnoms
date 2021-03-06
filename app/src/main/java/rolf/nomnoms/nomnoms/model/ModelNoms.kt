package rolf.nomnoms.nomnoms.model

class ModelNoms(val nomId: Long, val name: String, val subtitle: String, val description: String, val latestDate: Long, val defaultImage: Int)
class ModelNomEvent(val eventId: Long, val nomId: Long, val date: Long)
class NomEventViewModel(val nom: ModelNoms, val nomEvent: ModelNomEvent)
class NomImageModel(val imageId: Int, val imagePath: String, val nomId: Int)