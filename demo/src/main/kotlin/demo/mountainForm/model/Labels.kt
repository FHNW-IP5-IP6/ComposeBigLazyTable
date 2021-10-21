package demo.mountainForm.model

import model.ILabel

enum class Labels(val deutsch : String, val english : String) : ILabel {
    TITLE("Berg", "Mountain"),
    ID("ID"                             , "ID"),
    NAME("Name"                         , "Name"),
    HEIGHT("Höhe"                       , "Height"),
    TYPE("Typ"                          , "Type"),
    REGION("Region"                     , "Region"),
    CANTONS("Kantone"                   , "Cantons"),
    RANGE("Gebiet"                      , "Range"),
    ISOLATON("Dominanz"                 , "Isolation"),
    ISOLATIONPOINT("Isolationspunkt"    , "Isolation Point"),
    PROMINENCE("Schartenhöhe"           , "Prominence"),
    PROMINENCEPOINT("Prominenzpunkt"    , "Prominence Point"),
    IMAGECAPTION("Bildunterschrift"     , "Caption"),
    IMAGEURL("Bild Url"                 , "Image Url"),


    HEADERGROUP("Übersicht", "Overview"),
    GROUP("Berge", "Mountains"),


    //CANTONS
    CANTON_AG("AG", "AG"),
    CANTON_AR("AR", "AR"),
    CANTON_AI("AI", "AI"),
    CANTON_BL("BL", "BL"),
    CANTON_BS("BS", "BS"),
    CANTON_BE("BE", "BE"),
    CANTON_FR("FR", "FR"),
    CANTON_GE("GE", "GE"),
    CANTON_GL("GL", "GL"),
    CANTON_GR("GR", "GR"),
    CANTON_JU("JU", "JU"),
    CANTON_LU("LU", "LU"),
    CANTON_NE("NE", "NE"),
    CANTON_NW("NW", "NW"),
    CANTON_OW("OW", "OW"),
    CANTON_SG("SG", "SG"),
    CANTON_SH("SH", "SH"),
    CANTON_SZ("SZ", "SZ"),
    CANTON_SO("SO", "SO"),
    CANTON_TG("TG", "TG"),
    CANTON_TI("TI", "TI"),
    CANTON_UR("UR", "UR"),
    CANTON_VD("VD", "VD"),
    CANTON_VS("VS", "VS"),
    CANTON_ZG("ZG", "ZG"),
    CANTON_ZH("ZH", "ZH"),

    CANTON_EMPTY("",""),


    ;
}