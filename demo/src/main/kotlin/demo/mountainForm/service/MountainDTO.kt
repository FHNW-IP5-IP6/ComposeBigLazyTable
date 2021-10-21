package demo.mountainForm.service

class MountainDTO(args: List<String>) {

    private var id: Long                = args[0].toLong()
    private var name: String            = args[1]
    private var height: Double          = args[2].toDouble()
    private var type: String            = args[3]
    private var region: String          = args[4]
    private var cantons: String         = args[5]
    private var range: String           = args[6]
    private var isolation: Double       = args[7].toDouble()
    private var isolationPoint: String  = args[8]
    private var prominence: Double      = args[9].toDouble()
    private var prominencePoint: String = args[10]
    private var imageCaption: String    = args[11]

    fun toLine(delimiter: String): String {
        return java.lang.String.join(
            delimiter,
            java.lang.Long.toString(getId()!!),
            getName(),
            java.lang.Double.toString(getHeight()),
            getType(),
            getRegion(),
            getCantons(),
            getRange(),
            java.lang.Double.toString(getIsolation()),
            getIsolationPoint(),
            java.lang.Double.toString(getProminence()),
            getProminencePoint(),
            getImageCaption()
        )
    }

    fun getId(): Long {
        return id
    }

    fun getName(): String {
        return name
    }

    fun getHeight(): Double {
        return height
    }

    fun getType(): String {
        return type
    }

    fun getRegion(): String {
        return region
    }

    fun getCantons(): String {
        return cantons
    }

    fun getRange(): String {
        return range
    }

    fun getIsolation(): Double {
        return isolation
    }

    fun getIsolationPoint(): String {
        return isolationPoint
    }

    fun getProminence(): Double {
        return prominence
    }

    fun getProminencePoint(): String {
        return prominencePoint
    }

    fun getImageCaption(): String {
        return imageCaption
    }
}