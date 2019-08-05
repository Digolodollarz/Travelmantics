package tech.diggle.apps.travelmantics

import java.io.Serializable

class Deal : Serializable {
    var id: String? = null
    var title: String? = null
    var description: String? = null
    var price: String? = null
    var imageUrl: String? = null

    constructor()

    constructor(title: String, description: String, price: String, imageUrl: String) {
        this.id = id
        this.title = title
        this.description = description
        this.price = price
        this.imageUrl = imageUrl
    }
}